package com.example.musapiapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musapiapp.R;
import com.example.musapiapp.activities.contenido.*;
import com.example.musapiapp.activities.perfiles.PerfilArtistaActivity;
import com.example.musapiapp.dialogs.SeleccionarListaDialogFragment;
import com.example.musapiapp.dto.*;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.servicios.ContenidoGuardadoServicio;
import com.example.musapiapp.servicios.ListaServicio;
import com.example.musapiapp.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UcContenidoAdapter<T> extends RecyclerView.Adapter<UcContenidoAdapter<T>.ViewHolder> {

    private final List<T> items;
    private final String tipo;
    private final boolean showSave;
    private final Context context;
    private int idArtista = -1;
    private int indice = 0;
    private List<BusquedaCancionDTO> listaCanciones;

    public UcContenidoAdapter(Context context, List<T> items, String tipo, boolean showSave) {
        this.context = context;
        this.items = items;
        this.tipo = tipo;
        this.showSave = showSave;
    }

    public void setIdArtista(int idArtista) {
        this.idArtista = idArtista;
    }

    public void setListaCanciones(List<BusquedaCancionDTO> canciones) {
        this.listaCanciones = canciones;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_uc_contenido, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgFoto;
        private final TextView tvNombre;
        private final TextView tvAutor;
        private final MaterialButton btnGuardar;
        private final Button btnDetalles;
        private BusquedaAlbumDTO albumPublico;
        private InfoAlbumDTO albumPendiente;
        private ListaDeReproduccionDTO lista;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFoto = itemView.findViewById(R.id.imgFoto);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvAutor = itemView.findViewById(R.id.tvAutor);
            btnGuardar = itemView.findViewById(R.id.btnGuardar);
            btnDetalles = itemView.findViewById(R.id.btnDetalles);
        }

        public void bind(T item) {
            String nombre = "", autor = "", urlFoto = "";

            switch (tipo) {
                case "CANCION":
                    BusquedaCancionDTO c = (BusquedaCancionDTO) item;
                    nombre = c.getNombre();
                    autor = c.getNombreArtista();
                    urlFoto = c.getUrlFoto();
                    break;
                case "ALBUM":
                    albumPublico = (BusquedaAlbumDTO) item;
                    nombre = albumPublico.getNombreAlbum();
                    autor = albumPublico.getNombreArtista();
                    urlFoto = albumPublico.getUrlFoto();
                    break;
                case "ARTISTA":
                    BusquedaArtistaDTO art = (BusquedaArtistaDTO) item;
                    nombre = art.getNombre();
                    autor = "@" + art.getNombreUsuario();
                    urlFoto = art.getUrlFoto();
                    btnGuardar.setText("Seguir");
                    break;
                case "LISTA":
                    ListaDeReproduccionDTO l = (ListaDeReproduccionDTO) item;
                    nombre = l.getNombre();
                    urlFoto = l.getUrlFoto();
                    lista = l;
                    tvAutor.setVisibility(View.GONE);
                    break;
                case "ALBUM_PENDIENTE":
                    albumPendiente = (InfoAlbumDTO) item;
                    nombre = albumPendiente.getNombre();
                    urlFoto = albumPendiente.getUrlFoto();
                    tvAutor.setVisibility(View.GONE);
                    btnGuardar.setVisibility(View.GONE);
                    break;
                case "USUARIO_ADMIN":
                    BusquedaUsuarioDTO u = (BusquedaUsuarioDTO) item;
                    nombre = u.getNombre();
                    autor = u.getNombreUsuario() + " • " + u.getPais();
                    urlFoto = null;
                    btnGuardar.setVisibility(View.GONE);
                    btnDetalles.setText("Eliminar");
                    break;
            }

            tvNombre.setText(nombre);
            tvAutor.setText(autor);

            if (urlFoto != null && !urlFoto.isEmpty()) {
                Constantes.CargarImagen(urlFoto, imgFoto);
            } else {
                imgFoto.setBackgroundColor(0xFFCCCCCC);
            }

            btnGuardar.setVisibility(showSave ? View.VISIBLE : View.GONE);

            // Listeners de los botones pequeños
            btnGuardar.setOnClickListener(v -> onClickGuardar(item));
            btnDetalles.setOnClickListener(v -> onClickDetalles(item));

            // NUEVO: Listener en toda la fila (itemView)
            // Reemplaza al btnReproducir
            itemView.setOnClickListener(v -> {
                // Si es un tipo reproducible, reproduce.
                if (tipo.equals("CANCION") || tipo.equals("ALBUM") || tipo.equals("LISTA")) {
                    onClickReproducir(item);
                }
                // Si es un artista, usuario o pendiente (que no se reproducen),
                // hacemos que el clic abra los detalles para no dejar el clic "muerto".
                else {
                    onClickDetalles(item);
                }
            });
        }

        private void onClickGuardar(T item) {
            if (tipo.equals("CANCION")) {
                int idUsuario = SesionUsuario.getIdUsuario();

                ListaServicio listaServicio = ApiCliente.getClient().create(ListaServicio.class);
                listaServicio.obtenerListasPorUsuario(idUsuario)
                        .enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                if (!response.isSuccessful() || response.body() == null) {
                                    Toast.makeText(context, "Error al obtener listas", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                JsonArray datos = response.body().getAsJsonArray("datos");
                                List<ListaDeReproduccionDTO> listas = new Gson().fromJson(
                                        datos, new TypeToken<List<ListaDeReproduccionDTO>>() {}.getType()
                                );

                                if (listas == null || listas.isEmpty()) {
                                    Toast.makeText(context, "No tienes listas. Crea una primero.", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                new SeleccionarListaDialogFragment(listas, listaSeleccionada -> {
                                    BusquedaCancionDTO cancion = (BusquedaCancionDTO) item;

                                    ListaDeReproduccion_CancionDTO dto = new ListaDeReproduccion_CancionDTO(
                                            cancion.getIdCancion(),
                                            listaSeleccionada.getIdListaDeReproduccion(),
                                            SesionUsuario.getIdUsuario()
                                    );

                                    listaServicio.agregarCancionALista(dto).enqueue(new Callback<RespuestaCliente>() {
                                        @Override
                                        public void onResponse(Call<RespuestaCliente> call, Response<RespuestaCliente> response) {
                                            if (response.isSuccessful()) {
                                                Toast.makeText(context, "Canción agregada a la lista", Toast.LENGTH_SHORT).show();
                                                btnGuardar.setIconResource(R.drawable.ic_save_full);
                                            } else {
                                                Toast.makeText(context, "Error al agregar canción", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<RespuestaCliente> call, Throwable t) {
                                            ManejoErrores.mostrarToastError(context, t);
                                        }
                                    });

                                }).show(((AppCompatActivity) context).getSupportFragmentManager(), "SeleccionarLista");
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                ManejoErrores.mostrarToastError(context, t);
                            }
                        });
            }

            ContenidoGuardadoDTO dto = new ContenidoGuardadoDTO();
            dto.setIdUsuario(SesionUsuario.getIdUsuario());
            dto.setTipoDeContenido(tipo);

            switch (tipo) {
                case "ALBUM":
                    dto.setIdContenidoGuardado(((BusquedaAlbumDTO) item).getIdAlbum());
                    break;
                case "ARTISTA":
                    dto.setIdContenidoGuardado(((BusquedaArtistaDTO) item).getIdArtista());
                    break;
                case "LISTA":
                    dto.setIdContenidoGuardado(((ListaDeReproduccionDTO) item).getIdListaDeReproduccion());
                    break;
                default:
                    return;
            }

            if (!tipo.equals("CANCION")) {
                ApiCliente.getClient().create(ContenidoGuardadoServicio.class)
                        .guardarContenido(dto)
                        .enqueue(new Callback<RespuestaApi<String>>() {
                            @Override
                            public void onResponse(Call<RespuestaApi<String>> call, Response<RespuestaApi<String>> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    String mensaje = response.body().getMensaje();
                                    Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
                                    if (mensaje.equals("Contenido guardado exitosamente")) {
                                        btnGuardar.setVisibility(View.GONE);
                                        btnGuardar.setIconResource(R.drawable.ic_save_full);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<RespuestaApi<String>> call, Throwable t) {
                                ManejoErrores.mostrarToastError(context, t);
                            }
                        });
            }
        }

        private void onClickDetalles(T item) {
            // (Sin cambios en este método...)
            Intent intent = null;
            switch (tipo) {
                case "CANCION":
                    intent = new Intent(context, DetalleCancionActivity.class);
                    intent.putExtra("cancion", (BusquedaCancionDTO) item);
                    break;
                case "ALBUM":
                    intent = new Intent(context, AlbumDetalleActivity.class);
                    intent.putExtra("albumPublico", new Gson().toJson(albumPublico));
                    break;
                case "ALBUM_PENDIENTE":
                    intent = new Intent(context, AlbumDetalleActivity.class);
                    intent.putExtra("albumPendiente", new Gson().toJson(albumPendiente));
                    intent.putExtra("idArtista", idArtista);
                    break;
                case "ARTISTA":
                    intent = new Intent(context, PerfilArtistaActivity.class);
                    intent.putExtra("artista", (BusquedaArtistaDTO) item);
                    break;
                case "LISTA":
                    intent = new Intent(context, ListaDetalleActivity.class);
                    intent.putExtra("lista", new Gson().toJson(lista));
                    break;
                case "USUARIO_ADMIN":
                    intent = new Intent(context, EliminarUsuarioActivity.class);
                    intent.putExtra("usuario", (BusquedaUsuarioDTO) item);
                    break;
            }

            if (intent != null) context.startActivity(intent);
        }

        private void onClickReproducir(T item) {
            if (tipo.equals("CANCION") && listaCanciones != null) {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    ArrayList<BusquedaCancionDTO> lista = new ArrayList<>();
                    lista.add(listaCanciones.get(pos));
                    Reproductor.reproducirCancion(lista, 0, context);
                    context.startActivity(new Intent(context, ReproductorActivity.class));
                }
            } else if (tipo.equals("ALBUM")) {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    List<BusquedaAlbumDTO> albumes = (List<BusquedaAlbumDTO>) items;
                    ArrayList<BusquedaCancionDTO> canciones = new ArrayList<>(albumes.get(pos).getCanciones());
                    Reproductor.reproducirCancion(canciones, 0, context);
                }
            } else if (tipo.equals("LISTA")) {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    List<ListaDeReproduccionDTO> listas = (List<ListaDeReproduccionDTO>) items;
                    ArrayList<BusquedaCancionDTO> canciones = new ArrayList<>(listas.get(pos).getCanciones());
                    Reproductor.reproducirCancion(canciones, 0, context);
                }
            }
        }
    }
}
