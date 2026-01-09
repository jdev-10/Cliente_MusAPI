package com.example.musapiapp.dto;

import com.google.gson.annotations.SerializedName;

public class NotificacionDTO {

    @SerializedName("idNotificacion")
    private int idNotificacion;

    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("fechaEnvio")
    private String fechaEnvio;

    public NotificacionDTO() {
    }

    public NotificacionDTO(int idNotificacion, String mensaje, String fechaEnvio) {
        this.idNotificacion = idNotificacion;
        this.mensaje = mensaje;
        this.fechaEnvio = fechaEnvio;
    }

    public int getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(int idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(String fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }
}
