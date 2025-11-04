package com.example.musapiapp.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * Equivalente en Android de ClienteMusAPI.DTOs.BusquedaCancionDTO (WPF),
 * implementando Parcelable para poder enviarlo por Intents.
 */
public class BusquedaCancionDTO implements Parcelable {

    @SerializedName("idCancion")
    private int idCancion;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("duracion")
    private String duracion;

    @SerializedName("urlArchivo")
    private String urlArchivo;

    @SerializedName("urlFoto")
    private String urlFoto;

    @SerializedName("nombreArtista")
    private String nombreArtista;

    @SerializedName("fechaPublicacion")
    private String fechaPublicacion;

    @SerializedName("nombreAlbum")
    private String nombreAlbum;

    @SerializedName("categoriaMusical")
    private String categoriaMusical;

    // Constructor vacío para Gson
    public BusquedaCancionDTO() { }

    // Constructor con todos los campos
    public BusquedaCancionDTO(int idCancion,
                              String nombre,
                              String duracion,
                              String urlArchivo,
                              String urlFoto,
                              String nombreArtista,
                              String fechaPublicacion,
                              String nombreAlbum,
                              String categoriaMusical) {
        this.idCancion = idCancion;
        this.nombre = nombre;
        this.duracion = duracion;
        this.urlArchivo = urlArchivo;
        this.urlFoto = urlFoto;
        this.nombreArtista = nombreArtista;
        this.fechaPublicacion = fechaPublicacion;
        this.nombreAlbum = nombreAlbum;
        this.categoriaMusical = categoriaMusical;
    }

    // —— Parcelable implementation ——

    protected BusquedaCancionDTO(Parcel in) {
        idCancion        = in.readInt();
        nombre           = in.readString();
        duracion         = in.readString();
        urlArchivo       = in.readString();
        urlFoto          = in.readString();
        nombreArtista    = in.readString();
        fechaPublicacion = in.readString();
        nombreAlbum      = in.readString();
        categoriaMusical = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idCancion);
        dest.writeString(nombre);
        dest.writeString(duracion);
        dest.writeString(urlArchivo);
        dest.writeString(urlFoto);
        dest.writeString(nombreArtista);
        dest.writeString(fechaPublicacion);
        dest.writeString(nombreAlbum);
        dest.writeString(categoriaMusical);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BusquedaCancionDTO> CREATOR = new Creator<BusquedaCancionDTO>() {
        @Override
        public BusquedaCancionDTO createFromParcel(Parcel in) {
            return new BusquedaCancionDTO(in);
        }

        @Override
        public BusquedaCancionDTO[] newArray(int size) {
            return new BusquedaCancionDTO[size];
        }
    };

    // —— Getters & Setters ——

    public int getIdCancion() {
        return idCancion;
    }

    public void setIdCancion(int idCancion) {
        this.idCancion = idCancion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public String getUrlArchivo() {
        return urlArchivo;
    }

    public void setUrlArchivo(String urlArchivo) {
        this.urlArchivo = urlArchivo;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public String getNombreArtista() {
        return nombreArtista;
    }

    public void setNombreArtista(String nombreArtista) {
        this.nombreArtista = nombreArtista;
    }

    public String getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(String fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public String getNombreAlbum() {
        return nombreAlbum;
    }

    public void setNombreAlbum(String nombreAlbum) {
        this.nombreAlbum = nombreAlbum;
    }

    public String getCategoriaMusical() {
        return categoriaMusical;
    }

    public void setCategoriaMusical(String categoriaMusical) {
        this.categoriaMusical = categoriaMusical;
    }

    @Override
    public String toString() {
        return "BusquedaCancionDTO{" +
                "idCancion=" + idCancion +
                ", nombre='" + nombre + '\'' +
                ", duracion='" + duracion + '\'' +
                ", urlArchivo='" + urlArchivo + '\'' +
                ", urlFoto='" + urlFoto + '\'' +
                ", nombreArtista='" + nombreArtista + '\'' +
                ", fechaPublicacion='" + fechaPublicacion + '\'' +
                ", nombreAlbum='" + nombreAlbum + '\'' +
                ", categoriaMusical='" + categoriaMusical + '\'' +
                '}';
    }
}
