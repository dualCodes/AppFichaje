package com.example.appfichaje.data.model;

import com.google.gson.annotations.SerializedName;

public class FranjaHoraria {

    @SerializedName("dia")
    private String dia;

    @SerializedName("hora_entrada")
    private String horaEntrada;

    @SerializedName("hora_salida")
    private String horaSalida;

    public String getDia() {
        return dia;
    }

    public String getHoraEntrada() {
        return horaEntrada;
    }

    public String getHoraSalida() {
        return horaSalida;
    }
}
