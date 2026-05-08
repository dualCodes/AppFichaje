package com.example.appfichaje.data.model;

import com.google.gson.annotations.SerializedName;

public class CentroTrabajoResponse {

    @SerializedName("id_empresa")
    private int idEmpresa;

    @SerializedName("nombrecomercial")
    private String nombreComercial;

    @SerializedName("cif")
    private String cif;

    @SerializedName("lat")
    private Double lat;

    @SerializedName("lon")
    private Double lon;

    @SerializedName("radio")
    private Integer radio;

    public int getIdEmpresa() { return idEmpresa; }
    public String getNombreComercial() { return nombreComercial; }
    public String getCif() { return cif; }
    public Double getLat() { return lat; }
    public Double getLon() { return lon; }
    public Integer getRadio() { return radio; }
}
