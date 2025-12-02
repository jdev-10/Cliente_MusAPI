package com.example.musapiapp.activities.perfiles;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musapiapp.R;
import com.example.musapiapp.activities.contenido.ListaDetalleActivity;
import com.example.musapiapp.activities.perfiles.estadisticas.EstadisticasConsumoPersonalActivity;
import com.example.musapiapp.adapters.UcContenidoAdapter;
import com.example.musapiapp.dto.BusquedaAlbumDTO;
import com.example.musapiapp.dto.BusquedaCancionDTO;
import com.example.musapiapp.dto.ListaDeReproduccionDTO;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.servicios.ListaServicio;
import com.example.musapiapp.util.ManejoErrores;
import com.example.musapiapp.util.Reproductor;
import com.example.musapiapp.util.ReproductorUIHelper;
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

public class PerfilUsuarioActivity extends AppCompatActivity {
    private TextView txtNombre, txtUsuario;
    private ImageButton btnVolver;
    private Button btnCrearPerfilArtista, btnVerPerfilArtista, btnVerEstadisticas, btnEditarPerfil;
    private RecyclerView rvListas;
    private ListaServicio listaServicio;
    private Gson gson = new Gson();
    private ReproductorUIHelper reproductorHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil_usuario);

        reproductorHelper = new ReproductorUIHelper(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
            return insets;
        });

        // Bind views
        txtNombre             = findViewById(R.id.txtNombre);
        txtUsuario            = findViewById(R.id.txtUsuario);
        btnVolver             = findViewById(R.id.btnVolver);
        btnCrearPerfilArtista = findViewById(R.id.btnCrearPerfilArtista);
        btnVerPerfilArtista   = findViewById(R.id.btnVerPerfilArtista);
        btnVerEstadisticas    = findViewById(R.id.btnVerEstadisticas);
        btnEditarPerfil       = findViewById(R.id.btnEditarPerfil);
        rvListas              = findViewById(R.id.rvListas);

        listaServicio = ApiCliente.getClient().create(ListaServicio.class);

        rvListas.setLayoutManager(new LinearLayoutManager(this));

        btnVolver.setOnClickListener(v -> finish());

        btnCrearPerfilArtista.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirmación")
                    .setMessage("¿Deseas crear un perfil de artista?\nEsto asociará el perfil a tu cuenta.")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        startActivity(new Intent(this, CrearPerfilArtistaActivity.class));
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        btnVerPerfilArtista.setOnClickListener(v -> {
            Intent i = new Intent(this, PerfilArtistaActivity.class);
            i.putExtra("idArtista", SesionUsuario.getIdUsuario());
            startActivity(i);
        });

        btnVerEstadisticas.setOnClickListener(v -> {
            Intent i = new Intent(this, EstadisticasConsumoPersonalActivity.class);
            i.putExtra("idUsuario", SesionUsuario.getIdUsuario());
            startActivity(i);
        });

        btnEditarPerfil.setOnClickListener(v -> {
            startActivity(new Intent(this, EditarPerfilActivity.class));
        });

        cargarDatos();
        cargarListas();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatos();
        cargarListas();
        if (reproductorHelper != null){
            reproductorHelper.refrescarEstadoActual();
            Reproductor.inicializar(this, reproductorHelper);
        }
    }

    private void cargarDatos() {
        boolean esArtista = SesionUsuario.isArtista();
        btnVerPerfilArtista.setVisibility(esArtista ? View.VISIBLE : View.GONE);
        btnCrearPerfilArtista.setVisibility(esArtista ? View.GONE : View.VISIBLE);

        txtNombre.setText(SesionUsuario.getNombre());
        txtUsuario.setText("@" + SesionUsuario.getNombreUsuario());
    }

    private void cargarListas() {
        int idUsuario = SesionUsuario.getIdUsuario();
        listaServicio.obtenerListasPorUsuario(idUsuario)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call,
                                           Response<JsonObject> response) {

                        if (!response.isSuccessful() || response.body() == null) return;


                        List<ListaDeReproduccionDTO> listas = new Gson().fromJson(
                                response.body().get("datos").getAsJsonArray(),
                                new com.google.gson.reflect.TypeToken<List<ListaDeReproduccionDTO>>() {}.getType()
                        );
                        Log.e("RESPUESTA_JSON", response.body().toString());


                        for (ListaDeReproduccionDTO l: listas) {
                            Log.e("Lista", "Lista: "+l.getNombre());
                        }


                        UcContenidoAdapter<ListaDeReproduccionDTO> adapter =
                                new UcContenidoAdapter<>(PerfilUsuarioActivity.this, listas, "LISTA", true);
                        rvListas.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                        ManejoErrores.mostrarToastError(PerfilUsuarioActivity.this, t);
                    }
                });
    }
}
