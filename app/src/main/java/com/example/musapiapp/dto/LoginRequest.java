package com.example.musapiapp.dto;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    @SerializedName("correo")
    private String correo;

    @SerializedName("contrasenia")
    private String contrasenia;

    public LoginRequest() { }

    public LoginRequest(String correo, String contrasenia) {
        this.correo = correo;
        this.contrasenia = contrasenia;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "correo='" + correo + '\'' +
                ", contrasenia='" + contrasenia + '\'' +
                '}';
    }
}
