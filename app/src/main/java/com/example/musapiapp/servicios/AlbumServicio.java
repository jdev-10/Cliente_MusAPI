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

public interface AlbumServicio {

    @Multipart
    @POST("albumes/crear")
    Call<RespuestaApi<String>> crearAlbum(
            @Part("nombre") RequestBody nombre,
            @Part("idUsuario") RequestBody idUsuario,
            @Part MultipartBody.Part Foto
    );

    @GET("albumes/pendientes")
    Call<JsonObject> obtenerAlbumesPendientes(
            @Query("idPerfilArtista") int idPerfilArtista
    );

    @GET("albumes/artista")
    Call<JsonObject> obtenerAlbumesPublicos(
            @Query("idPerfilArtista") int idPerfilArtista
    );

    @GET("albumes/buscar")
    Call<RespuestaApi<List<BusquedaAlbumDTO>>> buscarAlbum(@Query("texto") String texto);

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
