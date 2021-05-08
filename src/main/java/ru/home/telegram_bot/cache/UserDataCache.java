package ru.home.telegram_bot.cache;

import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Component;
import ru.home.telegram_bot.botapi.BotState;
import ru.home.telegram_bot.botapi.TicTac;
import ru.home.telegram_bot.botapi.handlers.UserProfileData;
import ru.home.telegram_bot.cache.DataCache;

import java.util.ArrayList;
import java.util.Arrays;
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
    private static ArrayList<ArrayList<Object>> openXmatches = new ArrayList<ArrayList<Object>>(new ArrayList<>());
    private static ArrayList<ArrayList<Object>> openOmatches = new ArrayList<ArrayList<Object>>(new ArrayList<>());



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
            System.out.println("Creating_new_player");
            userProfileData = new UserProfileData();
        }
        return userProfileData;
     }

    //@Override
    public static void saveUserProfileData(int userId, UserProfileData userProfileData) {
        usersProfileData.put(userId, userProfileData);
    }

    public static void openTicTacMatch(String type,TicTac field , long chat_id){
        ArrayList<Object> info = new ArrayList<Object>();
        info.add(chat_id);
        info.add(field);
        if(type.equals("X")){
            openXmatches.add(info);
            System.out.println("Adding to X_field_database");
        }
        else {
            openOmatches.add(info);
            System.out.println("Adding to O_field_database");

        }
    }
    public static boolean xOpenMatches() {
        if(openXmatches.size()>0){
            return true;
        }
        return false;
    }
    public static boolean oOpenMatches() {
        if(openOmatches.size()>0){
            return true;
        }
        return false;
    }
    public static  long findPlayer(int userId,String type){
        UserProfileData userProfileData1 = UserDataCache.getUserProfileData(userId);
        System.out.println("userProfileData1:"+userProfileData1);
        ArrayList<Object> info2;
        if(type.equals("X")){
            info2 = openOmatches.get(0);
            openOmatches.remove(0);
        }
        else {
            info2 = openXmatches.get(0);
            openXmatches.remove(0);
        }
        TicTac ticTac = (TicTac) info2.get(1);
        System.out.println("Retrieved field from db: "+ Arrays.deepToString(ticTac.getField()));
        UserProfileData userProfileData2 = UserDataCache.getUserProfileData(ticTac.getPlayer1_id());
        ticTac.setOpponent(userId);

        System.out.println("Creator" + userProfileData2);
        userProfileData2.setTicTac(ticTac);

        userProfileData1.setTicTac(ticTac);
        System.out.println("Updated finder"+userProfileData1);
        long opponent_chat_id = (long) info2.get(0);
        return opponent_chat_id;

    }

}

