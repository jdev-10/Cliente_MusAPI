package com.example.musapiapp.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class EstadisticasNumeroUsuariosDTO implements Parcelable {

    @SerializedName("totalUsuarios")
    private int totalUsuarios;

    @SerializedName("totalArtistas")
    private int totalArtistas;

    public EstadisticasNumeroUsuariosDTO() { }

    public EstadisticasNumeroUsuariosDTO(int totalUsuarios, int totalArtistas) {
        this.totalUsuarios = totalUsuarios;
        this.totalArtistas = totalArtistas;
    }

    protected EstadisticasNumeroUsuariosDTO(Parcel in) {
        totalUsuarios = in.readInt();
        totalArtistas = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(totalUsuarios);
        dest.writeInt(totalArtistas);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EstadisticasNumeroUsuariosDTO> CREATOR = new Creator<EstadisticasNumeroUsuariosDTO>() {
        @Override
        public EstadisticasNumeroUsuariosDTO createFromParcel(Parcel in) {
            return new EstadisticasNumeroUsuariosDTO(in);
        }

        @Override
        public EstadisticasNumeroUsuariosDTO[] newArray(int size) {
            return new EstadisticasNumeroUsuariosDTO[size];
        }
    };

    public int getTotalUsuarios() {
        return totalUsuarios;
    }

    public void setTotalUsuarios(int totalUsuarios) {
        this.totalUsuarios = totalUsuarios;
    }

    public int getTotalArtistas() {
        return totalArtistas;
    }

    public void setTotalArtistas(int totalArtistas) {
        this.totalArtistas = totalArtistas;
    }

    @Override
    public String toString() {
        return "EstadisticasNumeroUsuariosDTO{" +
                "totalUsuarios=" + totalUsuarios +
                ", totalArtistas=" + totalArtistas +
                '}';
    }
}
