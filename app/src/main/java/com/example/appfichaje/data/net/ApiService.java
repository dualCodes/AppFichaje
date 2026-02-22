package com.example.appfichaje.data.net;

import com.example.appfichaje.data.model.EntradaResponse;
import com.example.appfichaje.data.model.FichajeGpsRequest;
import com.example.appfichaje.data.model.LoginRequest;
import com.example.appfichaje.data.model.LoginResponse;
import com.example.appfichaje.data.model.SalidaResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("/api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("/api/presencia/entrada")
    Call<EntradaResponse> ficharEntradaGps(@Body FichajeGpsRequest fichajeGpsRequest);

    @POST("/api/presencia/salida")
    Call<SalidaResponse> ficharSalidaGps(@Body FichajeGpsRequest fichajeGpsRequest);
}
