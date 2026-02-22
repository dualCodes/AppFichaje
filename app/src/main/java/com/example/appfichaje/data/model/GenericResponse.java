package com.example.appfichaje.data.model;

import com.google.gson.annotations.SerializedName;

public class GenericResponse {

    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
