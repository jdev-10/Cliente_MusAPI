package com.example.musapiapp.servicios;

import com.example.musapiapp.dto.NotificacionDTO;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface NotificacionServicio {

    @GET("notificaciones/pendientes/{idUsuario}")
    Call<List<NotificacionDTO>> obtenerPendientes(@Path("idUsuario") int idUsuario);

    @PUT("notificaciones/marcar-leida/{idNotificacion}")
    Call<String> marcarComoLeida(@Path("idNotificacion") int idNotificacion);
}
