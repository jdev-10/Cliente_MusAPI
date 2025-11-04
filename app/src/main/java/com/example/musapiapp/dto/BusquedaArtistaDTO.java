package com.example.musapiapp.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Equivalente en Android de ClienteMusAPI.DTOs.BusquedaArtistaDTO (WPF),
 * implementando Parcelable para poder enviarlo por Intents.
 */
public class BusquedaArtistaDTO implements Parcelable {

    @SerializedName("idArtista")
    private int idArtista;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("nombreUsuario")
    private String nombreUsuario;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("urlFoto")
    private String urlFoto;

    @SerializedName("canciones")
    private List<BusquedaCancionDTO> canciones;

    // Constructor vacío para Gson
    public BusquedaArtistaDTO() { }

    // Constructor con todos los campos
    public BusquedaArtistaDTO(int idArtista,
                              String nombre,
                              String nombreUsuario,
                              String descripcion,
                              String urlFoto,
                              List<BusquedaCancionDTO> canciones) {
        this.idArtista = idArtista;
        this.nombre = nombre;
        this.nombreUsuario = nombreUsuario;
        this.descripcion = descripcion;
        this.urlFoto = urlFoto;
        this.canciones = canciones;
    }

    // —— Parcelable implementation ——

    protected BusquedaArtistaDTO(Parcel in) {
        idArtista     = in.readInt();
        nombre        = in.readString();
        nombreUsuario = in.readString();
        descripcion   = in.readString();
        urlFoto       = in.readString();
        canciones     = in.createTypedArrayList(BusquedaCancionDTO.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idArtista);
        dest.writeString(nombre);
        dest.writeString(nombreUsuario);
        dest.writeString(descripcion);
        dest.writeString(urlFoto);
        dest.writeTypedList(canciones);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BusquedaArtistaDTO> CREATOR = new Creator<BusquedaArtistaDTO>() {
        @Override
        public BusquedaArtistaDTO createFromParcel(Parcel in) {
            return new BusquedaArtistaDTO(in);
        }
        @Override
        public BusquedaArtistaDTO[] newArray(int size) {
            return new BusquedaArtistaDTO[size];
        }
    };

    // —— Getters & Setters ——

    public int getIdArtista() {
        return idArtista;
    }

    public void setIdArtista(int idArtista) {
        this.idArtista = idArtista;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
        return "BusquedaArtistaDTO{" +
                "idArtista=" + idArtista +
                ", nombre='" + nombre + '\'' +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", urlFoto='" + urlFoto + '\'' +
                ", canciones=" + canciones +
                '}';
    }
}
