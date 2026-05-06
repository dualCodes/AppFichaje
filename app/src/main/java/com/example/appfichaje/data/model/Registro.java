package com.example.appfichaje.data.model;

import com.google.gson.annotations.SerializedName;

public class Registro {

    @SerializedName("id_registro")
    private int idRegistro;

    @SerializedName("hora_entrada")
    private String horaEntrada;

    @SerializedName("hora_salida")
    private String horaSalida;

    @SerializedName("duracion_minutos")
    private Integer duracionMinutos;

    @SerializedName("lat_entrada")
    private Double latEntrada;

    @SerializedName("long_entrada")
    private Double longEntrada;

    public int getIdRegistro() {
        return idRegistro;
    }

    public String getHoraEntrada() {
        return horaEntrada;
    }

    public String getHoraSalida() {
        return horaSalida;
    }

    public Integer getDuracionMinutos() {
        return duracionMinutos;
    }

    public Double getLatEntrada() {
        return latEntrada;
    }

    public Double getLongEntrada() {
        return longEntrada;
    }
}
