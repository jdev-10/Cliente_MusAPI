package com.example.musapiapp.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class CancionMasEscuchadaDTO implements Parcelable {

    @SerializedName("nombreCancion")
    private String nombreCancion;

    @SerializedName("nombreUsuarioArtista")
    private String nombreUsuarioArtista;

    @SerializedName("segundosEscuchados")
    private long segundosEscuchados;

    public CancionMasEscuchadaDTO() { }

    public CancionMasEscuchadaDTO(String nombreCancion, String nombreUsuarioArtista, long segundosEscuchados) {
        this.nombreCancion = nombreCancion;
        this.nombreUsuarioArtista = nombreUsuarioArtista;
        this.segundosEscuchados = segundosEscuchados;
    }

    protected CancionMasEscuchadaDTO(Parcel in) {
        nombreCancion = in.readString();
        nombreUsuarioArtista = in.readString();
        segundosEscuchados = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombreCancion);
        dest.writeString(nombreUsuarioArtista);
        dest.writeLong(segundosEscuchados);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CancionMasEscuchadaDTO> CREATOR = new Creator<CancionMasEscuchadaDTO>() {
        @Override
        public CancionMasEscuchadaDTO createFromParcel(Parcel in) {
            return new CancionMasEscuchadaDTO(in);
        }

        @Override
        public CancionMasEscuchadaDTO[] newArray(int size) {
            return new CancionMasEscuchadaDTO[size];
        }
    };

    public String getNombreCancion() {
        return nombreCancion;
    }

    public void setNombreCancion(String nombreCancion) {
        this.nombreCancion = nombreCancion;
    }

    public String getNombreUsuarioArtista() {
        return nombreUsuarioArtista;
    }

    public void setNombreUsuarioArtista(String nombreUsuarioArtista) {
        this.nombreUsuarioArtista = nombreUsuarioArtista;
    }

    public long getSegundosEscuchados() {
        return segundosEscuchados;
    }

    public void setSegundosEscuchados(long segundosEscuchados) {
        this.segundosEscuchados = segundosEscuchados;
    }

    @Override
    public String toString() {
        return "CancionMasEscuchadaDTO{" +
                "nombreCancion='" + nombreCancion + '\'' +
                ", nombreUsuarioArtista='" + nombreUsuarioArtista + '\'' +
                ", segundosEscuchados=" + segundosEscuchados +
                '}';
    }
}
