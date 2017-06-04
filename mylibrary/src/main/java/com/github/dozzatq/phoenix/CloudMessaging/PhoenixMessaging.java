package com.github.dozzatq.phoenix.CloudMessaging;

import android.os.AsyncTask;

import com.github.dozzatq.phoenix.Tasks.Task;
import com.github.dozzatq.phoenix.Tasks.TaskSource;
import com.github.dozzatq.phoenix.Tasks.Tasks;
import com.github.dozzatq.phoenix.Util.PhoenixIdGenerator;
import com.google.gson.Gson;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


/**
 * Created by dxfb on 23.05.2017.
 */

public class PhoenixMessaging {

    public static Task<String> newMessageTask(final HashMap<String, Object> dataValue,
                                       final String instanceIdToken,
                                       final String decodedAPIKey){
        TaskSource<String> task = new TaskSource<String>() {
            @Override
            public String call() throws Exception {
                HttpURLConnection urlConnection = null;
                FirebaseData firebaseData = new FirebaseData();
                firebaseData.setCollapseKey(PhoenixIdGenerator.generatePushId());
                Gson gsonParser = new Gson();
                firebaseData.setTo(instanceIdToken);
                firebaseData.setData(dataValue);
                String data= gsonParser.toJson(firebaseData);
                String result = null;

                urlConnection = post("https://fcm.googleapis.com/fcm/send",
                        "application/json", data, decodedAPIKey );

                //Read
                BufferedReader bufferedReader = null;
                bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

                String line = null;
                StringBuilder sb = new StringBuilder();

                while ((line = bufferedReader.readLine()) != null) {
                   sb.append(line);
                }
                bufferedReader.close();

                result = sb.toString();

                return result;
            }
        };
        return Tasks.execute(task);
    }

    private static HttpURLConnection post(String url, String contentType, String body, String key)
            throws IOException {
        if (url == null || body == null) {
            throw new IllegalArgumentException("arguments cannot be null");
        }
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = getConnection(url);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setFixedLengthStreamingMode(bytes.length);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", contentType);
        conn.setRequestProperty("Authorization", "key=" + key);
        OutputStream out = conn.getOutputStream();
        out.write(bytes);
        out.close();
        return conn;
    }

    private static HttpURLConnection getConnection(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        return conn;
    }

}
