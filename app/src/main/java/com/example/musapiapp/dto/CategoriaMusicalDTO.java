package com.example.musapiapp.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * Equivalente en Android de ClienteMusAPI.DTOs.CategoriaMusicalDTO (WPF),
 * implementando Parcelable para poder enviarlo por Intents.
 */
public class CategoriaMusicalDTO implements Parcelable {

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("idCategoriaMusical")
    private Integer idCategoriaMusical;  // Nullable

    // Constructor vacío para Gson
    public CategoriaMusicalDTO() { }

    // Constructor con todos los campos
    public CategoriaMusicalDTO(String nombre, String descripcion, Integer idCategoriaMusical) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.idCategoriaMusical = idCategoriaMusical;
    }

    // —— Parcelable implementation ——

    protected CategoriaMusicalDTO(Parcel in) {
        nombre = in.readString();
        descripcion = in.readString();
        idCategoriaMusical = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeString(descripcion);
        dest.writeValue(idCategoriaMusical);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CategoriaMusicalDTO> CREATOR = new Creator<CategoriaMusicalDTO>() {
        @Override
        public CategoriaMusicalDTO createFromParcel(Parcel in) {
            return new CategoriaMusicalDTO(in);
        }
        @Override
        public CategoriaMusicalDTO[] newArray(int size) {
            return new CategoriaMusicalDTO[size];
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

    public Integer getIdCategoriaMusical() {
        return idCategoriaMusical;
    }

    public void setIdCategoriaMusical(Integer idCategoriaMusical) {
        this.idCategoriaMusical = idCategoriaMusical;
    }

    @Override
    public String toString() {
        return "CategoriaMusicalDTO{" +
                "nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", idCategoriaMusical=" + idCategoriaMusical +
                '}';
    }
}
