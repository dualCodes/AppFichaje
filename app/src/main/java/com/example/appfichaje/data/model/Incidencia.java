package com.example.appfichaje.data.model;

import com.google.gson.annotations.SerializedName;

public class Incidencia {

    @SerializedName("id_incidencia")
    private int idIncidencia;

    @SerializedName("fecha_hora")
    private String fechaHora;

    @SerializedName("descripcion")
    private String descripcion;

    public int getIdIncidencia() {
        return idIncidencia;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
