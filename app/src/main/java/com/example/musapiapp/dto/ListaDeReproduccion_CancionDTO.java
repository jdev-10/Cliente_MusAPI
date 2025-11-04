package com.example.musapiapp.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * Equivalente en Android de ClienteMusAPI.DTOs.ListaDeReproduccion_CancionDTO (WPF),
 * implementando Parcelable para enviarlo por Intents.
 */
public class ListaDeReproduccion_CancionDTO implements Parcelable {

    @SerializedName("idCancion")
    private int idCancion;

    @SerializedName("idListaDeReproduccion")
    private int idListaDeReproduccion;

    @SerializedName("idUsuario")
    private int idUsuario;

    // Constructor vacío para Gson
    public ListaDeReproduccion_CancionDTO() { }

    // Constructor con todos los campos
    public ListaDeReproduccion_CancionDTO(int idCancion,
                                          int idListaDeReproduccion,
                                          int idUsuario) {
        this.idCancion = idCancion;
        this.idListaDeReproduccion = idListaDeReproduccion;
        this.idUsuario = idUsuario;
    }

    // —— Parcelable implementation ——

    protected ListaDeReproduccion_CancionDTO(Parcel in) {
        idCancion = in.readInt();
        idListaDeReproduccion = in.readInt();
        idUsuario = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idCancion);
        dest.writeInt(idListaDeReproduccion);
        dest.writeInt(idUsuario);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ListaDeReproduccion_CancionDTO> CREATOR = new Creator<ListaDeReproduccion_CancionDTO>() {
        @Override
        public ListaDeReproduccion_CancionDTO createFromParcel(Parcel in) {
            return new ListaDeReproduccion_CancionDTO(in);
        }
        @Override
        public ListaDeReproduccion_CancionDTO[] newArray(int size) {
            return new ListaDeReproduccion_CancionDTO[size];
        }
    };

    // —— Getters & Setters ——

    public int getIdCancion() {
        return idCancion;
    }

    public void setIdCancion(int idCancion) {
        this.idCancion = idCancion;
    }

    public int getIdListaDeReproduccion() {
        return idListaDeReproduccion;
    }

    public void setIdListaDeReproduccion(int idListaDeReproduccion) {
        this.idListaDeReproduccion = idListaDeReproduccion;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public String toString() {
        return "ListaDeReproduccion_CancionDTO{" +
                "idCancion=" + idCancion +
                ", idListaDeReproduccion=" + idListaDeReproduccion +
                ", idUsuario=" + idUsuario +
                '}';
    }
}
