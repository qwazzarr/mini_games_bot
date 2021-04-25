package ru.home.telegram_bot;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.home.telegram_bot.cache.UserDataCache;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.home.telegram_bot.botapi.TelegramFacade;
public class Minesbot extends TelegramWebhookBot {
    private String webHookPath;
    private String botUserName;
    private String botToken;
    private UserDataCache userDataCache;

    private TelegramFacade telegramFacade;
    public Minesbot(DefaultBotOptions botOptions , TelegramFacade telegramFacade , UserDataCache userDataCache) {
        super(botOptions);
        this.telegramFacade = telegramFacade;
        this.userDataCache = userDataCache;
    }

    public UserDataCache getUserDataCache() {
        return userDataCache;
    }

    public void setUserDataCache(UserDataCache userDataCache) {
        this.userDataCache = userDataCache;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotPath() {
        return webHookPath;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        SendMessage replyMessageToUser = telegramFacade.handleUpdate(update);

        return replyMessageToUser;
    }


    public void setWebHookPath(String webHookPath) {
        this.webHookPath = webHookPath;
    }

    public void setBotUserName(String botUserName) {
        this.botUserName = botUserName;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }
}
