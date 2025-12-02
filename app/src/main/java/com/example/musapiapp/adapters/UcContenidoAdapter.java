package com.example.musapiapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

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

public class UcContenidoAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // --- DEFINICIÓN DE TIPOS DE VISTA ---
    private static final int VIEW_TYPE_LIST = 0;   // Canciones, Listas, Admin (Cápsula rectangular)
    private static final int VIEW_TYPE_ARTIST = 1; // Artistas (Circular)
    private static final int VIEW_TYPE_ALBUM = 2;  // Álbumes (Tarjeta cuadrada)

    private final List<T> items;
    private final String tipo;
    private final boolean showSave;
    private final Context context;
    private int idArtista = -1;
    private List<BusquedaCancionDTO> listaCanciones;

    private int indice = 0;
    public void setIndice(int indice) {
        this.indice = indice;
    }

    public UcContenidoAdapter(Context context, List<T> items, String tipo, boolean showSave) {
        this.context = context;
        this.items = items;
        this.tipo = tipo;
        this.showSave = showSave;
    }

    public void setIdArtista(int idArtista) { this.idArtista = idArtista; }
    public void setListaCanciones(List<BusquedaCancionDTO> canciones) { this.listaCanciones = canciones; }

    // --- SELECCIÓN DEL LAYOUT SEGÚN EL TIPO ---
    @Override
    public int getItemViewType(int position) {
        if ("ARTISTA".equals(tipo)) {
            return VIEW_TYPE_ARTIST;
        } else if ("ALBUM".equals(tipo) || "ALBUM_PENDIENTE".equals(tipo)) {
            return VIEW_TYPE_ALBUM;
        } else {
            return VIEW_TYPE_LIST; // Canciones, Listas, Admin
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_ARTIST) {
            // Layout circular para artistas
            View view = inflater.inflate(R.layout.item_circular, parent, false);
            return new ArtistViewHolder(view);
        } else if (viewType == VIEW_TYPE_ALBUM) {
            // Layout tarjeta cuadrada para álbumes
            View view = inflater.inflate(R.layout.item_album_cuadrado, parent, false);
            return new AlbumViewHolder(view);
        } else {
            // Layout cápsula estándar para todo lo demás
            View view = inflater.inflate(R.layout.item_uc_contenido, parent, false);
            return new ListViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // 1. Obtenemos el tipo de vista para esta posición
        int viewType = getItemViewType(position);

        // 2. Obtenemos el objeto
        T item = items.get(position);

        // 3. Hacemos el cast seguro basándonos en el tipo, no en la clase
        if (viewType == VIEW_TYPE_ARTIST) {
            ((ArtistViewHolder) holder).bind(item);
        } else if (viewType == VIEW_TYPE_ALBUM) {
            ((AlbumViewHolder) holder).bind(item);
        } else {
            // VIEW_TYPE_LIST
            ((ListViewHolder) holder).bind(item);
        }
    }

    @Override
    public int getItemCount() { return items.size(); }

    // =================================================================================
    // 1. VIEWHOLDER LISTA (CANCIONES, LISTAS, ADMIN) - item_uc_contenido.xml
    // =================================================================================
    public class ListViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgFoto;
        private final TextView tvNombre;
        private final TextView tvAutor;
        private final MaterialButton btnGuardar;
        private final MaterialButton btnDetalles;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFoto = itemView.findViewById(R.id.imgFoto);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvAutor = itemView.findViewById(R.id.tvAutor);
            btnGuardar = itemView.findViewById(R.id.btnGuardar);
            btnDetalles = itemView.findViewById(R.id.btnDetalles);
        }

        public void bind(T item) {
            String nombre = "", autor = "", urlFoto = "";
            boolean esAdmin = "USUARIO_ADMIN".equals(tipo);

            // -- Lógica de Datos --
            if ("CANCION".equals(tipo)) {
                BusquedaCancionDTO c = (BusquedaCancionDTO) item;
                nombre = c.getNombre();
                autor = c.getNombreArtista();
                urlFoto = c.getUrlFoto();
            } else if ("LISTA".equals(tipo)) {
                ListaDeReproduccionDTO l = (ListaDeReproduccionDTO) item;
                nombre = l.getNombre();
                urlFoto = l.getUrlFoto();
                tvAutor.setVisibility(View.GONE);
            } else if (esAdmin) {
                BusquedaUsuarioDTO u = (BusquedaUsuarioDTO) item;
                nombre = u.getNombre();
                autor = u.getNombreUsuario();
                urlFoto = null;
            }

            // -- Asignación a la UI --
            tvNombre.setText(nombre);
            tvAutor.setText(autor);

            if (urlFoto != null && !urlFoto.isEmpty()) {
                Constantes.CargarImagen(urlFoto, imgFoto);
            } else {
                imgFoto.setBackgroundColor(0xFFCCCCCC);
            }

            // -- Lógica de Botones --
            if (esAdmin) {
                // Modo Admin: Ocultar guardar, Botón Eliminar Rojo
                btnGuardar.setVisibility(View.GONE);
                btnDetalles.setIconResource(R.drawable.ic_eliminar);
                btnDetalles.setIconTint(ColorStateList.valueOf(0xFFB80000)); // Rojo
                btnDetalles.setOnClickListener(v -> irADetalles(item)); // Ir a activity de eliminar
            } else {
                // Modo Normal: Mostrar guardar, Botón Opciones Negro
                btnGuardar.setVisibility(showSave ? View.VISIBLE : View.GONE);

                btnDetalles.setIconResource(R.drawable.ic_opciones);
                btnDetalles.setIconTint(ColorStateList.valueOf(0xFF000000)); // Negro

                // Usamos métodos locales dentro del ViewHolder
                btnGuardar.setOnClickListener(v -> onClickGuardar(item));
                btnDetalles.setOnClickListener(v -> irADetalles(item));

                // Click en la fila completa para reproducir
                itemView.setOnClickListener(v -> reproducirItem(getAdapterPosition()));
            }
        }

        // --- LÓGICA DE GUARDADO (MovidA AQUÍ DENTRO) ---
        private void onClickGuardar(T item) {
            // Caso 1: Guardar CANCIÓN en una Lista de Reproducción
            if ("CANCION".equals(tipo)) {
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
                                // Diálogo para seleccionar lista
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
                                                // Cambio visual del botón
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
            // Caso 2: Guardar ÁLBUM o LISTA en Favoritos (Contenido Guardado)
            else {
                ContenidoGuardadoDTO dto = new ContenidoGuardadoDTO();
                dto.setIdUsuario(SesionUsuario.getIdUsuario());
                dto.setTipoDeContenido(tipo);

                if (item instanceof BusquedaAlbumDTO) {
                    dto.setIdContenidoGuardado(((BusquedaAlbumDTO) item).getIdAlbum());
                } else if (item instanceof ListaDeReproduccionDTO) {
                    dto.setIdContenidoGuardado(((ListaDeReproduccionDTO) item).getIdListaDeReproduccion());
                } else {
                    return; // No soportado
                }

                ApiCliente.getClient().create(ContenidoGuardadoServicio.class).guardarContenido(dto)
                        .enqueue(new Callback<RespuestaApi<String>>() {
                            @Override
                            public void onResponse(Call<RespuestaApi<String>> call, Response<RespuestaApi<String>> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    Toast.makeText(context, response.body().getMensaje(), Toast.LENGTH_SHORT).show();
                                    if (response.body().getMensaje().contains("exitosamente")) {
                                        btnGuardar.setVisibility(View.GONE); // O cambiar ícono si prefieres
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
    }

    // =================================================================================
    // 2. VIEWHOLDER ARTISTA (CIRCULAR) - item_circular.xml
    // =================================================================================
    public class ArtistViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgCircular;
        private final TextView tvTituloCircular;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCircular = itemView.findViewById(R.id.imgCircular);
            tvTituloCircular = itemView.findViewById(R.id.tvTituloCircular);
        }

        public void bind(T item) {
            BusquedaArtistaDTO artista = (BusquedaArtistaDTO) item;
            tvTituloCircular.setText(artista.getNombre());

            if (artista.getUrlFoto() != null && !artista.getUrlFoto().isEmpty()) {
                Constantes.CargarImagen(artista.getUrlFoto(), imgCircular);
            } else {
                imgCircular.setBackgroundColor(0xFF333333);
            }
            // Click abre el perfil del artista
            itemView.setOnClickListener(v -> irADetalles(item));
        }
    }

    // =================================================================================
    // 3. VIEWHOLDER ALBUM (TARJETA) - item_album_cuadrado.xml
    // =================================================================================
    public class AlbumViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgAlbum;
        private final TextView tvTituloAlbum;
        private final TextView tvArtistaAlbum;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAlbum = itemView.findViewById(R.id.imgAlbum);
            tvTituloAlbum = itemView.findViewById(R.id.tvTituloAlbum);
            tvArtistaAlbum = itemView.findViewById(R.id.tvArtistaAlbum);
        }

        public void bind(T item) {
            String nombre = "", artista = "", urlFoto = "";

            if ("ALBUM".equals(tipo)) {
                BusquedaAlbumDTO alb = (BusquedaAlbumDTO) item;
                nombre = alb.getNombreAlbum();
                artista = alb.getNombreArtista();
                urlFoto = alb.getUrlFoto();
            } else if ("ALBUM_PENDIENTE".equals(tipo)) {
                InfoAlbumDTO albP = (InfoAlbumDTO) item;
                nombre = albP.getNombre();
                artista = "Pendiente";
                urlFoto = albP.getUrlFoto();
            }

            tvTituloAlbum.setText(nombre);
            tvArtistaAlbum.setText(artista);

            if (urlFoto != null && !urlFoto.isEmpty()) {
                Constantes.CargarImagen(urlFoto, imgAlbum);
            } else {
                imgAlbum.setBackgroundColor(0xFF333333);
            }

            // Click reproduce, Long Click abre detalles
            itemView.setOnClickListener(v -> reproducirItem(getAdapterPosition()));
            itemView.setOnLongClickListener(v -> {
                irADetalles(item);
                return true;
            });
        }
    }

    // =================================================================================
    // MÉTODOS AUXILIARES (Navegación y Reproducción)
    // =================================================================================

    private void irADetalles(T item) {
        Intent intent = null;
        switch (tipo) {
            case "CANCION":
                intent = new Intent(context, DetalleCancionActivity.class);
                intent.putExtra("cancion", (BusquedaCancionDTO) item);
                break;
            case "ARTISTA":
                intent = new Intent(context, PerfilArtistaActivity.class);
                intent.putExtra("artista", (BusquedaArtistaDTO) item);
                break;
            case "LISTA":
                intent = new Intent(context, ListaDetalleActivity.class);
                intent.putExtra("lista", new Gson().toJson((ListaDeReproduccionDTO) item));
                break;
            case "USUARIO_ADMIN":
                intent = new Intent(context, EliminarUsuarioActivity.class);
                intent.putExtra("usuario", (BusquedaUsuarioDTO) item);
                break;
            case "ALBUM":
            case "ALBUM_PENDIENTE":
                // Lógica compartida para ambos tipos de album
                intent = new Intent(context, AlbumDetalleActivity.class);
                if (item instanceof BusquedaAlbumDTO) {
                    intent.putExtra("albumPublico", new Gson().toJson((BusquedaAlbumDTO) item));
                } else {
                    intent.putExtra("albumPendiente", new Gson().toJson((InfoAlbumDTO) item));
                    intent.putExtra("idArtista", idArtista);
                }
                break;
        }
        if (intent != null) context.startActivity(intent);
    }

    private void reproducirItem(int pos) {
        if (pos == RecyclerView.NO_POSITION) return;

        if ("CANCION".equals(tipo) && listaCanciones != null) {
            ArrayList<BusquedaCancionDTO> lista = new ArrayList<>();
            // Nota: Aquí se asume que la posición en 'items' coincide con 'listaCanciones'
            // O que se usa la lista filtrada global.
            lista.add(listaCanciones.get(pos));
            Reproductor.reproducirCancion(lista, 0, context);
            context.startActivity(new Intent(context, ReproductorActivity.class));
        }
        else if ("ALBUM".equals(tipo)) {
            List<BusquedaAlbumDTO> albumes = (List<BusquedaAlbumDTO>) items;
            ArrayList<BusquedaCancionDTO> canciones = new ArrayList<>(albumes.get(pos).getCanciones());
            Reproductor.reproducirCancion(canciones, 0, context);
        }
        else if ("LISTA".equals(tipo)) {
            List<ListaDeReproduccionDTO> listas = (List<ListaDeReproduccionDTO>) items;
            // Verificamos si la lista tiene canciones cargadas
            if (listas.get(pos).getCanciones() != null && !listas.get(pos).getCanciones().isEmpty()) {
                ArrayList<BusquedaCancionDTO> canciones = new ArrayList<>(listas.get(pos).getCanciones());
                Reproductor.reproducirCancion(canciones, 0, context);
            } else {
                Toast.makeText(context, "La lista está vacía", Toast.LENGTH_SHORT).show();
            }
        }
    }
}