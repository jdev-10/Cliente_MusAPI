package com.example.musapiapp.activities.contenido;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musapiapp.R;
import com.example.musapiapp.dto.CategoriaMusicalDTO;
import com.example.musapiapp.dto.RespuestaCliente;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.servicios.CancionServicio;
import com.example.musapiapp.servicios.CategoriaMusicalServicio;
import com.example.musapiapp.util.ManejoErrores;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.InputStream;
import java.util.*;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubirCancionAlbumActivity extends AppCompatActivity {

    private static final int MAX_AUDIO_SIZE = 20 * 1024 * 1024;

    private int idPerfilArtista;
    private int idAlbum;

    private EditText etNombre;
    private Spinner spCategorias;
    private TextView tvAudioSeleccionado;

    private Uri uriAudio = null;
    private String duracionStr = "";
    private Map<String, Integer> mapNombreToId = new HashMap<>();

    private ActivityResultLauncher<Intent> seleccionarAudioLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_cancion_album);

        idPerfilArtista = getIntent().getIntExtra("idPerfilArtista", -1);
        idAlbum = getIntent().getIntExtra("idAlbum", -1);

        if (idPerfilArtista == -1 || idAlbum == -1) {
            Toast.makeText(this, "Parámetros inválidos", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etNombre = findViewById(R.id.etNombre);
        spCategorias = findViewById(R.id.spCategorias);
        tvAudioSeleccionado = findViewById(R.id.tvAudioSeleccionado);
        Button btnSeleccionarAudio = findViewById(R.id.btnSeleccionarAudio);
        Button btnSubir = findViewById(R.id.btnSubir);

        btnSeleccionarAudio.setOnClickListener(v -> seleccionarAudio());
        btnSubir.setOnClickListener(v -> subirCancion());

        seleccionarAudioLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri uri = result.getData().getData();
                if (uri != null && validarTamanio(uri, MAX_AUDIO_SIZE)) {
                    uriAudio = uri;
                    tvAudioSeleccionado.setText(obtenerNombreArchivo(uri));
                    try {
                        duracionStr = obtenerDuracion(uri);
                    } catch (Exception e) {
                        ManejoErrores.mostrarToastError(this, e);
                        duracionStr = "00:00";
                    }
                } else {
                    Toast.makeText(this, "Archivo demasiado grande", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cargarCategorias();
    }

    private void seleccionarAudio() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        seleccionarAudioLauncher.launch(intent);
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

    private String obtenerDuracion(Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(this, uri);
            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (duration == null) return "00:00";
            long millis = Long.parseLong(duration);
            long seconds = (millis / 1000) % 60;
            long minutes = (millis / (1000 * 60)) % 60;
            return String.format("%02d:%02d", minutes, seconds);
        } finally {
            try {
                retriever.release();
            } catch (Exception ignored) {}
        }
    }

    private String obtenerNombreArchivo(Uri uri) {
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index != -1) return cursor.getString(index);
            }
        } catch (Exception e) {
            ManejoErrores.mostrarToastError(this, e);
        }
        return "cancion.mp3";
    }

    private void cargarCategorias() {
        CategoriaMusicalServicio servicio = ApiCliente.getClient().create(CategoriaMusicalServicio.class);
        servicio.obtenerCategorias().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(SubirCancionAlbumActivity.this, "Error al cargar categorías", Toast.LENGTH_SHORT).show();
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

                ArrayAdapter<String> adapter = new ArrayAdapter<>(SubirCancionAlbumActivity.this,
                        android.R.layout.simple_spinner_item, nombres);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spCategorias.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                ManejoErrores.mostrarToastError(SubirCancionAlbumActivity.this, t);
            }
        });
    }

    private void subirCancion() {
        String nombre = etNombre.getText().toString().trim();
        if (nombre.isEmpty() || uriAudio == null || spCategorias.getSelectedItem() == null) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int idCat = mapNombreToId.get(spCategorias.getSelectedItem().toString());

        RequestBody nombreRB = RequestBody.create(MediaType.parse("text/plain"), nombre);
        RequestBody artistaRB = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(idPerfilArtista));
        RequestBody duracionRB = RequestBody.create(MediaType.parse("text/plain"), duracionStr);
        RequestBody catRB = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(idCat));
        RequestBody albumRB = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(idAlbum));
        RequestBody posRB = RequestBody.create(MediaType.parse("text/plain"), "0");

        MultipartBody.Part audioPart;
        try (InputStream is = getContentResolver().openInputStream(uriAudio)) {
            byte[] buf = new byte[is.available()];
            is.read(buf);
            RequestBody audioRB = RequestBody.create(
                    MediaType.parse(getContentResolver().getType(uriAudio)), buf);
            audioPart = MultipartBody.Part.createFormData("archivoCancion", obtenerNombreArchivo(uriAudio), audioRB);
        } catch (Exception e) {
            ManejoErrores.mostrarToastError(this, e);
            return;
        }

        CancionServicio servicio = ApiCliente.getClient().create(CancionServicio.class);
        servicio.subirCancion(nombreRB, artistaRB, duracionRB, catRB, albumRB, posRB, null, audioPart)
                .enqueue(new Callback<RespuestaCliente>() {
                    @Override
                    public void onResponse(Call<RespuestaCliente> call, Response<RespuestaCliente> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Toast.makeText(SubirCancionAlbumActivity.this, "Canción subida", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(SubirCancionAlbumActivity.this, "Error al subir canción", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<RespuestaCliente> call, Throwable t) {
                        ManejoErrores.mostrarToastError(SubirCancionAlbumActivity.this, t);
                    }
                });
    }
}
