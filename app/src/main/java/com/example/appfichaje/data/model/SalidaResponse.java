package com.example.appfichaje.data.model;

import com.google.gson.annotations.SerializedName;

public class SalidaResponse {

    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("id_registro")
    private int idRegistro;

    @SerializedName("hora_entrada")
    private String horaEntrada;

    @SerializedName("hora_salida")
    private String horaSalida;

    @SerializedName("duracion_minutos")
    private int duracionMinutos;

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

    public String getHoraSalida() {
        return horaSalida;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public double getDistanciaM() {
        return distanciaM;
    }
}
