package com.example.musapiapp.servicios;

import com.example.musapiapp.dto.BusquedaArtistaDTO;
import com.example.musapiapp.dto.ContenidoGuardadoDTO;
import com.example.musapiapp.util.RespuestaApi;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ContenidoGuardadoServicio {

    @POST("contenidoGuardado/guardar")
    Call<RespuestaApi<String>> guardarContenido(@Body ContenidoGuardadoDTO contenido);

    @GET("contenidoGuardado/albumes/{idUsuario}")
    Call<JsonObject> obtenerAlbumesGuardados(@Path("idUsuario") int idUsuario);

    @GET("contenidoGuardado/listas/{idUsuario}")
    Call<JsonObject> obtenerListasGuardadas(@Path("idUsuario") int idUsuario);

    @GET("contenidoGuardado/artistas/{idUsuario}")
    Call<RespuestaApi<List<BusquedaArtistaDTO>>> obtenerArtistasGuardados(@Path("idUsuario") int idUsuario);

}
