package com.example.mydepartment.service;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.mydepartment.utils.LocalStorage;
import com.example.mydepartment.utils.Requests;
import com.google.firebase.messaging.FirebaseMessagingService;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d("Token_", "Refreshed token: " + s);

        LocalStorage storage = new LocalStorage(this);

        String token = storage.getToken();
        new Thread(() -> {
            Requests requests = new Requests();
            requests.setToken(token);

            JSONObject object = new JSONObject();
            try {
                object.put("fcm_token", s);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            requests.sendFCMToken(object.toString());
        }).start();
    }
}
