package com.example.musapiapp.activities.contenido;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musapiapp.R;
import com.example.musapiapp.adapters.UcContenidoAdapter;
import com.example.musapiapp.dto.InfoAlbumDTO;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.servicios.AlbumServicio;
import com.example.musapiapp.util.ManejoErrores;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubirContenidoActivity extends AppCompatActivity {

    private RecyclerView rvAlbumes;
    private int idPerfilArtista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_contenido);

        // 🔙 Botón volver
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        // 🔄 ID del artista
        idPerfilArtista = getIntent().getIntExtra("idPerfilArtista", -1);
        if (idPerfilArtista == -1) {
            Toast.makeText(this, "ID de artista no válido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 🎵 Acciones de los botones
        findViewById(R.id.btnCrearAlbum).setOnClickListener(v -> {
            Intent intent = new Intent(this, CrearAlbumActivity.class);
            intent.putExtra("idPerfilArtista", idPerfilArtista);
            startActivity(intent);
        });

        findViewById(R.id.btnSubirSencillo).setOnClickListener(v -> {
            Intent intent = new Intent(this, SubirCancionActivity.class);
            intent.putExtra("idPerfilArtista", idPerfilArtista);
            startActivity(intent);
        });

        // 📜 Lista de álbumes pendientes
        rvAlbumes = findViewById(R.id.rvAlbumesPendientes);
        rvAlbumes.setLayoutManager(new LinearLayoutManager(this));

        cargarAlbumesPendientes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarAlbumesPendientes(); // Recarga cada vez que regresas
    }

    private void cargarAlbumesPendientes() {
        AlbumServicio servicio = ApiCliente.getClient().create(AlbumServicio.class);
        servicio.obtenerAlbumesPendientes(idPerfilArtista)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(SubirContenidoActivity.this, "Error al cargar álbumes pendientes", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        List<InfoAlbumDTO> albumes = new Gson().fromJson(
                                response.body().get("datos").getAsJsonArray(),
                                new com.google.gson.reflect.TypeToken<List<InfoAlbumDTO>>() {}.getType()
                        );

                        UcContenidoAdapter<InfoAlbumDTO> adapter =
                                new UcContenidoAdapter<>(SubirContenidoActivity.this, albumes, "ALBUM_PENDIENTE", false);
                        adapter.setIdArtista(idPerfilArtista);
                        rvAlbumes.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        ManejoErrores.mostrarToastError(SubirContenidoActivity.this, t);
                    }
                });
    }
}
