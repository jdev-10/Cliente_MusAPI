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

public interface CategoriaMusicalServicio {

    @GET("categoriasMusicales")
    Call<JsonObject> obtenerCategorias();

    @POST("categoriasMusicales/registrar")
    Call<JsonObject> registrarCategoriaJson(@Body CategoriaMusicalDTO categoria);

    @PUT("categoriasMusicales/{id}")
    Call<JsonObject> editarCategoriaJson(@Path("id") int idCategoria, @Body CategoriaMusicalDTO categoriaSinId);

}
