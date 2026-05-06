package com.example.appfichaje.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class HorarioHoyResponse {

    @SerializedName("dia_semana")
    private String diaSemana;

    @SerializedName("franjas")
    private List<FranjaHoraria> franjas;

    @SerializedName("tiene_horario")
    private boolean tieneHorario;

    public String getDiaSemana() {
        return diaSemana;
    }

    public List<FranjaHoraria> getFranjas() {
        return franjas;
    }

    public boolean isTieneHorario() {
        return tieneHorario;
    }
}
