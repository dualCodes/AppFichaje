package com.example.appfichaje.ui.main;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appfichaje.data.FichajeRepository;
import com.example.appfichaje.data.model.EntradaNfcResponse;
import com.example.appfichaje.data.model.EntradaResponse;
import com.example.appfichaje.data.model.EstadoResponse;
import com.example.appfichaje.data.model.HorarioHoyResponse;
import com.example.appfichaje.data.model.SalidaNfcResponse;
import com.example.appfichaje.data.model.SalidaResponse;
import com.example.appfichaje.data.vo.Resource;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FichajeViewModel extends ViewModel {

    private final FichajeRepository fichajeRepository;
    private final MutableLiveData<Resource<EntradaResponse>> entradaResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<SalidaResponse>> salidaResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<EntradaNfcResponse>> entradaNfcResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<SalidaNfcResponse>> salidaNfcResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<EstadoResponse>> estadoResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<HorarioHoyResponse>> horarioHoyResult = new MutableLiveData<>();

    public FichajeViewModel(Context context) {
        this.fichajeRepository = new FichajeRepository(context);
    }

    public LiveData<Resource<EntradaResponse>> getEntradaResult() { return entradaResult; }
    public LiveData<Resource<SalidaResponse>> getSalidaResult() { return salidaResult; }
    public LiveData<Resource<EntradaNfcResponse>> getEntradaNfcResult() { return entradaNfcResult; }
    public LiveData<Resource<SalidaNfcResponse>> getSalidaNfcResult() { return salidaNfcResult; }
    public LiveData<Resource<EstadoResponse>> getEstadoResult() { return estadoResult; }
    public LiveData<Resource<HorarioHoyResponse>> getHorarioHoyResult() { return horarioHoyResult; }

    public void ficharEntrada(double lat, double lon) {
        entradaResult.setValue(Resource.loading(null));
        fichajeRepository.ficharEntrada(lat, lon).enqueue(new Callback<EntradaResponse>() {
            @Override
            public void onResponse(Call<EntradaResponse> call, Response<EntradaResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    entradaResult.setValue(Resource.success(response.body()));
                    refreshEstado();
                } else {
                    String msg = parseErrorCode(response.code(), "entrada");
                    entradaResult.setValue(Resource.error(msg, null));
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
                    refreshEstado();
                } else {
                    String msg = parseErrorCode(response.code(), "salida");
                    salidaResult.setValue(Resource.error(msg, null));
                }
            }

            @Override
            public void onFailure(Call<SalidaResponse> call, Throwable t) {
                salidaResult.setValue(Resource.error("Fallo de red: " + t.getMessage(), null));
            }
        });
    }

    public void ficharEntradaNfc() {
        entradaNfcResult.setValue(Resource.loading(null));
        fichajeRepository.ficharEntradaNfc().enqueue(new Callback<EntradaNfcResponse>() {
            @Override
            public void onResponse(Call<EntradaNfcResponse> call, Response<EntradaNfcResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    entradaNfcResult.setValue(Resource.success(response.body()));
                    refreshEstado();
                } else {
                    String msg = parseErrorCode(response.code(), "entrada");
                    entradaNfcResult.setValue(Resource.error(msg, null));
                }
            }

            @Override
            public void onFailure(Call<EntradaNfcResponse> call, Throwable t) {
                entradaNfcResult.setValue(Resource.error("Fallo de red: " + t.getMessage(), null));
            }
        });
    }

    public void ficharSalidaNfc() {
        salidaNfcResult.setValue(Resource.loading(null));
        fichajeRepository.ficharSalidaNfc().enqueue(new Callback<SalidaNfcResponse>() {
            @Override
            public void onResponse(Call<SalidaNfcResponse> call, Response<SalidaNfcResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    salidaNfcResult.setValue(Resource.success(response.body()));
                    refreshEstado();
                } else {
                    String msg = parseErrorCode(response.code(), "salida");
                    salidaNfcResult.setValue(Resource.error(msg, null));
                }
            }

            @Override
            public void onFailure(Call<SalidaNfcResponse> call, Throwable t) {
                salidaNfcResult.setValue(Resource.error("Fallo de red: " + t.getMessage(), null));
            }
        });
    }

    public void refreshEstado() {
        fichajeRepository.getEstado().enqueue(new Callback<EstadoResponse>() {
            @Override
            public void onResponse(Call<EstadoResponse> call, Response<EstadoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    estadoResult.setValue(Resource.success(response.body()));
                } else {
                    estadoResult.setValue(Resource.error("Error obteniendo estado", null));
                }
            }

            @Override
            public void onFailure(Call<EstadoResponse> call, Throwable t) {
                estadoResult.setValue(Resource.error("Fallo de red: " + t.getMessage(), null));
            }
        });
    }

    public void loadHorarioHoy() {
        fichajeRepository.getHorarioHoy().enqueue(new Callback<HorarioHoyResponse>() {
            @Override
            public void onResponse(Call<HorarioHoyResponse> call, Response<HorarioHoyResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    horarioHoyResult.setValue(Resource.success(response.body()));
                } else {
                    horarioHoyResult.setValue(Resource.error("Sin horario asignado hoy", null));
                }
            }

            @Override
            public void onFailure(Call<HorarioHoyResponse> call, Throwable t) {
                horarioHoyResult.setValue(Resource.error("Fallo de red: " + t.getMessage(), null));
            }
        });
    }

    private String parseErrorCode(int code, String tipo) {
        if (code == 403) return "Estás fuera del radio de la empresa";
        if (code == 409) {
            if ("entrada".equals(tipo)) return "Ya tienes una entrada abierta";
            return "No hay entrada activa para fichar salida";
        }
        if (code == 401) return "Sesión expirada. Por favor, vuelve a iniciar sesión";
        return "Error " + code + " al fichar " + tipo;
    }
}

