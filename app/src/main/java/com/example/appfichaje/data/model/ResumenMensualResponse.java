package com.example.appfichaje.data.model;

import com.google.gson.annotations.SerializedName;

public class ResumenMensualResponse {

    @SerializedName("mes")
    private String mes;

    @SerializedName("id_trabajador")
    private int idTrabajador;

    @SerializedName("horas_trabajadas")
    private double horasTrabajadas;

    @SerializedName("horas_teoricas")
    private double horasTeoticas;

    @SerializedName("horas_extra")
    private double horasExtra;

    @SerializedName("num_fichajes")
    private int numFichajes;

    public String getMes() {
        return mes;
    }

    public int getIdTrabajador() {
        return idTrabajador;
    }

    public double getHorasTrabajadas() {
        return horasTrabajadas;
    }

    public double getHorasTeoticas() {
        return horasTeoticas;
    }

    public double getHorasExtra() {
        return horasExtra;
    }

    public int getNumFichajes() {
        return numFichajes;
    }
}
