package com.example.musapiapp.servicios;

import com.example.musapiapp.dto.ListaDeReproduccionDTO;
import com.example.musapiapp.dto.ListaDeReproduccion_CancionDTO;
import com.example.musapiapp.dto.RespuestaCliente;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Retrofit interface para ClienteMusAPI.Servicios.ListaServicio (WPF).
 */
public interface ListaServicio {

    /**
     * Crear una nueva lista de reproducción con datos y foto opcional.
     * POST /listasDeReproduccion/crear
     */
    @Multipart
    @POST("listasDeReproduccion/crear")
    Call<RespuestaCliente> crearLista(
            @Part("nombre") RequestBody nombre,
            @Part("descripcion") RequestBody descripcion,
            @Part("idUsuario") RequestBody idUsuario,
            @Part MultipartBody.Part foto  // puede ser null si no hay imagen
    );

    /**
     * Obtener todas las listas de reproducción de un usuario.
     * GET /listasDeReproduccion/usuario/{idUsuario}
     */
    @GET("listasDeReproduccion/usuario/{idUsuario}")
    Call<JsonObject> obtenerListasPorUsuario(
            @Path("idUsuario") int idUsuario
    );

    /**
     * Agregar una canción a una lista existente.
     * POST /listasDeReproduccion/agregar-cancion
     */
    @POST("listasDeReproduccion/agregar-cancion")
    Call<RespuestaCliente> agregarCancionALista(
            @Body ListaDeReproduccion_CancionDTO dto
    );

    @Multipart
    @PUT("listasDeReproduccion/editar")
    Call<Void> editarLista(
            @Part("nombre") RequestBody nombre,
            @Part("descripcion") RequestBody descripcion,
            @Part("idUsuario") RequestBody idUsuario,
            @Part MultipartBody.Part foto // puede ser null
    );


}
