package com.example.musapiapp.activities.contenido;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musapiapp.R;
import com.example.musapiapp.adapters.UcContenidoAdapter;
import com.example.musapiapp.dto.*;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.servicios.AlbumServicio;
import com.example.musapiapp.servicios.CancionServicio;
import com.example.musapiapp.util.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlbumDetalleActivity extends AppCompatActivity {

    private BusquedaAlbumDTO albumPublico;
    private InfoAlbumDTO albumPendiente;
    private int idArtista = -1;
    private boolean mostrarBotonGuardar = true;
    private ReproductorUIHelper reproductorHelper;
    private List<BusquedaCancionDTO> listaCanciones;

    private ImageView imgPortada;
    private TextView txtTitulo, txtAutor, txtFecha;
    private RecyclerView rvCanciones;
    private Button btnAgregarCancion, btnCerrar, btnPublicar, btnEditar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detalle);

        reproductorHelper = new ReproductorUIHelper(this);
        inicializarUI();

        if (getIntent().hasExtra("albumPublico")) {
            albumPublico = new Gson().fromJson(getIntent().getStringExtra("albumPublico"), BusquedaAlbumDTO.class);
            cargarInformacionAlbumPublico();
        } else if (getIntent().hasExtra("albumPendiente")) {
            albumPendiente = new Gson().fromJson(getIntent().getStringExtra("albumPendiente"), InfoAlbumDTO.class);
            idArtista = getIntent().getIntExtra("idArtista", -1);
            cargarInformacionAlbumPendiente();
        }

        btnCerrar.setOnClickListener(v -> finish());
        btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(this, CrearAlbumActivity.class);
            intent.putExtra("album", new Gson().toJson(albumPublico));
            startActivity(intent);
        });
    }

    private void inicializarUI() {
        imgPortada = findViewById(R.id.imgPortada);
        txtTitulo = findViewById(R.id.txtTitulo);
        txtAutor = findViewById(R.id.txtAutor);
        txtFecha = findViewById(R.id.txtFecha);
        rvCanciones = findViewById(R.id.rvCanciones);
        rvCanciones.setLayoutManager(new LinearLayoutManager(this));
        btnAgregarCancion = findViewById(R.id.btnAgregarCancion);
        btnCerrar = findViewById(R.id.btnCerrar);
        btnEditar = findViewById(R.id.btnEditarAlbum);
        btnPublicar = findViewById(R.id.btnPublicarAlbum);
    }

    private void cargarInformacionAlbumPublico() {
        txtTitulo.setText(albumPublico.getNombreAlbum());
        txtAutor.setText(albumPublico.getNombreArtista());
        txtFecha.setText(albumPublico.getFechaPublicacion());
        cargarImagen(albumPublico.getUrlFoto(), imgPortada);

        cargarCanciones(albumPublico.getCanciones());
        btnAgregarCancion.setVisibility(View.GONE);
        btnPublicar.setVisibility(View.GONE);
        btnEditar.setVisibility(View.GONE);
        if ((albumPublico.getNombreArtista().equals(SesionUsuario.getNombreUsuario()) || albumPublico.getNombreArtista().equals(SesionUsuario.getNombre())) && albumPublico != null){
            btnEditar.setVisibility(View.VISIBLE);
        }
    }

    private void cargarInformacionAlbumPendiente() {
        txtTitulo.setText(albumPendiente.getNombre());
        txtAutor.setText(albumPendiente.getNombreArtista());
        txtFecha.setText("Pendiente");
        cargarImagen(albumPendiente.getUrlFoto(), imgPortada);

        btnEditar.setVisibility(View.GONE);
        btnAgregarCancion.setVisibility(View.VISIBLE);
        btnAgregarCancion.setOnClickListener(v -> {
            Intent intent = new Intent(this, SubirCancionAlbumActivity.class);
            intent.putExtra("idAlbum", albumPendiente.getIdAlbum());
            intent.putExtra("idPerfilArtista", idArtista);
            startActivity(intent);
        });

        btnPublicar.setVisibility(View.VISIBLE);
        btnPublicar.setOnClickListener(v -> publicarAlbum());

        cargarCanciones(null); // desde servicio
    }

    private void publicarAlbum() {
        if (listaCanciones != null && listaCanciones.size() >= 2) {
            AlbumServicio servicio = ApiCliente.getClient().create(AlbumServicio.class);
            servicio.publicarAlbum(albumPendiente.getIdAlbum())
                    .enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(AlbumDetalleActivity.this, "Álbum publicado exitosamente", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(AlbumDetalleActivity.this, "Error al publicar álbum", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            ManejoErrores.mostrarToastError(AlbumDetalleActivity.this, t);
                        }
                    });
        } else {
            Toast.makeText(this, "Se requieren mínimo 2 canciones", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarCanciones(List<BusquedaCancionDTO> canciones) {
        if (canciones == null && albumPendiente != null) {
            CancionServicio servicio = ApiCliente.getClient().create(CancionServicio.class);
            servicio.obtenerCancionesPorAlbum(albumPendiente.getIdAlbum())
                    .enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                JsonArray datos = response.body().getAsJsonArray("datos");
                                listaCanciones = new Gson().fromJson(datos, new TypeToken<List<BusquedaCancionDTO>>() {}.getType());
                                mostrarCanciones(listaCanciones);
                            } else {
                                Toast.makeText(AlbumDetalleActivity.this, "Error al cargar canciones", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            ManejoErrores.mostrarToastError(AlbumDetalleActivity.this, t);
                        }
                    });
        } else if (canciones != null) {
            mostrarCanciones(canciones);
        }
    }

    private void mostrarCanciones(List<BusquedaCancionDTO> canciones) {
        UcContenidoAdapter<BusquedaCancionDTO> adapter = new UcContenidoAdapter<>(this, canciones, "CANCION", mostrarBotonGuardar);
        adapter.setListaCanciones(canciones);
        rvCanciones.setAdapter(adapter);

        // Calcular duración total
        long totalSegundos = 0;
        for (BusquedaCancionDTO c : canciones) {
            try {
                String[] partes = c.getDuracion().split(":");
                int min = Integer.parseInt(partes[0]);
                int seg = Integer.parseInt(partes[1]);
                totalSegundos += min * 60 + seg;
            } catch (Exception ignored) { }
        }

        long h = totalSegundos / 3600;
        long m = (totalSegundos % 3600) / 60;
        long s = totalSegundos % 60;
        String duracion = String.format("%02d:%02d:%02d", h, m, s);
        // Si tienes un TextView: txtDuracion.setText(duracion);
    }

    @SuppressLint("StaticFieldLeak")
    private void cargarImagen(String urlImagen, ImageView imageView) {
        if (urlImagen == null || urlImagen.isEmpty()) return;

        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... voids) {
                try {
                    URL url = new URL(Constantes.URL_BASE + urlImagen);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    String token = SesionUsuario.getToken();
                    con.setRequestProperty("Authorization", "Bearer " + token);
                    con.setDoInput(true);
                    con.connect();
                    InputStream input = con.getInputStream();
                    Bitmap bmp = BitmapFactory.decodeStream(input);
                    input.close();
                    return bmp;
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bmp) {
                if (bmp != null) {
                    imageView.setImageBitmap(bmp);
                }
            }
        }.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (albumPendiente != null) {
            cargarCanciones(null); // recargar si estamos en álbum pendiente
        }
        if (reproductorHelper != null) {
            reproductorHelper.refrescarEstadoActual();
            Reproductor.inicializar(this, reproductorHelper);
        }
    }
}
