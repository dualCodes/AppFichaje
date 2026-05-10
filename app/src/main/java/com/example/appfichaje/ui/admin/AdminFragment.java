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
import com.google.android.material.button.MaterialButtonToggleGroup;

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
        android.widget.LinearLayout layout = new android.widget.LinearLayout(requireContext());
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad, pad, 0);

        EditText etRadio = new EditText(requireContext());
        etRadio.setHint("Radio (metros)");
        etRadio.setInputType(InputType.TYPE_CLASS_NUMBER);
        if (centroActual != null && centroActual.getRadio() != null) {
            etRadio.setText(String.valueOf(centroActual.getRadio()));
        }

        EditText etLat = new EditText(requireContext());
        etLat.setHint("Latitud");
        etLat.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        if (centroActual != null && centroActual.getLat() != null) {
            etLat.setText(String.valueOf(centroActual.getLat()));
        }

        EditText etLon = new EditText(requireContext());
        etLon.setHint("Longitud");
        etLon.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        if (centroActual != null && centroActual.getLon() != null) {
            etLon.setText(String.valueOf(centroActual.getLon()));
        }

        layout.addView(etRadio);
        layout.addView(etLat);
        layout.addView(etLon);

        new AlertDialog.Builder(requireContext())
                .setTitle("Editar centro de trabajo")
                .setView(layout)
                .setPositiveButton("Guardar", (d, w) -> {
                    String valRadio = etRadio.getText().toString().trim();
                    String valLat   = etLat.getText().toString().trim();
                    String valLon   = etLon.getText().toString().trim();
                    if (valRadio.isEmpty()) {
                        Toast.makeText(requireContext(), "El radio no puede estar vacío", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        int radio = Integer.parseInt(valRadio);
                        Double lat = valLat.isEmpty() ? null : Double.parseDouble(valLat);
                        Double lon = valLon.isEmpty() ? null : Double.parseDouble(valLon);
                        adminViewModel.actualizarRadio(radio, lat, lon);
                    } catch (NumberFormatException e) {
                        Toast.makeText(requireContext(), "Valores numéricos inválidos", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
