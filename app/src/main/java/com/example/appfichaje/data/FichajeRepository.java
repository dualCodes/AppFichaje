package com.example.appfichaje.data;

import android.content.Context;

import com.example.appfichaje.data.model.EntradaResponse;
import com.example.appfichaje.data.model.FichajeGpsRequest;
import com.example.appfichaje.data.model.SalidaResponse;
import com.example.appfichaje.data.net.ApiClient;
import com.example.appfichaje.data.net.ApiService;

import retrofit2.Call;

public class FichajeRepository {

    private ApiService apiService;

    public FichajeRepository(Context context) {
        this.apiService = ApiClient.getClient(context).create(ApiService.class);
    }

    public Call<EntradaResponse> ficharEntrada(double lat, double lon) {
        return apiService.ficharEntradaGps(new FichajeGpsRequest(lat, lon));
    }

    public Call<SalidaResponse> ficharSalida(double lat, double lon) {
        return apiService.ficharSalidaGps(new FichajeGpsRequest(lat, lon));
    }

}
