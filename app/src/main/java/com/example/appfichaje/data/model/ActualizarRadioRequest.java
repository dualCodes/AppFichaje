package com.example.appfichaje.data.model;

import com.google.gson.annotations.SerializedName;

public class ActualizarRadioRequest {

    @SerializedName("radio")
    private Integer radio;

    public ActualizarRadioRequest(Integer radio) {
        this.radio = radio;
    }

    public Integer getRadio() {
        return radio;
    }
}
