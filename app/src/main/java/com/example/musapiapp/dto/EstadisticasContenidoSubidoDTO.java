package com.example.musapiapp.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class EstadisticasContenidoSubidoDTO implements Parcelable {

    @SerializedName("numeroOyentes")
    private int numeroOyentes;

    @SerializedName("numeroGuardados")
    private int numeroGuardados;

    public EstadisticasContenidoSubidoDTO() { }

    public EstadisticasContenidoSubidoDTO(int numeroOyentes, int numeroGuardados) {
        this.numeroOyentes = numeroOyentes;
        this.numeroGuardados = numeroGuardados;
    }

    protected EstadisticasContenidoSubidoDTO(Parcel in) {
        numeroOyentes = in.readInt();
        numeroGuardados = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(numeroOyentes);
        dest.writeInt(numeroGuardados);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EstadisticasContenidoSubidoDTO> CREATOR = new Creator<EstadisticasContenidoSubidoDTO>() {
        @Override
        public EstadisticasContenidoSubidoDTO createFromParcel(Parcel in) {
            return new EstadisticasContenidoSubidoDTO(in);
        }

        @Override
        public EstadisticasContenidoSubidoDTO[] newArray(int size) {
            return new EstadisticasContenidoSubidoDTO[size];
        }
    };

    public int getNumeroOyentes() {
        return numeroOyentes;
    }

    public void setNumeroOyentes(int numeroOyentes) {
        this.numeroOyentes = numeroOyentes;
    }

    public int getNumeroGuardados() {
        return numeroGuardados;
    }

    public void setNumeroGuardados(int numeroGuardados) {
        this.numeroGuardados = numeroGuardados;
    }

    @Override
    public String toString() {
        return "EstadisticasContenidoSubidoDTO{" +
                "numeroOyentes=" + numeroOyentes +
                ", numeroGuardados=" + numeroGuardados +
                '}';
    }
}
