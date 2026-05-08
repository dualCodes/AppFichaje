package com.example.appfichaje.data.model;

import com.google.gson.annotations.SerializedName;

public class TrabajadorLista {

    @SerializedName("id_trabajador")
    private int idTrabajador;

    @SerializedName("nif")
    private String nif;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("apellidos")
    private String apellidos;

    @SerializedName("email")
    private String email;

    @SerializedName("telef")
    private String telef;

    @SerializedName("rol")
    private String rol;

    @SerializedName("id_empresa")
    private Integer idEmpresa;

    @SerializedName("id_horario")
    private Integer idHorario;

    public int getIdTrabajador() { return idTrabajador; }
    public String getNif() { return nif; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
    public String getEmail() { return email; }
    public String getTelef() { return telef; }
    public String getRol() { return rol; }
    public Integer getIdEmpresa() { return idEmpresa; }
    public Integer getIdHorario() { return idHorario; }
}
