package com.example.appfichaje.data.model;

import com.google.gson.annotations.SerializedName;

public class CrearIncidenciaRequest {

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("fecha_hora")
    private String fechaHora;

    public CrearIncidenciaRequest(String descripcion) {
        this.descripcion = descripcion;
    }

    public CrearIncidenciaRequest(String descripcion, String fechaHora) {
        this.descripcion = descripcion;
        this.fechaHora = fechaHora;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getFechaHora() {
        return fechaHora;
    }
}
