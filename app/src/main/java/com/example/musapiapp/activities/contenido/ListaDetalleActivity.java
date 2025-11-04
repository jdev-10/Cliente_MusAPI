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

import com.example.musapiapp.R;
import com.example.musapiapp.adapters.UcContenidoAdapter;
import com.example.musapiapp.dto.BusquedaAlbumDTO;
import com.example.musapiapp.dto.BusquedaCancionDTO;
import com.example.musapiapp.dto.ContenidoGuardadoDTO;
import com.example.musapiapp.dto.ListaDeReproduccionDTO;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.servicios.ContenidoGuardadoServicio;
import com.example.musapiapp.util.Constantes;
import com.example.musapiapp.util.ManejoErrores;
import com.example.musapiapp.util.Reproductor;
import com.example.musapiapp.util.ReproductorUIHelper;
import com.example.musapiapp.util.SesionUsuario;
import com.google.gson.Gson;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListaDetalleActivity extends AppCompatActivity {

    private ListaDeReproduccionDTO lista;
    private boolean mostrarBotonGuardar = true;
    private boolean mostrarBotonEliminar = false;

    private ImageView imgPortada;
    private TextView txtTitulo, txtDescripcion, txtAutor, txtDuracion;
    private Button btnCerrar, btnGuardar, btnEditar;
    private RecyclerView rvCanciones;

    private ReproductorUIHelper reproductorHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_detalle);

        reproductorHelper = new ReproductorUIHelper(this);
        inicializarUI();

        lista = new Gson().fromJson(getIntent().getStringExtra("lista"), ListaDeReproduccionDTO.class);


        cargarDatos();

        btnCerrar.setOnClickListener(v -> finish());


        btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(this, CrearListaActivity.class);
            intent.putExtra("lista", new Gson().toJson(lista));
            startActivity(intent);
        });
    }

    private void inicializarUI() {
        imgPortada = findViewById(R.id.imgPortada);
        txtTitulo = findViewById(R.id.txtTitulo);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        txtAutor = findViewById(R.id.txtAutor);
        txtDuracion = findViewById(R.id.txtDuracion);
        rvCanciones = findViewById(R.id.rvCanciones);
        rvCanciones.setLayoutManager(new LinearLayoutManager(this));
        btnCerrar = findViewById(R.id.btnCerrar);
        btnEditar = findViewById(R.id.btnEditarLista);
    }

    private void cargarDatos() {
        if (lista == null) return;
        Log.e("Lista detalles", "Lista "+ lista.getNombre()+" con foto "+ lista.getUrlFoto());
        txtTitulo.setText(lista.getNombre());
        txtDescripcion.setText(lista.getDescripcion());


        cargarImagen(lista.getUrlFoto(), imgPortada);

        if (lista.getCanciones() != null) {
            mostrarCanciones(lista.getCanciones());
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
            } catch (Exception ignored) {}
        }

        long h = totalSegundos / 3600;
        long m = (totalSegundos % 3600) / 60;
        long s = totalSegundos % 60;
        String duracion = String.format("%02d:%02d:%02d", h, m, s);
        //txtDuracion.setText("Duración: " + duracion);
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
                    return BitmapFactory.decodeStream(input);
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
        cargarDatos();

        if (reproductorHelper != null) {
            reproductorHelper.refrescarEstadoActual();
            Reproductor.inicializar(this, reproductorHelper);
        }
    }
}
