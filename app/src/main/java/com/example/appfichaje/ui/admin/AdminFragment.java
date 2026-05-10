package com.example.appfichaje.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfichaje.R;
import com.example.appfichaje.data.model.CentroTrabajoResponse;
import com.example.appfichaje.data.model.TrabajadorLista;
import com.example.appfichaje.data.vo.Resource;
import com.example.appfichaje.ui.historial.RegistroAdapter;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.events.MapEventsReceiver;
import com.google.android.material.button.MaterialButtonToggleGroup;
import android.widget.SeekBar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AdminFragment extends Fragment {

    private AdminViewModel adminViewModel;
    private EmpleadoAdapter empleadoAdapter;

    private MaterialButtonToggleGroup toggleGroup;
    private View sectionEmpleados, sectionCentro;
    private ProgressBar progressAdmin, progressCentro;
    private RecyclerView rvEmpleados;
    private TextView tvEmpleadosEmpty, tvRadioActual, tvCentroNombre, tvCentroCoords;
    private Button btnEditarRadio;

    private MapView mapView;
    private CentroTrabajoResponse centroActual;

    // Referencias al diálogo activo de registros
    private RegistroAdapter dialogRegistroAdapter;
    private ProgressBar dialogProgressBar;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toggleGroup = view.findViewById(R.id.toggle_admin_section);
        sectionEmpleados = view.findViewById(R.id.section_empleados);
        sectionCentro = view.findViewById(R.id.section_centro);
        progressAdmin = view.findViewById(R.id.progress_admin);
        progressCentro = view.findViewById(R.id.progress_centro);
        rvEmpleados = view.findViewById(R.id.rv_empleados);
        tvEmpleadosEmpty = view.findViewById(R.id.tv_empty_empleados);
        tvRadioActual = view.findViewById(R.id.tv_radio_actual);
        tvCentroNombre = view.findViewById(R.id.tv_centro_nombre);
        tvCentroCoords = view.findViewById(R.id.tv_centro_coords);
        btnEditarRadio = view.findViewById(R.id.btn_editar_radio);

        empleadoAdapter = new EmpleadoAdapter(this::showRegistrosDialog);
        rvEmpleados.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvEmpleados.setAdapter(empleadoAdapter);

        AdminViewModelFactory factory = new AdminViewModelFactory(requireContext());
        adminViewModel = new ViewModelProvider(this, factory).get(AdminViewModel.class);

        setupObservers();
        setupToggle();
        setupMap(view);

        btnEditarRadio.setOnClickListener(v -> showEditarRadioDialog());

        adminViewModel.loadEmpleados();
    }

    private void setupToggle() {
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            if (checkedId == R.id.btn_tab_empleados) {
                sectionEmpleados.setVisibility(View.VISIBLE);
                sectionCentro.setVisibility(View.GONE);
                adminViewModel.loadEmpleados();
            } else if (checkedId == R.id.btn_tab_centro) {
                sectionEmpleados.setVisibility(View.GONE);
                sectionCentro.setVisibility(View.VISIBLE);
                adminViewModel.loadCentroTrabajo();
            }
        });
    }

    private void setupObservers() {
        adminViewModel.getEmpleadosResult().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    progressAdmin.setVisibility(View.VISIBLE);
                    tvEmpleadosEmpty.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    progressAdmin.setVisibility(View.GONE);
                    if (resource.data != null && resource.data.getTrabajadores() != null
                            && !resource.data.getTrabajadores().isEmpty()) {
                        empleadoAdapter.setEmpleados(resource.data.getTrabajadores());
                        tvEmpleadosEmpty.setVisibility(View.GONE);
                    } else {
                        tvEmpleadosEmpty.setVisibility(View.VISIBLE);
                        tvEmpleadosEmpty.setText("No se encontraron empleados");
                    }
                    break;
                case ERROR:
                    progressAdmin.setVisibility(View.GONE);
                    tvEmpleadosEmpty.setVisibility(View.VISIBLE);
                    tvEmpleadosEmpty.setText("Error: " + resource.message);
                    break;
            }
        });

        adminViewModel.getRegistrosEmpleadoResult().observe(getViewLifecycleOwner(), resource -> {
            if (dialogProgressBar != null) {
                dialogProgressBar.setVisibility(
                        resource.status == Resource.Status.LOADING ? View.VISIBLE : View.GONE);
            }
        if (resource.status == Resource.Status.SUCCESS
                    && dialogRegistroAdapter != null
                    && resource.data != null
                    && resource.data.getRegistros() != null) {
                dialogRegistroAdapter.setRegistros(resource.data.getRegistros());
            } else if (resource.status == Resource.Status.ERROR) {
                Toast.makeText(requireContext(), "Error: " + resource.message, Toast.LENGTH_SHORT).show();
            }
        });

        adminViewModel.getCentroTrabajoResult().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    progressCentro.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    progressCentro.setVisibility(View.GONE);
                    if (resource.data != null) {
                        centroActual = resource.data;
                        updateCentroUI(centroActual);
                    }
                    break;
                case ERROR:
                    progressCentro.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Error: " + resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        adminViewModel.getActualizarRadioResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS) {
                Toast.makeText(requireContext(), "Radio actualizado correctamente", Toast.LENGTH_SHORT).show();
                adminViewModel.loadCentroTrabajo();
            } else if (resource.status == Resource.Status.ERROR) {
                Toast.makeText(requireContext(), "Error: " + resource.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCentroUI(CentroTrabajoResponse centro) {
        if (centro.getNombreComercial() != null) {
            tvCentroNombre.setText(centro.getNombreComercial());
        }
        if (centro.getLat() != null && centro.getLon() != null) {
            tvCentroCoords.setText(String.format(Locale.getDefault(),
                    "Lat: %.6f  Lon: %.6f", centro.getLat(), centro.getLon()));
        } else {
            tvCentroCoords.setText("Coordenadas no configuradas");
        }
        tvRadioActual.setText("Radio: " + (centro.getRadio() != null ? centro.getRadio() : "--") + " m");
        updateMap(centro);
    }

    private void updateMap(CentroTrabajoResponse centro) {
        if (mapView == null || centro.getLat() == null || centro.getLon() == null) return;
        mapView.getOverlays().clear();
        GeoPoint geoPoint = new GeoPoint(centro.getLat(), centro.getLon());

        Marker marker = new Marker(mapView);
        marker.setPosition(geoPoint);
        marker.setTitle("Centro de trabajo");
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(marker);

        if (centro.getRadio() != null && centro.getRadio() > 0) {
            Polygon circle = new Polygon();
            circle.setPoints(Polygon.pointsAsCircle(geoPoint, centro.getRadio()));
            circle.setStrokeColor(0xFF1976D2);
            circle.setFillColor(0x221976D2);
            circle.setStrokeWidth(2f);
            mapView.getOverlays().add(circle);
        }

        mapView.getController().setZoom(15.0);
        mapView.getController().setCenter(geoPoint);
        mapView.invalidate();
    }

    private void setupMap(View view) {
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
        mapView = view.findViewById(R.id.map_container);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15.0);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mapView != null) mapView.onDetach();
        mapView = null;
    }

    private void showRegistrosDialog(TrabajadorLista empleado) {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_registros_empleado, null);

        RecyclerView rv = dialogView.findViewById(R.id.rv_registros_empleado);
        ProgressBar pb = dialogView.findViewById(R.id.progress_registros_empleado);
        Button btnBuscar = dialogView.findViewById(R.id.btn_buscar_registros);
        EditText etDesde = dialogView.findViewById(R.id.et_desde_registros);
        EditText etHasta = dialogView.findViewById(R.id.et_hasta_registros);
        TextView tvNombreEmpleadoDialog = dialogView.findViewById(R.id.tv_nombre_empleado_dialog);

        String nombreCompleto = empleado.getNombre() != null ? empleado.getNombre() : "";
        if (empleado.getApellidos() != null && !empleado.getApellidos().isEmpty()) {
            nombreCompleto += " " + empleado.getApellidos();
        }
        tvNombreEmpleadoDialog.setText(nombreCompleto);

        Calendar end = Calendar.getInstance();
        Calendar start = (Calendar) end.clone();
        start.set(Calendar.DAY_OF_MONTH, 1);
        etDesde.setText(dateFormat.format(start.getTime()));
        etHasta.setText(dateFormat.format(end.getTime()));

        RegistroAdapter adapter = new RegistroAdapter();
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        dialogRegistroAdapter = adapter;
        dialogProgressBar = pb;

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setTitle("Registros de empleado")
                .setNegativeButton("Cerrar", (d, w) -> {
                    dialogRegistroAdapter = null;
                    dialogProgressBar = null;
                })
                .create();

        btnBuscar.setOnClickListener(v -> {
            String desde = etDesde.getText().toString().trim();
            String hasta = etHasta.getText().toString().trim();
            adminViewModel.loadRegistrosEmpleado(empleado.getIdTrabajador(), desde, hasta);
        });

        adminViewModel.loadRegistrosEmpleado(empleado.getIdTrabajador(),
                etDesde.getText().toString(), etHasta.getText().toString());

        dialog.show();
    }

    private void showEditarRadioDialog() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_editar_centro, null);

        org.osmdroid.views.MapView dialogMap = dialogView.findViewById(R.id.map_dialog);
        TextView tvCoordsDialog = dialogView.findViewById(R.id.tv_coords_dialog);
        SeekBar seekbarRadio = dialogView.findViewById(R.id.seekbar_radio);
        EditText etRadio = dialogView.findViewById(R.id.et_radio_dialog);

        // --- Mapa ---
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
        dialogMap.setTileSource(TileSourceFactory.MAPNIK);
        dialogMap.setMultiTouchControls(true);

        double initLat = centroActual != null && centroActual.getLat() != null ? centroActual.getLat() : 40.4168;
        double initLon = centroActual != null && centroActual.getLon() != null ? centroActual.getLon() : -3.7038;
        final double[] selectedLatLon = {initLat, initLon};

        // Slider exponencial (1-50000 m)
        final int MAX_P = 1000;
        seekbarRadio.setMax(MAX_P);
        int initRadio = centroActual != null && centroActual.getRadio() != null
                ? Math.max(1, Math.min(50000, centroActual.getRadio())) : 100;
        final int[] currentRadio = {initRadio};
        etRadio.setText(String.valueOf(initRadio));
        seekbarRadio.setProgress(radioToProgress(initRadio, MAX_P));

        // Helper: redibuja marcador + círculo
        final Marker[] selMarker = {new Marker(dialogMap)};
        selMarker[0].setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        final Runnable[] redrawMap = {null};
        redrawMap[0] = () -> {
            dialogMap.getOverlays().removeIf(o -> o instanceof Marker || o instanceof Polygon);
            GeoPoint p = new GeoPoint(selectedLatLon[0], selectedLatLon[1]);
            selMarker[0].setPosition(p);
            dialogMap.getOverlays().add(selMarker[0]);
            if (currentRadio[0] > 0) {
                Polygon circle = new Polygon();
                circle.setPoints(Polygon.pointsAsCircle(p, currentRadio[0]));
                circle.setStrokeColor(0xFF1976D2);
                circle.setFillColor(0x221976D2);
                circle.setStrokeWidth(2f);
                dialogMap.getOverlays().add(circle);
            }
            dialogMap.invalidate();
        };

        dialogMap.getController().setZoom(15.0);
        dialogMap.getController().setCenter(new GeoPoint(initLat, initLon));
        tvCoordsDialog.setText(String.format(Locale.getDefault(), "Lat: %.6f  Lon: %.6f", initLat, initLon));

        MapEventsOverlay tapOverlay = new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                selectedLatLon[0] = p.getLatitude();
                selectedLatLon[1] = p.getLongitude();
                tvCoordsDialog.setText(String.format(Locale.getDefault(),
                        "Lat: %.6f  Lon: %.6f", p.getLatitude(), p.getLongitude()));
                redrawMap[0].run();
                return true;
            }
            @Override public boolean longPressHelper(GeoPoint p) { return false; }
        });
        dialogMap.getOverlays().add(0, tapOverlay);
        redrawMap[0].run();

        final boolean[] updating = {false};

        seekbarRadio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                if (!fromUser || updating[0]) return;
                updating[0] = true;
                int v = progressToRadio(progress, MAX_P);
                currentRadio[0] = v;
                String val = String.valueOf(v);
                etRadio.setText(val);
                etRadio.setSelection(val.length());
                redrawMap[0].run();
                updating[0] = false;
            }
            @Override public void onStartTrackingTouch(SeekBar sb) {}
            @Override public void onStopTrackingTouch(SeekBar sb) {}
        });

        etRadio.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(android.text.Editable s) {
                if (updating[0]) return;
                try {
                    int v = Math.max(1, Math.min(50000, Integer.parseInt(s.toString().trim())));
                    currentRadio[0] = v;
                    updating[0] = true;
                    seekbarRadio.setProgress(radioToProgress(v, MAX_P));
                    updating[0] = false;
                    redrawMap[0].run();
                } catch (NumberFormatException ignored) {}
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Modificar centro de trabajo")
                .setView(dialogView)
                .setPositiveButton("Guardar", (d, w) -> {
                    int radio;
                    try {
                        radio = Math.max(1, Math.min(50000,
                                Integer.parseInt(etRadio.getText().toString().trim())));
                    } catch (NumberFormatException e) {
                        radio = progressToRadio(seekbarRadio.getProgress(), MAX_P);
                    }
                    adminViewModel.actualizarRadio(radio, selectedLatLon[0], selectedLatLon[1]);
                })
                .setNegativeButton("Cancelar", null)
                .create();

        dialog.setOnShowListener(d -> dialogMap.onResume());
        dialog.setOnDismissListener(d -> {
            dialogMap.onPause();
            dialogMap.onDetach();
        });

        dialog.show();
    }

    private static int progressToRadio(int progress, int maxProgress) {
        if (progress <= 0) return 1;
        if (progress >= maxProgress) return 50000;
        return (int) Math.round(Math.exp((double) progress / maxProgress * Math.log(50000)));
    }

    private static int radioToProgress(int radio, int maxProgress) {
        if (radio <= 1) return 0;
        if (radio >= 50000) return maxProgress;
        return (int) Math.round(Math.log(radio) / Math.log(50000) * maxProgress);
    }
}
