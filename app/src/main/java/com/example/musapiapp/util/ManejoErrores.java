package com.example.musapiapp.util;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.HttpException;

public class ManejoErrores {

    public static String obtenerMensajeError(Throwable t) {
        if (t instanceof SocketTimeoutException) {
            return "La solicitud tardó demasiado. Verifica tu conexión e intenta más tarde.";
        } else if (t instanceof ConnectException) {
            return "El sistema falló al conectarse con el servidor, favor de intentar más tarde.";
        } else if (t instanceof HttpException) {
            HttpException httpEx = (HttpException) t;
            try {
                String errorJson = httpEx.response().errorBody().string();
                JsonObject obj = new Gson().fromJson(errorJson, JsonObject.class);
                return obj.has("mensaje") ? obj.get("mensaje").getAsString() : "Error desconocido.";
            } catch (Exception ex) {
                return "Error inesperado. Intenta más tarde.";
            }
        } else {
            return "Ocurrió un error inesperado: " + t.getMessage();
        }
    }

    public static void mostrarToastError(Context context, Throwable t) {
        String mensaje = obtenerMensajeError(t);
        Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show();
    }
}
