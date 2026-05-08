package com.example.appfichaje.data;

import android.content.Context;

import com.example.appfichaje.data.model.ActualizarRadioRequest;
import com.example.appfichaje.data.model.CentroTrabajoResponse;
import com.example.appfichaje.data.model.EmpleadosListResponse;
import com.example.appfichaje.data.model.GenericResponse;
import com.example.appfichaje.data.model.MisRegistrosResponse;
import com.example.appfichaje.data.net.ApiClient;
import com.example.appfichaje.data.net.ApiService;

import retrofit2.Call;

public class AdminRepository {

    private final ApiService apiService;

    public AdminRepository(Context context) {
        this.apiService = ApiClient.getClient(context).create(ApiService.class);
    }

    public Call<EmpleadosListResponse> getEmpleados() {
        return apiService.getEmpleados();
    }

    public Call<MisRegistrosResponse> getRegistrosEmpleado(int idTrabajador, String desde, String hasta) {
        return apiService.getRegistrosEmpleado(idTrabajador, desde, hasta);
    }

    public Call<CentroTrabajoResponse> getCentroTrabajo() {
        return apiService.getCentroTrabajo();
    }

    public Call<GenericResponse> actualizarRadio(int radioMetros) {
        return apiService.actualizarRadio(new ActualizarRadioRequest(radioMetros));
    }
}
