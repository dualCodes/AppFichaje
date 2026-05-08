package com.example.appfichaje.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {

    private static final String PREF_NAME = "app_prefs";
    private static final String TOKEN_KEY = "jwt_token";

    private static final String USER_PREFS = "user_prefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_ROL = "user_rol";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static String getToken(Context context) {
        return getPrefs(context).getString(TOKEN_KEY, null);
    }

    public static void saveToken(Context context, String token) {
        getPrefs(context).edit().putString(TOKEN_KEY, token).apply();
    }

    public static void clearToken(Context context) {
        getPrefs(context).edit().remove(TOKEN_KEY).apply();
        context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
                .edit().clear().apply();
    }

    public static void saveUserInfo(Context context, int userId, String nombre, String rol) {
        context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_USER_ID, userId)
                .putString(KEY_USER_NAME, nombre)
                .putString(KEY_USER_ROL, rol)
                .apply();
    }

    public static int getUserId(Context context) {
        return context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
                .getInt(KEY_USER_ID, -1);
    }

    public static String getUserName(Context context) {
        return context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
                .getString(KEY_USER_NAME, "");
    }

    public static String getUserRol(Context context) {
        return context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
                .getString(KEY_USER_ROL, "");
    }

    public static boolean isLoggedIn(Context context) {
        return getToken(context) != null;
    }
}

