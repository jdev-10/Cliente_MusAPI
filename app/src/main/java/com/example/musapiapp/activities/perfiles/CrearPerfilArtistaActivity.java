package com.example.musapiapp.activities.perfiles;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.musapiapp.R;
import com.example.musapiapp.dto.RespuestaCliente;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.servicios.UsuarioServicio;
import com.example.musapiapp.util.ManejoErrores;
import com.example.musapiapp.util.SesionUsuario;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearPerfilArtistaActivity extends AppCompatActivity {

    private static final long TAM_MAX = 10 * 1024 * 1024;

    private ImageView imgFoto;
    private EditText etDescripcion;
    private Button btnSubirFoto, btnConfirmar;
    private Uri uriFotoSeleccionada = null;

    private final ActivityResultLauncher<String> selectorDeImagen =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    uriFotoSeleccionada = uri;
                    try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                        if (cursor != null && cursor.moveToFirst()) {
                            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                            long size = cursor.getLong(sizeIndex);
                            if (size > TAM_MAX) {
                                Toast.makeText(this, "La imagen supera los 10MB", Toast.LENGTH_LONG).show();
                                uriFotoSeleccionada = null;
                                return;
                            }
                        }
                    } catch (Exception e) {
                        ManejoErrores.mostrarToastError(this, e);
                    }

                    try {
                        Glide.with(this).load(uri).into(imgFoto);
                    } catch (Exception e) {
                        ManejoErrores.mostrarToastError(this, e);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crear_perfil_artista);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imgFoto = findViewById(R.id.imgFotoPerfil);
        etDescripcion = findViewById(R.id.etDescripcion);
        btnSubirFoto = findViewById(R.id.btnSubirFoto);
        btnConfirmar = findViewById(R.id.btnConfirmar);

        btnSubirFoto.setOnClickListener(v -> selectorDeImagen.launch("image/*"));
        btnConfirmar.setOnClickListener(v -> crearPerfilArtista());
    }

    private void crearPerfilArtista() {
        String descripcion = etDescripcion.getText().toString().trim();
        if (descripcion.isEmpty()) {
            Toast.makeText(this, "La descripción no puede estar vacía", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody idUsuario = RequestBody.create(
                String.valueOf(SesionUsuario.getIdUsuario()),
                MediaType.parse("text/plain")
        );

        RequestBody desc = RequestBody.create(
                descripcion,
                MediaType.parse("text/plain")
        );

        MultipartBody.Part fotoPart = null;

        if (uriFotoSeleccionada != null) {
            try (InputStream inputStream = getContentResolver().openInputStream(uriFotoSeleccionada);
                 ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

                byte[] data = new byte[4096];
                int nRead;
                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                byte[] bytes = buffer.toByteArray();

                RequestBody archivoReq = RequestBody.create(
                        bytes,
                        MediaType.parse(getContentResolver().getType(uriFotoSeleccionada))
                );

                String extension = MimeTypeMap.getSingleton()
                        .getExtensionFromMimeType(getContentResolver().getType(uriFotoSeleccionada));
                String nombreArchivo = "foto_artista." + (extension != null ? extension : "jpg");

                fotoPart = MultipartBody.Part.createFormData("foto", nombreArchivo, archivoReq);

            } catch (Exception e) {
                ManejoErrores.mostrarToastError(this, e);
                return;
            }
        }

        UsuarioServicio servicio = ApiCliente.getClient().create(UsuarioServicio.class);
        servicio.crearPerfilArtista(idUsuario, desc, fotoPart)
                .enqueue(new Callback<RespuestaCliente>() {
                    @Override
                    public void onResponse(Call<RespuestaCliente> call, Response<RespuestaCliente> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Toast.makeText(CrearPerfilArtistaActivity.this, "Perfil creado exitosamente", Toast.LENGTH_LONG).show();
                            SesionUsuario.setArtista(true);
                            finish();
                        } else {
                            Toast.makeText(CrearPerfilArtistaActivity.this, "Error al crear perfil", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<RespuestaCliente> call, Throwable t) {
                        ManejoErrores.mostrarToastError(CrearPerfilArtistaActivity.this, t);
                    }
                });
    }
}
