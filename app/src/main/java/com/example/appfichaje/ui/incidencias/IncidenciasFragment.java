package com.example.appfichaje.ui.incidencias;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class IncidenciasFragment extends Fragment {

    private IncidenciasViewModel incidenciasViewModel;
    private IncidenciaAdapter incidenciaAdapter;

    private ProgressBar progressIncidencias;
    private TextView tvEmpty;
    private RecyclerView rvIncidencias;
    private FloatingActionButton fabNuevaIncidencia;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_incidencias, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressIncidencias = view.findViewById(R.id.progress_incidencias);
        tvEmpty = view.findViewById(R.id.tv_empty_incidencias);
        rvIncidencias = view.findViewById(R.id.rv_incidencias);
        fabNuevaIncidencia = view.findViewById(R.id.fab_nueva_incidencia);

        incidenciaAdapter = new IncidenciaAdapter();
        rvIncidencias.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvIncidencias.setAdapter(incidenciaAdapter);

        IncidenciasViewModelFactory factory = new IncidenciasViewModelFactory(requireContext());
        incidenciasViewModel = new ViewModelProvider(this, factory).get(IncidenciasViewModel.class);

        setupObservers();
        loadIncidencias();

        fabNuevaIncidencia.setOnClickListener(v -> showCrearIncidenciaDialog());
    }

    private void loadIncidencias() {
        // Load last 3 months
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar end = Calendar.getInstance();
        Calendar start = (Calendar) end.clone();
        start.add(Calendar.MONTH, -3);
        incidenciasViewModel.loadIncidencias(sdf.format(start.getTime()), sdf.format(end.getTime()));
    }

    private void showCrearIncidenciaDialog() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_crear_incidencia, null);
        TextInputEditText etDescripcion = dialogView.findViewById(R.id.et_descripcion_incidencia);

        new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Crear", (dialog, which) -> {
                    String desc = etDescripcion.getText() != null
                            ? etDescripcion.getText().toString().trim() : "";
                    if (desc.isEmpty()) {
                        Toast.makeText(requireContext(),
                                "La descripción no puede estar vacía", Toast.LENGTH_SHORT).show();
                    } else {
                        incidenciasViewModel.crearIncidencia(desc);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void setupObservers() {
        incidenciasViewModel.getIncidenciasResult().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    progressIncidencias.setVisibility(View.VISIBLE);
                    tvEmpty.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    progressIncidencias.setVisibility(View.GONE);
                    if (resource.data != null && resource.data.getIncidencias() != null
                            && !resource.data.getIncidencias().isEmpty()) {
                        incidenciaAdapter.setIncidencias(resource.data.getIncidencias());
                        tvEmpty.setVisibility(View.GONE);
                        rvIncidencias.setVisibility(View.VISIBLE);
                    } else {
                        tvEmpty.setVisibility(View.VISIBLE);
                        rvIncidencias.setVisibility(View.GONE);
                    }
                    break;
                case ERROR:
                    progressIncidencias.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        incidenciasViewModel.getCrearResult().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    fabNuevaIncidencia.setEnabled(false);
                    break;
                case SUCCESS:
                    fabNuevaIncidencia.setEnabled(true);
                    Toast.makeText(requireContext(),
                            "Incidencia creada correctamente", Toast.LENGTH_SHORT).show();
                    loadIncidencias();
                    break;
                case ERROR:
                    fabNuevaIncidencia.setEnabled(true);
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }
}
