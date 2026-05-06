package com.example.appfichaje.ui.historial;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appfichaje.data.FichajeRepository;
import com.example.appfichaje.data.model.MisRegistrosResponse;
import com.example.appfichaje.data.model.ResumenMensualResponse;
import com.example.appfichaje.data.vo.Resource;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistorialViewModel extends ViewModel {

    private final FichajeRepository fichajeRepository;
    private final MutableLiveData<Resource<MisRegistrosResponse>> registrosResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<ResumenMensualResponse>> resumenResult = new MutableLiveData<>();

    public HistorialViewModel(Context context) {
        this.fichajeRepository = new FichajeRepository(context);
    }

    public LiveData<Resource<MisRegistrosResponse>> getRegistrosResult() { return registrosResult; }
    public LiveData<Resource<ResumenMensualResponse>> getResumenResult() { return resumenResult; }

    public void loadRegistros(String desde, String hasta) {
        registrosResult.setValue(Resource.loading(null));
        fichajeRepository.getMisRegistros(desde, hasta).enqueue(new Callback<MisRegistrosResponse>() {
            @Override
            public void onResponse(Call<MisRegistrosResponse> call, Response<MisRegistrosResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    registrosResult.setValue(Resource.success(response.body()));
                } else {
                    registrosResult.setValue(Resource.error("Error cargando registros: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<MisRegistrosResponse> call, Throwable t) {
                registrosResult.setValue(Resource.error("Fallo de red: " + t.getMessage(), null));
            }
        });
    }

    public void loadResumenMensual(String mes) {
        resumenResult.setValue(Resource.loading(null));
        fichajeRepository.getResumenMensual(mes).enqueue(new Callback<ResumenMensualResponse>() {
            @Override
            public void onResponse(Call<ResumenMensualResponse> call, Response<ResumenMensualResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    resumenResult.setValue(Resource.success(response.body()));
                } else {
                    resumenResult.setValue(Resource.error("Sin datos de resumen", null));
                }
            }

            @Override
            public void onFailure(Call<ResumenMensualResponse> call, Throwable t) {
                resumenResult.setValue(Resource.error("Fallo de red: " + t.getMessage(), null));
            }
        });
    }
}
