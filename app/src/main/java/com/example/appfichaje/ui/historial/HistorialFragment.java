package com.example.appfichaje.ui.historial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HistorialFragment extends Fragment {

    private HistorialViewModel historialViewModel;
    private RegistroAdapter registroAdapter;

    private TextView tvHorasTrabajadas, tvHorasTeoticas, tvHorasExtra, tvMesActual;
    private Button btnMesAnterior, btnMesSiguiente;
    private ProgressBar progressHistorial;
    private RecyclerView rvRegistros;

    private final Calendar mesCalendar = Calendar.getInstance();
    private final SimpleDateFormat mesFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
    private final SimpleDateFormat desdeHastaFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_historial, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvHorasTrabajadas = view.findViewById(R.id.tv_horas_trabajadas);
        tvHorasTeoticas = view.findViewById(R.id.tv_horas_teoricas);
        tvHorasExtra = view.findViewById(R.id.tv_horas_extra);
        tvMesActual = view.findViewById(R.id.tv_mes_actual);
        btnMesAnterior = view.findViewById(R.id.btn_mes_anterior);
        btnMesSiguiente = view.findViewById(R.id.btn_mes_siguiente);
        progressHistorial = view.findViewById(R.id.progress_historial);
        rvRegistros = view.findViewById(R.id.rv_registros);

        registroAdapter = new RegistroAdapter();
        rvRegistros.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvRegistros.setAdapter(registroAdapter);

        HistorialViewModelFactory factory = new HistorialViewModelFactory(requireContext());
        historialViewModel = new ViewModelProvider(this, factory).get(HistorialViewModel.class);

        setupObservers();
        setupClickListeners();
        loadCurrentMonth();
    }

    private void setupClickListeners() {
        btnMesAnterior.setOnClickListener(v -> {
            mesCalendar.add(Calendar.MONTH, -1);
            loadCurrentMonth();
        });
        btnMesSiguiente.setOnClickListener(v -> {
            mesCalendar.add(Calendar.MONTH, 1);
            loadCurrentMonth();
        });
    }

    private void loadCurrentMonth() {
        String mes = mesFormat.format(mesCalendar.getTime());
        tvMesActual.setText(mes);

        historialViewModel.loadResumenMensual(mes);

        // Load registros for the month
        Calendar start = (Calendar) mesCalendar.clone();
        start.set(Calendar.DAY_OF_MONTH, 1);
        Calendar end = (Calendar) mesCalendar.clone();
        end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));

        String desde = desdeHastaFormat.format(start.getTime());
        String hasta = desdeHastaFormat.format(end.getTime());
        historialViewModel.loadRegistros(desde, hasta);
    }

    private void setupObservers() {
        historialViewModel.getResumenResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource.data != null) {
                double trabajadas = resource.data.getHorasTrabajadas();
                double teoricas = resource.data.getHorasTeoticas();
                double extra = resource.data.getHorasExtra();
                tvHorasTrabajadas.setText(String.format(Locale.getDefault(), "%.1fh", trabajadas));
                tvHorasTeoticas.setText(String.format(Locale.getDefault(), "%.1fh", teoricas));
                String extraStr = String.format(Locale.getDefault(), "%.1fh", Math.abs(extra));
                tvHorasExtra.setText(extra >= 0 ? "+" + extraStr : "-" + extraStr);
                tvHorasExtra.setTextColor(extra >= 0
                        ? requireContext().getColor(android.R.color.holo_green_dark)
                        : requireContext().getColor(android.R.color.holo_red_dark));
            } else {
                tvHorasTrabajadas.setText("--");
                tvHorasTeoticas.setText("--");
                tvHorasExtra.setText("--");
            }
        });

        historialViewModel.getRegistrosResult().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    progressHistorial.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    progressHistorial.setVisibility(View.GONE);
                    if (resource.data != null) {
                        registroAdapter.setRegistros(resource.data.getRegistros());
                    }
                    break;
                case ERROR:
                    progressHistorial.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }
}
