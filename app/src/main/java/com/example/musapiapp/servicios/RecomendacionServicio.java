package com.example.musapiapp.servicios;

import com.example.musapiapp.dto.BusquedaCancionDTO;
import com.example.musapiapp.util.RespuestaApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecomendacionServicio {

    @GET("recomendaciones")
    Call<RespuestaApi<List<BusquedaCancionDTO>>> obtenerRecomendaciones(
            @Query("idUsuario") int idUsuario
    );
}
