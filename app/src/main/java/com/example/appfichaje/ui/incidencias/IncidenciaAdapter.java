package com.example.appfichaje.ui.incidencias;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfichaje.R;
import com.example.appfichaje.data.model.Incidencia;

import java.util.ArrayList;
import java.util.List;

public class IncidenciaAdapter extends RecyclerView.Adapter<IncidenciaAdapter.ViewHolder> {

    private List<Incidencia> incidencias = new ArrayList<>();

    public void setIncidencias(List<Incidencia> incidencias) {
        this.incidencias = incidencias != null ? incidencias : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_incidencia, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Incidencia incidencia = incidencias.get(position);
        holder.tvDescripcion.setText(incidencia.getDescripcion());

        String fechaHora = incidencia.getFechaHora();
        if (fechaHora != null) {
            // Format "2026-05-06T08:00:00" -> "06/05/2026 08:00"
            String formatted = fechaHora.replace("T", " ");
            if (formatted.length() >= 16) {
                String datePart = formatted.substring(0, 10);
                String timePart = formatted.substring(11, 16);
                String[] dateParts = datePart.split("-");
                if (dateParts.length == 3) {
                    formatted = dateParts[2] + "/" + dateParts[1] + "/" + dateParts[0] + " " + timePart;
                }
            }
            holder.tvFechaHora.setText(formatted);
        } else {
            holder.tvFechaHora.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return incidencias.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFechaHora, tvDescripcion;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFechaHora = itemView.findViewById(R.id.tv_fecha_incidencia);
            tvDescripcion = itemView.findViewById(R.id.tv_descripcion_incidencia);
        }
    }
}
