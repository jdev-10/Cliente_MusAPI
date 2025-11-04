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

/**
 * Retrofit interface equivalente a ClienteMusAPI.Servicios.ContenidoGuardadoServicio (WPF).
 */
public interface ContenidoGuardadoServicio {

    /**
     * Guardar contenido (álbum, lista o artista) para un usuario.
     * POST /contenidoGuardado/guardar
     * Body JSON: ContenidoGuardadoDTO
     * Respuesta JSON: { "mensaje": "...", "datos": ... }
     */
    @POST("contenidoGuardado/guardar")
    Call<RespuestaApi<String>> guardarContenido(@Body ContenidoGuardadoDTO contenido);

    /**
     * Obtener álbumes guardados por un usuario.
     * GET /contenidoGuardado/albumes/{idUsuario}
     * Respuesta JSON: { "mensaje": "...", "datos": [ { … }, … ] }
     */
    @GET("contenidoGuardado/albumes/{idUsuario}")
    Call<JsonObject> obtenerAlbumesGuardados(@Path("idUsuario") int idUsuario);

    /**
     * Obtener listas de reproducción guardadas por un usuario.
     * GET /contenidoGuardado/listas/{idUsuario}
     */
    @GET("contenidoGuardado/listas/{idUsuario}")
    Call<JsonObject> obtenerListasGuardadas(@Path("idUsuario") int idUsuario);

    /**
     * Obtener artistas guardados por un usuario.
     * GET /contenidoGuardado/artistas/{idUsuario}
     */
    @GET("contenidoGuardado/artistas/{idUsuario}")
    Call<RespuestaApi<List<BusquedaArtistaDTO>>> obtenerArtistasGuardados(@Path("idUsuario") int idUsuario);

}
