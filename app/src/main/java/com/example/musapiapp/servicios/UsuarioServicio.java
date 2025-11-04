package com.example.musapiapp.servicios;

import com.example.musapiapp.dto.BusquedaArtistaDTO;
import com.example.musapiapp.dto.BusquedaUsuarioDTO;
import com.example.musapiapp.dto.EvaluacionDTO;
import com.example.musapiapp.dto.ListaDeReproduccion_CancionDTO;
import com.example.musapiapp.dto.LoginRequest;
import com.example.musapiapp.dto.PerfilArtistaDTO;
import com.example.musapiapp.dto.RespuestaCliente;
import com.example.musapiapp.dto.UsuarioDTO;
import com.example.musapiapp.util.RespuestaApi;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Retrofit interface equivalente a ClienteMusAPI.Servicios.UsuarioServicio (WPF).
 */
public interface UsuarioServicio {

    /**
     * Registrar un nuevo usuario.
     * POST /usuarios/registrar
     * Body JSON: UsuarioDTO
     */
    @POST("usuarios/registrar")
    Call<RespuestaCliente> registrarUsuario(@Body UsuarioDTO usuario);

    /**
     * Iniciar sesión y obtener datos + token.
     * POST /usuarios/login
     * Body JSON: LoginRequest
     */
    @POST("usuarios/login")
    Call<JsonObject> iniciarSesion(@Body LoginRequest loginRequest);

    /**
     * Editar perfil de usuario (campos opcionales + foto).
     * PUT /usuarios/{idUsuario}/editar-perfil
     */
    @Multipart
    @PUT("usuarios/{idUsuario}/editar-perfil")
    Call<RespuestaCliente> editarPerfil(
            @Path("idUsuario") int idUsuario,
            @Part("nombre") RequestBody nombre,
            @Part("nombreUsuario") RequestBody nombreUsuario,
            @Part("pais") RequestBody pais,
            @Part("descripcion") RequestBody descripcion,
            @Part MultipartBody.Part foto               // puede ser null
    );

    /**
     * Crear perfil de artista para el usuario.
     * POST /usuarios/crear-perfilArtista
     */
    @Multipart
    @POST("usuarios/crear-perfilArtista")
    Call<RespuestaCliente> crearPerfilArtista(
            @Part("idUsuario") RequestBody idUsuario,
            @Part("descripcion") RequestBody descripcion,
            @Part MultipartBody.Part foto               // puede ser null
    );

    /**
     * Obtener perfil de artista.
     * GET /usuarios/artista/{idArtista}
     */
    @GET("usuarios/artista/{idArtista}")
    Call<RespuestaApi<BusquedaArtistaDTO>> obtenerPerfilArtista(@Path("idArtista") int idArtista);

    /**
     * Buscar artistas por texto.
     * GET /usuarios/artistas/buscar?texto=…
     */
    @GET("usuarios/artistas/buscar")
    Call<RespuestaApi<List<BusquedaArtistaDTO>>> buscarArtista(@Query("texto") String texto);

    /**
     * Registrar evaluación de artista.
     * POST /evaluaciones/registrar
     * Body JSON: EvaluacionDTO
     */
    @POST("evaluaciones/registrar")
    Call<RespuestaApi<String>> evaluarArtista(@Body EvaluacionDTO evaluacionDTO);

    @DELETE("usuarios/{idUsuario}/eliminar")
    Call<RespuestaApi<String>> eliminarUsuario(
            @Path("idUsuario") int idUsuario,
            @Query("motivo") String motivo
    );

    @GET("usuarios/buscar")
    Call<RespuestaApi<List<BusquedaUsuarioDTO>>> buscarUsuario(
            @Query("texto") String texto,
            @Query("idUsuario") int idUsuario
    );

}
