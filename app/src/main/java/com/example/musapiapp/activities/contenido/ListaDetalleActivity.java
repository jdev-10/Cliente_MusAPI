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
import com.example.musapiapp.dto.BusquedaCancionDTO;
import com.example.musapiapp.dto.ListaDeReproduccionDTO;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.util.Constantes;
import com.example.musapiapp.util.Reproductor;
import com.example.musapiapp.util.ReproductorUIHelper;
import com.example.musapiapp.util.SesionUsuario;
import com.google.gson.Gson;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ListaDetalleActivity extends AppCompatActivity {

    private ListaDeReproduccionDTO lista;
    private boolean mostrarBotonGuardar = true;
    
    private ImageView imgPortada;
    private TextView txtTitulo, txtDescripcion, txtAutor, txtDuracion;
    private RecyclerView rvCanciones;
    
    private ImageButton btnCerrar; 
    private Button btnEditar;

    private ReproductorUIHelper reproductorHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_detalle);

        reproductorHelper = new ReproductorUIHelper(this);
        inicializarUI();

        String listaJson = getIntent().getStringExtra("lista");
        if (listaJson != null) {
            lista = new Gson().fromJson(listaJson, ListaDeReproduccionDTO.class);
            cargarDatos();
        }

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
    }

    // Mantenemos tu lógica de carga de imagen con AsyncTask tal cual la tenías
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
        // Recargar datos por si se editó la lista en la otra pantalla
        // Nota: Idealmente deberías recargar la lista de la API aquí, 
        // pero mantenemos tu lógica actual de cargarDatos() con el objeto local.
        cargarDatos(); 

        if (reproductorHelper != null) {
            reproductorHelper.refrescarEstadoActual();
            Reproductor.inicializar(this, reproductorHelper);
        }
    }
}
