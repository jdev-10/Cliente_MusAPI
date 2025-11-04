package com.example.musapiapp.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * Equivalente en Android de ClienteMusAPI.DTOs.InfoAlbumDTO (WPF),
 * implementando Parcelable para enviarlo por Intents.
 */
public class InfoAlbumDTO implements Parcelable {

    @SerializedName("idAlbum")
    private int idAlbum;

    @SerializedName("nombreArtista")
    private String nombreArtista;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("urlFoto")
    private String urlFoto;

    @SerializedName("fechaPublicacion")
    private String fechaPublicacion;

    // Constructor vacío para Gson
    public InfoAlbumDTO() { }

    // Constructor con todos los campos
    public InfoAlbumDTO(int idAlbum,
                        String nombreArtista,
                        String nombre,
                        String urlFoto,
                        String fechaPublicacion) {
        this.idAlbum = idAlbum;
        this.nombreArtista = nombreArtista;
        this.nombre = nombre;
        this.urlFoto = urlFoto;
        this.fechaPublicacion = fechaPublicacion;
    }

    // —— Parcelable implementation ——

    protected InfoAlbumDTO(Parcel in) {
        idAlbum           = in.readInt();
        nombreArtista     = in.readString();
        nombre            = in.readString();
        urlFoto           = in.readString();
        fechaPublicacion  = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idAlbum);
        dest.writeString(nombreArtista);
        dest.writeString(nombre);
        dest.writeString(urlFoto);
        dest.writeString(fechaPublicacion);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<InfoAlbumDTO> CREATOR = new Creator<InfoAlbumDTO>() {
        @Override
        public InfoAlbumDTO createFromParcel(Parcel in) {
            return new InfoAlbumDTO(in);
        }
        @Override
        public InfoAlbumDTO[] newArray(int size) {
            return new InfoAlbumDTO[size];
        }
    };

    // —— Getters & Setters ——

    public int getIdAlbum() {
        return idAlbum;
    }

    public void setIdAlbum(int idAlbum) {
        this.idAlbum = idAlbum;
    }

    public String getNombreArtista() {
        return nombreArtista;
    }

    public void setNombreArtista(String nombreArtista) {
        this.nombreArtista = nombreArtista;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public String getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(String fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    @Override
    public String toString() {
        return "InfoAlbumDTO{" +
                "idAlbum=" + idAlbum +
                ", nombreArtista='" + nombreArtista + '\'' +
                ", nombre='" + nombre + '\'' +
                ", urlFoto='" + urlFoto + '\'' +
                ", fechaPublicacion='" + fechaPublicacion + '\'' +
                '}';
    }
}
