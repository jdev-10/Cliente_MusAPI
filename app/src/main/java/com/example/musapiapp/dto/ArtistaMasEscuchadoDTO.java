package com.example.musapiapp.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class ArtistaMasEscuchadoDTO implements Parcelable {

    @SerializedName("nombreArtista")
    private String nombreArtista;

    @SerializedName("segundosEscuchados")
    private long segundosEscuchados;

    public ArtistaMasEscuchadoDTO() { }

    public ArtistaMasEscuchadoDTO(String nombreArtista, long segundosEscuchados) {
        this.nombreArtista = nombreArtista;
        this.segundosEscuchados = segundosEscuchados;
    }

    protected ArtistaMasEscuchadoDTO(Parcel in) {
        nombreArtista = in.readString();
        segundosEscuchados = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombreArtista);
        dest.writeLong(segundosEscuchados);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ArtistaMasEscuchadoDTO> CREATOR = new Creator<ArtistaMasEscuchadoDTO>() {
        @Override
        public ArtistaMasEscuchadoDTO createFromParcel(Parcel in) {
            return new ArtistaMasEscuchadoDTO(in);
        }

        @Override
        public ArtistaMasEscuchadoDTO[] newArray(int size) {
            return new ArtistaMasEscuchadoDTO[size];
        }
    };

    public String getNombreArtista() {
        return nombreArtista;
    }

    public void setNombreArtista(String nombreArtista) {
        this.nombreArtista = nombreArtista;
    }

    public long getSegundosEscuchados() {
        return segundosEscuchados;
    }

    public void setSegundosEscuchados(long segundosEscuchados) {
        this.segundosEscuchados = segundosEscuchados;
    }

    @Override
    public String toString() {
        return "ArtistaMasEscuchadoDTO{" +
                "nombreArtista='" + nombreArtista + '\'' +
                ", segundosEscuchados=" + segundosEscuchados +
                '}';
    }
}
