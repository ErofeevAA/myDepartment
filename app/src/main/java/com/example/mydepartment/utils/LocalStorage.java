package com.example.mydepartment.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalStorage {
    private final String PREF_KEY_TOKEN = "token";
    private final String PREF_KEY_NAME = "name";
    private final String PREF_KEY_SURNAME = "surname";
    private final String PREF_KEY_ROLE = "role";
    private final String PREF_KEY_EMAIL = "email";
    private final String PREF_KEY_GROUP = "group";
    // private final String PREF_KEY_GROUP = "group";

    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;

    public LocalStorage(Context context) {
        String PREF_NAME = "storage_auth";
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void setToken(String token) {
        editor.putString(PREF_KEY_TOKEN, token);
        editor.apply();
    }

    public void setName(String name) {
        editor.putString(PREF_KEY_NAME, name);
        editor.apply();
    }

    public void setSurname(String surname) {
        editor.putString(PREF_KEY_SURNAME, surname);
        editor.apply();
    }

    public void setRole(String role) {
        editor.putString(PREF_KEY_ROLE, role);
        editor.apply();
    }

    public void setEmail(String email) {
        editor.putString(PREF_KEY_EMAIL, email);
        editor.apply();
    }

    public void setGroup(String group) {
        editor.putString(PREF_KEY_GROUP, group);
        editor.apply();
    }

    public String getToken() {
        return preferences.getString(PREF_KEY_TOKEN, null);
    }

    public String getName() {
        return preferences.getString(PREF_KEY_NAME, null);
    }

    public String getSurname() {
        return preferences.getString(PREF_KEY_SURNAME, null);
    }

    public String getRole() {
        return preferences.getString(PREF_KEY_ROLE, null);
    }

    public String getEmail() {
        return preferences.getString(PREF_KEY_EMAIL, null);
    }

    public String getGroup() {
        return preferences.getString(PREF_KEY_GROUP, null);
    }

    public void clear() {
        editor.clear().apply();
    }
}
