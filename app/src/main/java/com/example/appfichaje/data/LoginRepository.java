package com.example.appfichaje.data;

import android.content.Context;

import com.example.appfichaje.data.model.GenericResponse;
import com.example.appfichaje.data.model.LoginRequest;
import com.example.appfichaje.data.model.LoginResponse;
import com.example.appfichaje.data.model.SolicitarCambioPasswordRequest;
import com.example.appfichaje.data.net.ApiClient;
import com.example.appfichaje.data.net.ApiService;

import retrofit2.Call;

public class LoginRepository {

    private ApiService apiService;

    public LoginRepository(Context context) {
        this.apiService = ApiClient.getClient(context).create(ApiService.class);
    }

    public Call<LoginResponse> login(String email, String password) {
        return apiService.login(new LoginRequest(email, password));
    }

    public Call<GenericResponse> solicitarCambioPassword(String email) {
        return apiService.solicitarCambioPassword(new SolicitarCambioPasswordRequest(email));
    }
}
