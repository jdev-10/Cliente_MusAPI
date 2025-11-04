package com.example.musapiapp.activities.menu;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musapiapp.R;
import com.example.musapiapp.dto.CategoriaMusicalDTO;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.servicios.CategoriaMusicalServicio;
import com.example.musapiapp.util.ManejoErrores;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriaMusicalFormActivity extends AppCompatActivity {

    private Button btnVolver;
    private EditText etNombre, etDescripcion;
    private Button btnGuardar;
    private CategoriaMusicalDTO categoriaEditando = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria_musical_form);

        btnVolver = findViewById(R.id.btnVolver);
        etNombre = findViewById(R.id.etNombre);
        etDescripcion = findViewById(R.id.etDescripcion);
        btnGuardar = findViewById(R.id.btnGuardar);

        btnVolver.setOnClickListener(v -> finish());

        if (getIntent().hasExtra("categoria")) {
            String categoriaJson = getIntent().getStringExtra("categoria");
            categoriaEditando = new Gson().fromJson(categoriaJson, CategoriaMusicalDTO.class);
            cargarDatos(categoriaEditando);
        }

        btnGuardar.setOnClickListener(v -> guardarCategoria());
    }

    private void cargarDatos(CategoriaMusicalDTO categoria) {
        etNombre.setText(categoria.getNombre());
        etDescripcion.setText(categoria.getDescripcion());
    }

    private void guardarCategoria() {
        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        if (nombre.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_LONG).show();
            return;
        }

        CategoriaMusicalDTO dto = new CategoriaMusicalDTO();
        dto.setNombre(nombre);
        dto.setDescripcion(descripcion);

        CategoriaMusicalServicio servicio = ApiCliente.getClient().create(CategoriaMusicalServicio.class);

        Call<JsonObject> llamada;
        if (categoriaEditando != null) {
            llamada = servicio.editarCategoriaJson(categoriaEditando.getIdCategoriaMusical(), dto);
        } else {
            llamada = servicio.registrarCategoriaJson(dto);
        }

        llamada.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CategoriaMusicalFormActivity.this,
                            "Categoría actualizada con éxito", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(CategoriaMusicalFormActivity.this,
                            "Error al actualizar la categoría", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                ManejoErrores.mostrarToastError(CategoriaMusicalFormActivity.this, t);
                Log.e("API_ERROR", t.getMessage(), t);
            }
        });
    }
}
