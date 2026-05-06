package com.example.appfichaje.ui.historial;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfichaje.R;
import com.example.appfichaje.data.model.Registro;

import java.util.ArrayList;
import java.util.List;

public class RegistroAdapter extends RecyclerView.Adapter<RegistroAdapter.ViewHolder> {

    private List<Registro> registros = new ArrayList<>();

    public void setRegistros(List<Registro> registros) {
        this.registros = registros != null ? registros : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_registro, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Registro registro = registros.get(position);

        // Extract date from hora_entrada (format: "2026-05-06T08:00:00" or "2026-05-06 08:00:00")
        String horaEntrada = registro.getHoraEntrada();
        String horaSalida = registro.getHoraSalida();

        if (horaEntrada != null) {
            String[] parts = horaEntrada.replace("T", " ").split(" ");
            if (parts.length >= 1) {
                holder.tvFecha.setText(formatDate(parts[0]));
            }
            if (parts.length >= 2) {
                holder.tvHoraEntrada.setText(shortenTime(parts[1]));
            }
        }

        if (horaSalida != null) {
            String[] parts = horaSalida.replace("T", " ").split(" ");
            if (parts.length >= 2) {
                holder.tvHoraSalida.setText(shortenTime(parts[1]));
            } else {
                holder.tvHoraSalida.setText("--:--");
            }
        } else {
            holder.tvHoraSalida.setText("Abierto");
        }

        if (registro.getDuracionMinutos() != null) {
            int min = registro.getDuracionMinutos();
            holder.tvDuracion.setText((min / 60) + "h " + (min % 60) + "m");
        } else {
            holder.tvDuracion.setText("--");
        }
    }

    private String formatDate(String isoDate) {
        // Convert "2026-05-06" to "06/05/2026"
        String[] parts = isoDate.split("-");
        if (parts.length == 3) {
            return parts[2] + "/" + parts[1] + "/" + parts[0];
        }
        return isoDate;
    }

    private String shortenTime(String time) {
        // Keep only HH:mm
        if (time != null && time.length() >= 5) {
            return time.substring(0, 5);
        }
        return time != null ? time : "--:--";
    }

    @Override
    public int getItemCount() {
        return registros.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFecha, tvHoraEntrada, tvHoraSalida, tvDuracion;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tv_fecha_registro);
            tvHoraEntrada = itemView.findViewById(R.id.tv_hora_entrada_reg);
            tvHoraSalida = itemView.findViewById(R.id.tv_hora_salida_reg);
            tvDuracion = itemView.findViewById(R.id.tv_duracion);
        }
    }
}
