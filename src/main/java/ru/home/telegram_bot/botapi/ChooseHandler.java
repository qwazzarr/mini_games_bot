package ru.home.telegram_bot.botapi;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.glassfish.hk2.api.DuplicateServiceException;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.home.telegram_bot.botapi.handlers.Introduction_Case;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.home.telegram_bot.botapi.handlers.UserProfileData;
import ru.home.telegram_bot.cache.UserDataCache;
import ru.home.telegram_bot.botapi.Button_handler;
import ru.home.telegram_bot.Minesbot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.home.telegram_bot.botapi.BotState.*;

@Component
public class ChooseHandler {


    private static final List<String> available_games = Arrays.asList("mines", "tic_tac");

    public static SendMessage processInputMessage(BotState currentState, String message , int user_id , long chat_id) throws IOException {

        if (currentState == MAKING_MOVE) {
            //check whether the matrix is complete(exists)
            //make a move, update_database , ++move_counter
            String message_text = message.replaceAll("Move:","");
            String[] data = message_text.split(",");
            UserProfileData userProfileData = UserDataCache.getUserProfileData(user_id);
            Field field = userProfileData.getField();
            try{
                int i_guess = Integer.valueOf(data[1].replaceAll(" ",""))-1;
                int j_guess = Integer.valueOf(data[0].replaceAll(" ",""))-1;
                String type = userProfileData.getNext_move();
                if (i_guess < 0 | j_guess <0) {
                    throw new ValueException("Both coordinates values should be more than 0");
                }
                if (j_guess > field.X_DIM -1 | i_guess > field.Y_DIM -1) {
                    throw new ValueException("One of the coordinates are bigger than matrix size");
                }
                if (!type.contains("mine") && !type.contains("free")) {
                    throw new ValueException("Move should be either 'mine' or 'free' ");
                }
            }
            catch (Exception exception){
                SendMessage reply = new SendMessage(chat_id,"Check input data: "+exception.getMessage());
                return reply;
            }
            UserDataCache.setUsersCurrentBotState(user_id, MAKING_MOVE);
            SendMessage reply;

            field.update_matrix(Integer.valueOf(data[0].replaceAll(" ",""))-1,Integer.valueOf(data[1].replaceAll(" ",""))-1,userProfileData.getNext_move());
            if (field.isActive()) {
                System.out.println("Active");
                reply = field.print_matrix(message , chat_id);
                reply.setReplyMarkup(Button_handler.moveButtons(field));
            }
            else {
                UserDataCache.setUsersCurrentBotState(user_id, CHOOSE_SIZE);
                reply = field.end_message(message , chat_id , field );

            }
            userProfileData.setField(field);
            UserDataCache.saveUserProfileData(user_id, userProfileData);
            return reply;
        }
        else if (currentState == CHOOSE_TYPE){
            String message_text = message.replaceAll("Type:", "");
            message_text = message_text.replaceAll(" ","");
            UserDataCache.setUsersCurrentBotState(user_id, CHOOSE_TYPE);
            UserProfileData userProfileData = UserDataCache.getUserProfileData(user_id);
            userProfileData.setNext_move(message_text);
            Field field = userProfileData.getField();

            String reply_text = "Your type of move:" + message_text;
            reply_text+="\n";
            reply_text+="If you want to change your move type , choose mine/free";
            reply_text+="\n";
            SendMessage reply = field.print_matrix(message , chat_id);
            reply.setText(reply.getText()+"\n"+reply_text);
            reply.setReplyMarkup(Button_handler.moveButtons(field));


            UserDataCache.saveUserProfileData(user_id, userProfileData);
            return reply;


        }
        else if (currentState == CHOOSE_COMPLEXITY) {
            //retrieve matrix from the database

            System.out.println("Choose_complexity_handler");
            String message_text = message.replaceAll("Level:", "");
            message_text = message_text.replaceAll(" ","");
            try{
                if ((!message_text.contains("HARD")&&!message_text.contains("MEDIUM")&&!message_text.contains("EASY"))) {
                    System.out.println(message_text);
                    throw new ValueException("Check input data: "+ message_text);
                }
            }
            catch (Exception e) {
                SendMessage reply = new SendMessage(chat_id,"Check input data :)");
                return reply;
            }
            UserDataCache.setUsersCurrentBotState(user_id, CHOOSE_COMPLEXITY);
            UserProfileData userProfileData = UserDataCache.getUserProfileData(user_id);
            Field field = userProfileData.getField();
            SendMessage reply = field.set_complexity(message_text , chat_id);
            System.out.println("Number_of_mines:"+field.getNumber_of_mines());
            userProfileData.setField(field);

            UserDataCache.saveUserProfileData(user_id, userProfileData);
            //initiate its mine reproducing
            //ofc print matrix
            return reply;

        }
        else if (currentState == CHOOSE_SIZE) {
            UserProfileData userProfileData = UserDataCache.getUserProfileData(user_id);
            String message_text = message.replaceAll("Size:", "");
            message_text = message_text.replaceAll(" ","");
            int x_dim;
            int y_dim;
            try {
                if ((!message_text.contains("LARGE")&&!message_text.contains("MEDIUM")&&!message_text.contains("SMALL"))) {
                    System.out.println(message_text);
                    throw new ValueException("Check input data: "+ message_text);
                }
            }
            catch(Exception e){
                SendMessage reply = new SendMessage(chat_id,"Check input data :)");
                return reply;
            }
            UserDataCache.setUsersCurrentBotState(user_id, CHOOSE_SIZE);
            Field field = new Field(message_text);
            userProfileData.setField(field);
            SendMessage reply = field.size_info(message,chat_id);
            //field.generate_mines_location();
            UserDataCache.saveUserProfileData(user_id, userProfileData);

            return reply;

        }
        else if (currentState == INTRODUCTION) {
            //initiate the user ?
            UserDataCache.setUsersCurrentBotState(user_id, INTRODUCTION);
            UserProfileData userProfileData = UserDataCache.getUserProfileData(user_id);
            Introduction_Case handler = new Introduction_Case(message, user_id);
            SendMessage reply = handler.generate_reply();
            UserDataCache.saveUserProfileData(user_id, userProfileData);
            return reply;
        }
        else if(currentState == CHOOSE_GAME) {
            UserProfileData userProfileData = UserDataCache.getUserProfileData(user_id);
            message = message.replace("Game:","");
            if (!available_games.contains(message)){
                UserDataCache.saveUserProfileData(user_id, userProfileData);
                return new SendMessage(chat_id ,"Please use the keyboard to choose the game:"+message);
            }
            UserDataCache.setUsersCurrentBotState(user_id, CHOOSE_GAME);
            if(message.equals("mines")){
                SendMessage reply = new SendMessage(chat_id,"You chose to play Minesweeper" + "\n" + "Chose the size of your field");
                UserDataCache.saveUserProfileData(user_id, userProfileData);
                reply.setReplyMarkup(Button_handler.minesSizeButtons());
                return reply;
            }
            if(message.equals("tic_tac")){
                SendMessage reply = new SendMessage(chat_id,"You chose to play Tic-Tac-Toe" + "\n" + "Choose what you play");
                reply.setReplyMarkup(Button_handler.ticTacType());
                userProfileData.setActive_tic_tac(false);
                UserDataCache.saveUserProfileData(user_id, userProfileData);
                return reply;
            }
        }

        else if (currentState == CHOOSE_ROLE) {
            UserProfileData userProfileData = UserDataCache.getUserProfileData(user_id);
            if (userProfileData.isActive_tic_tac()){
                return new SendMessage(chat_id,"You already started the game");
            }
            message = message.replace("Tic_tac_type:", "");
            if (!message.equals("X") && !message.equals("O")) {
                return new SendMessage(chat_id, "Sorry, repeat your type");
            }
            if (message.equals("X")) {
                UserDataCache.setUsersCurrentBotState(user_id, CHOOSE_ROLE);
                SendMessage reply = new SendMessage(chat_id, "Please, make your first move");
                reply.setReplyMarkup(Button_handler.firstcrossMove());
                UserDataCache.saveUserProfileData(user_id, userProfileData);
                return reply;
            }
            if (!UserDataCache.xOpenMatches()) {

                UserDataCache.setUsersCurrentBotState(user_id, SEARCHING_GAME);
                TicTac ticTac = new TicTac(-1, -1, user_id, "0",chat_id);
                userProfileData.setTicTac(ticTac);
                UserDataCache.openTicTacMatch("O", ticTac, chat_id);
                userProfileData.setActive_tic_tac(true);
                UserDataCache.saveUserProfileData(user_id, userProfileData);
                SendMessage reply = new SendMessage(chat_id,"We are looking for your opponent");
                return reply;
            }
            else {
                UserDataCache.setUsersCurrentBotState(user_id, PLAYING_TIC_TAC);
                UserDataCache.findPlayer(user_id,"0");
                TicTac ticTac = userProfileData.getTicTac();
                ticTac.setPlayer2_chat_id(chat_id);
                UserProfileData userProfileData1 = UserDataCache.getUserProfileData(ticTac.OpponentID(user_id));
                userProfileData.setTicTac(ticTac);
                userProfileData1.setTicTac(ticTac);
                userProfileData.setActive_tic_tac(true);
                SendMessage reply = new SendMessage(chat_id,"We found an opponent for you. Make your move!");
                reply.setReplyMarkup(Button_handler.ticTacMove(ticTac,"O"));
                UserDataCache.saveUserProfileData(user_id, userProfileData);
                return reply;
// execute method and handle any error responses.
// handle response.
            }


        }
        else if (currentState == LAUNCH_MATCH) {
            UserProfileData userProfileData = UserDataCache.getUserProfileData(user_id);
            if (UserDataCache.getUsersCurrentBotState(user_id) != CHOOSE_ROLE) {
                return new SendMessage(chat_id,"");
            }
            if (userProfileData.isActive_tic_tac()){
                return new SendMessage(chat_id,"You already started the game");
            }
            userProfileData.setActive_tic_tac(true);
            message = message.replace("Tic_tac_start:","");
            String[] data = message.split(",");

            if (!UserDataCache.oOpenMatches()){
                UserDataCache.setUsersCurrentBotState(user_id,SEARCHING_GAME);
                TicTac ticTac = new TicTac(Integer.valueOf(data[0]),Integer.valueOf(data[1]),user_id,"X",chat_id);
                ticTac.setTic_tac_move1(false);
                userProfileData.setTicTac(ticTac);
                UserDataCache.openTicTacMatch("X",ticTac,chat_id);
                UserDataCache.saveUserProfileData(user_id, userProfileData);
                SendMessage reply = new SendMessage(chat_id,"We are looking for your opponent ");
                return reply;
            }
            else {
                UserDataCache.setUsersCurrentBotState(user_id,PLAYING_TIC_TAC);
                // find the opponent , take his matrix , make move, send him message , send message with confirmation of the move
                long opp_chat_id = UserDataCache.findPlayer(user_id,"X");

                TicTac ticTac = userProfileData.getTicTac();
                System.out.println(ticTac);
                ticTac.setPlayer2_chat_id(chat_id);
                SendMessage reply = ticTac.makeMove(Integer.valueOf(data[0]),Integer.valueOf(data[1]),user_id,chat_id);
                userProfileData.setTicTac(ticTac);
                UserProfileData userProfileData1 = UserDataCache.getUserProfileData(ticTac.OpponentID(user_id));
                userProfileData1.setTicTac(ticTac);
                UserDataCache.saveUserProfileData(user_id, userProfileData);
                UserDataCache.saveUserProfileData(ticTac.OpponentID(user_id),userProfileData1);
                ApiRequest.ApiToOpponent(Button_handler.ticTacMove(ticTac,"O"),opp_chat_id,"Your opponent has been found. Make a move");

                //
                return reply;
            }

        }
        else if(currentState == PLAYING_TIC_TAC){
            message = message.replace("Tic_tac_move:","");
            String[] data = message.split(",");
            UserProfileData userProfileData = UserDataCache.getUserProfileData(user_id);
            TicTac ticTac = userProfileData.getTicTac();
            System.out.println("Tic_tac: "+Arrays.deepToString(ticTac.getField()));
            System.out.println("Move , type:" + data[2]);
            UserProfileData userProfileData1 = UserDataCache.getUserProfileData(ticTac.OpponentID(user_id));
            SendMessage reply = ticTac.makeMove(Integer.valueOf(data[0]),Integer.valueOf(data[1]),user_id,chat_id);
            if(reply.getText().equals("This square is already taken ,try again") |reply.getText().equals("Please wait for your opponent move")) {
                System.out.println("Filthy scum trying to move twice");
                return reply;
            }
            userProfileData.setTicTac(ticTac);
            userProfileData1.setTicTac(ticTac);
            String status = ticTac.isFinished();
            if (!status.equals("not")) {
                if(status.equals("cross") |status.equals("circle") ){
                    reply = new SendMessage(chat_id,"You won! Congratulations :)");
                    UserDataCache.setUsersCurrentBotState(user_id,INTRODUCTION);
                    UserDataCache.saveUserProfileData(user_id, userProfileData);
                    UserDataCache.saveUserProfileData(ticTac.OpponentID(user_id),userProfileData1);
                    ApiRequest.ApiToOpponent(Button_handler.lost_field(ticTac),ticTac.opponentChatID(chat_id),"You lost. Tap any square if you want to start a new game");
                    return reply;

                }
                else {
                    reply = new SendMessage(chat_id,"That's a draw !");
                    UserDataCache.setUsersCurrentBotState(user_id,INTRODUCTION);
                    UserDataCache.saveUserProfileData(user_id, userProfileData);
                    UserDataCache.saveUserProfileData(ticTac.OpponentID(user_id),userProfileData1);
                    ApiRequest.ApiToOpponent(Button_handler.lost_field(ticTac),ticTac.opponentChatID(chat_id),"That's a draw. Tap any square if you want to start a new game");
                    return reply;
                }
            }
            String opponent_type;
            System.out.println("Play_phase");
            if (data[2] == "X"){
                opponent_type = "O";
            }
            else {
                opponent_type = "X";
            }

            UserDataCache.saveUserProfileData(user_id, userProfileData);
            UserDataCache.saveUserProfileData(ticTac.OpponentID(user_id),userProfileData1);
            System.out.println("Sending tictac through api : " + Arrays.deepToString(ticTac.getField()));
            ApiRequest.ApiToOpponent(Button_handler.ticTacMove(ticTac,opponent_type),ticTac.opponentChatID(chat_id),"Your opponent has made their move. Your turn");
            return reply;

        }

        return new SendMessage();
    }
}
