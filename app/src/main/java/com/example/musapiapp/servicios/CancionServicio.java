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

public interface CancionServicio {

    @Multipart
    @POST("canciones/subir")
    Call<RespuestaCliente> subirCancion(
            @Part("nombre") RequestBody nombre,
            @Part("idPerfilArtistas") RequestBody idPerfilArtistas,
            @Part("duracionStr") RequestBody duracionStr,
            @Part("idCategoriaMusical") RequestBody idCategoriaMusical,
            @Part("idAlbum") RequestBody idAlbum,
            @Part("posicionEnAlbum") RequestBody posicionEnAlbum,
            @Part MultipartBody.Part urlFoto,
            @Part MultipartBody.Part archivoCancion
    );

    @GET("canciones/album/{idAlbum}/canciones")
    Call<JsonObject> obtenerCancionesPorAlbum(
            @Path("idAlbum") int idAlbum
    );

    @GET("canciones/artista")
    Call<JsonObject> obtenerSencillosPorArtista(
            @Query("idPerfilArtista") int idPerfilArtista
    );

    @GET("canciones/buscar")
    Call<RespuestaApi<List<BusquedaCancionDTO>>> buscarCancion(@Query("texto") String texto);


    @POST("escucha/registrar")
    Call<JsonObject> registrarEscucha(@Body EscuchaDTO escuchaDTO);
}
