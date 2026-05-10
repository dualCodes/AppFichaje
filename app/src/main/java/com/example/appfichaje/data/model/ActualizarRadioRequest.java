package com.example.appfichaje.data.model;

import com.google.gson.annotations.SerializedName;

public class ActualizarRadioRequest {

    @SerializedName("radio")
    private Integer radio;

    @SerializedName("lat")
    private Double lat;

    @SerializedName("lon")
    private Double lon;

    public ActualizarRadioRequest(Integer radio, Double lat, Double lon) {
        this.radio = radio;
        this.lat = lat;
        this.lon = lon;
    }

    public Integer getRadio() { return radio; }
    public Double getLat() { return lat; }
    public Double getLon() { return lon; }
}
