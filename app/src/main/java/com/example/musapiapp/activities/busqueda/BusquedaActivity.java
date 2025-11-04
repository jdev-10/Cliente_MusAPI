package com.example.musapiapp.activities.busqueda;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musapiapp.R;
import com.example.musapiapp.adapters.UcContenidoAdapter;
import com.example.musapiapp.dto.BusquedaAlbumDTO;
import com.example.musapiapp.dto.BusquedaArtistaDTO;
import com.example.musapiapp.dto.BusquedaCancionDTO;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.servicios.AlbumServicio;
import com.example.musapiapp.servicios.CancionServicio;
import com.example.musapiapp.servicios.UsuarioServicio;
import com.example.musapiapp.util.ManejoErrores;
import com.example.musapiapp.util.Reproductor;
import com.example.musapiapp.util.ReproductorUIHelper;
import com.example.musapiapp.util.RespuestaApi;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusquedaActivity extends AppCompatActivity {

    private EditText etBusqueda;
    private Spinner spinnerTipo;
    private RecyclerView rvResultados;
    private ReproductorUIHelper reproductorHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda);

        reproductorHelper = new ReproductorUIHelper(this);

        etBusqueda = findViewById(R.id.etBusqueda);
        spinnerTipo = findViewById(R.id.spinnerTipo);
        rvResultados = findViewById(R.id.rvResultados);

        rvResultados.setLayoutManager(new LinearLayoutManager(this));

        Button btnBuscar = findViewById(R.id.btnBuscar);
        Button btnVolver = findViewById(R.id.btnVolver);
        Button btnPerfil = findViewById(R.id.btnPerfil);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.tipos_busqueda, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapter);

        btnBuscar.setOnClickListener(v -> buscarContenido());
        btnVolver.setOnClickListener(v -> finish());

        btnPerfil.setOnClickListener(v ->
                startActivity(new Intent(this, com.example.musapiapp.activities.perfiles.PerfilUsuarioActivity.class))
        );

        // Si se invocó con un texto desde el menú principal
        String query = getIntent().getStringExtra("query");
        if (query != null) {
            etBusqueda.setText(query);
            buscarContenido();
        }

        spinnerTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                buscarContenido(); // buscar cada que cambie el tipo
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void buscarContenido() {
        String texto = etBusqueda.getText().toString().trim();
        String tipoSeleccionado = spinnerTipo.getSelectedItem().toString(); // "Canción", "Álbum", "Artista"

        if (texto.isEmpty()) return;

        rvResultados.setAdapter(null); // limpiar resultados

        switch (tipoSeleccionado) {
            case "Canción":
                CancionServicio cancionSvc = ApiCliente.getClient().create(CancionServicio.class);
                cancionSvc.buscarCancion(texto).enqueue(new Callback<RespuestaApi<List<BusquedaCancionDTO>>>() {
                    @Override
                    public void onResponse(Call<RespuestaApi<List<BusquedaCancionDTO>>> call,
                                           Response<RespuestaApi<List<BusquedaCancionDTO>>> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(BusquedaActivity.this, "Error al obtener canciones.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        List<BusquedaCancionDTO> canciones = response.body().getDatos();

                        UcContenidoAdapter<BusquedaCancionDTO> adapter =
                                new UcContenidoAdapter<>(BusquedaActivity.this, canciones, "CANCION", true);
                        adapter.setListaCanciones(canciones);
                        adapter.setIndice(0);
                        rvResultados.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(Call<RespuestaApi<List<BusquedaCancionDTO>>> call, Throwable t) {
                        ManejoErrores.mostrarToastError(BusquedaActivity.this, t);
                    }
                });
                break;

            case "Álbum":
                AlbumServicio albumSvc = ApiCliente.getClient().create(AlbumServicio.class);
                albumSvc.buscarAlbum(texto).enqueue(new Callback<RespuestaApi<List<BusquedaAlbumDTO>>>() {
                    @Override
                    public void onResponse(Call<RespuestaApi<List<BusquedaAlbumDTO>>> call,
                                           Response<RespuestaApi<List<BusquedaAlbumDTO>>> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(BusquedaActivity.this, "Error al obtener álbumes.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        List<BusquedaAlbumDTO> albumes = response.body().getDatos();
                        rvResultados.setAdapter(new UcContenidoAdapter<>(BusquedaActivity.this, albumes, "ALBUM", true));
                    }

                    @Override
                    public void onFailure(Call<RespuestaApi<List<BusquedaAlbumDTO>>> call, Throwable t) {
                        ManejoErrores.mostrarToastError(BusquedaActivity.this, t);
                    }
                });
                break;

            case "Artista":
                UsuarioServicio usuarioSvc = ApiCliente.getClient().create(UsuarioServicio.class);
                usuarioSvc.buscarArtista(texto).enqueue(new Callback<RespuestaApi<List<BusquedaArtistaDTO>>>() {
                    @Override
                    public void onResponse(Call<RespuestaApi<List<BusquedaArtistaDTO>>> call,
                                           Response<RespuestaApi<List<BusquedaArtistaDTO>>> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(BusquedaActivity.this, "Error al obtener artistas.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        List<BusquedaArtistaDTO> artistas = response.body().getDatos();
                        rvResultados.setAdapter(new UcContenidoAdapter<>(BusquedaActivity.this, artistas, "ARTISTA", true));
                    }

                    @Override
                    public void onFailure(Call<RespuestaApi<List<BusquedaArtistaDTO>>> call, Throwable t) {
                        ManejoErrores.mostrarToastError(BusquedaActivity.this, t);
                    }
                });
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (reproductorHelper != null) reproductorHelper.limpiar();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (reproductorHelper != null){
            reproductorHelper.refrescarEstadoActual();
            Reproductor.inicializar(this, reproductorHelper);
        }
    }
}
