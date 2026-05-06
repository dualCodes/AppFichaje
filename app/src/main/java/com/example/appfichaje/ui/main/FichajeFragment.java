package com.example.appfichaje.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.appfichaje.R;
import com.example.appfichaje.data.model.EstadoResponse;
import com.example.appfichaje.ui.login.LoginActivity;
import com.example.appfichaje.utils.NotificationScheduler;
import com.example.appfichaje.utils.TokenManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.card.MaterialCardView;

public class FichajeFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int NOTIF_PERMISSION_REQUEST_CODE = 2;

    private FichajeViewModel fichajeViewModel;
    private FusedLocationProviderClient fusedLocationClient;

    private String currentGpsAction = "";

    private Button btnEntradaGps, btnSalidaGps, btnEntradaNfc, btnSalidaNfc, btnLogout;
    private TextView tvBienvenida, tvEstadoActual, tvHoraEntradaEstado, tvNfcStatus;
    private MaterialCardView cardNfc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fichaje, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnEntradaGps = view.findViewById(R.id.btn_entrada_gps);
        btnSalidaGps = view.findViewById(R.id.btn_salida_gps);
        btnEntradaNfc = view.findViewById(R.id.btn_entrada_nfc);
        btnSalidaNfc = view.findViewById(R.id.btn_salida_nfc);
        btnLogout = view.findViewById(R.id.btn_logout);
        tvBienvenida = view.findViewById(R.id.tv_bienvenida);
        tvEstadoActual = view.findViewById(R.id.tv_estado_actual);
        tvHoraEntradaEstado = view.findViewById(R.id.tv_hora_entrada_estado);
        tvNfcStatus = view.findViewById(R.id.tv_nfc_status);
        cardNfc = view.findViewById(R.id.card_nfc);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        FichajeViewModelFactory factory = new FichajeViewModelFactory(requireContext());
        fichajeViewModel = new ViewModelProvider(requireActivity(), factory).get(FichajeViewModel.class);

        String nombre = TokenManager.getUserName(requireContext());
        if (nombre != null && !nombre.isEmpty()) {
            tvBienvenida.setText("Hola, " + nombre);
        }

        checkNfcAvailability();
        setupClickListeners();
        setupObservers();

        fichajeViewModel.refreshEstado();
        fichajeViewModel.loadHorarioHoy();

        requestNotificationPermission();
    }

    private void checkNfcAvailability() {
        android.nfc.NfcManager nfcManager =
                (android.nfc.NfcManager) requireContext().getSystemService(android.content.Context.NFC_SERVICE);
        if (nfcManager != null) {
            android.nfc.NfcAdapter nfcAdapter = nfcManager.getDefaultAdapter();
            if (nfcAdapter == null) {
                tvNfcStatus.setText("Este dispositivo no tiene NFC");
                btnEntradaNfc.setEnabled(false);
                btnSalidaNfc.setEnabled(false);
            } else if (!nfcAdapter.isEnabled()) {
                tvNfcStatus.setText("NFC desactivado. Actívalo en Ajustes.");
                btnEntradaNfc.setEnabled(false);
                btnSalidaNfc.setEnabled(false);
            } else {
                tvNfcStatus.setText("NFC activo. Acerca el teléfono al lector NFC o usa los botones.");
            }
        }
    }

    private void setupClickListeners() {
        btnEntradaGps.setOnClickListener(v -> {
            currentGpsAction = "entrada";
            handleFichajeGps();
        });
        btnSalidaGps.setOnClickListener(v -> {
            currentGpsAction = "salida";
            handleFichajeGps();
        });
        btnEntradaNfc.setOnClickListener(v -> fichajeViewModel.ficharEntradaNfc());
        btnSalidaNfc.setOnClickListener(v -> fichajeViewModel.ficharSalidaNfc());
        btnLogout.setOnClickListener(v -> logout());
    }

    private void setupObservers() {
        fichajeViewModel.getEstadoResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource.data != null) {
                EstadoResponse estado = resource.data;
                if (estado.isDentro()) {
                    tvEstadoActual.setText("Dentro");
                    tvEstadoActual.setTextColor(getResources().getColor(android.R.color.holo_green_dark, null));
                    if (estado.getHoraEntrada() != null) {
                        tvHoraEntradaEstado.setText("Entrada: " + estado.getHoraEntrada());
                        tvHoraEntradaEstado.setVisibility(View.VISIBLE);
                    }
                } else {
                    tvEstadoActual.setText("Fuera");
                    tvEstadoActual.setTextColor(getResources().getColor(android.R.color.holo_red_dark, null));
                    tvHoraEntradaEstado.setVisibility(View.GONE);
                }
            }
        });

        fichajeViewModel.getEntradaResult().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    setButtonsEnabled(false);
                    break;
                case SUCCESS:
                    setButtonsEnabled(true);
                    Toast.makeText(requireContext(),
                            "Entrada registrada a las " + resource.data.getHoraEntrada(),
                            Toast.LENGTH_LONG).show();
                    break;
                case ERROR:
                    setButtonsEnabled(true);
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG).show();
                    break;
            }
        });

        fichajeViewModel.getSalidaResult().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    setButtonsEnabled(false);
                    break;
                case SUCCESS:
                    setButtonsEnabled(true);
                    Toast.makeText(requireContext(),
                            "Salida registrada. Duración: " + resource.data.getDuracionMinutos() + " min",
                            Toast.LENGTH_LONG).show();
                    break;
                case ERROR:
                    setButtonsEnabled(true);
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG).show();
                    break;
            }
        });

        fichajeViewModel.getEntradaNfcResult().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    setButtonsEnabled(false);
                    break;
                case SUCCESS:
                    setButtonsEnabled(true);
                    Toast.makeText(requireContext(),
                            "Entrada NFC registrada a las " + resource.data.getHoraEntrada(),
                            Toast.LENGTH_LONG).show();
                    break;
                case ERROR:
                    setButtonsEnabled(true);
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG).show();
                    break;
            }
        });

        fichajeViewModel.getSalidaNfcResult().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    setButtonsEnabled(false);
                    break;
                case SUCCESS:
                    setButtonsEnabled(true);
                    Toast.makeText(requireContext(),
                            "Salida NFC registrada. Duración: " + resource.data.getDuracionMinutos() + " min",
                            Toast.LENGTH_LONG).show();
                    break;
                case ERROR:
                    setButtonsEnabled(true);
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG).show();
                    break;
            }
        });

        fichajeViewModel.getHorarioHoyResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == com.example.appfichaje.data.vo.Resource.Status.SUCCESS
                    && resource.data != null
                    && resource.data.isTieneHorario()
                    && resource.data.getFranjas() != null) {
                NotificationScheduler.createNotificationChannel(requireContext());
                NotificationScheduler.scheduleReminders(requireContext(), resource.data.getFranjas());
            }
        });
    }

    public void onNfcTagDetected() {
        // Called from MainActivity when NFC tag is detected
        // Auto-fichaje based on current estado
        com.example.appfichaje.data.vo.Resource<EstadoResponse> estadoResource =
                fichajeViewModel.getEstadoResult().getValue();
        if (estadoResource != null && estadoResource.data != null) {
            if (estadoResource.data.isDentro()) {
                fichajeViewModel.ficharSalidaNfc();
                Toast.makeText(requireContext(), "NFC detectado: fichando salida...", Toast.LENGTH_SHORT).show();
            } else {
                fichajeViewModel.ficharEntradaNfc();
                Toast.makeText(requireContext(), "NFC detectado: fichando entrada...", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Default to entrada if estado unknown
            fichajeViewModel.ficharEntradaNfc();
            Toast.makeText(requireContext(), "NFC detectado: fichando entrada...", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleFichajeGps() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                if ("entrada".equals(currentGpsAction)) {
                    fichajeViewModel.ficharEntrada(location.getLatitude(), location.getLongitude());
                } else {
                    fichajeViewModel.ficharSalida(location.getLatitude(), location.getLongitude());
                }
            } else {
                Toast.makeText(requireContext(),
                        "No se pudo obtener la ubicación. Activa el GPS y reintenta.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void requestNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIF_PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void setButtonsEnabled(boolean enabled) {
        btnEntradaGps.setEnabled(enabled);
        btnSalidaGps.setEnabled(enabled);
        btnEntradaNfc.setEnabled(enabled);
        btnSalidaNfc.setEnabled(enabled);
    }

    private void logout() {
        NotificationScheduler.cancelAll(requireContext());
        TokenManager.clearToken(requireContext());
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!currentGpsAction.isEmpty()) {
                    handleFichajeGps();
                }
            } else {
                Toast.makeText(requireContext(),
                        "Permiso de ubicación denegado", Toast.LENGTH_LONG).show();
            }
        }
    }
}
