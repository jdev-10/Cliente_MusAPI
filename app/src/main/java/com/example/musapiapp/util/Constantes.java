// src/main/java/com/example/musapiapp/util/Constantes.java
package com.example.musapiapp.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

public class Constantes {
    //public static final String PUERTO   = "8088";
    //Localhost
    //public static final String URL_BASE = "http://10.0.2.2:"+PUERTO;
    public static final String URL_BASE = "https://commutatively-unstrategic-felipe.ngrok-free.dev:";
    public static final String URL_API  = URL_BASE + "/api/";
/*
    @SuppressLint("StaticFieldLeak")
    public static void CargarImagen(String urlImagen, ImageView imageView) {
        if (urlImagen == null || urlImagen.isEmpty()) {
            return;
        }

        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... voids) {
                try {
                    URL url = new URL(  Constantes.URL_BASE +urlImagen);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    String token = SesionUsuario.getToken();
                    String bearer = token != null ? "Bearer " + token : "";
                    connection.setRequestProperty("Authorization", bearer);
                    connection.setDoInput(true);
                    connection.connect();

                    InputStream input = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(input);
                    input.close();

                    return bitmap;
                } catch (Exception e) {
                    //ivFoto.setImageResource(R.drawable.musapi_logo);
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }.execute();
    }*/
    public static void CargarImagen(String urlImagen, ImageView imageView) {
        // 1. Validaciones básicas
        if (urlImagen == null || urlImagen.isEmpty() || imageView == null) {
            return;
        }

        // 2. Construcción de la URL completa
        String urlCompleta = Constantes.URL_BASE + urlImagen;

        // 3. Configuración de la cabecera con el Token (Igual que en tu ChatActivity)
        LazyHeaders.Builder headersBuilder = new LazyHeaders.Builder();
        String token = SesionUsuario.getToken();

        if (token != null && !token.isEmpty()) {
            headersBuilder.addHeader("Authorization", "Bearer " + token);
        }

        // 4. Creación del objeto GlideUrl
        GlideUrl glideUrl = new GlideUrl(urlCompleta, headersBuilder.build());

        // 5. Carga de la imagen usando el Contexto del propio ImageView
        Glide.with(imageView.getContext())
                .load(glideUrl)
                // Opcional: .placeholder(R.drawable.cargando) // Mientras carga
                // Opcional: .error(R.drawable.error)         // Si falla
                .into(imageView);
    }
}
