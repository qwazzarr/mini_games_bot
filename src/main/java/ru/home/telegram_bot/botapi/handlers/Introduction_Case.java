package ru.home.telegram_bot.botapi.handlers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.springframework.stereotype.Component;

public class Introduction_Case {
    Message message;
    int id;
    public Introduction_Case(Message message , int id) {
        this.message = message;
        this.id = id;

    }

    public SendMessage generate_reply() {
        SendMessage reply = new SendMessage(message.getChatId(),"Hello BITCH! If you want TO START,please write 'Size: desirable_x , desirable_y' ");
        return reply;
    }
}
