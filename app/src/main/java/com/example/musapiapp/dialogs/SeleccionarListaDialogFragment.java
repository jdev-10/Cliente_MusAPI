package com.example.musapiapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musapiapp.R;
import com.example.musapiapp.adapters.ListaAdapter;
import com.example.musapiapp.dto.ListaDeReproduccionDTO;

import java.util.List;

public class SeleccionarListaDialogFragment extends DialogFragment {

    private List<ListaDeReproduccionDTO> listas;
    private ListaSeleccionadaListener listener;

    public interface ListaSeleccionadaListener {
        void onListaSeleccionada(ListaDeReproduccionDTO lista);
    }

    public SeleccionarListaDialogFragment(List<ListaDeReproduccionDTO> listas, ListaSeleccionadaListener listener) {
        this.listas = listas;
        this.listener = listener;
    }

    @Nullable
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_seleccionar_lista, null);

        RecyclerView recycler = view.findViewById(R.id.recyclerListas);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(new ListaAdapter(listas, listener));

        builder.setView(view)
                .setTitle("Seleccionar lista")
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        return builder.create();
    }
}
