package com.example.mydepartment.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
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
        final String URL_API = "http://192.168.43.79/api";

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
            reader.close();
            response = stringBuilder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap loadImage(String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            input.close();
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void loadPDF(String link) {
        int count;
        try {
            URL url = new URL(link);
            URLConnection connection = url.openConnection();
            connection.connect();

            InputStream input = new BufferedInputStream(url.openStream(),
                    8192);
            Log.d("path", Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());

            int index = 0;
            for (int i = link.length() - 1; ; --i) {
                if (link.charAt(i) == '/') {
                    index = i;
                    break;
                }
            }
            String fileName = link.substring(index);

            OutputStream output = new FileOutputStream(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
                    + fileName);

            byte[] data = new byte[1024];

            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();

        } catch (Exception e) {
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
        send(URI, data);
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

    public void getComments(String subjectID, String sectionID) {
        method = "GET";
        final String URI = "/subjects/" + subjectID + "/sections/" + sectionID + "/comments";
        send(URI, null);
    }

    public void sendComments(String subjectID, String sectionID, String data) {
        method = "POST";
        final String URI = "/subjects/" + subjectID + "/sections/" + sectionID + "/comments";
        send(URI, data);
    }

    public void getNotifications() {
        method = "GET";
        final String URI = "/notifications";
        send(URI, null);
    }

    public void sendNotificationsID(String id) {
        method = "PATCH";
        final String URI = "/notifications/" + id;
        send(URI, null);
    }

    public void sendFCMToken(String data) {
        method = "POST";
        final String URI = "/notifications";
        send(URI, data);
    }

    public void groups() {
        method = "GET";
        final String URI = "/groups";
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
