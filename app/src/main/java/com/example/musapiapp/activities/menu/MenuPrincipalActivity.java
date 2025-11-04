package com.example.musapiapp.activities.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musapiapp.R;
import com.example.musapiapp.adapters.UcContenidoAdapter;
import com.example.musapiapp.dto.BusquedaAlbumDTO;
import com.example.musapiapp.dto.BusquedaArtistaDTO;
import com.example.musapiapp.dto.ListaDeReproduccionDTO;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.servicios.ContenidoGuardadoServicio;
import com.example.musapiapp.util.ManejoErrores;
import com.example.musapiapp.util.Reproductor;
import com.example.musapiapp.util.ReproductorUIHelper;
import com.example.musapiapp.util.RespuestaApi;
import com.example.musapiapp.util.SesionUsuario;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuPrincipalActivity extends AppCompatActivity {

    private EditText etBusqueda;
    private Button btnBuscar, btnMenuAdmin, btnPerfil, btnCerrarSesion, btnCrearLista;
    private RecyclerView rvAlbumes, rvListas, rvArtistas;
    private ContenidoGuardadoServicio servicio;
    private final Gson gson = new Gson();
    private ReproductorUIHelper reproductorHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        reproductorHelper = new ReproductorUIHelper(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
            return insets;
        });

        // Vistas
        etBusqueda     = findViewById(R.id.etBusqueda);
        btnBuscar      = findViewById(R.id.btnBuscar);
        btnMenuAdmin   = findViewById(R.id.btnMenuAdmin);
        btnPerfil      = findViewById(R.id.btnPerfil);
        btnCerrarSesion= findViewById(R.id.btnCerrarSesion);
        btnCrearLista  = findViewById(R.id.btnCrearLista);

        rvAlbumes  = findViewById(R.id.rvAlbumes);
        rvListas   = findViewById(R.id.rvListas);
        rvArtistas = findViewById(R.id.rvArtistas);

        rvAlbumes.setLayoutManager(new LinearLayoutManager(this));
        rvListas.setLayoutManager(new LinearLayoutManager(this));
        rvArtistas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        if (!SesionUsuario.getEsAdmin()) {
            btnMenuAdmin.setVisibility(View.GONE);
        }

        servicio = ApiCliente.getClient().create(ContenidoGuardadoServicio.class);

        btnBuscar.setOnClickListener(v -> {
            String q = etBusqueda.getText().toString().trim();
            startActivity(new Intent(this, com.example.musapiapp.activities.busqueda.BusquedaActivity.class)
                    .putExtra("query", q));
        });

        btnMenuAdmin.setOnClickListener(v ->
                startActivity(new Intent(this, MenuAdminActivity.class)));

        btnPerfil.setOnClickListener(v ->
                startActivity(new Intent(this, com.example.musapiapp.activities.perfiles.PerfilUsuarioActivity.class)));

        btnCerrarSesion.setOnClickListener(v -> {
            SesionUsuario.clear();
            startActivity(new Intent(this, com.example.musapiapp.activities.inicio.InicioSesionActivity.class));
            finish();
        });

        btnCrearLista.setOnClickListener(v ->
                startActivity(new Intent(this, com.example.musapiapp.activities.contenido.CrearListaActivity.class)));

        cargarContenidoGuardado();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarContenidoGuardado();
        if (reproductorHelper != null){
            reproductorHelper.refrescarEstadoActual();
            Reproductor.inicializar(this, reproductorHelper);
        }
    }

    private <T> List<T> parseDatos(JsonObject body, Type typeToken) {
        if (!body.has("datos")) return Collections.emptyList();
        JsonArray arr = body.getAsJsonArray("datos");
        return gson.fromJson(arr, typeToken);
    }

    private void cargarContenidoGuardado() {
        int idUsuario = SesionUsuario.getIdUsuario();

        Type typeAlbum   = new TypeToken<List<BusquedaAlbumDTO>>(){}.getType();
        Type typeLista   = new TypeToken<List<ListaDeReproduccionDTO>>(){}.getType();
        Type typeArtista = new TypeToken<List<BusquedaArtistaDTO>>(){}.getType();

        // — Álbumes —
        servicio.obtenerAlbumesGuardados(idUsuario).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> resp) {
                if (!resp.isSuccessful() || resp.body() == null) return;
                List<BusquedaAlbumDTO> albumes = parseDatos(resp.body(), typeAlbum);
                UcContenidoAdapter<BusquedaAlbumDTO> adapter =
                        new UcContenidoAdapter<>(MenuPrincipalActivity.this, albumes, "ALBUM", false);
                rvAlbumes.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                ManejoErrores.mostrarToastError(MenuPrincipalActivity.this, t);
            }
        });

        // — Listas —
        servicio.obtenerListasGuardadas(idUsuario).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> resp) {
                if (!resp.isSuccessful() || resp.body() == null) return;
                List<ListaDeReproduccionDTO> listas = parseDatos(resp.body(), typeLista);
                UcContenidoAdapter<ListaDeReproduccionDTO> adapter =
                        new UcContenidoAdapter<>(MenuPrincipalActivity.this, listas, "LISTA", false);
                rvListas.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                ManejoErrores.mostrarToastError(MenuPrincipalActivity.this, t);
            }
        });

        // — Artistas —
        servicio.obtenerArtistasGuardados(idUsuario).enqueue(new Callback<RespuestaApi<List<BusquedaArtistaDTO>>>() {
            @Override
            public void onResponse(Call<RespuestaApi<List<BusquedaArtistaDTO>>> call, Response<RespuestaApi<List<BusquedaArtistaDTO>>> resp) {
                if (!resp.isSuccessful() || resp.body() == null) return;
                List<BusquedaArtistaDTO> artistas = resp.body().getDatos();
                UcContenidoAdapter<BusquedaArtistaDTO> adapter =
                        new UcContenidoAdapter<>(MenuPrincipalActivity.this, artistas, "ARTISTA", false);
                rvArtistas.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<RespuestaApi<List<BusquedaArtistaDTO>>> call, Throwable t) {
                ManejoErrores.mostrarToastError(MenuPrincipalActivity.this, t);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (reproductorHelper != null) reproductorHelper.limpiar();
    }
}
