package com.example.appfichaje.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfichaje.R;
import com.example.appfichaje.data.model.TrabajadorLista;

import java.util.ArrayList;
import java.util.List;

public class EmpleadoAdapter extends RecyclerView.Adapter<EmpleadoAdapter.ViewHolder> {

    public interface OnEmpleadoClickListener {
        void onEmpleadoClick(TrabajadorLista empleado);
    }

    private List<TrabajadorLista> empleados = new ArrayList<>();
    private final OnEmpleadoClickListener listener;

    public EmpleadoAdapter(OnEmpleadoClickListener listener) {
        this.listener = listener;
    }

    public void setEmpleados(List<TrabajadorLista> empleados) {
        this.empleados = empleados != null ? empleados : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_empleado, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TrabajadorLista empleado = empleados.get(position);

        String nombre = empleado.getNombre() != null ? empleado.getNombre() : "";
        if (empleado.getApellidos() != null && !empleado.getApellidos().isEmpty()) {
            nombre += " " + empleado.getApellidos();
        }
        holder.tvNombre.setText(nombre);
        holder.tvEmail.setText(empleado.getEmail() != null ? empleado.getEmail() : "");
        holder.tvRol.setText(empleado.getRol() != null ? empleado.getRol() : "");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onEmpleadoClick(empleado);
        });
    }

    @Override
    public int getItemCount() {
        return empleados.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvEmail, tvRol;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_nombre_empleado);
            tvEmail = itemView.findViewById(R.id.tv_email_empleado);
            tvRol = itemView.findViewById(R.id.tv_rol_empleado);
        }
    }
}
