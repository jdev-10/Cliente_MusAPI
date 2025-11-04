package com.example.musapiapp.activities.contenido;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musapiapp.R;
import com.example.musapiapp.activities.menu.MenuAdminActivity;
import com.example.musapiapp.dto.BusquedaUsuarioDTO;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.servicios.UsuarioServicio;
import com.example.musapiapp.util.ManejoErrores;
import com.example.musapiapp.util.RespuestaApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EliminarUsuarioActivity extends AppCompatActivity {

    private TextView tvDatosUsuario, tvContador;
    private EditText etMotivo;
    private Button btnCancelar, btnEliminar, btnVolver;
    private BusquedaUsuarioDTO usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eliminar_usuario);

        tvDatosUsuario = findViewById(R.id.tvDatosUsuario);
        tvContador = findViewById(R.id.tvContador);
        etMotivo = findViewById(R.id.etMotivo);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnVolver = findViewById(R.id.btnVolver);

        try {
            usuario = getIntent().getParcelableExtra("usuario");
            if (usuario == null) {
                throw new Exception("No se recibió el usuario a eliminar.");
            }

            tvDatosUsuario.setText(usuario.getNombre() + " (" + usuario.getNombreUsuario() + ")\n" + usuario.getCorreo());

        } catch (Exception e) {
            ManejoErrores.mostrarToastError(this, e);
            finish();
            return;
        }

        etMotivo.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvContador.setText(s.length() + "/100");
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        btnCancelar.setOnClickListener(v -> finish());
        btnVolver.setOnClickListener(v -> finish());

        btnEliminar.setOnClickListener(v -> {
            String motivo = etMotivo.getText().toString().trim();
            if (motivo.isEmpty()) {
                Toast.makeText(this, "Por favor ingrese un motivo para la eliminación", Toast.LENGTH_SHORT).show();
                return;
            }

            new AlertDialog.Builder(this)
                    .setTitle("Confirmar eliminación")
                    .setMessage("¿Está seguro que desea eliminar la cuenta de " + usuario.getNombre() + "?\nEsta acción no se puede deshacer.")
                    .setPositiveButton("Sí", (dialog, which) -> eliminarUsuario(motivo))
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void eliminarUsuario(String motivo) {
        UsuarioServicio servicio = ApiCliente.getClient().create(UsuarioServicio.class);
        servicio.eliminarUsuario(usuario.getIdUsuario(), motivo).enqueue(new Callback<RespuestaApi<String>>() {
            @Override
            public void onResponse(Call<RespuestaApi<String>> call, Response<RespuestaApi<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(EliminarUsuarioActivity.this, "Usuario eliminado correctamente.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(EliminarUsuarioActivity.this, MenuAdminActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(EliminarUsuarioActivity.this, "No se pudo eliminar el usuario. Intente más tarde.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RespuestaApi<String>> call, Throwable t) {
                ManejoErrores.mostrarToastError(EliminarUsuarioActivity.this, t);
            }
        });
    }
}
