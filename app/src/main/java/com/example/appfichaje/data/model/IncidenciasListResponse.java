package com.example.appfichaje.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class IncidenciasListResponse {

    @SerializedName("incidencias")
    private List<Incidencia> incidencias;

    @SerializedName("total")
    private int total;

    public List<Incidencia> getIncidencias() {
        return incidencias;
    }

    public int getTotal() {
        return total;
    }
}
