package com.example.musapiapp.activities.perfiles;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.musapiapp.R;
import com.example.musapiapp.dto.EdicionPerfilDTO;
import com.example.musapiapp.util.Pais;
import com.example.musapiapp.dto.BusquedaArtistaDTO;
import com.example.musapiapp.dto.RespuestaCliente;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.servicios.UsuarioServicio;
import com.example.musapiapp.util.ManejoErrores;
import com.example.musapiapp.util.RespuestaApi;
import com.example.musapiapp.util.SesionUsuario;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarPerfilActivity extends AppCompatActivity {

    private ImageView imgFoto;
    private Button btnVolver, btnSubirFoto, btnConfirmar;
    private EditText etNombre, etDescripcion;
    private TextView tvDescripcionLabel;
    private Spinner spinnerPais;

    private EdicionPerfilDTO edicionPerfil = new EdicionPerfilDTO();
    private Uri selectedImageUri;
    private UsuarioServicio usuarioServicio;

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    try {
                        Glide.with(this).load(uri).into(imgFoto);
                        edicionPerfil.setFoto(copyUriToTempFile(uri));
                    } catch (Exception e) {
                        ManejoErrores.mostrarToastError(this, e);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_perfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
            return insets;
        });

        usuarioServicio = ApiCliente.getClient().create(UsuarioServicio.class);

        imgFoto       = findViewById(R.id.imgFoto);
        btnVolver     = findViewById(R.id.btnVolver);
        btnSubirFoto  = findViewById(R.id.btnSubirFoto);
        etNombre      = findViewById(R.id.etNombre);
        etDescripcion = findViewById(R.id.etDescripcion);
        spinnerPais   = findViewById(R.id.spinnerPais);
        btnConfirmar  = findViewById(R.id.btnConfirmar);
        tvDescripcionLabel = findViewById(R.id.tvDescripcionLabel);

        btnVolver.setOnClickListener(v -> finish());
        btnSubirFoto.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        btnConfirmar.setOnClickListener(v -> onClickConfirmar());

        cargarPaises();
        cargarDatosIniciales();
    }

    private void cargarPaises() {
        List<Pais> countries = new ArrayList<>();
        countries.add(new Pais("AR","Argentina"));
        countries.add(new Pais("BO","Bolivia"));
        // ... agrega los demás como en tu registro ...

        Collections.sort(countries, Comparator.comparing(Pais::getNombre, String.CASE_INSENSITIVE_ORDER));
        ArrayAdapter<Pais> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                countries);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPais.setAdapter(adapter);
    }

    private void cargarDatosIniciales() {
        etNombre.setText(SesionUsuario.getNombre());

        String codigoActual = SesionUsuario.getPais();
        ArrayAdapter<Pais> adapter = (ArrayAdapter<Pais>) spinnerPais.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).getCodigo().equalsIgnoreCase(codigoActual)) {
                spinnerPais.setSelection(i);
                break;
            }
        }

        boolean esArtista = SesionUsuario.isArtista();
        int visible = esArtista ? View.VISIBLE : View.GONE;
        tvDescripcionLabel.setVisibility(visible);
        etDescripcion.setVisibility(visible);
        btnSubirFoto.setVisibility(visible);
        imgFoto.setVisibility(visible);
        if (!esArtista) return;

        usuarioServicio.obtenerPerfilArtista(SesionUsuario.getIdUsuario())
                .enqueue(new Callback<RespuestaApi<BusquedaArtistaDTO>>() {
                    @Override
                    public void onResponse(Call<RespuestaApi<BusquedaArtistaDTO>> call,
                                           Response<RespuestaApi<BusquedaArtistaDTO>> response) {
                        if (!response.isSuccessful() || response.body() == null) return;

                        BusquedaArtistaDTO perfil = response.body().getDatos();
                        if (perfil == null) return;

                        etDescripcion.setText(perfil.getDescripcion());

                        String urlFoto = perfil.getUrlFoto();
                        if (urlFoto != null && !urlFoto.isEmpty()) {
                            String fullUrl = ApiCliente.getUrlArchivos() + urlFoto;
                            GlideUrl glideUrl = new GlideUrl(
                                    fullUrl,
                                    new LazyHeaders.Builder()
                                            .addHeader("Authorization", "Bearer " + SesionUsuario.getToken())
                                            .build()
                            );
                            Glide.with(EditarPerfilActivity.this)
                                    .load(glideUrl)
                                    .into(imgFoto);
                        }
                    }

                    @Override
                    public void onFailure(Call<RespuestaApi<BusquedaArtistaDTO>> call, Throwable t) {
                        ManejoErrores.mostrarToastError(EditarPerfilActivity.this, t);
                    }
                });
    }

    private void onClickConfirmar() {
        String nombre = etNombre.getText().toString().trim();
        if (nombre.isEmpty()) {
            etNombre.setError("Requerido");
            return;
        }
        Pais pais = (Pais) spinnerPais.getSelectedItem();
        if (pais == null) {
            Toast.makeText(this, "Selecciona un país", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody nombreBody = RequestBody.create(MediaType.parse("text/plain"), nombre);
        RequestBody nombreUsuarioBody = RequestBody.create(MediaType.parse("text/plain"), SesionUsuario.getNombreUsuario());
        RequestBody paisBody = RequestBody.create(MediaType.parse("text/plain"), pais.getCodigo());

        String desc = SesionUsuario.isArtista() ? etDescripcion.getText().toString().trim() : "";
        RequestBody descripcionBody = RequestBody.create(MediaType.parse("text/plain"), desc);

        MultipartBody.Part fotoPart = null;
        if (edicionPerfil.getFoto() != null) {
            try {
                File fotoFile = new File(edicionPerfil.getFoto());
                RequestBody fotoRequest = RequestBody.create(MediaType.parse("image/*"), fotoFile);
                fotoPart = MultipartBody.Part.createFormData("foto", fotoFile.getName(), fotoRequest);
            } catch (Exception e) {
                ManejoErrores.mostrarToastError(this, e);
                return;
            }
        }

        usuarioServicio.editarPerfil(
                        SesionUsuario.getIdUsuario(),
                        nombreBody,
                        nombreUsuarioBody,
                        paisBody,
                        descripcionBody,
                        fotoPart
                )
                .enqueue(new Callback<RespuestaCliente>() {
                    @Override
                    public void onResponse(Call<RespuestaCliente> call,
                                           Response<RespuestaCliente> resp) {
                        if (resp.isSuccessful() && resp.body()!=null && resp.body().isSuccess()) {
                            SesionUsuario.setNombre(nombre);
                            SesionUsuario.setPais(pais.getCodigo());
                            Toast.makeText(EditarPerfilActivity.this,
                                    "Perfil actualizado", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(EditarPerfilActivity.this,
                                    "Error al actualizar perfil", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<RespuestaCliente> call, Throwable t) {
                        ManejoErrores.mostrarToastError(EditarPerfilActivity.this, t);
                    }
                });
    }

    private String copyUriToTempFile(Uri uri) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            File tmp = new File(getCacheDir(), "perfil_foto.jpg");
            try (FileOutputStream fos = new FileOutputStream(tmp)) {
                byte[] buf = new byte[4096];
                int len;
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }
            }
            return tmp.getAbsolutePath();
        } catch (Exception e) {
            ManejoErrores.mostrarToastError(this, e);
            return null;
        }
    }
}
