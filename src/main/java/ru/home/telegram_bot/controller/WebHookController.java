package ru.home.telegram_bot.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.home.telegram_bot.Minesbot;


@RestController
public class WebHookController {
    private final Minesbot telegramBot;

    public WebHookController(Minesbot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        long startTime = System.nanoTime();
        BotApiMethod<?> reply = telegramBot.onWebhookUpdateReceived(update);
        long endTime = System.nanoTime();
        System.out.println("Processing time :"+(endTime-startTime));
        return reply;
    }
}
