package com.example.musapiapp.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Equivalente en Android de ClienteMusAPI.DTOs.BusquedaAlbumDTO (WPF),
 * ahora implementando Parcelable para poder enviarlo por Intents.
 */
public class BusquedaAlbumDTO implements Parcelable {

    @SerializedName("idAlbum")
    private int idAlbum;

    @SerializedName("nombreAlbum")
    private String nombreAlbum;

    @SerializedName("nombreArtista")
    private String nombreArtista;

    @SerializedName("fechaPublicacion")
    private String fechaPublicacion;

    @SerializedName("urlFoto")
    private String urlFoto;

    @SerializedName("canciones")
    private List<BusquedaCancionDTO> canciones;

    // Constructor vacío para Gson
    public BusquedaAlbumDTO() { }

    // Constructor con todos los campos
    public BusquedaAlbumDTO(int idAlbum,
                            String nombreAlbum,
                            String nombreArtista,
                            String fechaPublicacion,
                            String urlFoto,
                            List<BusquedaCancionDTO> canciones) {
        this.idAlbum = idAlbum;
        this.nombreAlbum = nombreAlbum;
        this.nombreArtista = nombreArtista;
        this.fechaPublicacion = fechaPublicacion;
        this.urlFoto = urlFoto;
        this.canciones = canciones;
    }

    // —— Parcelable implementation ——

    protected BusquedaAlbumDTO(Parcel in) {
        idAlbum          = in.readInt();
        nombreAlbum      = in.readString();
        nombreArtista    = in.readString();
        fechaPublicacion = in.readString();
        urlFoto          = in.readString();
        canciones        = in.createTypedArrayList(BusquedaCancionDTO.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idAlbum);
        dest.writeString(nombreAlbum);
        dest.writeString(nombreArtista);
        dest.writeString(fechaPublicacion);
        dest.writeString(urlFoto);
        dest.writeTypedList(canciones);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BusquedaAlbumDTO> CREATOR = new Creator<BusquedaAlbumDTO>() {
        @Override
        public BusquedaAlbumDTO createFromParcel(Parcel in) {
            return new BusquedaAlbumDTO(in);
        }
        @Override
        public BusquedaAlbumDTO[] newArray(int size) {
            return new BusquedaAlbumDTO[size];
        }
    };

    // —— Getters & Setters ——

    public int getIdAlbum() {
        return idAlbum;
    }

    public void setIdAlbum(int idAlbum) {
        this.idAlbum = idAlbum;
    }

    public String getNombreAlbum() {
        return nombreAlbum;
    }

    public void setNombreAlbum(String nombreAlbum) {
        this.nombreAlbum = nombreAlbum;
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

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public List<BusquedaCancionDTO> getCanciones() {
        return canciones;
    }

    public void setCanciones(List<BusquedaCancionDTO> canciones) {
        this.canciones = canciones;
    }

    @Override
    public String toString() {
        return "BusquedaAlbumDTO{" +
                "idAlbum=" + idAlbum +
                ", nombreAlbum='" + nombreAlbum + '\'' +
                ", nombreArtista='" + nombreArtista + '\'' +
                ", fechaPublicacion='" + fechaPublicacion + '\'' +
                ", urlFoto='" + urlFoto + '\'' +
                ", canciones=" + canciones +
                '}';
    }
}
