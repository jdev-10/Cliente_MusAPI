package com.example.musapiapp.activities.inicio;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musapiapp.R;
import com.example.musapiapp.dto.LoginRequest;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.servicios.UsuarioServicio;
import com.example.musapiapp.util.SesionUsuario;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InicioSesionActivity extends AppCompatActivity {

    private EditText etCorreo, etContrasenia;
    private Button btnLogin, btnExit; // btnRegister sale de aquí
    private TextView btnRegister;     // Y se declara como TextView independiente
    private UsuarioServicio usuarioServicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
            return insets;
        });

        etCorreo      = findViewById(R.id.etCorreo);
        etContrasenia = findViewById(R.id.etContrasenia);
        btnLogin      = findViewById(R.id.btnLogin);
        btnRegister   = findViewById(R.id.btnRegister);
        btnExit       = findViewById(R.id.btnExit);

        usuarioServicio = ApiCliente.getClient().create(UsuarioServicio.class);

        btnLogin.setOnClickListener(v -> onClickIniciarSesion());
        btnRegister.setOnClickListener(v -> onClickRegistrar());
        btnExit.setOnClickListener(v -> finish());
    }

    private void onClickIniciarSesion() {
        String correo = etCorreo.getText().toString().trim();
        String pass   = etContrasenia.getText().toString();

        if (!esCorreoValido(correo) || !esContraseniaValida(pass)) {
            return;
        }

        LoginRequest req = new LoginRequest(correo, pass);
        usuarioServicio.iniciarSesion(req).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> resp) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    Toast.makeText(InicioSesionActivity.this,
                            "Error de login", Toast.LENGTH_SHORT).show();
                    return;
                }

                JsonObject datos = resp.body().getAsJsonObject("datos");
                if (datos == null || !datos.has("token")) {
                    Toast.makeText(InicioSesionActivity.this,
                            "Respuesta inválida", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Guardar token
                String token = datos.get("token").getAsString();
                ApiCliente.setToken(token);
                SesionUsuario.setToken(token);

                // Guardar los datos del usuario
                SesionUsuario.setIdUsuario(datos.get("idUsuario").getAsInt());
                SesionUsuario.setNombre(datos.get("nombre").getAsString());
                SesionUsuario.setNombreUsuario(datos.get("nombreUsuario").getAsString());
                SesionUsuario.setCorreo(datos.get("correo").getAsString());
                SesionUsuario.setPais(datos.get("pais").getAsString());
                SesionUsuario.setAdmin(datos.get("esAdmin").getAsBoolean());
                SesionUsuario.setArtista(datos.get("esArtista").getAsBoolean());

                // Ir a pantalla principal
                startActivity(new Intent(InicioSesionActivity.this,
                        com.example.musapiapp.activities.menu.MenuPrincipalActivity.class));
                finish();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(InicioSesionActivity.this,
                        "Fallo de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onClickRegistrar() {
        startActivity(new Intent(this, RegistroActivity.class));
    }

    private boolean esCorreoValido(String correo) {
        if (correo.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreo.setError("Correo inválido");
            return false;
        }
        etCorreo.setError(null);
        return true;
    }

    private boolean esContraseniaValida(String pass) {
        if (pass.isEmpty()) {
            etContrasenia.setError("Contraseña requerida");
            return false;
        }
        etContrasenia.setError(null);
        return true;
    }
}
