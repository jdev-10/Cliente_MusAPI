package com.example.musapiapp.activities.contenido;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.musapiapp.R;
import com.example.musapiapp.dto.BusquedaCancionDTO;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.util.Constantes;
import com.example.musapiapp.util.ManejoErrores;

public class DetalleCancionActivity extends AppCompatActivity {

    private ImageView imgFoto;
    private TextView txtNombre, txtAutor, txtAlbum, txtDuracion, txtFecha;
    private View layoutAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_cancion);
        imgFoto = findViewById(R.id.imgFoto);
        txtNombre = findViewById(R.id.txtNombre);
        txtAutor = findViewById(R.id.txtAutor);
        txtAlbum = findViewById(R.id.txtAlbum);
        txtDuracion = findViewById(R.id.txtDuracion);
        txtFecha = findViewById(R.id.txtFecha);
        layoutAlbum = findViewById(R.id.spAlbum);

        Button btnCerrar = findViewById(R.id.btnCerrar);
        btnCerrar.setOnClickListener(v -> finish());

        ImageButton btnVolverHeader = findViewById(R.id.btnVolverHeader);
        btnVolverHeader.setOnClickListener(v -> finish());

        try {
            BusquedaCancionDTO cancion = getIntent().getParcelableExtra("cancion");

            if (cancion == null) {
                throw new Exception("No se pudo cargar la canción desde el intent.");
            }

            mostrarDatos(cancion);

        } catch (Exception ex) {
            ManejoErrores.mostrarToastError(this, ex);
            finish();
        }
    }

    private void mostrarDatos(BusquedaCancionDTO c) {
        txtNombre.setText(c.getNombre());
        txtAutor.setText(c.getNombreArtista());
        txtDuracion.setText(c.getDuracion());
        txtFecha.setText(c.getFechaPublicacion());

        if (c.getNombreAlbum() != null && !c.getNombreAlbum().isEmpty()) {
            txtAlbum.setText(c.getNombreAlbum());
            layoutAlbum.setVisibility(View.VISIBLE);
        } else {
            layoutAlbum.setVisibility(View.GONE);
        }

        try {
            if (c.getUrlFoto() != null && !c.getUrlFoto().isEmpty()) {
                Constantes.CargarImagen(c.getUrlFoto(), imgFoto);
            } else {
                imgFoto.setImageResource(R.drawable.ic_album); 
            }
        } catch (Exception e) {
            ManejoErrores.mostrarToastError(this, e);
            imgFoto.setImageResource(R.drawable.ic_album);
        }
    }
}
