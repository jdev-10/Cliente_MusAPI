package com.example.musapiapp.util;

import com.google.gson.annotations.SerializedName;

public class RespuestaApi<T> {

    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("datos")
    private T datos;

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public T getDatos() {
        return datos;
    }

    public void setDatos(T datos) {
        this.datos = datos;
    }
}
