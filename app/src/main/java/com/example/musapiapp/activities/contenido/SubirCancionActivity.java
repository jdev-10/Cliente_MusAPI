package com.example.musapiapp.activities.contenido;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musapiapp.R;
import com.example.musapiapp.dto.CategoriaMusicalDTO;
import com.example.musapiapp.dto.RespuestaCliente;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.servicios.CancionServicio;
import com.example.musapiapp.servicios.CategoriaMusicalServicio;
import com.example.musapiapp.util.ManejoErrores;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubirCancionActivity extends AppCompatActivity {

    private static final int MAX_IMAGE_SIZE = 10 * 1024 * 1024;
    private static final int MAX_AUDIO_SIZE = 20 * 1024 * 1024;
    private int idPerfilArtista = -1;
    private int idAlbum = 0;

    private ImageView imgFoto;
    private EditText etNombreCancion;
    private Spinner spinnerCategoria;
    private TextView txtNombreArchivo, txtDuracion;

    private Uri uriFoto = null;
    private Uri uriCancion = null;
    private String duracionStr = "";

    private ActivityResultLauncher<Intent> subirImagenLauncher;
    private ActivityResultLauncher<Intent> subirAudioLauncher;

    private final Map<String, Integer> mapNombreToId = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_cancion);

        idPerfilArtista = getIntent().getIntExtra("idPerfilArtista", -1);
        idAlbum = getIntent().getIntExtra("idAlbum", 0);

        imgFoto = findViewById(R.id.imgFoto);
        etNombreCancion = findViewById(R.id.etNombreCancion);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        txtNombreArchivo = findViewById(R.id.txtNombreArchivo);
        txtDuracion = findViewById(R.id.txtDuracion);

        Button btnVolver = findViewById(R.id.btnVolver);
        Button btnSubirFoto = findViewById(R.id.btnSubirFoto);
        Button btnSubirArchivo = findViewById(R.id.btnSubirArchivo);
        Button btnConfirmar = findViewById(R.id.btnConfirmar);

        btnVolver.setOnClickListener(v -> onBackPressed());
        btnSubirFoto.setOnClickListener(v -> seleccionarImagen());
        btnSubirArchivo.setOnClickListener(v -> seleccionarAudio());
        btnConfirmar.setOnClickListener(v -> confirmarSubida());

        subirImagenLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri uri = result.getData().getData();
                if (uri != null) {
                    if (validarTamanio(uri, MAX_IMAGE_SIZE)) {
                        uriFoto = uri;
                        Glide.with(this).load(uri).into(imgFoto);
                    } else {
                        Toast.makeText(this, "La imagen es demasiado grande", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        subirAudioLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri uri = result.getData().getData();
                if (uri != null) {
                    if (validarTamanio(uri, MAX_AUDIO_SIZE)) {
                        uriCancion = uri;
                        txtNombreArchivo.setText("Archivo seleccionado: " + obtenerNombreArchivo(uri));
                        try {
                            duracionStr = obtenerDuracion(uri);
                        } catch (Exception e) {
                            ManejoErrores.mostrarToastError(this, e);
                            duracionStr = "N/A";
                        }
                        txtDuracion.setText("Duración: " + duracionStr);
                    } else {
                        Toast.makeText(this, "El archivo de audio es demasiado grande", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        cargarCategorias();
    }

    private void seleccionarImagen() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        subirImagenLauncher.launch(intent);
    }

    private void seleccionarAudio() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        subirAudioLauncher.launch(intent);
    }

    private String obtenerNombreArchivo(Uri uri) {
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                return cursor.getString(nameIndex);
            }
        } catch (Exception e) {
            ManejoErrores.mostrarToastError(this, e);
        }
        return "Desconocido";
    }

    private String obtenerDuracion(Uri uri) throws IOException {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            mmr.setDataSource(this, uri);
            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long millis = Long.parseLong(duration);
            long seconds = (millis / 1000) % 60;
            long minutes = (millis / (1000 * 60)) % 60;
            return String.format("%02d:%02d", minutes, seconds);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo obtener la duración del archivo", e);
        } finally {
            mmr.release();
        }
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

    private void cargarCategorias() {
        CategoriaMusicalServicio servicio = ApiCliente.getClient().create(CategoriaMusicalServicio.class);
        servicio.obtenerCategorias().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(SubirCancionActivity.this, "Error al obtener categorías", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<CategoriaMusicalDTO> categorias = new Gson().fromJson(
                        response.body().getAsJsonArray("datos"),
                        new com.google.gson.reflect.TypeToken<List<CategoriaMusicalDTO>>() {}.getType()
                );

                List<String> nombres = new ArrayList<>();
                for (CategoriaMusicalDTO cat : categorias) {
                    nombres.add(cat.getNombre());
                    mapNombreToId.put(cat.getNombre(), cat.getIdCategoriaMusical());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(SubirCancionActivity.this,
                        android.R.layout.simple_spinner_item, nombres);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategoria.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                ManejoErrores.mostrarToastError(SubirCancionActivity.this, t);
            }
        });
    }

    private void confirmarSubida() {
        String nombre = etNombreCancion.getText().toString().trim();

        if (nombre.isEmpty() || spinnerCategoria.getSelectedItem() == null || uriCancion == null || (idAlbum == 0 && uriFoto == null)) {
            Toast.makeText(this, "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        int idCat = mapNombreToId.get(spinnerCategoria.getSelectedItem().toString());

        RequestBody nombreRB = RequestBody.create(MediaType.parse("text/plain"), nombre);
        RequestBody idArtistaRB = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(idPerfilArtista));
        RequestBody duracionRB = RequestBody.create(MediaType.parse("text/plain"), duracionStr);
        RequestBody catRB = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(idCat));
        RequestBody albumRB = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(idAlbum));
        RequestBody posRB = RequestBody.create(MediaType.parse("text/plain"), "0");

        MultipartBody.Part audioPart;
        try (InputStream is = getContentResolver().openInputStream(uriCancion)) {
            byte[] buf = new byte[is.available()];
            is.read(buf);
            RequestBody audioRB = RequestBody.create(MediaType.parse(getContentResolver().getType(uriCancion)), buf);
            audioPart = MultipartBody.Part.createFormData("archivoCancion", obtenerNombreArchivo(uriCancion), audioRB);
        } catch (Exception e) {
            ManejoErrores.mostrarToastError(this, e);
            return;
        }

        MultipartBody.Part fotoPart = null;
        if (idAlbum == 0 && uriFoto != null) {
            try (InputStream isf = getContentResolver().openInputStream(uriFoto)) {
                byte[] buf = new byte[isf.available()];
                isf.read(buf);
                RequestBody fotoRB = RequestBody.create(MediaType.parse(getContentResolver().getType(uriFoto)), buf);
                fotoPart = MultipartBody.Part.createFormData("urlFoto", obtenerNombreArchivo(uriFoto), fotoRB);
            } catch (Exception e) {
                ManejoErrores.mostrarToastError(this, e);
                return;
            }
        }

        CancionServicio servicio = ApiCliente.getClient().create(CancionServicio.class);
        servicio.subirCancion(nombreRB, idArtistaRB, duracionRB, catRB, albumRB, posRB, fotoPart, audioPart)
                .enqueue(new Callback<RespuestaCliente>() {
                    @Override
                    public void onResponse(Call<RespuestaCliente> call, Response<RespuestaCliente> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isSuccess()) {
                                Toast.makeText(SubirCancionActivity.this, "Canción subida exitosamente", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(SubirCancionActivity.this, "Error: " + response.body().getMensaje(), Toast.LENGTH_SHORT).show();
                                Log.e("API_SUBIR_CANCION", "Error lógico: " + response.body().getMensaje());
                            }
                        } else {
                            try {
                                String errorBody = response.errorBody().string();
                                JsonObject errorJson = new Gson().fromJson(errorBody, JsonObject.class);
                                String mensaje = errorJson.has("mensaje")
                                        ? errorJson.get("mensaje").getAsString()
                                        : "Error desconocido";
                                Toast.makeText(SubirCancionActivity.this, "Error del servidor: " + mensaje, Toast.LENGTH_LONG).show();
                                Log.e("API_SUBIR_CANCION", "Respuesta error: " + mensaje);
                            } catch (Exception e) {
                                ManejoErrores.mostrarToastError(SubirCancionActivity.this, e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<RespuestaCliente> call, Throwable t) {
                        ManejoErrores.mostrarToastError(SubirCancionActivity.this, t);
                    }
                });
    }
}
