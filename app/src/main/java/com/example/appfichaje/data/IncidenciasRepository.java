package com.example.appfichaje.data;

import android.content.Context;

import com.example.appfichaje.data.model.CrearIncidenciaRequest;
import com.example.appfichaje.data.model.Incidencia;
import com.example.appfichaje.data.model.IncidenciasListResponse;
import com.example.appfichaje.data.net.ApiClient;
import com.example.appfichaje.data.net.ApiService;

import retrofit2.Call;

public class IncidenciasRepository {

    private final ApiService apiService;

    public IncidenciasRepository(Context context) {
        this.apiService = ApiClient.getClient(context).create(ApiService.class);
    }

    public Call<Incidencia> crearIncidencia(String descripcion) {
        return apiService.crearIncidencia(new CrearIncidenciaRequest(descripcion));
    }

    public Call<IncidenciasListResponse> getMisIncidencias(String desde, String hasta) {
        return apiService.getMisIncidencias(desde, hasta);
    }
}
