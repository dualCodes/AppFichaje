package com.example.appfichaje.ui.login;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.appfichaje.MainActivity;
import com.example.appfichaje.data.model.LoginResponse;
import com.example.appfichaje.databinding.ActivityLoginBinding;
import com.example.appfichaje.utils.TokenManager;

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
                case LOADING:
                    binding.progressLogin.setVisibility(View.VISIBLE);
                    binding.loginButton.setEnabled(false);
                    break;
                case SUCCESS:
                    binding.progressLogin.setVisibility(View.GONE);
                    binding.loginButton.setEnabled(true);
                    LoginResponse data = loginResponseResource.data;
                    if (data != null && data.getAccessToken() != null) {
                        // Save token
                        saveToken(data.getAccessToken());
                        // Save user info
                        if (data.getUsuario() != null) {
                            TokenManager.saveUserInfo(
                                    this,
                                    data.getUsuario().getId(),
                                    data.getUsuario().getNombre(),
                                    data.getUsuario().getRol()
                            );
                        }
                        // Navigate to MainActivity
                        startActivity(new Intent(this, com.example.appfichaje.MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Respuesta de login inválida", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case ERROR:
                    binding.progressLogin.setVisibility(View.GONE);
                    binding.loginButton.setEnabled(true);
                    Toast.makeText(this, loginResponseResource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        loginViewModel.getCambioPasswordResult().observe(this, resource -> {
            switch (resource.status) {
                case SUCCESS:
                    Toast.makeText(this,
                            "Se ha enviado un correo con el enlace para cambiar la contraseña",
                            Toast.LENGTH_LONG).show();
                    break;
                case ERROR:
                    Toast.makeText(this, "Error: " + resource.message, Toast.LENGTH_LONG).show();
                    break;
                case LOADING:
                    break;
            }
        });

        binding.loginButton.setOnClickListener(v -> {
            String email = binding.nifEmailEditText.getText().toString();
            String password = binding.passwordEditText.getText().toString();
            loginViewModel.login(email, password);
        });

        binding.btnSolicitarCambioPassword.setOnClickListener(v -> showSolicitarCambioPasswordDialog());
    }

    private void showSolicitarCambioPasswordDialog() {
        EditText etEmail = new EditText(this);
        etEmail.setHint("Introduce tu email");
        etEmail.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        new AlertDialog.Builder(this)
                .setTitle("Solicitar cambio de contraseña")
                .setMessage("Recibirás un correo con el enlace para establecer una nueva contraseña.")
                .setView(etEmail)
                .setPositiveButton("Enviar", (d, w) -> {
                    String email = etEmail.getText().toString().trim();
                    if (!email.isEmpty()) {
                        loginViewModel.solicitarCambioPassword(email);
                    } else {
                        Toast.makeText(this, "Introduce un email válido", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void saveToken(String token) {
        TokenManager.saveToken(this, token);
    }
}
