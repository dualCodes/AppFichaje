package com.example.appfichaje.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class EmpleadosListResponse {

    @SerializedName("trabajadores")
    private List<TrabajadorLista> trabajadores;

    @SerializedName("total")
    private int total;

    public List<TrabajadorLista> getTrabajadores() {
        return trabajadores;
    }

    public int getTotal() {
        return total;
    }
}
