package ru.home.telegram_bot.botapi;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.glassfish.hk2.api.DuplicateServiceException;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.home.telegram_bot.botapi.handlers.Introduction_Case;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.home.telegram_bot.botapi.handlers.UserProfileData;
import ru.home.telegram_bot.cache.UserDataCache;
import ru.home.telegram_bot.Minesbot;
import static ru.home.telegram_bot.botapi.BotState.*;

@Component
public class ChooseHandler {



    public static SendMessage processInputMessage(BotState currentState, Message message) {

        int user_id = message.getFrom().getId();
        if (currentState == MAKING_MOVE) {
            //check whether the matrix is complete(exists)
            //make a move, update_database , ++move_counter
            String message_text = message.getText().replaceAll("Move:","");
            String[] data = message_text.split(",");
            UserProfileData userProfileData = UserDataCache.getUserProfileData(message.getFrom().getId());
            Field field = userProfileData.getField();
            try{
                int i_guess = Integer.valueOf(data[2].replaceAll(" ",""))-1;
                int j_guess = Integer.valueOf(data[1].replaceAll(" ",""))-1;
                String type = data[0].replaceAll(" ","");
                if (i_guess < 0 | j_guess <0) {
                    throw new ValueException("Both coordinates values should be more than 0");
                }
                if (i_guess > field.X_DIM -1 | j_guess > field.Y_DIM -1) {
                    throw new ValueException("One of the coordinates are bigger than matrix size");
                }
                if (!type.contains("mine") && !type.contains("free")) {
                    throw new ValueException("Move should be either 'mine' or 'free' ");
                }
            }
            catch (Exception exception){
                SendMessage reply = new SendMessage(message.getChatId(),"Check input data: "+exception.getMessage());
                return reply;
            }
            UserDataCache.setUsersCurrentBotState(user_id, MAKING_MOVE);
            SendMessage reply;
            System.out.println(message.getFrom().getId());
            field.update_matrix(Integer.valueOf(data[1].replaceAll(" ",""))-1,Integer.valueOf(data[2].replaceAll(" ",""))-1,data[0].replaceAll(" ",""));
            if (!field.isActive()) {
                reply = field.end_message(message);
                UserDataCache.setUsersCurrentBotState(message.getFrom().getId(), CHOOSE_SIZE);
            }
            else {
                reply = field.print_matrix(message);
            }
            userProfileData.setField(field);
            return reply;
        }
        else if (currentState == CHOOSE_COMPLEXITY) {
            //retrieve matrix from the database
            String message_text = message.getText().replaceAll("Level:", "");
            message_text = message_text.replaceAll(" ","");
            try{
                if ((!message_text.contains("HARD")&&!message_text.contains("MEDIUM")&&!message_text.contains("EASY"))) {
                    System.out.println(message_text);
                    throw new ValueException("Check input data: "+ message_text);
                }
            }
            catch (Exception e) {
                SendMessage reply = new SendMessage(message.getChatId(),"Check input data :)");
                return reply;
            }
            UserDataCache.setUsersCurrentBotState(user_id, CHOOSE_COMPLEXITY);
            UserProfileData userProfileData = UserDataCache.getUserProfileData(message.getFrom().getId());
            Field field = userProfileData.getField();
            SendMessage reply = field.set_complexity(message);
            userProfileData.setField(field);
            //initiate its mine reproducing
            //ofc print matrix
            return reply;

        }
        else if (currentState == CHOOSE_SIZE) {
            UserProfileData userProfileData = UserDataCache.getUserProfileData(message.getFrom().getId());
            String message_text = message.getText().replaceAll("Size:", "");
            String[] data = message_text.split(",");
            int x_dim;
            int y_dim;
            try {
                x_dim = Integer.parseInt(String.valueOf(data[0].replaceAll(" ","")));
                y_dim = Integer.parseInt(String.valueOf(data[1].replaceAll(" ","")));
                if (x_dim < 4 | y_dim < 4) {
                    throw new ValueException("x_dim and y_dim should be at least 4");
                } else if (x_dim > 20 | y_dim > 20) {
                    throw new ValueException("Right now our platform supports dimensions below 20");
                }
            }
            catch(Exception e){
                SendMessage reply = new SendMessage(message.getChatId(),"Check input data :)");
                return reply;
            }
            UserDataCache.setUsersCurrentBotState(user_id, CHOOSE_SIZE);
            Field field = new Field(x_dim,y_dim);
            userProfileData.setField(field);
            SendMessage reply = field.size_info(message);
            //field.generate_mines_location();
            UserDataCache.saveUserProfileData(message.getFrom().getId(), userProfileData);

            return reply;

        }
        else if (currentState == INTRODUCTION) {
            //initiate the user ?
            UserDataCache.setUsersCurrentBotState(user_id, INTRODUCTION);
            UserProfileData userProfileData = UserDataCache.getUserProfileData(user_id);
            Introduction_Case handler = new Introduction_Case(message, message.getFrom().getId());
            SendMessage reply = handler.generate_reply();
            UserDataCache.saveUserProfileData(message.getFrom().getId(), userProfileData);
            return reply;
        }
        return new SendMessage();
    }
}
