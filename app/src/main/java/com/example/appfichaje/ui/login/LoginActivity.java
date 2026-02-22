package com.example.appfichaje.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.appfichaje.MainActivity;
import com.example.appfichaje.data.model.LoginResponse;
import com.example.appfichaje.databinding.ActivityLoginBinding;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        LoginViewModelFactory factory = new LoginViewModelFactory(getApplication());
        loginViewModel = new ViewModelProvider(this, factory).get(LoginViewModel.class);

        loginViewModel.getLoginResult().observe(this, loginResponseResource -> {
            switch (loginResponseResource.status) {
                case SUCCESS:
                    LoginResponse data = loginResponseResource.data;
                    if (data != null && data.getAccessToken() != null) {
                        // Save token
                        saveToken(data.getAccessToken());
                        // Navigate to MainActivity
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Respuesta de login inválida", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case ERROR:
                    Toast.makeText(this, loginResponseResource.message, Toast.LENGTH_SHORT).show();
                    break;
                case LOADING:
                    // Show a loading indicator if you have one
                    break;
            }
        });

        binding.loginButton.setOnClickListener(v -> {
            String email = binding.nifEmailEditText.getText().toString();
            String password = binding.passwordEditText.getText().toString();
            loginViewModel.login(email, password);
        });
    }

    private void saveToken(String token) {
        try {
            KeyGenParameterSpec keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC;
            String mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec);

            EncryptedSharedPreferences sharedPreferences = (EncryptedSharedPreferences) EncryptedSharedPreferences.create(
                    "secret_shared_prefs",
                    mainKeyAlias,
                    this,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            sharedPreferences.edit().putString("jwt_token", token).apply();

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }
}
