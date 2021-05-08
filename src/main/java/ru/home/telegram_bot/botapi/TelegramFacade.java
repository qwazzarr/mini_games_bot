package ru.home.telegram_bot.botapi;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.home.telegram_bot.cache.UserDataCache;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergei Viacheslaev
 */
@Component
@Slf4j
public class TelegramFacade {

    String button_text = null;

    public SendMessage handleUpdate(Update update) throws IOException {
        button_text = null;
        SendMessage replyMessage = null;
        //System.out.println("Querry");
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
             button_text = processCallbackQuery(callbackQuery);
             System.out.println("Text_from_button:" + button_text);
        }

        Message message = update.getMessage();

        if (button_text!=null|(message != null && message.hasText())) {
            replyMessage = handleInputMessage(update);
        }


        return replyMessage;
    }

    private SendMessage handleInputMessage(Update update) throws IOException {
        String inputMsg;
        final int userId;
        final long chatId;
        Message message = update.getMessage();
        if (update.hasCallbackQuery()) {
            inputMsg = button_text;
            CallbackQuery buttonQuery = update.getCallbackQuery();
            userId = buttonQuery.getFrom().getId();
            chatId = buttonQuery.getMessage().getChatId();
        }
        else {
            inputMsg = message.getText();
            userId = message.getFrom().getId();
            chatId = message.getChatId();
        }
        BotState botState;
        SendMessage replyMessage;
        BotState current_state = UserDataCache.getUsersCurrentBotState(userId);
        if ( inputMsg.startsWith("Size:")) {
            botState = BotState.CHOOSE_SIZE;
        }
        else if (inputMsg.equals("/new_mines_bot")) {
            inputMsg = "Game:mines";
            botState = BotState.CHOOSE_GAME;
        }
        else if (inputMsg.startsWith("Tic_tac_start:")) {
            botState = BotState.LAUNCH_MATCH;
        }
        else if(inputMsg.startsWith("Tic_tac_type:")){
            botState = BotState.CHOOSE_ROLE;
        }
        else if(inputMsg.startsWith("Tic_tac_move:")){
            botState=BotState.PLAYING_TIC_TAC;

        }
        else if(inputMsg.startsWith("Game:")){
            botState = BotState.CHOOSE_GAME;
        }
        else if (inputMsg.equals("/start")) {
            botState = BotState.INTRODUCTION;
            // change to choose the bot
        }
        else if (inputMsg.startsWith("Level:")) {
            if (current_state == BotState.CHOOSE_SIZE) {
                System.out.println("Choose!");
                botState = BotState.CHOOSE_COMPLEXITY;
            }
            else {
                botState = current_state;
            }
        }
        else if(inputMsg.startsWith("Type:")){
            if(current_state == BotState.CHOOSE_COMPLEXITY|current_state == BotState.MAKING_MOVE){
                botState = BotState.CHOOSE_TYPE;
            }
            else botState = current_state;
        }
        else if (inputMsg.equals("/new_tic_tac")) {
            inputMsg = "Game:tic_tac";
            botState = BotState.CHOOSE_GAME;
        }
        else if (inputMsg.startsWith("Move:")) {
            if (current_state == BotState.CHOOSE_TYPE | current_state == BotState.MAKING_MOVE) {
                System.out.println("Making_move");
                botState = BotState.MAKING_MOVE;
            }
            else botState = current_state;
        }
        else {
            botState = BotState.INTRODUCTION;
        }
        //botState = BotState.INTRODUCTION; // for clearing cache
        //UserDataCache.setUsersCurrentBotState(userId, botState);
        System.out.println("Trying to process: "+botState);
        replyMessage = ChooseHandler.processInputMessage(botState, inputMsg ,userId,chatId);

        return replyMessage;
    }
    private String processCallbackQuery(CallbackQuery buttonQuery) {
        String message = buttonQuery.getData();
        return message;
    }

}
