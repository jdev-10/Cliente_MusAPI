package com.example.musapiapp.activities.perfiles.estadisticas;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musapiapp.R;
import com.example.musapiapp.dto.EstadisticasContenidoSubidoDTO;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.servicios.EstadisticasServicio;
import com.example.musapiapp.util.ManejoErrores;
import com.example.musapiapp.util.RespuestaApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EstadisticasContenidoSubidoActivity extends AppCompatActivity {

    private int idPerfilArtista = -1;

    private TextView tvTotalOyentes, tvTotalGuardados;
    private Spinner spTipoContenido;
    private Button btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas_contenido_subido);

        idPerfilArtista = getIntent().getIntExtra("idPerfilArtista", -1);
        if (idPerfilArtista == -1) {
            finish();
            return;
        }

        tvTotalOyentes = findViewById(R.id.tvTotalOyentes);
        tvTotalGuardados = findViewById(R.id.tvTotalGuardados);
        spTipoContenido = findViewById(R.id.spTipoContenido);
        btnVolver = findViewById(R.id.btnVolver);

        if (spTipoContenido.getAdapter() == null) {
            String[] opciones = {"Canción", "Álbum"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, opciones);
            spTipoContenido.setAdapter(adapter);
        }

        btnVolver.setOnClickListener(v -> finish());

        spTipoContenido.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                cargarDatos();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });

        cargarDatos();
    }

    private void cargarDatos() {
        String tipoContenido;
        int seleccionado = spTipoContenido.getSelectedItemPosition();
        if (seleccionado == 0) {
            tipoContenido = "Cancion";
        } else if (seleccionado == 1) {
            tipoContenido = "Album";
        } else {
            return;
        }

        EstadisticasServicio servicio = ApiCliente.getClient().create(EstadisticasServicio.class);
        servicio.obtenerEstadisticasContenidoSubido(idPerfilArtista, tipoContenido)
                .enqueue(new Callback<RespuestaApi<EstadisticasContenidoSubidoDTO>>() {
                    @Override
                    public void onResponse(Call<RespuestaApi<EstadisticasContenidoSubidoDTO>> call,
                                           Response<RespuestaApi<EstadisticasContenidoSubidoDTO>> response) {
                        if (!response.isSuccessful() || response.body() == null) return;

                        EstadisticasContenidoSubidoDTO dto = response.body().getDatos();
                        if (dto == null) return;

                        tvTotalOyentes.setText("Total de oyentes registrados: " + dto.getNumeroOyentes());
                        tvTotalGuardados.setText("Veces que se han guardado: " + dto.getNumeroGuardados());
                    }

                    @Override
                    public void onFailure(Call<RespuestaApi<EstadisticasContenidoSubidoDTO>> call, Throwable t) {
                        ManejoErrores.mostrarToastError(EstadisticasContenidoSubidoActivity.this, t);
                    }
                });
    }
}
