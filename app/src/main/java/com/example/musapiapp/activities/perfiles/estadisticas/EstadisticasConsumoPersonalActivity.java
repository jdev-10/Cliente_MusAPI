package com.example.musapiapp.activities.perfiles.estadisticas;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musapiapp.R;
import com.example.musapiapp.dto.EstadisticasPersonalesDTO;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.servicios.EstadisticasServicio;
import com.example.musapiapp.util.ManejoErrores;
import com.example.musapiapp.util.RespuestaApi;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EstadisticasConsumoPersonalActivity extends AppCompatActivity {

    private int idUsuario;
    private TextView tvArtistas, tvCanciones, tvTiempo, tvFechaInicio, tvFechaFin;
    private Button btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas_consumo_personal);

        idUsuario = getIntent().getIntExtra("idUsuario", -1);

        tvArtistas = findViewById(R.id.tvArtistas);
        tvCanciones = findViewById(R.id.tvCanciones);
        tvTiempo = findViewById(R.id.tvTiempo);
        tvFechaInicio = findViewById(R.id.tvFechaInicio);
        tvFechaFin = findViewById(R.id.tvFechaFin);
        btnVolver = findViewById(R.id.btnVolver);

        btnVolver.setOnClickListener(v -> finish());

        tvFechaInicio.setOnClickListener(v -> mostrarDialogoFecha(tvFechaInicio));
        tvFechaFin.setOnClickListener(v -> mostrarDialogoFecha(tvFechaFin));
    }

    private void mostrarDialogoFecha(TextView textView) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
            textView.setText(fecha);
            cargarDatos();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void cargarDatos() {
        String fechaInicio = getFecha(tvFechaInicio);
        String fechaFin = getFecha(tvFechaFin);

        if (fechaInicio == null || fechaFin == null) return;

        EstadisticasServicio servicio = ApiCliente.getClient().create(EstadisticasServicio.class);
        servicio.obtenerEstadisticasPersonales(idUsuario, fechaInicio, fechaFin).enqueue(new Callback<RespuestaApi<EstadisticasPersonalesDTO>>() {
            @Override
            public void onResponse(Call<RespuestaApi<EstadisticasPersonalesDTO>> call, Response<RespuestaApi<EstadisticasPersonalesDTO>> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().getDatos() == null) return;

                EstadisticasPersonalesDTO dto = response.body().getDatos();

                // Artistas
                if (!dto.getTopArtistas().isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    int i = 1;
                    for (String artista : dto.getTopArtistas()) {
                        sb.append(i++).append(". ").append(artista).append("\n");
                    }
                    tvArtistas.setText(sb.toString().trim());
                } else {
                    tvArtistas.setText("Ningún artista registrado");
                }

                // Canciones
                if (!dto.getTopCanciones().isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    int i = 1;
                    for (String cancion : dto.getTopCanciones()) {
                        sb.append(i++).append(". ").append(cancion).append("\n");
                    }
                    tvCanciones.setText(sb.toString().trim());
                } else {
                    tvCanciones.setText("Ninguna canción registrada");
                }

                // Tiempo
                long segundos = dto.getSegundosEscuchados();
                long min = segundos / 60;
                long seg = segundos % 60;
                tvTiempo.setText(min + " minutos y " + seg + " segundos");
            }

            @Override
            public void onFailure(Call<RespuestaApi<EstadisticasPersonalesDTO>> call, Throwable t) {
                ManejoErrores.mostrarToastError(EstadisticasConsumoPersonalActivity.this, t);
            }
        });
    }

    private String getFecha(TextView tv) {
        String f = tv.getText().toString().trim();
        return f.toLowerCase().contains("seleccionar") ? null : f;
    }
}
