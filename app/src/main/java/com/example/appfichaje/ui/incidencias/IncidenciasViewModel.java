package com.example.appfichaje.ui.incidencias;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appfichaje.data.IncidenciasRepository;
import com.example.appfichaje.data.model.Incidencia;
import com.example.appfichaje.data.model.IncidenciasListResponse;
import com.example.appfichaje.data.vo.Resource;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncidenciasViewModel extends ViewModel {

    private final IncidenciasRepository incidenciasRepository;
    private final MutableLiveData<Resource<IncidenciasListResponse>> incidenciasResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<Incidencia>> crearResult = new MutableLiveData<>();

    public IncidenciasViewModel(Context context) {
        this.incidenciasRepository = new IncidenciasRepository(context);
    }

    public LiveData<Resource<IncidenciasListResponse>> getIncidenciasResult() { return incidenciasResult; }
    public LiveData<Resource<Incidencia>> getCrearResult() { return crearResult; }

    public void loadIncidencias(String desde, String hasta) {
        incidenciasResult.setValue(Resource.loading(null));
        incidenciasRepository.getMisIncidencias(desde, hasta).enqueue(new Callback<IncidenciasListResponse>() {
            @Override
            public void onResponse(Call<IncidenciasListResponse> call, Response<IncidenciasListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    incidenciasResult.setValue(Resource.success(response.body()));
                } else {
                    incidenciasResult.setValue(Resource.error("Error cargando incidencias: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<IncidenciasListResponse> call, Throwable t) {
                incidenciasResult.setValue(Resource.error("Fallo de red: " + t.getMessage(), null));
            }
        });
    }

    public void crearIncidencia(String descripcion) {
        crearResult.setValue(Resource.loading(null));
        incidenciasRepository.crearIncidencia(descripcion).enqueue(new Callback<Incidencia>() {
            @Override
            public void onResponse(Call<Incidencia> call, Response<Incidencia> response) {
                if (response.isSuccessful() && response.body() != null) {
                    crearResult.setValue(Resource.success(response.body()));
                } else {
                    crearResult.setValue(Resource.error("Error al crear incidencia: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<Incidencia> call, Throwable t) {
                crearResult.setValue(Resource.error("Fallo de red: " + t.getMessage(), null));
            }
        });
    }
}
