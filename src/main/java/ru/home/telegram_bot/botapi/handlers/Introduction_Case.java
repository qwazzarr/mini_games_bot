package ru.home.telegram_bot.botapi.handlers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.springframework.stereotype.Component;
import ru.home.telegram_bot.botapi.Button_handler;

public class Introduction_Case {
    String message;
    long id;
    public Introduction_Case(String message , long id) {
        this.message = message;
        this.id = id;

    }

    public SendMessage generate_reply() {
        SendMessage reply = new SendMessage(id,"Hello player! Please choose a game ; )");
        reply.setReplyMarkup(Button_handler.introductionButtons());
        return reply;
    }
}
