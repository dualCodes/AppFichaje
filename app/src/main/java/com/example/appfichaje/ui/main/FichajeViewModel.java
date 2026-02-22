package com.example.appfichaje.ui.main;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appfichaje.data.FichajeRepository;
import com.example.appfichaje.data.model.EntradaResponse;
import com.example.appfichaje.data.model.SalidaResponse;
import com.example.appfichaje.data.vo.Resource;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FichajeViewModel extends ViewModel {

    private final FichajeRepository fichajeRepository;
    private final MutableLiveData<Resource<EntradaResponse>> entradaResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<SalidaResponse>> salidaResult = new MutableLiveData<>();

    public FichajeViewModel(Context context) {
        this.fichajeRepository = new FichajeRepository(context);
    }

    public LiveData<Resource<EntradaResponse>> getEntradaResult() {
        return entradaResult;
    }

    public LiveData<Resource<SalidaResponse>> getSalidaResult() {
        return salidaResult;
    }

    public void ficharEntrada(double lat, double lon) {
        entradaResult.setValue(Resource.loading(null));
        fichajeRepository.ficharEntrada(lat, lon).enqueue(new Callback<EntradaResponse>() {
            @Override
            public void onResponse(Call<EntradaResponse> call, Response<EntradaResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    entradaResult.setValue(Resource.success(response.body()));
                } else {
                    entradaResult.setValue(Resource.error("Error Fichaje Entrada: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<EntradaResponse> call, Throwable t) {
                entradaResult.setValue(Resource.error("Fallo de red: " + t.getMessage(), null));
            }
        });
    }

    public void ficharSalida(double lat, double lon) {
        salidaResult.setValue(Resource.loading(null));
        fichajeRepository.ficharSalida(lat, lon).enqueue(new Callback<SalidaResponse>() {
            @Override
            public void onResponse(Call<SalidaResponse> call, Response<SalidaResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    salidaResult.setValue(Resource.success(response.body()));
                } else {
                    salidaResult.setValue(Resource.error("Error Fichaje Salida: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<SalidaResponse> call, Throwable t) {
                salidaResult.setValue(Resource.error("Fallo de red: " + t.getMessage(), null));
            }
        });
    }
}
