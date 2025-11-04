package com.example.musapiapp.activities.busqueda;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musapiapp.R;
import com.example.musapiapp.adapters.UcContenidoAdapter;
import com.example.musapiapp.dto.BusquedaUsuarioDTO;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.servicios.UsuarioServicio;
import com.example.musapiapp.util.ManejoErrores;
import com.example.musapiapp.util.RespuestaApi;
import com.example.musapiapp.util.SesionUsuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusquedaUsuarioActivity extends AppCompatActivity {

    private EditText etBusqueda;
    private RecyclerView rvUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda_usuario);

        etBusqueda = findViewById(R.id.etBusqueda);
        rvUsuarios = findViewById(R.id.rvUsuarios);
        rvUsuarios.setLayoutManager(new LinearLayoutManager(this));

        Button btnBuscar = findViewById(R.id.btnBuscar);
        Button btnVolver = findViewById(R.id.btnVolver);

        btnBuscar.setOnClickListener(v -> buscarUsuarios());
        btnVolver.setOnClickListener(v -> finish());

        String query = getIntent().getStringExtra("query");
        if (query != null) {
            etBusqueda.setText(query);
            buscarUsuarios();
        }
    }

    private void buscarUsuarios() {
        String texto = etBusqueda.getText().toString().trim();
        if (texto.isEmpty()) return;

        rvUsuarios.setAdapter(null);

        int idUsuario = SesionUsuario.getIdUsuario();

        UsuarioServicio servicio = ApiCliente.getClient().create(UsuarioServicio.class);
        servicio.buscarUsuario(texto, idUsuario).enqueue(new Callback<RespuestaApi<List<BusquedaUsuarioDTO>>>() {
            @Override
            public void onResponse(Call<RespuestaApi<List<BusquedaUsuarioDTO>>> call,
                                   Response<RespuestaApi<List<BusquedaUsuarioDTO>>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(BusquedaUsuarioActivity.this, "Error al obtener usuarios del servidor.", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<BusquedaUsuarioDTO> usuarios = response.body().getDatos();
                if (usuarios == null || usuarios.isEmpty()) {
                    Toast.makeText(BusquedaUsuarioActivity.this, "No se encontraron usuarios", Toast.LENGTH_SHORT).show();
                    return;
                }

                UcContenidoAdapter<BusquedaUsuarioDTO> adapter =
                        new UcContenidoAdapter<>(BusquedaUsuarioActivity.this, usuarios, "USUARIO_ADMIN", false);
                rvUsuarios.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<RespuestaApi<List<BusquedaUsuarioDTO>>> call, Throwable t) {
                ManejoErrores.mostrarToastError(BusquedaUsuarioActivity.this, t);
            }
        });
    }
}
