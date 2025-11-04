package com.example.musapiapp.servicios;

import com.example.musapiapp.dto.InfoAlbumDTO;
import com.example.musapiapp.dto.BusquedaAlbumDTO;
import com.example.musapiapp.dto.RespuestaCliente;
import com.example.musapiapp.util.RespuestaApi;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Retrofit interface equivalente a ClienteMusAPI.Servicios.AlbumServicio (WPF).
 */
public interface AlbumServicio {

    /**
     * Crear un álbum (MultipartFormDataContent en WPF).
     */
    @Multipart
    @POST("albumes/crear")
    Call<RespuestaApi<String>> crearAlbum(
            @Part("nombre") RequestBody nombre,
            @Part("idUsuario") RequestBody idUsuario,
            @Part MultipartBody.Part Foto
    );


    /**
     * Obtener álbumes pendientes de aprobación.
     * En WPF: GET /albumes/pendientes?idPerfilArtista=…
     */
    @GET("albumes/pendientes")
    Call<JsonObject> obtenerAlbumesPendientes(
            @Query("idPerfilArtista") int idPerfilArtista
    );

    /**
     * Obtener álbumes públicos de un artista.
     * En WPF: GET /albumes/artista?idPerfilArtista=…
     */
    @GET("albumes/artista")
    Call<JsonObject> obtenerAlbumesPublicos(
            @Query("idPerfilArtista") int idPerfilArtista
    );

    /**
     * Buscar álbumes por texto.
     * En WPF: GET /albumes/buscar?texto=…
     */
    @GET("albumes/buscar")
    Call<RespuestaApi<List<BusquedaAlbumDTO>>> buscarAlbum(@Query("texto") String texto);

    /**
     * Publicar (poner en público) un álbum ya creado.
     * En WPF: PUT /albumes/publicar/{idAlbum}
     */
    @PUT("albumes/publicar/{idAlbum}")
    Call<JsonObject> publicarAlbum(
            @Path("idAlbum") long idAlbum
    );

    @Multipart
    @PUT("albumes/editar")
    Call<RespuestaApi<String>> editarAlbum(
            @Part("nombre") RequestBody nombre,
            @Part("idUsuario") RequestBody idUsuario,
            @Part MultipartBody.Part foto
    );

}
