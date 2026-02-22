package com.example.appfichaje.data.model;

import com.google.gson.annotations.SerializedName;

public class EntradaResponse {

    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("id_registro")
    private int idRegistro;

    @SerializedName("hora_entrada")
    private String horaEntrada;

    @SerializedName("distancia_m")
    private double distanciaM;

    public String getMensaje() {
        return mensaje;
    }

    public int getIdRegistro() {
        return idRegistro;
    }

    public String getHoraEntrada() {
        return horaEntrada;
    }

    public double getDistanciaM() {
        return distanciaM;
    }
}
