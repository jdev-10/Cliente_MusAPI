package com.example.musapiapp.activities.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musapiapp.R;
import com.example.musapiapp.activities.busqueda.BusquedaUsuarioActivity;
import com.example.musapiapp.util.ManejoErrores;

public class MenuAdminActivity extends AppCompatActivity {

    private Button btnCategoriasMusicales;
    private Button btnReportes;
    private Button btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_admin);

        btnCategoriasMusicales = findViewById(R.id.btnCategoriasMusicales);
        btnReportes = findViewById(R.id.btnReportes);
        btnVolver = findViewById(R.id.btnVolver);
        Button btnBuscarUsuarios = findViewById(R.id.btnBuscarUsuarios);
        EditText etBusquedaUsuarios = findViewById(R.id.etBusquedaUsuarios);

        btnBuscarUsuarios.setOnClickListener(v -> {
            String texto = etBusquedaUsuarios.getText().toString().trim();
            if (texto.isEmpty()) {
                Toast.makeText(MenuAdminActivity.this, "Escribe algo para buscar", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(MenuAdminActivity.this, BusquedaUsuarioActivity.class);
            intent.putExtra("query", texto);
            startActivity(intent);
        });

        btnCategoriasMusicales.setOnClickListener(view -> {
            Intent intent = new Intent(MenuAdminActivity.this, CategoriasMusicalesActivity.class);
            startActivity(intent);
        });

        btnReportes.setOnClickListener(view -> {
            Intent intent = new Intent(MenuAdminActivity.this, ReportesActivity.class);
            startActivity(intent);
        });

        btnVolver.setOnClickListener(view -> {
            Intent intent = new Intent(MenuAdminActivity.this, MenuPrincipalActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
