package com.example.musapiapp.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.musapiapp.R;
import com.example.musapiapp.dto.EvaluacionDTO;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.servicios.UsuarioServicio;
import com.example.musapiapp.util.ManejoErrores;
import com.example.musapiapp.util.SesionUsuario;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialogEvaluarArtista extends DialogFragment {

    private int idArtista;
    private int calificacion = 0;

    public DialogEvaluarArtista(int idArtista) {
        this.idArtista = idArtista;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_evaluar_artista, container, false);

        LinearLayout layoutEstrellas = view.findViewById(R.id.layoutEstrellas);
        EditText etComentario = view.findViewById(R.id.etComentario);
        Button btnEnviar = view.findViewById(R.id.btnEnviar);
        Button btnCancelar = view.findViewById(R.id.btnCancelar);

        for (int i = 1; i <= 5; i++) {
            ImageView estrella = new ImageView(requireContext());
            
            estrella.setImageResource(R.drawable.ic_star_outline);
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100);
            params.setMargins(8, 0, 8, 0);
            estrella.setLayoutParams(params);
            
            estrella.setTag(i);
            estrella.setOnClickListener(v -> {
                calificacion = (int) v.getTag();
                actualizarEstrellas(layoutEstrellas);
            });
            layoutEstrellas.addView(estrella);
        }

        btnCancelar.setOnClickListener(v -> dismiss());

        btnEnviar.setOnClickListener(v -> {
            if (calificacion == 0) {
                Toast.makeText(requireContext(), "Selecciona una calificación", Toast.LENGTH_SHORT).show();
                return;
            }
            String comentario = etComentario.getText().toString().trim();

            EvaluacionDTO evaluacion = new EvaluacionDTO(
                    SesionUsuario.getIdUsuario(),
                    idArtista,
                    comentario,
                    calificacion
            );

            UsuarioServicio usuarioSvc = ApiCliente.getClient().create(UsuarioServicio.class);
            usuarioSvc.evaluarArtista(evaluacion)
                    .enqueue(new Callback<com.example.musapiapp.util.RespuestaApi<String>>() {
                        @Override
                        public void onResponse(Call<com.example.musapiapp.util.RespuestaApi<String>> call,
                                               Response<com.example.musapiapp.util.RespuestaApi<String>> response) {
                            if (!response.isSuccessful() || response.body() == null) {
                                Toast.makeText(requireContext(), "Error al enviar evaluación", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Toast.makeText(requireContext(), response.body().getDatos(), Toast.LENGTH_SHORT).show();
                            dismiss();
                        }

                        @Override
                        public void onFailure(Call<com.example.musapiapp.util.RespuestaApi<String>> call, Throwable t) {
                            ManejoErrores.mostrarToastError(requireContext(), t);
                        }
                    });
        });

        return view;
    }

    private void actualizarEstrellas(LinearLayout layoutEstrellas) {
        for (int i = 0; i < layoutEstrellas.getChildCount(); i++) {
            ImageView estrella = (ImageView) layoutEstrellas.getChildAt(i);
            if (i < calificacion) {
                
                estrella.setImageResource(R.drawable.ic_star_filled);
                estrella.setColorFilter(Color.parseColor("#ABFFBD"));
            } else {
                estrella.setImageResource(R.drawable.ic_star_outline);
                estrella.setColorFilter(Color.GRAY);
            }
        }
    }
}
