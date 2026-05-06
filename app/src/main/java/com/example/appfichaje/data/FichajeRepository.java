package com.example.appfichaje.data;

import android.content.Context;

import com.example.appfichaje.data.model.EntradaNfcResponse;
import com.example.appfichaje.data.model.EntradaResponse;
import com.example.appfichaje.data.model.EstadoResponse;
import com.example.appfichaje.data.model.FichajeGpsRequest;
import com.example.appfichaje.data.model.HorarioHoyResponse;
import com.example.appfichaje.data.model.MisRegistrosResponse;
import com.example.appfichaje.data.model.ResumenMensualResponse;
import com.example.appfichaje.data.model.SalidaNfcResponse;
import com.example.appfichaje.data.model.SalidaResponse;
import com.example.appfichaje.data.net.ApiClient;
import com.example.appfichaje.data.net.ApiService;

import retrofit2.Call;

public class FichajeRepository {

    private final ApiService apiService;

    public FichajeRepository(Context context) {
        this.apiService = ApiClient.getClient(context).create(ApiService.class);
    }

    public Call<EntradaResponse> ficharEntrada(double lat, double lon) {
        return apiService.ficharEntradaGps(new FichajeGpsRequest(lat, lon));
    }

    public Call<SalidaResponse> ficharSalida(double lat, double lon) {
        return apiService.ficharSalidaGps(new FichajeGpsRequest(lat, lon));
    }

    public Call<EntradaNfcResponse> ficharEntradaNfc() {
        return apiService.ficharEntradaNfc();
    }

    public Call<SalidaNfcResponse> ficharSalidaNfc() {
        return apiService.ficharSalidaNfc();
    }

    public Call<EstadoResponse> getEstado() {
        return apiService.getEstado();
    }

    public Call<HorarioHoyResponse> getHorarioHoy() {
        return apiService.getHorarioHoy();
    }

    public Call<MisRegistrosResponse> getMisRegistros(String desde, String hasta) {
        return apiService.getMisRegistros(desde, hasta);
    }

    public Call<ResumenMensualResponse> getResumenMensual(String mes) {
        return apiService.getResumenMensual(mes);
    }
}

