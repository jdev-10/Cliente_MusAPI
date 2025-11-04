package com.example.musapiapp.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class EstadisticasPersonalesDTO implements Parcelable {

    @SerializedName("topCanciones")
    private List<String> topCanciones;

    @SerializedName("topArtistas")
    private List<String> topArtistas;

    @SerializedName("segundosEscuchados")
    private long segundosEscuchados;

    public EstadisticasPersonalesDTO() {
        this.topCanciones = new ArrayList<>();
        this.topArtistas = new ArrayList<>();
    }

    public EstadisticasPersonalesDTO(List<String> topCanciones, List<String> topArtistas, long segundosEscuchados) {
        this.topCanciones = topCanciones;
        this.topArtistas = topArtistas;
        this.segundosEscuchados = segundosEscuchados;
    }

    protected EstadisticasPersonalesDTO(Parcel in) {
        topCanciones = in.createStringArrayList();
        topArtistas = in.createStringArrayList();
        segundosEscuchados = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(topCanciones);
        dest.writeStringList(topArtistas);
        dest.writeLong(segundosEscuchados);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EstadisticasPersonalesDTO> CREATOR = new Creator<EstadisticasPersonalesDTO>() {
        @Override
        public EstadisticasPersonalesDTO createFromParcel(Parcel in) {
            return new EstadisticasPersonalesDTO(in);
        }

        @Override
        public EstadisticasPersonalesDTO[] newArray(int size) {
            return new EstadisticasPersonalesDTO[size];
        }
    };

    public List<String> getTopCanciones() {
        return topCanciones;
    }

    public void setTopCanciones(List<String> topCanciones) {
        this.topCanciones = topCanciones;
    }

    public List<String> getTopArtistas() {
        return topArtistas;
    }

    public void setTopArtistas(List<String> topArtistas) {
        this.topArtistas = topArtistas;
    }

    public long getSegundosEscuchados() {
        return segundosEscuchados;
    }

    public void setSegundosEscuchados(long segundosEscuchados) {
        this.segundosEscuchados = segundosEscuchados;
    }

    @Override
    public String toString() {
        return "EstadisticasPersonalesDTO{" +
                "topCanciones=" + topCanciones +
                ", topArtistas=" + topArtistas +
                ", segundosEscuchados=" + segundosEscuchados +
                '}';
    }
}
