package com.example.musapiapp.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * Equivalente en Android de ClienteMusAPI.DTOs.AlbumDTO (WPF),
 * ahora implementando Parcelable para poder enviarlo por Intents.
 */
public class AlbumDTO implements Parcelable {

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("idUsuario")
    private int idUsuario;

    @SerializedName("fotoPath")
    private String fotoPath;

    // Constructor vacío para Gson
    public AlbumDTO() { }

    // Constructor con todos los campos
    public AlbumDTO(String nombre, int idUsuario, String fotoPath) {
        this.nombre = nombre;
        this.idUsuario = idUsuario;
        this.fotoPath = fotoPath;
    }

    // —— Parcelable implementation ——

    protected AlbumDTO(Parcel in) {
        nombre    = in.readString();
        idUsuario = in.readInt();
        fotoPath  = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeInt(idUsuario);
        dest.writeString(fotoPath);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AlbumDTO> CREATOR = new Creator<AlbumDTO>() {
        @Override
        public AlbumDTO createFromParcel(Parcel in) {
            return new AlbumDTO(in);
        }

        @Override
        public AlbumDTO[] newArray(int size) {
            return new AlbumDTO[size];
        }
    };

    // —— Getters & Setters ——

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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
        return "AlbumDTO{" +
                "nombre='" + nombre + '\'' +
                ", idUsuario=" + idUsuario +
                ", fotoPath='" + fotoPath + '\'' +
                '}';
    }
}
