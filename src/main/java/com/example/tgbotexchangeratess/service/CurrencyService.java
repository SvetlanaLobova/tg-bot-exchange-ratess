package com.example.tgbotexchangeratess.service;

import com.example.tgbotexchangeratess.model.CurrencyModel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.Scanner;

public class CurrencyService {
    public static String getCurrencyRate(String message, CurrencyModel model) throws IOException, ParseException, JSONException, ArrayIndexOutOfBoundsException {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request;
        String messageArray[] = message.split(" ");
        request = new Request.Builder()
                .url("https://api.apilayer.com/fixer/convert?to=" + messageArray[2] + "&from=" + messageArray[1] + "&amount=" + messageArray[0])
                .addHeader("apikey", "sGrcTiE5qVFJqPKsm78rRNmIU1x2bW5o")
                .build();
        Response response = client.newCall(request).execute();
        Scanner scanner = new Scanner(response.body().string());
        String res = "";
        while (scanner.hasNext()) {
            res += scanner.nextLine();
        }

        JSONObject object = new JSONObject(res);

        model.setResult(object.getInt("result"));

        return messageArray[0] + " " + messageArray[1] + " is " + model.getResult() + " " + messageArray[2];
    }
}
