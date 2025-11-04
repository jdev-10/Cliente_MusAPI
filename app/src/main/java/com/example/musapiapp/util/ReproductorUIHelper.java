package com.example.musapiapp.util;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.musapiapp.R;
import com.example.musapiapp.activities.contenido.ReproductorActivity;
import com.example.musapiapp.dto.BusquedaCancionDTO;

public class ReproductorUIHelper implements Reproductor.ReproductorListener {

    private final Activity activity;
    private TextView txtMiniTitulo;
    private ImageButton btnMiniPlayPause;
    private LinearLayout barraReproductor;


    public ReproductorUIHelper(Activity activity) {
        this.activity = activity;
        inicializar();
    }

    private void inicializar() {
        txtMiniTitulo = activity.findViewById(R.id.txtMiniTitulo);
        btnMiniPlayPause = activity.findViewById(R.id.btnMiniPlayPause);
        barraReproductor = activity.findViewById(R.id.barraReproductor);


        if (txtMiniTitulo == null || btnMiniPlayPause == null) return;

        Reproductor.inicializar(activity, this);

        btnMiniPlayPause.setOnClickListener(v -> Reproductor.pausarReanudar());

        // Mostrar estado actual si ya hay reproducción
        BusquedaCancionDTO actual = Reproductor.getCancionActual();
        barraReproductor.setOnClickListener(v -> {
            Log.d("Reproductor", "debería abrir el reproductor grande");
            activity.startActivity(new Intent(activity, ReproductorActivity.class));
        });
        if (actual != null) {
            txtMiniTitulo.setText(actual.getNombre() + " - " + actual.getNombreArtista());
            btnMiniPlayPause.setImageResource(Reproductor.estaReproduciendo()
                    ? R.drawable.ic_pause : R.drawable.ic_play);
            barraReproductor.setOnClickListener(v -> {
                Log.d("Reproductor", "debería abrir el reproductor grande");
                activity.startActivity(new Intent(activity, ReproductorActivity.class));
            });

        } else {
            txtMiniTitulo.setText("Sin reproducción");
            btnMiniPlayPause.setImageResource(R.drawable.ic_play);
        }
    }

    @Override
    public void onReproduccionIniciada() {
        activity.runOnUiThread(() -> {
            BusquedaCancionDTO actual = Reproductor.getCancionActual();
            if (actual != null && txtMiniTitulo != null) {
                txtMiniTitulo.setText(actual.getNombre() + " - " + actual.getNombreArtista());
            }
            if (btnMiniPlayPause != null) {
                btnMiniPlayPause.setImageResource(R.drawable.ic_pause);
            }
        });
    }

    @Override
    public void onReproductorPausado() {
        Log.d("Reproductor", "Cancion pausada, el reproductor de abajo deberia pausarse");

        activity.runOnUiThread(() -> {
            if (btnMiniPlayPause != null) {
                btnMiniPlayPause.setImageResource(R.drawable.ic_play);
            }
        });
    }

    @Override
    public void onReproduccionFinalizada() {
        Log.d("Reproductor", "Cancion detenida, el reproductor de abajo deberia pausarse");
        activity.runOnUiThread(() -> {
            if (btnMiniPlayPause != null) {
                btnMiniPlayPause.setImageResource(R.drawable.ic_play);
            }
        });
    }

    @Override
    public void onReproduccionReanudada() {
        Log.d("Reproductor", "Cancion reanudada, el reproductor de abajo deberia play");
        activity.runOnUiThread(() -> {
            if (btnMiniPlayPause != null) {
                btnMiniPlayPause.setImageResource(R.drawable.ic_pause);
            }
        });
    }

    public void limpiar() {
        Reproductor.removerListener(this);
    }

    public void refrescarEstadoActual() {
        BusquedaCancionDTO actual = Reproductor.getCancionActual();

        if (actual != null) {
            txtMiniTitulo.setText(actual.getNombre() + " - " + actual.getNombreArtista());
            btnMiniPlayPause.setImageResource(Reproductor.estaReproduciendo()
                    ? R.drawable.ic_pause : R.drawable.ic_play);
        } else {
            txtMiniTitulo.setText("Sin reproducción");
            btnMiniPlayPause.setImageResource(R.drawable.ic_play);
        }
    }

}
