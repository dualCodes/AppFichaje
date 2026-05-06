package com.example.appfichaje.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class TokenManager {

    private static final String PREF_NAME = "secret_shared_prefs";
    private static final String TOKEN_KEY = "jwt_token";

    private static final String USER_PREFS = "user_prefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_ROL = "user_rol";

    public static String getToken(Context context) {
        try {
            KeyGenParameterSpec keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC;
            String mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec);

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    PREF_NAME,
                    mainKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            return sharedPreferences.getString(TOKEN_KEY, null);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void clearToken(Context context) {
        try {
            KeyGenParameterSpec keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC;
            String mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec);

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    PREF_NAME,
                    mainKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            sharedPreferences.edit().remove(TOKEN_KEY).apply();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
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

