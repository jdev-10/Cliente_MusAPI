package com.example.musapiapp.activities.perfiles.chat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.musapiapp.R;
import com.example.musapiapp.dto.BusquedaArtistaDTO;
import com.example.musapiapp.dto.ChatMessageDTO;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.util.Constantes;
import com.example.musapiapp.util.ManejoErrores;
import com.example.musapiapp.util.SesionUsuario;
import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class ChatActivity extends AppCompatActivity {

    private WebSocketClient webSocketClient;
    private final Gson gson = new Gson();

    private EditText inputMensaje;
    private LinearLayout mensajesLayout;
    private ScrollView scrollMensajes;
    private TextView txtNombreChat;
    private ImageView imgPerfil;

    private int idPerfilArtista;
    private String nombreUsuario;
    private BusquedaArtistaDTO artista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        artista = getIntent().getParcelableExtra("artista");
        if (artista == null) {
            finish();
            return;
        }

        idPerfilArtista = artista.getIdArtista();
        nombreUsuario = SesionUsuario.getNombreUsuario();

        inputMensaje = findViewById(R.id.inputMensaje);
        mensajesLayout = findViewById(R.id.mensajesLayout);
        scrollMensajes = findViewById(R.id.scrollMensajes);
        txtNombreChat = findViewById(R.id.txtNombreChat);
        imgPerfil = findViewById(R.id.imgPerfil);
        Button btnEnviar = findViewById(R.id.btnEnviar);
        Button btnVolver = findViewById(R.id.btnVolver);

        txtNombreChat.setText("Chat de artista: @" + artista.getNombreUsuario());

        // Cargar imagen del artista
        if (artista.getUrlFoto() != null && !artista.getUrlFoto().isEmpty()) {
            try {
                GlideUrl glideUrl = new GlideUrl(
                        ApiCliente.getUrlArchivos() + artista.getUrlFoto(),
                        new LazyHeaders.Builder()
                                .addHeader("Authorization", "Bearer " + SesionUsuario.getToken())
                                .build());
                Glide.with(this).load(glideUrl).into(imgPerfil);
            } catch (Exception e) {
                ManejoErrores.mostrarToastError(this, e);
            }
        }

        btnVolver.setOnClickListener(v -> finish());

        btnEnviar.setOnClickListener(v -> {
            String texto = inputMensaje.getText().toString().trim();
            if (!texto.isEmpty() && webSocketClient != null && webSocketClient.isOpen()) {
                ChatMessageDTO dto = new ChatMessageDTO(nombreUsuario, texto, idPerfilArtista);
                webSocketClient.send(gson.toJson(dto));
                inputMensaje.setText("");
            }
        });

        conectarWebSocket();
    }

    private void conectarWebSocket() {
        URI uri = URI.create(Constantes.URL_BASE.replace("http", "ws") + "/ws/" + idPerfilArtista);

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                System.out.println("WebSocket conectado.");
            }

            @Override
            public void onMessage(String message) {
                ChatMessageDTO recibido = gson.fromJson(message, ChatMessageDTO.class);
                new Handler(Looper.getMainLooper()).post(() -> mostrarMensaje(recibido));
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("Socket cerrado: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
                runOnUiThread(() -> ManejoErrores.mostrarToastError(ChatActivity.this, ex));
            }
        };

        webSocketClient.connect();
    }

    private void mostrarMensaje(ChatMessageDTO mensaje) {
        TextView txt = new TextView(this);
        txt.setText(mensaje.getNombreUsuario() + ": " + mensaje.getMensaje());
        txt.setTextSize(16f);
        txt.setPadding(8, 8, 8, 8);
        mensajesLayout.addView(txt);
        scrollMensajes.post(() -> scrollMensajes.fullScroll(ScrollView.FOCUS_DOWN));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.close();
        }
    }
}
