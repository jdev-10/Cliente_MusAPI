package com.example.musapiapp.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * Equivalente en Android de ClienteMusAPI.DTOs.ListaReproduccionDTO (WPF),
 * implementando Parcelable para enviarlo por Intents.
 */
public class ListaReproduccionDTO implements Parcelable {

    @SerializedName("Nombre")
    private String nombre;

    @SerializedName("Descripcion")
    private String descripcion;

    @SerializedName("IdUsuario")
    private int idUsuario;

    @SerializedName("FotoPath")
    private String fotoPath;

    // Constructor vacío para Gson
    public ListaReproduccionDTO() { }

    // Constructor con todos los campos
    public ListaReproduccionDTO(String nombre,
                                String descripcion,
                                int idUsuario,
                                String fotoPath) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.idUsuario = idUsuario;
        this.fotoPath = fotoPath;
    }

    // —— Parcelable implementation ——

    protected ListaReproduccionDTO(Parcel in) {
        nombre      = in.readString();
        descripcion = in.readString();
        idUsuario   = in.readInt();
        fotoPath    = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeString(descripcion);
        dest.writeInt(idUsuario);
        dest.writeString(fotoPath);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ListaReproduccionDTO> CREATOR = new Creator<ListaReproduccionDTO>() {
        @Override
        public ListaReproduccionDTO createFromParcel(Parcel in) {
            return new ListaReproduccionDTO(in);
        }
        @Override
        public ListaReproduccionDTO[] newArray(int size) {
            return new ListaReproduccionDTO[size];
        }
    };

    // —— Getters & Setters ——

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getFotoPath() {
        return fotoPath;
    }

    public void setFotoPath(String fotoPath) {
        this.fotoPath = fotoPath;
    }

    @Override
    public String toString() {
        return "ListaReproduccionDTO{" +
                "nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", idUsuario=" + idUsuario +
                ", fotoPath='" + fotoPath + '\'' +
                '}';
    }
}
