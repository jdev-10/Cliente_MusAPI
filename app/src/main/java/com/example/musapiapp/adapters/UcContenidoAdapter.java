package com.example.musapiapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.musapiapp.R;
import com.example.musapiapp.activities.contenido.AlbumDetalleActivity;
import com.example.musapiapp.activities.contenido.DetalleCancionActivity;
import com.example.musapiapp.activities.contenido.EliminarUsuarioActivity;
import com.example.musapiapp.activities.contenido.ListaDetalleActivity;
import com.example.musapiapp.activities.contenido.ReproductorActivity;
import com.example.musapiapp.activities.perfiles.PerfilArtistaActivity;
import com.example.musapiapp.dialogs.SeleccionarListaDialogFragment;
import com.example.musapiapp.dto.BusquedaAlbumDTO;
import com.example.musapiapp.dto.BusquedaArtistaDTO;
import com.example.musapiapp.dto.BusquedaCancionDTO;
import com.example.musapiapp.dto.BusquedaUsuarioDTO;
import com.example.musapiapp.dto.ContenidoGuardadoDTO;
import com.example.musapiapp.dto.EscuchaDTO;
import com.example.musapiapp.dto.InfoAlbumDTO;
import com.example.musapiapp.dto.ListaDeReproduccionDTO;
import com.example.musapiapp.dto.ListaDeReproduccion_CancionDTO;
import com.example.musapiapp.dto.RespuestaCliente;
import com.example.musapiapp.network.ApiCliente;
import com.example.musapiapp.servicios.ContenidoGuardadoServicio;
import com.example.musapiapp.servicios.ListaServicio;
import com.example.musapiapp.util.Constantes;
import com.example.musapiapp.util.ManejoErrores;
import com.example.musapiapp.util.Reproductor;
import com.example.musapiapp.util.RespuestaApi;
import com.example.musapiapp.util.SesionUsuario;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.res.ColorStateList;
import android.util.Log;


