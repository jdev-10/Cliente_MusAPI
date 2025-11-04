package com.example.musapiapp.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Equivalente en Android de ClienteMusAPI.DTOs.ListaDeReproduccionDTO (WPF),
 * implementando Parcelable para enviarlo por Intents.
 */
public class ListaDeReproduccionDTO implements Parcelable {

    @SerializedName("idListaDeReproduccion")
    private int idListaDeReproduccion;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("urlFoto")
    private String urlFoto;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("canciones")
    private List<BusquedaCancionDTO> canciones;

    // Constructor vacío para Gson
    public ListaDeReproduccionDTO() { }

    // Constructor con todos los campos
    public ListaDeReproduccionDTO(int idListaDeReproduccion,
                                  String nombre,
                                  String urlFoto,
                                  String descripcion,
                                  List<BusquedaCancionDTO> canciones) {
        this.idListaDeReproduccion = idListaDeReproduccion;
        this.nombre = nombre;
        this.urlFoto = urlFoto;
        this.descripcion = descripcion;
        this.canciones = canciones;
    }

    // —— Parcelable implementation ——

    protected ListaDeReproduccionDTO(Parcel in) {
        idListaDeReproduccion = in.readInt();
        nombre                 = in.readString();
        urlFoto                = in.readString();
        descripcion            = in.readString();
        canciones              = in.createTypedArrayList(BusquedaCancionDTO.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idListaDeReproduccion);
        dest.writeString(nombre);
        dest.writeString(urlFoto);
        dest.writeString(descripcion);
        dest.writeTypedList(canciones);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ListaDeReproduccionDTO> CREATOR = new Creator<ListaDeReproduccionDTO>() {
        @Override
        public ListaDeReproduccionDTO createFromParcel(Parcel in) {
            return new ListaDeReproduccionDTO(in);
        }
        @Override
        public ListaDeReproduccionDTO[] newArray(int size) {
            return new ListaDeReproduccionDTO[size];
        }
    };

    // —— Getters & Setters ——

    public int getIdListaDeReproduccion() {
        return idListaDeReproduccion;
    }

    public void setIdListaDeReproduccion(int idListaDeReproduccion) {
        this.idListaDeReproduccion = idListaDeReproduccion;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<BusquedaCancionDTO> getCanciones() {
        return canciones;
    }

    public void setCanciones(List<BusquedaCancionDTO> canciones) {
        this.canciones = canciones;
    }

    @Override
    public String toString() {
        return "ListaDeReproduccionDTO{" +
                "idListaDeReproduccion=" + idListaDeReproduccion +
                ", nombre='" + nombre + '\'' +
                ", urlFoto='" + urlFoto + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", canciones=" + canciones +
                '}';
    }
}
