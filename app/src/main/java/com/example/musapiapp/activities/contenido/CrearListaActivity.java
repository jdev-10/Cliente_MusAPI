package com.example.musapiapp.activities.contenido;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musapiapp.R;
import com.example.musapiapp.dto.ListaDeReproduccionDTO;
import com.example.musapiapp.dto.RespuestaCliente;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.servicios.ListaServicio;
import com.example.musapiapp.util.Constantes;
import com.example.musapiapp.util.ManejoErrores;
import com.example.musapiapp.util.SesionUsuario;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearListaActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1;

    private EditText etNombre, etDescripcion;
    private ImageView imgPortada;
    private Button btnSubirFoto, btnGuardar, btnCancelar;

    private Uri uriImagenSeleccionada = null;
    private boolean esEdicion = false;
    private ListaDeReproduccionDTO listaEditar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_lista);

        etNombre = findViewById(R.id.etNombreLista);
        etDescripcion = findViewById(R.id.etDescripcion);
        imgPortada = findViewById(R.id.imgPortada);
        btnSubirFoto = findViewById(R.id.btnSeleccionarFoto);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);

        // Verificar si estamos editando
        if (getIntent().hasExtra("lista")) {
            esEdicion = true;
            listaEditar = new Gson().fromJson(getIntent().getStringExtra("lista"), ListaDeReproduccionDTO.class);
            cargarDatosParaEditar();
        }

        btnSubirFoto.setOnClickListener(v -> abrirGaleria());
        btnGuardar.setOnClickListener(v -> guardarLista());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void cargarDatosParaEditar() {
        etNombre.setText(listaEditar.getNombre());
        etDescripcion.setText(listaEditar.getDescripcion());

        if (listaEditar.getUrlFoto() != null && !listaEditar.getUrlFoto().isEmpty()) {
            Constantes.CargarImagen(listaEditar.getUrlFoto(), imgPortada);
        }

        btnGuardar.setText("Editar");
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            uriImagenSeleccionada = data.getData();
            imgPortada.setImageURI(uriImagenSeleccionada);
        }
    }

    private void guardarLista() {
        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        if (nombre.isEmpty()) {
            etNombre.setError("Campo requerido");
            return;
        }

        ListaServicio servicio = ApiCliente.getClient().create(ListaServicio.class);

        // Datos textuales
        RequestBody rbNombre = RequestBody.create(nombre, MediaType.parse("text/plain"));
        RequestBody rbDescripcion = RequestBody.create(descripcion, MediaType.parse("text/plain"));
        RequestBody rbIdUsuario = RequestBody.create(
                String.valueOf(SesionUsuario.getIdUsuario()), MediaType.parse("text/plain"));

        MultipartBody.Part imagenPart = null;
        if (uriImagenSeleccionada != null) {
            try (InputStream is = getContentResolver().openInputStream(uriImagenSeleccionada)) {
                byte[] buf = new byte[is.available()];
                is.read(buf);
                RequestBody fotoRB = RequestBody.create(MediaType.parse(getContentResolver().getType(uriImagenSeleccionada)), buf);
                imagenPart = MultipartBody.Part.createFormData("foto", obtenerNombreArchivo(uriImagenSeleccionada), fotoRB);
            } catch (Exception e) {
                ManejoErrores.mostrarToastError(this, e);
                return;
            }
        }



        if (!esEdicion) {
            Call<RespuestaCliente> llamada;
            llamada = servicio.crearLista(rbNombre, rbDescripcion, rbIdUsuario, imagenPart);
            llamada.enqueue(new Callback<RespuestaCliente>() {
                @Override
                public void onResponse(Call<RespuestaCliente> call, Response<RespuestaCliente> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(CrearListaActivity.this,
                                esEdicion ? "Lista editada exitosamente" : "Lista creada exitosamente",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(CrearListaActivity.this, "Error al guardar la lista", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<RespuestaCliente> call, Throwable t) {
                    ManejoErrores.mostrarToastError(CrearListaActivity.this, t);
                }
            });
        } else {
            Call<Void> llamada;
            RequestBody rbIdLista = RequestBody.create(
                    String.valueOf(listaEditar.getIdListaDeReproduccion()), MediaType.parse("text/plain"));
            llamada = servicio.editarLista(rbNombre, rbDescripcion, rbIdLista, imagenPart);
            llamada.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(CrearListaActivity.this,
                                esEdicion ? "Lista editada exitosamente" : "Lista creada exitosamente",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(CrearListaActivity.this, "Error al guardar la lista", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    ManejoErrores.mostrarToastError(CrearListaActivity.this, t);
                }
            });
        }


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
