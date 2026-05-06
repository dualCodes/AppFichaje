package com.example.appfichaje.data.model;

import com.google.gson.annotations.SerializedName;

public class EntradaNfcResponse {

    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("id_registro")
    private int idRegistro;

    @SerializedName("hora_entrada")
    private String horaEntrada;

    public String getMensaje() {
        return mensaje;
    }

    public int getIdRegistro() {
        return idRegistro;
    }

    public String getHoraEntrada() {
        return horaEntrada;
    }
}
