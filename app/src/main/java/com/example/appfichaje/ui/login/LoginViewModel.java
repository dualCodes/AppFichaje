package com.example.appfichaje.ui.login;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appfichaje.data.LoginRepository;
import com.example.appfichaje.data.model.GenericResponse;
import com.example.appfichaje.data.model.LoginResponse;
import com.example.appfichaje.data.vo.Resource;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {

    private final LoginRepository loginRepository;
    private final MutableLiveData<Resource<LoginResponse>> loginResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<GenericResponse>> cambioPasswordResult = new MutableLiveData<>();

    public LoginViewModel(Context context) {
        this.loginRepository = new LoginRepository(context);
    }

    public LiveData<Resource<LoginResponse>> getLoginResult() {
        return loginResult;
    }

    public LiveData<Resource<GenericResponse>> getCambioPasswordResult() {
        return cambioPasswordResult;
    }

    public void login(String email, String password) {
        loginResult.setValue(Resource.loading(null));
        loginRepository.login(email, password).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    loginResult.setValue(Resource.success(response.body()));
                } else {
                    String errorMessage = "Error de autenticación. Código: " + response.code();
                    loginResult.setValue(Resource.error(errorMessage, null));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                loginResult.setValue(Resource.error("Fallo de red: " + t.getMessage(), null));
            }
        });
    }

    public void solicitarCambioPassword(String email) {
        cambioPasswordResult.setValue(Resource.loading(null));
        loginRepository.solicitarCambioPassword(email).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful()) {
                    cambioPasswordResult.setValue(Resource.success(
                            response.body() != null ? response.body() : new GenericResponse()));
                } else {
                    cambioPasswordResult.setValue(Resource.error("Error: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                cambioPasswordResult.setValue(Resource.error("Fallo de red: " + t.getMessage(), null));
            }
        });
    }
}
