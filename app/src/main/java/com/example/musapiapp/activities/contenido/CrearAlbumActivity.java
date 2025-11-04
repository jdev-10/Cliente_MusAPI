package com.example.musapiapp.activities.contenido;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musapiapp.R;
import com.example.musapiapp.dto.BusquedaAlbumDTO;
import com.example.musapiapp.dto.RespuestaCliente;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.servicios.AlbumServicio;
import com.example.musapiapp.util.Constantes;
import com.example.musapiapp.util.ManejoErrores;
import com.example.musapiapp.util.RespuestaApi;
import com.example.musapiapp.util.SesionUsuario;
import com.google.gson.Gson;

import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearAlbumActivity extends AppCompatActivity {

    private static final int MAX_IMAGE_SIZE = 10 * 1024 * 1024;

    private EditText etNombre;
    private ImageView imgPortada;
    private Uri uriImagen = null;

    private boolean esEdicion = false;
    private int idAlbumEditar = -1;
    private String urlFotoExistente = null;

    private ActivityResultLauncher<Intent> subirImagenLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_album);

        etNombre = findViewById(R.id.etNombre);
        imgPortada = findViewById(R.id.imgPortada);
        Button btnSubirFoto = findViewById(R.id.btnSubirFoto);
        Button btnGuardar = findViewById(R.id.btnGuardar);
        Button btnCancelar = findViewById(R.id.btnCancelar);

        // Detectar si venimos a editar
        if (getIntent().hasExtra("album")) {
            esEdicion = true;
            BusquedaAlbumDTO album = new Gson().fromJson(getIntent().getStringExtra("album"), BusquedaAlbumDTO.class);
            idAlbumEditar = album.getIdAlbum();
            etNombre.setText(album.getNombreAlbum());
            urlFotoExistente = album.getUrlFoto();

            if (urlFotoExistente != null && !urlFotoExistente.isEmpty()) {
                Constantes.CargarImagen(urlFotoExistente, imgPortada);
            }

            btnGuardar.setText("Editar");
        }

        btnSubirFoto.setOnClickListener(v -> seleccionarImagen());
        btnGuardar.setOnClickListener(v -> guardarAlbum());
        btnCancelar.setOnClickListener(v -> finish());

        subirImagenLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri uri = result.getData().getData();
                if (uri != null && validarTamanio(uri, MAX_IMAGE_SIZE)) {
                    uriImagen = uri;
                    Glide.with(this).load(uri).into(imgPortada);
                } else {
                    Toast.makeText(this, "La imagen es demasiado grande", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void seleccionarImagen() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        subirImagenLauncher.launch(intent);
    }

    private boolean validarTamanio(Uri uri, long maxBytes) {
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                long size = cursor.getLong(sizeIndex);
                return size <= maxBytes;
            }
        } catch (Exception e) {
            ManejoErrores.mostrarToastError(this, e);
        }
        return false;
    }

    private void guardarAlbum() {
        String nombre = etNombre.getText().toString().trim();
        if (nombre.isEmpty()) {
            Toast.makeText(this, "Ingresa el nombre del álbum", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody nombreRB = RequestBody.create(MediaType.parse("text/plain"), nombre);
        RequestBody idUsuarioRB = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(SesionUsuario.getIdUsuario()));

        MultipartBody.Part fotoPart = null;
        if (uriImagen != null) {
            try (InputStream is = getContentResolver().openInputStream(uriImagen)) {
                byte[] buf = new byte[is.available()];
                is.read(buf);
                RequestBody fotoRB = RequestBody.create(MediaType.parse(getContentResolver().getType(uriImagen)), buf);
                fotoPart = MultipartBody.Part.createFormData("foto", obtenerNombreArchivo(uriImagen), fotoRB);
            } catch (Exception e) {
                ManejoErrores.mostrarToastError(this, e);
                return;
            }
        }

        AlbumServicio servicio = ApiCliente.getClient().create(AlbumServicio.class);

        if (!esEdicion) {
            // Crear nuevo álbum
            servicio.crearAlbum(nombreRB, idUsuarioRB, fotoPart)
                    .enqueue(getCallback("Álbum creado exitosamente"));
        } else {
            // Editar álbum existente
            RequestBody idAlbumRB = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(idAlbumEditar));
            servicio.editarAlbum(nombreRB, idAlbumRB, fotoPart)
                    .enqueue(getCallback("Álbum editado exitosamente"));
        }
    }

    private Callback<RespuestaApi<String>> getCallback(String mensajeExito) {
        return new Callback<RespuestaApi<String>>() {
            @Override
            public void onResponse(Call<RespuestaApi<String>> call, Response<RespuestaApi<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String mensaje = response.body().getMensaje();
                    if (mensaje != null && mensaje.toLowerCase().contains("exitosamente")) {
                        Toast.makeText(CrearAlbumActivity.this, mensajeExito, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(CrearAlbumActivity.this, "Error: " + mensaje, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CrearAlbumActivity.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RespuestaApi<String>> call, Throwable t) {
                ManejoErrores.mostrarToastError(CrearAlbumActivity.this, t);
            }
        };
    }

    private String obtenerNombreArchivo(Uri uri) {
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index != -1) {
                    return cursor.getString(index);
                }
            }
        } catch (Exception e) {
            ManejoErrores.mostrarToastError(this, e);
        }
        return "portada.jpg";
    }
}
