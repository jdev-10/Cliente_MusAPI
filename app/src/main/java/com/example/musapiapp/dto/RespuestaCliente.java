package com.example.musapiapp.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Wrapper estándar para respuestas de tu API.
 */
public class RespuestaCliente {
    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("datos")
    private Object datos;  // o JsonElement, o List<AlgoDTO> según el endpoint

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

    /** Conveniencia: asume éxito si el mensaje es no nulo o tienes otro campo */
    public boolean isSuccess() {
        return mensaje != null && !mensaje.isEmpty();
    }
}
