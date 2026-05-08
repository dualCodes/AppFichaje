package com.example.appfichaje.ui.admin;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appfichaje.data.AdminRepository;
import com.example.appfichaje.data.model.CentroTrabajoResponse;
import com.example.appfichaje.data.model.EmpleadosListResponse;
import com.example.appfichaje.data.model.GenericResponse;
import com.example.appfichaje.data.model.MisRegistrosResponse;
import com.example.appfichaje.data.vo.Resource;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminViewModel extends ViewModel {

    private final AdminRepository adminRepository;

    private final MutableLiveData<Resource<EmpleadosListResponse>> empleadosResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<MisRegistrosResponse>> registrosEmpleadoResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<CentroTrabajoResponse>> centroTrabajoResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<GenericResponse>> actualizarRadioResult = new MutableLiveData<>();

    public AdminViewModel(Context context) {
        this.adminRepository = new AdminRepository(context);
    }

    public LiveData<Resource<EmpleadosListResponse>> getEmpleadosResult() { return empleadosResult; }
    public LiveData<Resource<MisRegistrosResponse>> getRegistrosEmpleadoResult() { return registrosEmpleadoResult; }
    public LiveData<Resource<CentroTrabajoResponse>> getCentroTrabajoResult() { return centroTrabajoResult; }
    public LiveData<Resource<GenericResponse>> getActualizarRadioResult() { return actualizarRadioResult; }

    public void loadEmpleados() {
        empleadosResult.setValue(Resource.loading(null));
        adminRepository.getEmpleados().enqueue(new Callback<EmpleadosListResponse>() {
            @Override
            public void onResponse(Call<EmpleadosListResponse> call, Response<EmpleadosListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    empleadosResult.setValue(Resource.success(response.body()));
                } else {
                    empleadosResult.setValue(Resource.error("Error al cargar empleados: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<EmpleadosListResponse> call, Throwable t) {
                empleadosResult.setValue(Resource.error("Fallo de red: " + t.getMessage(), null));
            }
        });
    }

    public void loadRegistrosEmpleado(int empleadoId, String desde, String hasta) {
        registrosEmpleadoResult.setValue(Resource.loading(null));
        adminRepository.getRegistrosEmpleado(empleadoId, desde, hasta).enqueue(new Callback<MisRegistrosResponse>() {
            @Override
            public void onResponse(Call<MisRegistrosResponse> call, Response<MisRegistrosResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    registrosEmpleadoResult.setValue(Resource.success(response.body()));
                } else {
                    registrosEmpleadoResult.setValue(Resource.error("Error: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<MisRegistrosResponse> call, Throwable t) {
                registrosEmpleadoResult.setValue(Resource.error("Fallo de red: " + t.getMessage(), null));
            }
        });
    }

    public void loadCentroTrabajo() {
        centroTrabajoResult.setValue(Resource.loading(null));
        adminRepository.getCentroTrabajo().enqueue(new Callback<CentroTrabajoResponse>() {
            @Override
            public void onResponse(Call<CentroTrabajoResponse> call, Response<CentroTrabajoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    centroTrabajoResult.setValue(Resource.success(response.body()));
                } else {
                    centroTrabajoResult.setValue(Resource.error("Error cargando centro: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<CentroTrabajoResponse> call, Throwable t) {
                centroTrabajoResult.setValue(Resource.error("Fallo de red: " + t.getMessage(), null));
            }
        });
    }

    public void actualizarRadio(int radioMetros) {
        actualizarRadioResult.setValue(Resource.loading(null));
        adminRepository.actualizarRadio(radioMetros).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful()) {
                    GenericResponse body = response.body() != null ? response.body() : new GenericResponse();
                    actualizarRadioResult.setValue(Resource.success(body));
                } else {
                    actualizarRadioResult.setValue(Resource.error("Error al actualizar radio: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                actualizarRadioResult.setValue(Resource.error("Fallo de red: " + t.getMessage(), null));
            }
        });
    }
}
