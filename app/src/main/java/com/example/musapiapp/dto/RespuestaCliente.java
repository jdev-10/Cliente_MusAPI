package com.example.musapiapp.dto;

import com.google.gson.annotations.SerializedName;

public class RespuestaCliente {
    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("datos")
    private Object datos;

    public String getMensaje() {
        return mensaje;
    }
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Object getDatos() {
        return datos;
    }
    public void setDatos(Object datos) {
        this.datos = datos;
    }

    public boolean isSuccess() {
        return mensaje != null && !mensaje.isEmpty();
    }
}
