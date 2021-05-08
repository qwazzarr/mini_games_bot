package ru.home.telegram_bot.botapi;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ApiRequest {


    public static void ApiToOpponent(InlineKeyboardMarkup keyboardMarkup, long chat_id, String text) throws IOException {
        HttpClient httpclient = HttpClients.createDefault();
        List<List<InlineKeyboardButton>> keyboard = keyboardMarkup.getKeyboard();

        URL url = new URL ("https://api.telegram.org/bot1743311159:AAGFKwrWUj0jBey7VZCwYZKeXKeE64Baedc/sendMessage");
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);







        //HttpPost httppost = new HttpPost("https://api.telegram.org/bot1778589497:AAE2CGovQ673DEaIZoWgCMFa5zClnwYIWbk/sendMessage");

        JSONObject params = new JSONObject();
        params.put("chat_id",Long.toString(chat_id));
        params.put("text",text);
        JSONArray field_of_buttons = new JSONArray();
        JSONArray row_of_buttons;
        for( List<InlineKeyboardButton> row : keyboard) {
            row_of_buttons = new JSONArray();
            for(InlineKeyboardButton button : row){
                JSONObject button_json = new JSONObject();
                //JSONArray param_aray = new JSONArray();
                //JSONObject button_param = new JSONObject();
                button_json.put("text",button.getText());
                button_json.put("callback_data",button.getCallbackData());
                //param_aray.put(button_param);
                //button_json.put("InlineKeyboardButton",param_aray);
                row_of_buttons.put(button_json);
            }
            field_of_buttons.put(row_of_buttons);
        }
        JSONObject last_json = new JSONObject();
        last_json.put("inline_keyboard",field_of_buttons);
        params.put("reply_markup",last_json);

        try(OutputStream os = con.getOutputStream()) {
            byte[] input = params.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            ;
        }
        //StringEntity requestEntity = new StringEntity(params.toString(), ContentType.APPLICATION_JSON);

        //httppost.setEntity(requestEntity);
        // System.out.println(requestEntity);
        // HttpResponse response = httpclient.execute(httppost);
        //HttpEntity entity = response.getEntity();
        //System.out.println(response);
    }


}
