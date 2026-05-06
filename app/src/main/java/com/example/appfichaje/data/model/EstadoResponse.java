package com.example.appfichaje.data.model;

import com.google.gson.annotations.SerializedName;

public class EstadoResponse {

    @SerializedName("estado")
    private String estado;

    @SerializedName("hora_entrada")
    private String horaEntrada;

    @SerializedName("id_registro")
    private Integer idRegistro;

    @SerializedName("ultima_salida")
    private String ultimaSalida;

    public String getEstado() {
        return estado;
    }

    public String getHoraEntrada() {
        return horaEntrada;
    }

    public Integer getIdRegistro() {
        return idRegistro;
    }

    public String getUltimaSalida() {
        return ultimaSalida;
    }

    public boolean isDentro() {
        return "dentro".equals(estado);
    }
}
