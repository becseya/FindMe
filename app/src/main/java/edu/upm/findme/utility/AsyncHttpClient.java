package edu.upm.findme.utility;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AsyncHttpClient {

    private static final String KEY_SUCCESS = "SUCCESS";
    private static final String KEY_PAYLOAD = "PAYLOAD";

    private final OkHttpClient client = new OkHttpClient();
    private final Executor executor = Executors.newSingleThreadExecutor();

    private void executeRequest(Request request, ResponseHandler responseHandler) {

        Handler messageHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message message) {
                boolean success = message.getData().getBoolean(KEY_SUCCESS, false);
                responseHandler.onResponse(success, message.getData().getString(KEY_PAYLOAD));
            }
        };

        Runnable requestTask = new Runnable() {
            public void run() {
                boolean success;
                String payload;

                // Execute request
                try {
                    Response response = client.newCall(request).execute();
                    success = response.isSuccessful();
                    payload = response.body().string();
                } catch (Exception e) {
                    success = false;
                    payload = e.toString();
                }

                // Send response message to Handler
                Message msg;
                Bundle data;

                msg = messageHandler.obtainMessage();
                data = msg.getData();
                data.putBoolean(KEY_SUCCESS, success);
                data.putString(KEY_PAYLOAD, payload);
                msg.sendToTarget();
            }
        };

        executor.execute(requestTask);
    }

    public void post(String url, RequestBody body, ResponseHandler responseHandler) {
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        executeRequest(request, responseHandler);
    }

    public void get(String url, ResponseHandler responseHandler) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        executeRequest(request, responseHandler);
    }

    public interface ResponseHandler {
        void onResponse(boolean success, String payload);
    }
}
