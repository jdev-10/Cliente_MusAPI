package com.example.musapiapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musapiapp.R;
import com.example.musapiapp.dialogs.SeleccionarListaDialogFragment;
import com.example.musapiapp.dto.ListaDeReproduccionDTO;
import com.example.musapiapp.util.Constantes;

import java.util.List;

public class ListaAdapter extends RecyclerView.Adapter<ListaAdapter.ViewHolder> {

    private final List<ListaDeReproduccionDTO> listas;
    private final SeleccionarListaDialogFragment.ListaSeleccionadaListener listener;

    public ListaAdapter(List<ListaDeReproduccionDTO> listas, SeleccionarListaDialogFragment.ListaSeleccionadaListener listener) {
        this.listas = listas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lista_seleccion, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListaDeReproduccionDTO lista = listas.get(position);
        holder.txtNombre.setText(lista.getNombre());
        holder.txtDescripcion.setText(lista.getDescripcion());

        if (lista.getUrlFoto() != null && !lista.getUrlFoto().isEmpty()) {
            Constantes.CargarImagen(lista.getUrlFoto(), holder.imgFoto);
        }

        holder.itemView.setOnClickListener(v -> listener.onListaSeleccionada(lista));
    }

    @Override
    public int getItemCount() {
        return listas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtDescripcion;
        ImageView imgFoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreLista);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcionLista);
            imgFoto = itemView.findViewById(R.id.imgFotoLista);
        }
    }
}
