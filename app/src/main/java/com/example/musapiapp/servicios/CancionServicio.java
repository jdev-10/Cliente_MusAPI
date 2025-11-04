package com.example.musapiapp.servicios;

import com.example.musapiapp.dto.BusquedaCancionDTO;
import com.example.musapiapp.dto.EscuchaDTO;
import com.example.musapiapp.dto.RespuestaCliente;
import com.example.musapiapp.util.RespuestaApi;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Retrofit interface equivalente a ClienteMusAPI.Servicios.CancionServicio (WPF).
 */
public interface CancionServicio {

    /**
     * Subir una canción con metadatos y archivos (imagen + audio).
     */
    @Multipart
    @POST("canciones/subir")
    Call<RespuestaCliente> subirCancion(
            @Part("nombre") RequestBody nombre,
            @Part("idPerfilArtistas") RequestBody idPerfilArtistas, // lista coma-separada
            @Part("duracionStr") RequestBody duracionStr,
            @Part("idCategoriaMusical") RequestBody idCategoriaMusical,
            @Part("idAlbum") RequestBody idAlbum,                   // puede omitirse si -1
            @Part("posicionEnAlbum") RequestBody posicionEnAlbum,   // idem
            @Part MultipartBody.Part urlFoto,                       // puede ser null
            @Part MultipartBody.Part archivoCancion                 // puede ser null
    );

    /**
     * Obtener canciones de un álbum.
     * WPF: GET /canciones/album/{idAlbum}/canciones
     */
    @GET("canciones/album/{idAlbum}/canciones")
    Call<JsonObject> obtenerCancionesPorAlbum(
            @Path("idAlbum") int idAlbum
    );

    /**
     * Obtener sencillos de un artista.
     * WPF: GET /canciones/artista?idPerfilArtista=…
     */
    @GET("canciones/artista")
    Call<JsonObject> obtenerSencillosPorArtista(
            @Query("idPerfilArtista") int idPerfilArtista
    );

    /**
     * Buscar canciones por texto.
     * WPF: GET /canciones/buscar?texto=…
     */
    @GET("canciones/buscar")
    Call<RespuestaApi<List<BusquedaCancionDTO>>> buscarCancion(@Query("texto") String texto);


    @POST("escucha/registrar")
    Call<JsonObject> registrarEscucha(@Body EscuchaDTO escuchaDTO);
}
