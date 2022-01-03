package com.example.mydepartment.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Requests {
    private String method = null;
    private String response = null;

    private int statusCode = 0;
    private String token = null;

    public Requests() {}

    private void send(String uri, String data) {
        //for local test
        // 192.168.1.48
        // 192.168.43.79
        final String URL_API = "http://192.168.1.48/api";

        try {
            URL url = new URL(URL_API + uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            if (token != null) {
                Log.d("token", token);
                connection.setRequestProperty("Authorization", "Bearer " + token);
            }

            if (!method.equals("GET")) {
                connection.setDoOutput(true);
            }

            if (data != null) {
                OutputStream stream = connection.getOutputStream();
                stream.write(data.getBytes(StandardCharsets.UTF_8));
                stream.flush();
                stream.close();
            }

            statusCode = connection.getResponseCode();
            InputStreamReader reader;
            if (statusCode == 201 || statusCode == 200) {
                reader = new InputStreamReader(connection.getInputStream());
            } else {
                reader = new InputStreamReader(connection.getErrorStream());
            }
            BufferedReader buffer = new BufferedReader(reader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = buffer.readLine()) != null) {
                stringBuilder.append(line);
            }
            buffer.close();
            response = stringBuilder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void login(String data) {
        final String URI = "/login";
        method = "POST";
        if (data.equals("")) {
            return;
        }
        send(URI, data);
    }

    public void register(String data) {
        method = "POST";
        final String URI = "/register";
    }

    public void logout() {
        method = "POST";
        final String URI = "/logout";
        send(URI, null);
    }

    public void subjects() {
        method = "GET";
        final String URI = "/subjects";
        send(URI, null);
    }

    public void sections(String subjectID) {
        method = "GET";
        final String URI = "/subjects/" + subjectID + "/sections";
        send(URI, null);
    }

    public void comments(String subjectID, String sectionID) {
        method = "GET";
        final String URI = "/subjects/" + subjectID + "/sections/" + sectionID + "/comments";
        send(URI, null);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponse() {
        return response;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