public class UcContenidoAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_LIST = 0;
    private static final int VIEW_TYPE_ARTIST = 1;
    private static final int VIEW_TYPE_ALBUM = 2;

    private final List<T> items;
    private final String tipo;
    private final boolean showSave;
    private final Context context;
    private int idArtista = -1;

    private List<BusquedaCancionDTO> listaCanciones;

    private int indice = 0;
    public void setIndice(int indice) { this.indice = indice; }

    public UcContenidoAdapter(Context context, List<T> items, String tipo, boolean showSave) {
        this.context = context;
        this.items = items;
        this.tipo = tipo;
        this.showSave = showSave;
    }

    public void setIdArtista(int idArtista) { this.idArtista = idArtista; }
    public void setListaCanciones(List<BusquedaCancionDTO> canciones) { this.listaCanciones = canciones; }

    @Override
    public int getItemViewType(int position) {
        if ("ARTISTA".equals(tipo)) return VIEW_TYPE_ARTIST;
        else if ("ALBUM".equals(tipo) || "ALBUM_PENDIENTE".equals(tipo)) return VIEW_TYPE_ALBUM;
        else return VIEW_TYPE_LIST;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_ARTIST) {
            View view = inflater.inflate(R.layout.item_circular, parent, false);
            return new ArtistViewHolder(view);
        } else if (viewType == VIEW_TYPE_ALBUM) {
            View view = inflater.inflate(R.layout.item_album_cuadrado, parent, false);
            return new AlbumViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_uc_contenido, parent, false);
            return new ListViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        T item = items.get(position);

        if (viewType == VIEW_TYPE_ARTIST) {
            ((ArtistViewHolder) holder).bind(item);
        } else if (viewType == VIEW_TYPE_ALBUM) {
            ((AlbumViewHolder) holder).bind(item);
        } else {
            ((ListViewHolder) holder).bind(item);
        }
    }

    @Override
    public int getItemCount() { return items.size(); }

    private void cargarImagenConGlide(String url, ImageView imageView, int placeholder) {

        imageView.setImageResource(placeholder);
        imageView.setImageTintList(ColorStateList.valueOf(0x80FFFFFF));

        if (url == null || url.trim().isEmpty()) {
            Log.d("UC_IMG", "urlFoto VACIA -> placeholder");
            return;
        }

        try {
            String cleanUrl = url.trim();

            String finalUrl = (cleanUrl.startsWith("http://") || cleanUrl.startsWith("https://"))
                    ? cleanUrl
                    : (Constantes.URL_BASE + cleanUrl);

            String token = SesionUsuario.getToken();
            String bearer = (token != null && !token.trim().isEmpty()) ? "Bearer " + token.trim() : "";

            GlideUrl glideUrl = new GlideUrl(finalUrl, new LazyHeaders.Builder()
                    .addHeader("Authorization", bearer)
                    .build());

            imageView.setImageTintList(null);

            Glide.with(context)
                    .load(glideUrl)
                    .placeholder(placeholder)
                    .error(placeholder)
                    .centerCrop()
                    .into(imageView);

            Log.d("UC_IMG", "Cargando imagen: " + finalUrl);

        } catch (Exception e) {

            imageView.setImageResource(placeholder);
            imageView.setImageTintList(ColorStateList.valueOf(0x80FFFFFF));
            Log.e("UC_IMG", "Error cargando imagen: " + e.getMessage(), e);
        }
    }

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

            tvAutor.setVisibility(View.VISIBLE);

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

            tvNombre.setText(nombre);
            tvAutor.setText(autor);

            if (!esAdmin) {
                cargarImagenConGlide(urlFoto, imgFoto, R.drawable.ic_album);
            }

            if (esAdmin) {
                btnGuardar.setVisibility(View.GONE);
                btnDetalles.setIconResource(R.drawable.ic_back_black);
                btnDetalles.setIconTint(ColorStateList.valueOf(0xFFB80000));
                btnDetalles.setOnClickListener(v -> irADetalles(item));

                itemView.setOnClickListener(v -> irADetalles(item));

            } else {
                btnGuardar.setVisibility(showSave ? View.VISIBLE : View.GONE);
                btnDetalles.setIconResource(R.drawable.ic_opciones);
                btnDetalles.setIconTint(ColorStateList.valueOf(0xFFFFFFFF));

                btnGuardar.setOnClickListener(v -> onClickGuardar(item));
                btnDetalles.setOnClickListener(v -> irADetalles(item));

                itemView.setOnClickListener(v -> reproducirItem(getBindingAdapterPosition()));
            }
        }

        private void onClickGuardar(T item) {
            if ("CANCION".equals(tipo)) {
                int idUsuario = SesionUsuario.getIdUsuario();
                ListaServicio listaServicio = ApiCliente.getClient().create(ListaServicio.class);

                listaServicio.obtenerListasPorUsuario(idUsuario).enqueue(new Callback<JsonObject>() {
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
                                        Toast.makeText(context, "Canción agregada", Toast.LENGTH_SHORT).show();
                                        btnGuardar.setIconResource(R.drawable.ic_save_full);
                                    } else {
                                        Toast.makeText(context, "Error al agregar", Toast.LENGTH_SHORT).show();
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

            } else {
                ContenidoGuardadoDTO dto = new ContenidoGuardadoDTO();
                dto.setIdUsuario(SesionUsuario.getIdUsuario());
                dto.setTipoDeContenido(tipo);

                if (item instanceof BusquedaAlbumDTO) {
                    dto.setIdContenidoGuardado(((BusquedaAlbumDTO) item).getIdAlbum());
                } else if (item instanceof ListaDeReproduccionDTO) {
                    dto.setIdContenidoGuardado(((ListaDeReproduccionDTO) item).getIdListaDeReproduccion());
                } else return;

                ApiCliente.getClient().create(ContenidoGuardadoServicio.class).guardarContenido(dto)
                        .enqueue(new Callback<RespuestaApi<String>>() {
                            @Override
                            public void onResponse(Call<RespuestaApi<String>> call, Response<RespuestaApi<String>> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    Toast.makeText(context, response.body().getMensaje(), Toast.LENGTH_SHORT).show();
                                    if (response.body().getMensaje().contains("exitosamente")) {
                                        btnGuardar.setVisibility(View.GONE);
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

            cargarImagenConGlide(artista.getUrlFoto(), imgCircular, R.drawable.ic_usuario_perfil);

            itemView.setOnClickListener(v -> irADetalles(item));
        }
    }
    public class AlbumViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgAlbum;
        private final TextView tvTituloAlbum;
        private final TextView tvArtistaAlbum;
        private final MaterialButton btnGuardarAlbum;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAlbum = itemView.findViewById(R.id.imgAlbum);
            tvTituloAlbum = itemView.findViewById(R.id.tvTituloAlbum);
            tvArtistaAlbum = itemView.findViewById(R.id.tvArtistaAlbum);
            btnGuardarAlbum = itemView.findViewById(R.id.btnGuardarAlbum);
        }

        public void bind(T item) {
            String nombre = "", artista = "", urlFoto = "";
            btnGuardarAlbum.setVisibility(View.GONE);
            btnGuardarAlbum.setEnabled(true);
            btnGuardarAlbum.setOnClickListener(null);

            if ("ALBUM".equals(tipo)) {
                BusquedaAlbumDTO alb = (BusquedaAlbumDTO) item;
                nombre = alb.getNombreAlbum();
                artista = alb.getNombreArtista();
                urlFoto = alb.getUrlFoto();

                if (showSave) {
                    btnGuardarAlbum.setVisibility(View.VISIBLE);
                    btnGuardarAlbum.setOnClickListener(v ->
                            guardarAlbum(alb.getIdAlbum(), btnGuardarAlbum)
                    );
                }

            } else if ("ALBUM_PENDIENTE".equals(tipo)) {
                InfoAlbumDTO albP = (InfoAlbumDTO) item;
                nombre = albP.getNombre();
                artista = "Pendiente";
                urlFoto = albP.getUrlFoto();
            }

            tvTituloAlbum.setText(nombre);
            tvArtistaAlbum.setText(artista);

            cargarImagenConGlide(urlFoto, imgAlbum, R.drawable.ic_album);

            itemView.setOnClickListener(v -> reproducirItem(getBindingAdapterPosition()));

            itemView.setOnLongClickListener(v -> {
                irADetalles(item);
                return true;
            });
        }
    }

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

        if ("CANCION".equals(tipo)) {
            ArrayList<BusquedaCancionDTO> listaParaReproducir;

            if (listaCanciones != null && !listaCanciones.isEmpty()) {
                listaParaReproducir = new ArrayList<>(listaCanciones);
            } else {
                try {
                    listaParaReproducir = new ArrayList<>((List<BusquedaCancionDTO>) items);
                } catch (Exception e) {
                    listaParaReproducir = new ArrayList<>();
                    listaParaReproducir.add((BusquedaCancionDTO) items.get(pos));
                }
            }

            if (pos < listaParaReproducir.size()) {
                Reproductor.reproducirCancion(listaParaReproducir, pos, context);
                context.startActivity(new Intent(context, ReproductorActivity.class));
            }
        }
        else if ("ALBUM".equals(tipo)) {
            List<BusquedaAlbumDTO> albumes = (List<BusquedaAlbumDTO>) items;
            if (albumes.get(pos).getCanciones() != null && !albumes.get(pos).getCanciones().isEmpty()) {
                ArrayList<BusquedaCancionDTO> canciones = new ArrayList<>(albumes.get(pos).getCanciones());
                Reproductor.reproducirCancion(canciones, 0, context);
                context.startActivity(new Intent(context, ReproductorActivity.class));
            } else {
                Toast.makeText(context, "El álbum no tiene canciones", Toast.LENGTH_SHORT).show();
            }
        }
        else if ("LISTA".equals(tipo)) {
            List<ListaDeReproduccionDTO> listas = (List<ListaDeReproduccionDTO>) items;
            ListaDeReproduccionDTO listaSel = listas.get(pos);

            if (listaSel.getCanciones() == null || listaSel.getCanciones().isEmpty()) {
                irADetalles(items.get(pos));
            } else {
                ArrayList<BusquedaCancionDTO> canciones = new ArrayList<>(listaSel.getCanciones());
                Reproductor.reproducirCancion(canciones, 0, context);
                context.startActivity(new Intent(context, ReproductorActivity.class));
            }
        }
    }

    private void guardarAlbum(int idAlbum, MaterialButton btn) {
        ContenidoGuardadoDTO dto = new ContenidoGuardadoDTO();
        dto.setIdUsuario(SesionUsuario.getIdUsuario());
        dto.setTipoDeContenido("ALBUM");
        dto.setIdContenidoGuardado(idAlbum);

        ApiCliente.getClient()
                .create(ContenidoGuardadoServicio.class)
                .guardarContenido(dto)
                .enqueue(new Callback<RespuestaApi<String>>() {
                    @Override
                    public void onResponse(Call<RespuestaApi<String>> call, Response<RespuestaApi<String>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String msg = response.body().getMensaje();
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

                            if (msg != null && msg.toLowerCase().contains("exitosamente")) {
                                btn.setIconResource(R.drawable.ic_save_full);
                                btn.setEnabled(false);
                            }
                        } else {
                            Toast.makeText(context, "Error al guardar álbum", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<RespuestaApi<String>> call, Throwable t) {
                        ManejoErrores.mostrarToastError(context, t);
                    }
                });
    }

}
