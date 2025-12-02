package com.example.musapiapp.activities.inicio;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musapiapp.R;
import com.example.musapiapp.dto.RespuestaCliente;
import com.example.musapiapp.util.Pais;
import com.example.musapiapp.dto.UsuarioDTO;
import com.example.musapiapp.dto.LoginRequest;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.servicios.UsuarioServicio;
import com.example.musapiapp.util.ManejoErrores;
import com.example.musapiapp.util.SesionUsuario;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroActivity extends AppCompatActivity {

    private EditText etUsuario, etCorreoReg, etContraseniaReg;
    private Spinner spinnerPais;
    private Button btnRegistrar, btnCancelar;
    private UsuarioServicio usuarioServicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sb = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sb.left, sb.top, sb.right, sb.bottom);
            return insets;
        });

        etUsuario      = findViewById(R.id.etUsuario);
        etCorreoReg    = findViewById(R.id.etCorreoReg);
        etContraseniaReg = findViewById(R.id.etContraseniaReg);
        spinnerPais    = findViewById(R.id.spinnerPais);
        btnRegistrar   = findViewById(R.id.btnRegistrar);
        btnCancelar    = findViewById(R.id.btnCancelar);

        usuarioServicio = ApiCliente.getClient().create(UsuarioServicio.class);

        cargarPaises();

        btnRegistrar.setOnClickListener(v -> onClickRegistrar());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void cargarPaises() {
        List<Pais> paises = new ArrayList<>();
        paises.add(new Pais("AR","Argentina"));
        paises.add(new Pais("BO","Bolivia"));
        paises.add(new Pais("BR","Brasil"));
        paises.add(new Pais("CA","Canadá"));
        paises.add(new Pais("CL","Chile"));
        paises.add(new Pais("CO","Colombia"));
        paises.add(new Pais("CR","Costa Rica"));
        paises.add(new Pais("CU","Cuba"));
        paises.add(new Pais("DO","República Dominicana"));
        paises.add(new Pais("EC","Ecuador"));
        paises.add(new Pais("SV","El Salvador"));
        paises.add(new Pais("US","Estados Unidos"));
        paises.add(new Pais("GT","Guatemala"));
        paises.add(new Pais("HN","Honduras"));
        paises.add(new Pais("JM","Jamaica"));
        paises.add(new Pais("MX","México"));
        paises.add(new Pais("NI","Nicaragua"));
        paises.add(new Pais("PA","Panamá"));
        paises.add(new Pais("PY","Paraguay"));
        paises.add(new Pais("PE","Perú"));
        paises.add(new Pais("PR","Puerto Rico"));
        paises.add(new Pais("UY","Uruguay"));
        paises.add(new Pais("VE","Venezuela"));

        Collections.sort(paises, Comparator.comparing(Pais::getNombre, String.CASE_INSENSITIVE_ORDER));

        ArrayAdapter<Pais> adapter = new ArrayAdapter<>(
                this,
                R.layout.item_spinner_selected,
                paises
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPais.setAdapter(adapter);
    }

    private void onClickRegistrar() {
        String usuario = etUsuario.getText().toString().trim();
        String correo  = etCorreoReg.getText().toString().trim();
        String pass    = etContraseniaReg.getText().toString();

        if (!validarNombre(usuario) ||
                !validarCorreo(correo) ||
                !validarContrasenia(pass) ||
                spinnerPais.getSelectedItem() == null) {
            Toast.makeText(this, "Llene los campos correctamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre(usuario);
        dto.setCorreo(correo);
        dto.setNombreUsuario(usuario);
        dto.setContrasenia(pass);
        dto.setPais(((Pais)spinnerPais.getSelectedItem()).getCodigo());
        dto.setEsAdmin(false);
        dto.setEsArtista(false);

        usuarioServicio.registrarUsuario(dto).enqueue(new Callback<RespuestaCliente>() {
            @Override
            public void onResponse(Call<RespuestaCliente> call, Response<RespuestaCliente> resp) {
                if (resp.isSuccessful() && resp.body() != null && resp.body().isSuccess()) {
                    Toast.makeText(RegistroActivity.this, "Usuario registrado correctamente.", Toast.LENGTH_SHORT).show();
                    iniciarSesionAutomatica(correo, pass);
                } else {
                    String msg = (resp.body() != null) ? resp.body().getMensaje() : "Error desconocido";
                    Toast.makeText(RegistroActivity.this, "Error al registrar usuario: " + msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RespuestaCliente> call, Throwable t) {
                ManejoErrores.mostrarToastError(RegistroActivity.this, t);
            }
        });
    }

    private void iniciarSesionAutomatica(String correo, String pass) {
        LoginRequest req = new LoginRequest(correo, pass);
        usuarioServicio.iniciarSesion(req).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> resp) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    Toast.makeText(RegistroActivity.this, "Error al iniciar sesión automática", Toast.LENGTH_SHORT).show();
                    return;
                }

                JsonObject datos = resp.body().getAsJsonObject("datos");
                if (datos == null || !datos.has("token")) {
                    Toast.makeText(RegistroActivity.this, "Respuesta inválida al iniciar sesión", Toast.LENGTH_SHORT).show();
                    return;
                }

                String token = datos.get("token").getAsString();
                ApiCliente.setToken(token);
                SesionUsuario.setToken(token);

                SesionUsuario.setIdUsuario(datos.get("idUsuario").getAsInt());
                SesionUsuario.setNombre(datos.get("nombre").getAsString());
                SesionUsuario.setNombreUsuario(datos.get("nombreUsuario").getAsString());
                SesionUsuario.setCorreo(datos.get("correo").getAsString());
                SesionUsuario.setPais(datos.get("pais").getAsString());
                SesionUsuario.setAdmin(datos.get("esAdmin").getAsBoolean());
                SesionUsuario.setArtista(datos.get("esArtista").getAsBoolean());

                startActivity(new Intent(RegistroActivity.this,
                        com.example.musapiapp.activities.menu.MenuPrincipalActivity.class));
                finish();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                ManejoErrores.mostrarToastError(RegistroActivity.this, t);
            }
        });
    }

    private boolean validarNombre(String nombre) {
        if (nombre.length() < 3 || nombre.length() > 30) {
            etUsuario.setError("3–30 letras, dígitos, '_' o '.'");
            return false;
        }
        for (char c : nombre.toCharArray()) {
            if (!Character.isLetterOrDigit(c) && c != '_' && c != '.') {
                etUsuario.setError("Caracteres inválidos");
                return false;
            }
        }
        etUsuario.setError(null);
        return true;
    }

    private boolean validarCorreo(String correo) {
        if (correo.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreoReg.setError("Correo inválido");
            return false;
        }
        etCorreoReg.setError(null);
        return true;
    }

    private boolean validarContrasenia(String pass) {
        if (pass.length() < 8 || pass.length() > 100) {
            etContraseniaReg.setError("8–100 caracteres");
            return false;
        }
        int num = 0, may = 0, min = 0;
        for (char c : pass.toCharArray()) {
            if (Character.isDigit(c)) num++;
            else if (Character.isUpperCase(c)) may++;
            else if (Character.isLowerCase(c)) min++;
        }
        if (num > 0 && may > 0 && min > 0) {
            etContraseniaReg.setError(null);
            return true;
        }
        etContraseniaReg.setError("Debe incluir número, mayúsc. y minúsc.");
        return false;
    }
}
