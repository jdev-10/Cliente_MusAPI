package com.example.musapiapp.activities.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musapiapp.R;
import com.example.musapiapp.dto.CategoriaMusicalDTO;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.servicios.CategoriaMusicalServicio;
import com.example.musapiapp.util.ManejoErrores;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriasMusicalesActivity extends AppCompatActivity {

    private Button btnVolver;
    private Button btnNuevaCategoria;
    private LinearLayout layoutCategorias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias_musicales);

        btnVolver = findViewById(R.id.btnVolver);
        btnNuevaCategoria = findViewById(R.id.btnNuevaCategoria);
        layoutCategorias = findViewById(R.id.layoutCategorias);

        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(CategoriasMusicalesActivity.this, MenuAdminActivity.class);
            startActivity(intent);
            finish();
        });

        btnNuevaCategoria.setOnClickListener(v -> {
            Intent intent = new Intent(this, CategoriaMusicalFormActivity.class);
            startActivityForResult(intent, 1001);
        });

        cargarCategorias(); // Similar a Page_Loaded
    }

    private void cargarCategorias() {
        CategoriaMusicalServicio servicio = ApiCliente.getClient().create(CategoriaMusicalServicio.class);
        servicio.obtenerCategorias().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(CategoriasMusicalesActivity.this, "Error al obtener categorías", Toast.LENGTH_SHORT).show();
                    return;
                }

                JsonObject json = response.body();
                List<CategoriaMusicalDTO> categorias = new Gson().fromJson(
                        json.getAsJsonArray("datos"),
                        new com.google.gson.reflect.TypeToken<List<CategoriaMusicalDTO>>() {}.getType()
                );

                for (CategoriaMusicalDTO categoria : categorias) {
                    View item = LayoutInflater.from(CategoriasMusicalesActivity.this)
                            .inflate(R.layout.item_categoria, layoutCategorias, false);

                    TextView tvNombre = item.findViewById(R.id.tvNombre);
                    TextView tvDescripcion = item.findViewById(R.id.tvDescripcion);
                    View btnEditar = item.findViewById(R.id.btnEditar);

                    tvNombre.setText(categoria.getNombre());
                    tvDescripcion.setText(categoria.getDescripcion());

                    btnEditar.setOnClickListener(v -> {
                        Intent intent = new Intent(CategoriasMusicalesActivity.this, CategoriaMusicalFormActivity.class);
                        intent.putExtra("categoria", new Gson().toJson(categoria));
                        startActivityForResult(intent, 1001);
                    });

                    layoutCategorias.addView(item);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                ManejoErrores.mostrarToastError(CategoriasMusicalesActivity.this, t);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            layoutCategorias.removeAllViews();
            cargarCategorias();
        }
    }
}
