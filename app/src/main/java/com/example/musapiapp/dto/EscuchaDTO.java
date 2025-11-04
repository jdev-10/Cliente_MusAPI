package com.example.musapiapp.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class EscuchaDTO implements Parcelable {

    @SerializedName("idUsuario")
    private int idUsuario;

    @SerializedName("idCancion")
    private int idCancion;

    @SerializedName("segundosEscucha")
    private int segundosEscucha;

    public EscuchaDTO() { }

    public EscuchaDTO(int idUsuario, int idCancion, int segundosEscucha) {
        this.idUsuario = idUsuario;
        this.idCancion = idCancion;
        this.segundosEscucha = segundosEscucha;
    }

    protected EscuchaDTO(Parcel in) {
        idUsuario = in.readInt();
        idCancion = in.readInt();
        segundosEscucha = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idUsuario);
        dest.writeInt(idCancion);
        dest.writeInt(segundosEscucha);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EscuchaDTO> CREATOR = new Creator<EscuchaDTO>() {
        @Override
        public EscuchaDTO createFromParcel(Parcel in) {
            return new EscuchaDTO(in);
        }

        @Override
        public EscuchaDTO[] newArray(int size) {
            return new EscuchaDTO[size];
        }
    };

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdCancion() {
        return idCancion;
    }

    public void setIdCancion(int idCancion) {
        this.idCancion = idCancion;
    }

    public int getSegundosEscucha() {
        return segundosEscucha;
    }

    public void setSegundosEscucha(int segundosEscucha) {
        this.segundosEscucha = segundosEscucha;
    }
}
