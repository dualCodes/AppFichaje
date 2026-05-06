package com.example.appfichaje.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MisRegistrosResponse {

    @SerializedName("registros")
    private List<Registro> registros;

    @SerializedName("total")
    private int total;

    public List<Registro> getRegistros() {
        return registros;
    }

    public int getTotal() {
        return total;
    }
}
