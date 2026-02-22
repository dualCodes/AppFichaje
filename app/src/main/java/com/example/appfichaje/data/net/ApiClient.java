package com.example.appfichaje.data.net;

import android.content.Context;

import com.example.appfichaje.utils.TokenManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "https://yaliora113.eu.pythonanywhere.com/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        // Interceptor para reintentar en caso de error 500
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);
                int tryCount = 0;
                while (!response.isSuccessful() && response.code() >= 500 && tryCount < 3) {
                    tryCount++;
                    response.close();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    response = chain.proceed(request);
                }
                return response;
            }
        });

        // Interceptor para añadir el token JWT
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder();

            if (!original.url().encodedPath().contains("/api/auth/login")) {
                String token = TokenManager.getToken(context.getApplicationContext());
                if (token != null) {
                    requestBuilder.header("Authorization", "Bearer " + token);
                }
            }

            Request newRequest = requestBuilder.build();
            return chain.proceed(newRequest);
        });

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        return retrofit;
    }
}
