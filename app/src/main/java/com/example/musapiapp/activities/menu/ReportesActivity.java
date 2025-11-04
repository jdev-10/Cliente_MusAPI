package com.example.musapiapp.activities.menu;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musapiapp.R;
import com.example.musapiapp.dto.*;
import com.example.musapiapp.servicios.EstadisticasServicio;
import com.example.musapiapp.util.ManejoErrores;
import com.example.musapiapp.util.RespuestaApi;
import com.example.musapiapp.network.ApiCliente;

import java.text.SimpleDateFormat;
import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportesActivity extends AppCompatActivity {

    private Button btnVolver;
    private Spinner spTipoReporte;
    private LinearLayout layoutUsuarios, layoutTop;
    private TextView tvTotalUsuarios, tvTotalArtistas, tvTotalOyentes, tvTopResultados, tvTituloTop;
    private TextView tvFechaInicio, tvFechaFin;

    private EstadisticasServicio servicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes);

        btnVolver = findViewById(R.id.btnVolver);
        spTipoReporte = findViewById(R.id.spTipoReporte);
        layoutUsuarios = findViewById(R.id.layoutUsuarios);
        layoutTop = findViewById(R.id.layoutTop);
        tvTotalUsuarios = findViewById(R.id.tvTotalUsuarios);
        tvTotalArtistas = findViewById(R.id.tvTotalArtistas);
        tvTotalOyentes = findViewById(R.id.tvTotalOyentes);
        tvTopResultados = findViewById(R.id.tvTopResultados);
        tvTituloTop = findViewById(R.id.tvTituloTop);
        tvFechaInicio = findViewById(R.id.tvFechaInicio);
        tvFechaFin = findViewById(R.id.tvFechaFin);

        tvFechaInicio.setOnClickListener(v -> mostrarDialogoFecha(tvFechaInicio));
        tvFechaFin.setOnClickListener(v -> mostrarDialogoFecha(tvFechaFin));

        servicio = ApiCliente.getClient().create(EstadisticasServicio.class);

        btnVolver.setOnClickListener(v -> finish());

        spTipoReporte.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cargarDatos();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void mostrarDialogoFecha(TextView textView) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.getTime());
            textView.setText(fecha);
            cargarDatos();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void cargarDatos() {
        switch (spTipoReporte.getSelectedItemPosition()) {
            case 0:
                layoutUsuarios.setVisibility(View.VISIBLE);
                layoutTop.setVisibility(View.GONE);
                cargarUsuarios();
                break;
            case 1:
                tvTituloTop.setText("Canciones más escuchadas");
                layoutUsuarios.setVisibility(View.GONE);
                layoutTop.setVisibility(View.VISIBLE);
                cargarCanciones();
                break;
            case 2:
                tvTituloTop.setText("Artistas más escuchados");
                layoutUsuarios.setVisibility(View.GONE);
                layoutTop.setVisibility(View.VISIBLE);
                cargarArtistas();
                break;
        }
    }

    private void cargarUsuarios() {
        servicio.obtenerEstadisticasUsuarios().enqueue(new Callback<RespuestaApi<EstadisticasNumeroUsuariosDTO>>() {
            @Override
            public void onResponse(Call<RespuestaApi<EstadisticasNumeroUsuariosDTO>> call,
                                   Response<RespuestaApi<EstadisticasNumeroUsuariosDTO>> resp) {
                if (!resp.isSuccessful() || resp.body() == null) return;
                EstadisticasNumeroUsuariosDTO dto = resp.body().getDatos();
                if (dto == null) return;

                tvTotalArtistas.setText("Usuarios Artistas: " + dto.getTotalArtistas());
                tvTotalOyentes.setText("Usuarios no Artistas: " + dto.getTotalUsuarios());
                tvTotalUsuarios.setText("Usuarios: " + (dto.getTotalUsuarios() + dto.getTotalArtistas()));
            }

            @Override
            public void onFailure(Call<RespuestaApi<EstadisticasNumeroUsuariosDTO>> call, Throwable t) {
                ManejoErrores.mostrarToastError(ReportesActivity.this, t);
            }
        });
    }

    private void cargarArtistas() {
        String fechaInicio = getFechaFormateada(tvFechaInicio);
        String fechaFin = getFechaFormateada(tvFechaFin);

        if (fechaInicio == null || fechaFin == null) {
            tvTopResultados.setText("No hay artistas escuchados en el periodo seleccionado.");
            return;
        }

        servicio.obtenerEstadisticasArtistas(fechaInicio, fechaFin)
                .enqueue(new Callback<RespuestaApi<List<ArtistaMasEscuchadoDTO>>>() {
                    @Override
                    public void onResponse(Call<RespuestaApi<List<ArtistaMasEscuchadoDTO>>> call,
                                           Response<RespuestaApi<List<ArtistaMasEscuchadoDTO>>> resp) {
                        if (!resp.isSuccessful() || resp.body() == null) return;
                        List<ArtistaMasEscuchadoDTO> lista = resp.body().getDatos();
                        if (lista == null || lista.isEmpty()) {
                            tvTopResultados.setText("No hay artistas escuchados en el periodo seleccionado.");
                            return;
                        }

                        StringBuilder builder = new StringBuilder();
                        int pos = 1;
                        for (ArtistaMasEscuchadoDTO dto : lista) {
                            long min = dto.getSegundosEscuchados() / 60;
                            long seg = dto.getSegundosEscuchados() % 60;
                            builder.append(pos++)
                                    .append(". ").append(dto.getNombreArtista())
                                    .append(" - ").append(min).append("m ").append(seg).append("s\n\n");
                        }
                        tvTopResultados.setText(builder.toString());
                    }

                    @Override
                    public void onFailure(Call<RespuestaApi<List<ArtistaMasEscuchadoDTO>>> call, Throwable t) {
                        ManejoErrores.mostrarToastError(ReportesActivity.this, t);
                    }
                });
    }

    private void cargarCanciones() {
        String fechaInicio = getFechaFormateada(tvFechaInicio);
        String fechaFin = getFechaFormateada(tvFechaFin);

        if (fechaInicio == null || fechaFin == null) {
            tvTopResultados.setText("No hay canciones escuchadas en el periodo seleccionado.");
            return;
        }

        servicio.obtenerEstadisticasCanciones(fechaInicio, fechaFin)
                .enqueue(new Callback<RespuestaApi<List<CancionMasEscuchadaDTO>>>() {
                    @Override
                    public void onResponse(Call<RespuestaApi<List<CancionMasEscuchadaDTO>>> call,
                                           Response<RespuestaApi<List<CancionMasEscuchadaDTO>>> resp) {
                        if (!resp.isSuccessful() || resp.body() == null) return;
                        List<CancionMasEscuchadaDTO> lista = resp.body().getDatos();
                        if (lista == null || lista.isEmpty()) {
                            tvTopResultados.setText("No hay canciones escuchadas en el periodo seleccionado.");
                            return;
                        }

                        StringBuilder builder = new StringBuilder();
                        int pos = 1;
                        for (CancionMasEscuchadaDTO dto : lista) {
                            long min = dto.getSegundosEscuchados() / 60;
                            long seg = dto.getSegundosEscuchados() % 60;
                            builder.append(pos++)
                                    .append(". ").append(dto.getNombreCancion())
                                    .append(" - ").append(min).append("m ").append(seg).append("s\n")
                                    .append(dto.getNombreUsuarioArtista()).append("\n\n");
                        }
                        tvTopResultados.setText(builder.toString());
                    }

                    @Override
                    public void onFailure(Call<RespuestaApi<List<CancionMasEscuchadaDTO>>> call, Throwable t) {
                        ManejoErrores.mostrarToastError(ReportesActivity.this, t);
                    }
                });
    }

    private String getFechaFormateada(TextView textView) {
        String texto = textView.getText().toString().trim();
        if (texto.toLowerCase().contains("seleccionar") || texto.isEmpty()) {
            return null;
        }
        return texto;
    }
}
