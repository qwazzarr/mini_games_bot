package ru.home.telegram_bot.cache;

import org.springframework.stereotype.Component;
import ru.home.telegram_bot.botapi.BotState;
import ru.home.telegram_bot.botapi.handlers.UserProfileData;
import ru.home.telegram_bot.cache.DataCache;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory cache.
 * usersBotStates: user_id and user's bot state
 * usersProfileData: user_id  and user's profile data.
 */

@Component
public class UserDataCache implements DataCache {
    private static Map<Integer, BotState> usersBotStates = new HashMap<>();
    private static Map<Integer, UserProfileData> usersProfileData = new HashMap<>();

    //@Override
    public static void setUsersCurrentBotState(int userId, BotState botState) {
        usersBotStates.put(userId, botState);
    }

    //@Override
    public static BotState getUsersCurrentBotState(int userId) {
        BotState botState = usersBotStates.get(userId);
        if (botState == null) {
            botState = BotState.INTRODUCTION;
        }

        return botState;
    }


    //@Override
    public static UserProfileData getUserProfileData(int userId) {
        UserProfileData userProfileData = usersProfileData.get(userId);
        if (userProfileData == null) {
            userProfileData = new UserProfileData();
        }
        return userProfileData;
     }

    //@Override
    public static void saveUserProfileData(int userId, UserProfileData userProfileData) {
        usersProfileData.put(userId, userProfileData);
    }
}

