package com.example.appfichaje.data.model;

import com.google.gson.annotations.SerializedName;

public class SolicitarCambioPasswordRequest {

    @SerializedName("email")
    private String email;

    public SolicitarCambioPasswordRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
