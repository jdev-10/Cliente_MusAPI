package com.example.musapiapp.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Equivalente en Android de ClienteMusAPI.DTOs.LoginRequest (WPF).
 */
public class LoginRequest {

    @SerializedName("correo")
    private String correo;

    @SerializedName("contrasenia")
    private String contrasenia;

    // Constructor vacío para Gson
    public LoginRequest() { }

    // Constructor con todos los campos
    public LoginRequest(String correo, String contrasenia) {
        this.correo = correo;
        this.contrasenia = contrasenia;
    }

    // Getters & Setters

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
