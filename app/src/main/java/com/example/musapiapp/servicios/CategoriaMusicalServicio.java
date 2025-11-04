package com.example.musapiapp.servicios;

import com.example.musapiapp.dto.CategoriaMusicalDTO;
import com.example.musapiapp.dto.RespuestaCliente;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Retrofit interface equivalente a ClienteMusAPI.Servicios.CategoriaMusicalServicio (WPF).
 */
public interface CategoriaMusicalServicio {

    /**
     * Obtener todas las categorías musicales.
     * GET /categoriasMusicales
     * Respuesta JSON: { "mensaje": "...", "datos": [ { ... }, ... ] }
     */
    @GET("categoriasMusicales")
    Call<JsonObject> obtenerCategorias();

    /**
     * Registrar una nueva categoría musical.
     * POST /categoriasMusicales/registrar
     * Body JSON: CategoriaMusicalDTO
     */
    @POST("categoriasMusicales/registrar")
    Call<JsonObject> registrarCategoriaJson(@Body CategoriaMusicalDTO categoria); // adicional

    @PUT("categoriasMusicales/{id}")
    Call<JsonObject> editarCategoriaJson(@Path("id") int idCategoria, @Body CategoriaMusicalDTO categoriaSinId); // adicional

}
