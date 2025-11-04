package com.example.musapiapp.servicios;

import com.example.musapiapp.dto.ArtistaMasEscuchadoDTO;
import com.example.musapiapp.dto.CancionMasEscuchadaDTO;
import com.example.musapiapp.dto.EstadisticasContenidoSubidoDTO;
import com.example.musapiapp.dto.EstadisticasNumeroUsuariosDTO;
import com.example.musapiapp.dto.EstadisticasPersonalesDTO;
import com.example.musapiapp.util.RespuestaApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EstadisticasServicio {

    @GET("estadisticas/contenidoSubido")
    Call<RespuestaApi<EstadisticasContenidoSubidoDTO>> obtenerEstadisticasContenidoSubido(
            @Query("idPerfilArtista") int idPerfilArtista,
            @Query("tipoContenido") String tipoContenido
    );


    @GET("estadisticas/personales")
    Call<RespuestaApi<EstadisticasPersonalesDTO>> obtenerEstadisticasPersonales(
            @Query("idUsuario") int idUsuario,
            @Query("fechaInicio") String fechaInicio,
            @Query("fechaFin") String fechaFin
    );

    @GET("estadisticas/numeroUsuarios")
    Call<RespuestaApi<EstadisticasNumeroUsuariosDTO>> obtenerEstadisticasUsuarios();

    @GET("estadisticas/topArtistas")
    Call<RespuestaApi<List<ArtistaMasEscuchadoDTO>>> obtenerEstadisticasArtistas(
            @Query("fechaInicio") String fechaInicio,
            @Query("fechaFin") String fechaFin
    );

    @GET("estadisticas/topCanciones")
    Call<RespuestaApi<List<CancionMasEscuchadaDTO>>> obtenerEstadisticasCanciones(
            @Query("fechaInicio") String fechaInicio,
            @Query("fechaFin") String fechaFin
    );
}
