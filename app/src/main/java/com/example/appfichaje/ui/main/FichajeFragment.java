package com.example.appfichaje.ui.main;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.Settings;
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

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

import com.example.appfichaje.R;
import com.example.appfichaje.data.model.EstadoResponse;
import com.example.appfichaje.ui.login.LoginActivity;
import com.example.appfichaje.utils.NotificationScheduler;
import com.example.appfichaje.utils.TimeUtils;
import com.example.appfichaje.utils.TokenManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.card.MaterialCardView;

public class FichajeFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int NOTIF_PERMISSION_REQUEST_CODE = 2;
    private static final int LOCATION_SETTINGS_REQUEST_CODE = 3;

    private FichajeViewModel fichajeViewModel;
    private FusedLocationProviderClient fusedLocationClient;
    private NfcAdapter nfcAdapter;

    private final BroadcastReceiver nfcStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (NfcAdapter.ACTION_ADAPTER_STATE_CHANGED.equals(intent.getAction())) {
                checkNfcAvailability();
            }
        }
    };

    private String currentGpsAction = "";
    private String pendingNfcAction = null; // "entrada" o "salida"
    private boolean dentroActual = false;
    private AlertDialog nfcWaitingDialog;

    private Button btnFichajeGps, btnFichajeNfc, btnLogout;
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

        btnFichajeGps = view.findViewById(R.id.btn_fichaje_gps);
        btnFichajeNfc = view.findViewById(R.id.btn_fichaje_nfc);
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
            nfcAdapter = nfcManager.getDefaultAdapter();
            if (nfcAdapter == null) {
                tvNfcStatus.setText("Este dispositivo no tiene NFC");
                btnFichajeNfc.setEnabled(false);
            } else if (!nfcAdapter.isEnabled()) {
                tvNfcStatus.setText("NFC desactivado. Pulsa el botón para activarlo.");
                btnFichajeNfc.setEnabled(true); // habilitado para mostrar diálogo
            } else {
                tvNfcStatus.setText("NFC activo. Acerca el teléfono al lector NFC o usa el botón.");
                btnFichajeNfc.setEnabled(true);
            }
        }
    }

    private void setupClickListeners() {
        btnFichajeGps.setOnClickListener(v -> {
            currentGpsAction = dentroActual ? "salida" : "entrada";
            handleFichajeGps();
        });
        btnFichajeNfc.setOnClickListener(v -> {
            if (nfcAdapter != null && !nfcAdapter.isEnabled()) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("NFC desactivado")
                        .setMessage("Para fichar por NFC necesitas activarlo. ¿Abrir ajustes de NFC?")
                        .setPositiveButton("Abrir ajustes", (d, w) ->
                                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS)))
                        .setNegativeButton("Cancelar", null)
                        .show();
                return;
            }
            showNfcWaitingDialog(dentroActual ? "salida" : "entrada");
        });
        btnLogout.setOnClickListener(v -> logout());
    }

    private void setupObservers() {
        fichajeViewModel.getEstadoResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource.data != null) {
                EstadoResponse estado = resource.data;
                updateFichajeButtons(estado.isDentro());
                if (estado.isDentro()) {
                    tvEstadoActual.setText("Dentro");
                    tvEstadoActual.setTextColor(getResources().getColor(android.R.color.holo_green_dark, null));
                    if (estado.getHoraEntrada() != null) {
                        tvHoraEntradaEstado.setText("Entrada: " + TimeUtils.utcIsoToLocalTime(estado.getHoraEntrada()));
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
                            "Entrada registrada a las " + TimeUtils.utcIsoToLocalTime(resource.data.getHoraEntrada()),
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
                            "Entrada NFC registrada a las " + TimeUtils.utcIsoToLocalTime(resource.data.getHoraEntrada()),
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

    private void updateFichajeButtons(boolean dentro) {
        dentroActual = dentro;
        if (dentro) {
            btnFichajeGps.setText("Fichar Salida GPS");
            btnFichajeGps.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#C62828")));
            btnFichajeNfc.setText("Fichar Salida NFC");
        } else {
            btnFichajeGps.setText("Fichar Entrada GPS");
            btnFichajeGps.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#2E7D32")));
            btnFichajeNfc.setText("Fichar Entrada NFC");
        }
    }

    private void showNfcWaitingDialog(String action) {
        pendingNfcAction = action;
        String mensaje = "entrada".equals(action)
                ? "Acerca el móvil al lector NFC para fichar entrada"
                : "Acerca el móvil al lector NFC para fichar salida";

        nfcWaitingDialog = new AlertDialog.Builder(requireContext())
                .setTitle("Fichaje NFC")
                .setMessage(mensaje)
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    pendingNfcAction = null;
                    dialog.dismiss();
                })
                .setCancelable(true)
                .setOnCancelListener(dialog -> pendingNfcAction = null)
                .create();

        nfcWaitingDialog.show();
    }

    public void onNfcTagDetected() {
        // Si no hay acción pendiente (no se pulsó ningún botón), ignorar
        if (pendingNfcAction == null) {
            Toast.makeText(requireContext(),
                    "Usa los botones para iniciar el fichaje NFC", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cerrar el diálogo de espera
        if (nfcWaitingDialog != null && nfcWaitingDialog.isShowing()) {
            nfcWaitingDialog.dismiss();
            nfcWaitingDialog = null;
        }

        // Ejecutar la acción según el botón que se pulsó
        if ("entrada".equals(pendingNfcAction)) {
            fichajeViewModel.ficharEntradaNfc();
        } else {
            fichajeViewModel.ficharSalidaNfc();
        }
        pendingNfcAction = null;
    }

    private void handleFichajeGps() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setMaxUpdates(1)
                .setWaitForAccurateLocation(false)
                .build();

        LocationSettingsRequest settingsRequest = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(requireContext());
        settingsClient.checkLocationSettings(settingsRequest)
                .addOnSuccessListener(response -> obtenerUbicacionYFichar(locationRequest))
                .addOnFailureListener(e -> {
                    if (e instanceof ResolvableApiException) {
                        // El GPS está apagado: mostrar diálogo nativo para activarlo
                        try {
                            startIntentSenderForResult(
                                    ((ResolvableApiException) e).getResolution().getIntentSender(),
                                    LOCATION_SETTINGS_REQUEST_CODE,
                                    null, 0, 0, 0, null);
                        } catch (IntentSender.SendIntentException sendEx) {
                            Toast.makeText(requireContext(),
                                    "No se pudo abrir la configuración de GPS",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(requireContext(),
                                "GPS no disponible en este dispositivo",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void obtenerUbicacionYFichar(LocationRequest locationRequest) {
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                ficharConUbicacion(location.getLatitude(), location.getLongitude());
            } else {
                fusedLocationClient.requestLocationUpdates(locationRequest,
                        new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult result) {
                                fusedLocationClient.removeLocationUpdates(this);
                                if (!result.getLocations().isEmpty()) {
                                    android.location.Location loc = result.getLocations().get(0);
                                    ficharConUbicacion(loc.getLatitude(), loc.getLongitude());
                                } else {
                                    Toast.makeText(requireContext(),
                                            "No se pudo obtener la ubicación. Reintenta.",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        },
                        android.os.Looper.getMainLooper());
            }
        });
    }

    private void ficharConUbicacion(double lat, double lon) {
        if ("entrada".equals(currentGpsAction)) {
            fichajeViewModel.ficharEntrada(lat, lon);
        } else {
            fichajeViewModel.ficharSalida(lat, lon);
        }
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
        btnFichajeGps.setEnabled(enabled);
        btnFichajeNfc.setEnabled(enabled);
    }

    private void logout() {
        NotificationScheduler.cancelAll(requireContext());
        TokenManager.clearToken(requireContext());
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Registrar receptor de cambios de estado NFC
        IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED);
        requireContext().registerReceiver(nfcStateReceiver, filter);
        // Re-comprobar por si el usuario activó NFC desde ajustes
        checkNfcAvailability();
    }

    @Override
    public void onPause() {
        super.onPause();
        requireContext().unregisterReceiver(nfcStateReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_SETTINGS_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // El usuario activó el GPS: reintentar
                handleFichajeGps();
            } else {
                Toast.makeText(requireContext(),
                        "El GPS debe estar activo para fichar por ubicación",
                        Toast.LENGTH_LONG).show();
            }
        }
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
