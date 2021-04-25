package ru.home.telegram_bot.botapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.home.telegram_bot.cache.UserDataCache;

/**
 * @author Sergei Viacheslaev
 */
@Component
@Slf4j
public class TelegramFacade {


    public SendMessage handleUpdate(Update update) {
        SendMessage replyMessage = null;

        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            //log.info("New message from User:{}, chatId: {},  with text: {}",
                   // message.getFrom().getUserName(), message.getChatId(), message.getText());
            replyMessage = handleInputMessage(message);
        }

        return replyMessage;
    }

    private SendMessage handleInputMessage(Message message) {
        String inputMsg = message.getText();
        int userId = message.getFrom().getId();
        BotState botState;
        SendMessage replyMessage;
        BotState current_state = UserDataCache.getUsersCurrentBotState(userId);
        if ( inputMsg.startsWith("Size:")) {
            botState = BotState.CHOOSE_SIZE;
        }
        else if (inputMsg == "/start") {
            botState = BotState.INTRODUCTION;
        }
        else if (inputMsg.startsWith("Level:")) {
            if (current_state == BotState.CHOOSE_SIZE) {
                botState = BotState.CHOOSE_COMPLEXITY;
            }
            else botState = current_state;
        }
        else if (inputMsg.startsWith("Move:")) {
            if (current_state == BotState.CHOOSE_COMPLEXITY | current_state == BotState.MAKING_MOVE) {
                botState = BotState.MAKING_MOVE;
            }
            else botState = current_state;
        }
        else {
            botState = current_state;
        }
        //botState = BotState.INTRODUCTION; // for clearing cache
        //UserDataCache.setUsersCurrentBotState(userId, botState);

        replyMessage = ChooseHandler.processInputMessage(botState, message);

        return replyMessage;
    }


}
