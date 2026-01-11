package com.example.musapiapp.activities.perfiles;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.musapiapp.R;
import com.example.musapiapp.activities.contenido.SubirContenidoActivity;
import com.example.musapiapp.activities.perfiles.chat.ChatActivity;
import com.example.musapiapp.activities.perfiles.estadisticas.EstadisticasContenidoSubidoActivity;
import com.example.musapiapp.adapters.UcContenidoAdapter;
import com.example.musapiapp.dialogs.DialogEvaluarArtista;
import com.example.musapiapp.dto.*;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.servicios.*;
import com.example.musapiapp.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilArtistaActivity extends AppCompatActivity {

    private int idUsuarioArtista = -1;
    private int idPerfilArtista = -1;

    private TextView txtNombre, txtUsuario, txtDescripcion;
    private ImageView imgFoto;
    private LinearLayout menuArtista, menuOyente;
    private RecyclerView rvAlbumes, rvSencillos;
    private Button btnEvaluar, btnSeguir;
    private ReproductorUIHelper reproductorHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_artista);

        reproductorHelper = new ReproductorUIHelper(this);

        txtNombre = findViewById(R.id.txtNombre);
        txtUsuario = findViewById(R.id.txtUsuario);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        imgFoto = findViewById(R.id.imgFoto);
        menuArtista = findViewById(R.id.menuArtista);
        menuOyente = findViewById(R.id.menuOyente);
        rvAlbumes = findViewById(R.id.rvAlbumes);
        rvSencillos = findViewById(R.id.rvSencillos);
        btnEvaluar = findViewById(R.id.btnEvaluar);
        btnSeguir = findViewById(R.id.btnSeguir);

        rvAlbumes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvSencillos.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.btnVolver).setOnClickListener(v -> onBackPressed());


        BusquedaArtistaDTO artistaRecibido = getIntent().getParcelableExtra("artista");
        if (artistaRecibido != null) {
            idPerfilArtista = artistaRecibido.getIdArtista();
            cargarVistaDesdeDTO(artistaRecibido);


            if (idPerfilArtista != -1) {
                cargarAlbumes(idPerfilArtista);
                cargarSencillos(idPerfilArtista);
            }
            return;
        }


        idUsuarioArtista = getIntent().getIntExtra("idArtista", -1);
        if (idUsuarioArtista == -1) {
            Toast.makeText(this, "ID inválido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cargarDatosArtista();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (reproductorHelper != null) {
            reproductorHelper.refrescarEstadoActual();
            Reproductor.inicializar(this, reproductorHelper);
        }

        if (idUsuarioArtista != -1) {
            cargarDatosArtista();
        }

        if (idPerfilArtista != -1) {
            cargarAlbumes(idPerfilArtista);
            cargarSencillos(idPerfilArtista);
        }
    }

    private void cargarDatosArtista() {
        UsuarioServicio usuarioServicio = ApiCliente.getClient().create(UsuarioServicio.class);

        usuarioServicio.obtenerPerfilArtista(idUsuarioArtista)
                .enqueue(new Callback<RespuestaApi<BusquedaArtistaDTO>>() {
                    @Override
                    public void onResponse(Call<RespuestaApi<BusquedaArtistaDTO>> call,
                                           Response<RespuestaApi<BusquedaArtistaDTO>> response) {
                        if (!response.isSuccessful() || response.body() == null) return;

                        BusquedaArtistaDTO perfil = response.body().getDatos();
                        if (perfil == null) return;

                        idPerfilArtista = perfil.getIdArtista();

                        cargarVistaDesdeDTO(perfil);

                        cargarAlbumes(idPerfilArtista);
                        cargarSencillos(idPerfilArtista);
                    }

                    @Override
                    public void onFailure(Call<RespuestaApi<BusquedaArtistaDTO>> call, Throwable t) {
                        ManejoErrores.mostrarToastError(PerfilArtistaActivity.this, t);
                    }
                });
    }

    private void cargarAlbumes(int idPerfilArtista) {
        AlbumServicio albumServicio = ApiCliente.getClient().create(AlbumServicio.class);

        albumServicio.obtenerAlbumesPublicos(idPerfilArtista)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (!response.isSuccessful() || response.body() == null) return;

                        List<BusquedaAlbumDTO> albumes = new Gson().fromJson(
                                response.body().get("datos").getAsJsonArray(),
                                new com.google.gson.reflect.TypeToken<List<BusquedaAlbumDTO>>() {}.getType()
                        );

                        UcContenidoAdapter<BusquedaAlbumDTO> adapter =
                                new UcContenidoAdapter<>(PerfilArtistaActivity.this, albumes, "ALBUM", true);
                        rvAlbumes.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        ManejoErrores.mostrarToastError(PerfilArtistaActivity.this, t);
                    }
                });
    }

    private void cargarSencillos(int idPerfilArtista) {
        CancionServicio cancionServicio = ApiCliente.getClient().create(CancionServicio.class);

        cancionServicio.obtenerSencillosPorArtista(idPerfilArtista)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (!response.isSuccessful() || response.body() == null) return;

                        List<BusquedaCancionDTO> canciones = new Gson().fromJson(
                                response.body().get("datos").getAsJsonArray(),
                                new com.google.gson.reflect.TypeToken<List<BusquedaCancionDTO>>() {}.getType()
                        );

                        UcContenidoAdapter<BusquedaCancionDTO> adapter =
                                new UcContenidoAdapter<>(PerfilArtistaActivity.this, canciones, "CANCION", true);
                        adapter.setListaCanciones(canciones);
                        adapter.setIndice(0);
                        rvSencillos.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        ManejoErrores.mostrarToastError(PerfilArtistaActivity.this, t);
                    }
                });
    }

    private void cargarVistaDesdeDTO(BusquedaArtistaDTO perfil) {

        menuArtista.setVisibility(View.GONE);
        menuOyente.setVisibility(View.GONE);

        txtNombre.setText(perfil.getNombre());
        txtUsuario.setText("@" + perfil.getNombreUsuario());
        txtDescripcion.setText(perfil.getDescripcion());

        // Imagen
        if (perfil.getUrlFoto() != null && !perfil.getUrlFoto().isEmpty()) {
            GlideUrl glideUrl = new GlideUrl(
                    ApiCliente.getUrlArchivos() + perfil.getUrlFoto(),
                    new LazyHeaders.Builder()
                            .addHeader("Authorization", "Bearer " + SesionUsuario.getToken())
                            .build()
            );
            Glide.with(this)
                    .load(glideUrl)
                    .placeholder(R.drawable.ic_usuario_perfil)
                    .error(R.drawable.ic_usuario_perfil)
                    .into(imgFoto);
        } else {
            imgFoto.setImageResource(R.drawable.ic_usuario_perfil);
        }

        // Guardamos idPerfil (tu DTO lo trae aquí)
        idPerfilArtista = perfil.getIdArtista();

        boolean esMiPerfil = perfil.getNombreUsuario().equals(SesionUsuario.getNombreUsuario());

        if (esMiPerfil) {
            menuArtista.setVisibility(View.VISIBLE);

            findViewById(R.id.btnEstadisticas).setOnClickListener(v -> {
                Intent intent = new Intent(this, EstadisticasContenidoSubidoActivity.class);
                intent.putExtra("idPerfilArtista", idPerfilArtista);
                startActivity(intent);
            });

            findViewById(R.id.btnSubirContenido).setOnClickListener(v -> {
                Intent intent = new Intent(this, SubirContenidoActivity.class);
                intent.putExtra("idPerfilArtista", idPerfilArtista);
                startActivity(intent);
            });

            findViewById(R.id.btnEditarPerfil).setOnClickListener(v -> {
                Intent intent = new Intent(this, EditarPerfilActivity.class);
                intent.putExtra("idPerfilArtista", idPerfilArtista);
                startActivity(intent);
            });

        } else {
            menuOyente.setVisibility(View.VISIBLE);

            btnEvaluar.setOnClickListener(v ->
                    new DialogEvaluarArtista(idPerfilArtista)
                            .show(getSupportFragmentManager(), "DialogEvaluar"));

            btnSeguir.setOnClickListener(v -> {
                ContenidoGuardadoDTO dto = new ContenidoGuardadoDTO(
                        SesionUsuario.getIdUsuario(),
                        idPerfilArtista,
                        "ARTISTA"
                );

                ContenidoGuardadoServicio servicio = ApiCliente.getClient().create(ContenidoGuardadoServicio.class);
                servicio.guardarContenido(dto).enqueue(new Callback<RespuestaApi<String>>() {
                    @Override
                    public void onResponse(Call<RespuestaApi<String>> call, Response<RespuestaApi<String>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String mensaje = response.body().getMensaje();
                            Toast.makeText(PerfilArtistaActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                            if (mensaje.equals("Contenido guardado exitosamente")) {
                                btnSeguir.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<RespuestaApi<String>> call, Throwable t) {
                        ManejoErrores.mostrarToastError(PerfilArtistaActivity.this, t);
                    }
                });
            });
        }

        findViewById(R.id.btnChat).setOnClickListener(v -> {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("artista", perfil);
            startActivity(intent);
        });
    }
}
