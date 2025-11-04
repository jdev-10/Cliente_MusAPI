package com.example.musapiapp.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import com.example.musapiapp.activities.menu.CategoriaMusicalFormActivity;
import com.example.musapiapp.dto.BusquedaCancionDTO;
import com.example.musapiapp.dto.EscuchaDTO;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.servicios.CancionServicio;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Reproductor {
    private static MediaPlayer mediaPlayer;
    private static ArrayList<BusquedaCancionDTO> listaCanciones;
    private static int indiceActual = 0;
    private static float volumenActual = 1.0f;
    private static Context contexto;
    //Registro de escuchas
    private static int segundosReproducidos = 0;
    private static boolean escuchaEsRegistrable = false;
    private static int segundosParaRegistrarEscucha = 10;
    private static int idCancionActual = 0;

    private static android.os.Handler handler;
    private static Runnable runnable;



    public interface ReproductorListener {
        void onReproduccionIniciada();
        void onReproductorPausado();
        void onReproduccionFinalizada();
        void onReproduccionReanudada();
    }

    private static ReproductorListener listener;

    public static BusquedaCancionDTO getCancionActual() {
        if (listaCanciones != null && indiceActual >= 0 && indiceActual < listaCanciones.size()) {
            return listaCanciones.get(indiceActual);
        }
        return null;
    }


    public static void inicializar(Context ctx, ReproductorListener l) {
        contexto = ctx;
        listener = l;
    }

    // Métodos por implementar...

    public static void reproducirCancion(ArrayList<BusquedaCancionDTO> canciones, int indice, Context ctx) {
        contexto = ctx;

        // Si el contexto también es un listener, lo usamos automáticamente
        if (ctx instanceof ReproductorListener) {
            listener = (ReproductorListener) ctx;
        }

        reproducirCancion(canciones, indice);
    }
    public static void reproducirCancion(ArrayList<BusquedaCancionDTO> canciones, int indice) {
        if (canciones == null || canciones.size() == 0 || indice < 0 || indice >= canciones.size()) return;

        listaCanciones = canciones;
        indiceActual = indice;

        new Thread(() -> {
            try {
                if (mediaPlayer != null) {
                    detener(); // 🔒 asegurar que no se solapen
                }

                String token = SesionUsuario.getToken();
                String bearer = token != null ? "Bearer " + token : "";

                BusquedaCancionDTO cancion = canciones.get(indice);
                String urlCompleta = Constantes.URL_BASE + cancion.getUrlArchivo();

                URL cancionUrl = new URL(urlCompleta);
                HttpURLConnection connection = (HttpURLConnection) cancionUrl.openConnection();
                connection.setRequestProperty("Authorization", bearer);
                connection.setRequestMethod("GET");
                connection.connect();

                File archivoTemp = File.createTempFile("cancion_temp", ".mp3", contexto.getCacheDir());
                archivoTemp.deleteOnExit();

                try (InputStream input = connection.getInputStream();
                     FileOutputStream output = new FileOutputStream(archivoTemp)) {

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = input.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                }

                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(archivoTemp.getAbsolutePath());
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setVolume(volumenActual, volumenActual);

                mediaPlayer.setOnPreparedListener(mp -> {
                    mp.start();
                    idCancionActual = cancion.getIdCancion();
                    iniciarConteoEscucha();
                    if (listener != null) listener.onReproduccionIniciada();
                });

                mediaPlayer.setOnCompletionListener(mp -> {
                    registrarEscucha();
                    detenerConteoEscucha();

                    if (listener != null) listener.onReproduccionFinalizada();

                    if (indiceActual + 1 < listaCanciones.size()) {
                        indiceActual++;
                        reproducirCancion(listaCanciones, indiceActual);
                    }
                });

                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }



    public static void pausarReanudar() {
        if (mediaPlayer == null) return;

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            if (listener != null) listener.onReproductorPausado();
        } else {
            mediaPlayer.start();
            if (listener != null) listener.onReproduccionReanudada();
        }
    }

    public static void detener() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            registrarEscucha();
            detenerConteoEscucha();

            if (listener != null) listener.onReproductorPausado();
        }
    }

    public static void siguienteCancion() {
        if (indiceActual + 1 < listaCanciones.size()) {
            indiceActual++;
            reproducirCancion(listaCanciones, indiceActual);
        }
    }

    public static void cancionAnterior() {
        if (indiceActual > 0) {
            indiceActual--;
            reproducirCancion(listaCanciones, indiceActual);
        }
    }

    public static void setVolumen(float volumen) {
        volumenActual = Math.max(0f, Math.min(1f, volumen));
        if (mediaPlayer != null)
            mediaPlayer.setVolume(volumenActual, volumenActual);
    }

    public static boolean estaReproduciendo() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public static int getDuracion() {
        return mediaPlayer != null ? mediaPlayer.getDuration() : 0;
    }

    public static int getPosicion() {
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
    }

    public static void seekTo(int milisegundos) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(milisegundos);
        }
    }

    public static int getIndiceActual() {
        return indiceActual;
    }

    public static List<BusquedaCancionDTO> getListaCanciones() {
        return listaCanciones;
    }

    public static void removerListener(ReproductorListener l) {
        if (listener == l) {
            listener = null;
        }
    }


    private static void iniciarConteoEscucha() {
        detenerConteoEscucha();
        segundosReproducidos = 0;
        escuchaEsRegistrable = false;
        Log.e("Reproductor", "la escucha NO es registrable");
        handler = new android.os.Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                segundosReproducidos++;
                if (segundosReproducidos == segundosParaRegistrarEscucha) {
                    Log.e("Reproductor", "la escucha es registrable con "+listaCanciones.get(indiceActual).getDuracion()+" segundos");
                    escuchaEsRegistrable = true;
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    private static void detenerConteoEscucha() {
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
            handler = null;
            runnable = null;
        }
    }

    private static void registrarEscucha() {
        Log.e("Reproductor", "Se intenta registrar escucha");
        if (!escuchaEsRegistrable || idCancionActual == 0 || SesionUsuario.getIdUsuario() == 0) return;
        Log.e("Reproductor", "la escucha sí es registrable");
        escuchaEsRegistrable = false;

        EscuchaDTO escucha = new EscuchaDTO(
                SesionUsuario.getIdUsuario(),
                idCancionActual,
                segundosReproducidos
        );

        CancionServicio servicio = ApiCliente.getClient().create(CancionServicio.class);
        //servicio.registrarEscucha(escucha);
        Call<JsonObject> llamada = servicio.registrarEscucha(escucha);
        Log.e("Reproductor", "se intenta la llamada");
        llamada.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.isSuccessful()) {
                    Log.e("Reproductor", "la escucha es registrada!");
                } else {
                    Toast.makeText(contexto, "Error al registrar la categoría", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(contexto, "Error de red", Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", t.getMessage(), t);
            }
        });
    }


}