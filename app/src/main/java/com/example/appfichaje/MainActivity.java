package com.example.appfichaje;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.appfichaje.databinding.ActivityMainBinding;
import com.example.appfichaje.ui.main.FichajeViewModel;
import com.example.appfichaje.ui.main.FichajeViewModelFactory;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private ActivityMainBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private FichajeViewModel fichajeViewModel;

    private String currentAction = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        FichajeViewModelFactory factory = new FichajeViewModelFactory(getApplication());
        fichajeViewModel = new ViewModelProvider(this, factory).get(FichajeViewModel.class);

        setupObservers();
        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.btnFicharEntrada.setOnClickListener(v -> {
            currentAction = "entrada";
            handleFichaje(currentAction);
        });
        binding.btnFicharSalida.setOnClickListener(v -> {
            currentAction = "salida";
            handleFichaje(currentAction);
        });
    }

    private void setupObservers() {
        fichajeViewModel.getEntradaResult().observe(this, resource -> {
            switch (resource.status) {
                case LOADING:
                    setLoading(true);
                    binding.tvEstado.setText("Fichando entrada...");
                    break;
                case SUCCESS:
                    setLoading(false);
                    String msgEntrada = resource.data.getMensaje() + " a las " + resource.data.getHoraEntrada();
                    binding.tvEstado.setText("Estado: " + msgEntrada);
                    Toast.makeText(this, msgEntrada, Toast.LENGTH_LONG).show();
                    break;
                case ERROR:
                    setLoading(false);
                    binding.tvEstado.setText("Estado: " + resource.message);
                    Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show();
                    break;
            }
        });

        fichajeViewModel.getSalidaResult().observe(this, resource -> {
            switch (resource.status) {
                case LOADING:
                    setLoading(true);
                    binding.tvEstado.setText("Fichando salida...");
                    break;
                case SUCCESS:
                    setLoading(false);
                    String msgSalida = resource.data.getMensaje() + " a las " + resource.data.getHoraSalida();
                    binding.tvEstado.setText("Estado: " + msgSalida);
                    Toast.makeText(this, msgSalida, Toast.LENGTH_LONG).show();
                    break;
                case ERROR:
                    setLoading(false);
                    binding.tvEstado.setText("Estado: " + resource.message);
                    Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }

    private void handleFichaje(String tipo) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                if (tipo.equals("entrada")) {
                    fichajeViewModel.ficharEntrada(location.getLatitude(), location.getLongitude());
                } else {
                    fichajeViewModel.ficharSalida(location.getLatitude(), location.getLongitude());
                }
            } else {
                Toast.makeText(MainActivity.this, "No se pudo obtener la ubicación. Activa el GPS y reintenta.", Toast.LENGTH_LONG).show();
            }
        });
    }
    
    private void setLoading(boolean isLoading) {
        binding.btnFicharEntrada.setEnabled(!isLoading);
        binding.btnFicharSalida.setEnabled(!isLoading);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso concedido. Puedes fichar.", Toast.LENGTH_SHORT).show();
                if (!currentAction.isEmpty()) {
                    handleFichaje(currentAction);
                }
            } else {
                Toast.makeText(this, "Permiso denegado. No se puede fichar.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
