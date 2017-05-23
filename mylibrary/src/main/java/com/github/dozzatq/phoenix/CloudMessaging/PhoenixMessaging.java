package com.github.dozzatq.phoenix.CloudMessaging;

import android.os.AsyncTask;

import com.github.dozzatq.phoenix.Util.PhoenixIdGenerator;
import com.github.dozzatq.phoenix.Util.Task;
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

    private final static String ERROR_PARSE = "{error}";

    public static Task<String> newMessageTask(final HashMap<String, Object> dataValue,
                                       final String instanceIdToken,
                                       final String decodedAPIKey){
        final PhoenixPushTask phoenixPushTask = new PhoenixPushTask(dataValue, instanceIdToken, decodedAPIKey);
        final Task<String> task = new Task<String>() {
            @Override
            public void cancelTask() {
                phoenixPushTask.cancel(true);
            }
        };
        phoenixPushTask.setTask(task);
        phoenixPushTask.execute();
        return task;
    }

    private static class PhoenixPushTask extends AsyncTask<Void, Void, PushTaskResult> {

        private HashMap<String, Object> dataValue;
        private String instanceIdToken;
        private String decodedAPIKey;
        private Task<String> task;

        PhoenixPushTask(HashMap<String, Object> dataValue, String instanceIdToken, String decodedAPIKey) {

            this.dataValue = dataValue;
            this.instanceIdToken = instanceIdToken;
            this.decodedAPIKey = decodedAPIKey;
        }

        @Override
        protected void onPostExecute(PushTaskResult taskResult) {
            if (!taskResult.isResult())
                task.notifyFailureListener(taskResult.getException());
            else
                task.notifySuccessListeners(taskResult.getResultString());
            super.onPostExecute(taskResult);
        }

        @Override
        protected void onCancelled() {
            task.notifyCanceledListeners();
            super.onCancelled();
        }

        @Override
        protected PushTaskResult doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;
            FirebaseData firebaseData = new FirebaseData();
            firebaseData.setCollapseKey(PhoenixIdGenerator.generatePushId());
            Gson gsonParser = new Gson();
            firebaseData.setTo(instanceIdToken);
            firebaseData.setData(dataValue);
            String data= gsonParser.toJson(firebaseData);
            String result = null;

            try {
                urlConnection = post("https://fcm.googleapis.com/fcm/send","application/json", data, decodedAPIKey );
            } catch (IOException e) {
                return new PushTaskResult(e, null, false);
            }

            //Read
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            } catch (IOException e) {
                return new PushTaskResult(e, null, false);
            }

            String line = null;
            StringBuilder sb = new StringBuilder();

            try {
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                return new PushTaskResult(e, null, false);
            }

            try {
                bufferedReader.close();
            } catch (IOException e) {
                return new PushTaskResult(e, null, false);
            }
            result = sb.toString();

            return new PushTaskResult(null, result, true);
        }

        public void setTask(Task<String> task) {
            this.task = task;
        }
    }

    private static class PushTaskResult{
        private Exception exception;
        private String resultString;
        private boolean result;

        private PushTaskResult(Exception exception, String resultString, boolean result) {
            this.exception = exception;
            this.resultString = resultString;
            this.result = result;
        }

        public boolean isResult() {
            return result;
        }

        public String getResultString() {
            return resultString;
        }

        public Exception getException() {
            return exception;
        }
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
