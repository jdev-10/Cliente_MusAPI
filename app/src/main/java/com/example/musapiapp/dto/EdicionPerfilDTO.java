package com.example.musapiapp.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * Equivalente en Android de ClienteMusAPI.DTOs.EdicionPerfilDTO (WPF),
 * implementando Parcelable para enviarlo por Intents.
 */
public class EdicionPerfilDTO implements Parcelable {

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("nombreUsuario")
    private String nombreUsuario;

    @SerializedName("pais")
    private String pais;

    // Solo para artistas
    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("foto")
    private String foto;

    // Constructor vacío para Gson
    public EdicionPerfilDTO() { }

    // Constructor con todos los campos
    public EdicionPerfilDTO(String nombre,
                            String nombreUsuario,
                            String pais,
                            String descripcion,
                            String foto) {
        this.nombre = nombre;
        this.nombreUsuario = nombreUsuario;
        this.pais = pais;
        this.descripcion = descripcion;
        this.foto = foto;
    }

    // —— Parcelable implementation ——

    protected EdicionPerfilDTO(Parcel in) {
        nombre         = in.readString();
        nombreUsuario  = in.readString();
        pais           = in.readString();
        descripcion    = in.readString();
        foto           = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeString(nombreUsuario);
        dest.writeString(pais);
        dest.writeString(descripcion);
        dest.writeString(foto);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EdicionPerfilDTO> CREATOR = new Creator<EdicionPerfilDTO>() {
        @Override
        public EdicionPerfilDTO createFromParcel(Parcel in) {
            return new EdicionPerfilDTO(in);
        }
        @Override
        public EdicionPerfilDTO[] newArray(int size) {
            return new EdicionPerfilDTO[size];
        }
    };

    // —— Getters & Setters ——

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

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Override
    public String toString() {
        return "EdicionPerfilDTO{" +
                "nombre='" + nombre + '\'' +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", pais='" + pais + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", foto='" + foto + '\'' +
                '}';
    }
}
