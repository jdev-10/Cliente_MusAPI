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

public class Constantes {
    public static final String PUERTO   = "8088";
    // En el emulador 10.0.2.2 apunta a tu localhost
    public static final String URL_BASE = "http://192.168.1.68:"+PUERTO;
    public static final String URL_API  = URL_BASE + "/api/";

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
    }
}
