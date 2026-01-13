// src/main/java/com/example/musapiapp/util/Constantes.java
package com.example.musapiapp.util;

import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

public class Constantes {
    //public static final String PUERTO   = "8088";
    //Localhost
    //public static final String URL_BASE = "http://10.0.2.2:"+PUERTO;
    public static final String URL_BASE = "https://commutatively-unstrategic-felipe.ngrok-free.dev";
    public static final String URL_API  = URL_BASE + "/api/";
    public static final String URL_WS   = "wss://commutatively-unstrategic-felipe.ngrok-free.dev/ws/";
    public static void CargarImagen(String urlImagen, ImageView imageView) {
        if (urlImagen == null || urlImagen.isEmpty() || imageView == null) {
            return;
        }

        String urlCompleta = Constantes.URL_BASE + urlImagen;

        LazyHeaders.Builder headersBuilder = new LazyHeaders.Builder();
        String token = SesionUsuario.getToken();

        if (token != null && !token.isEmpty()) {
            headersBuilder.addHeader("Authorization", "Bearer " + token);
        }

        GlideUrl glideUrl = new GlideUrl(urlCompleta, headersBuilder.build());

        Glide.with(imageView.getContext())
                .load(glideUrl)
                .into(imageView);
    }
}
