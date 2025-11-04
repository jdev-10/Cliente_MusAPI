package com.example.musapiapp.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * Equivalente en Android de ClienteMusAPI.DTOs.PerfilArtistaDTO (WPF),
 * implementando Parcelable para enviarlo por Intents.
 */
public class PerfilArtistaDTO implements Parcelable {

    @SerializedName("IdUsuario")
    private int idUsuario;

    @SerializedName("Descripcion")
    private String descripcion;

    @SerializedName("FotoPath")
    private String fotoPath;

    // Constructor vacío para Gson
    public PerfilArtistaDTO() { }

    // Constructor con todos los campos
    public PerfilArtistaDTO(int idUsuario, String descripcion, String fotoPath) {
        this.idUsuario = idUsuario;
        this.descripcion = descripcion;
        this.fotoPath = fotoPath;
    }

    // —— Parcelable implementation ——

    protected PerfilArtistaDTO(Parcel in) {
        idUsuario   = in.readInt();
        descripcion = in.readString();
        fotoPath    = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idUsuario);
        dest.writeString(descripcion);
        dest.writeString(fotoPath);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PerfilArtistaDTO> CREATOR = new Creator<PerfilArtistaDTO>() {
        @Override
        public PerfilArtistaDTO createFromParcel(Parcel in) {
            return new PerfilArtistaDTO(in);
        }
        @Override
        public PerfilArtistaDTO[] newArray(int size) {
            return new PerfilArtistaDTO[size];
        }
    };

    // —— Getters & Setters ——

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFotoPath() {
        return fotoPath;
    }

    public void setFotoPath(String fotoPath) {
        this.fotoPath = fotoPath;
    }

    @Override
    public String toString() {
        return "PerfilArtistaDTO{" +
                "idUsuario=" + idUsuario +
                ", descripcion='" + descripcion + '\'' +
                ", fotoPath='" + fotoPath + '\'' +
                '}';
    }
}
