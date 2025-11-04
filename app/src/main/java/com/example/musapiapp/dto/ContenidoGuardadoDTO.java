package com.example.musapiapp.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * Equivalente en Android de ClienteMusAPI.DTOs.ContenidoGuardadoDTO (WPF),
 * implementando Parcelable para enviarlo por Intents.
 */
public class ContenidoGuardadoDTO implements Parcelable {

    @SerializedName("idUsuario")
    private int idUsuario;

    @SerializedName("idContenidoGuardado")
    private int idContenidoGuardado;

    @SerializedName("tipoDeContenido")
    private String tipoDeContenido;

    // Constructor vacío para Gson
    public ContenidoGuardadoDTO() { }

    // Constructor con todos los campos
    public ContenidoGuardadoDTO(int idUsuario, int idContenidoGuardado, String tipoDeContenido) {
        this.idUsuario = idUsuario;
        this.idContenidoGuardado = idContenidoGuardado;
        this.tipoDeContenido = tipoDeContenido;
    }

    // —— Parcelable implementation ——

    protected ContenidoGuardadoDTO(Parcel in) {
        idUsuario = in.readInt();
        idContenidoGuardado = in.readInt();
        tipoDeContenido = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idUsuario);
        dest.writeInt(idContenidoGuardado);
        dest.writeString(tipoDeContenido);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ContenidoGuardadoDTO> CREATOR = new Creator<ContenidoGuardadoDTO>() {
        @Override
        public ContenidoGuardadoDTO createFromParcel(Parcel in) {
            return new ContenidoGuardadoDTO(in);
        }
        @Override
        public ContenidoGuardadoDTO[] newArray(int size) {
            return new ContenidoGuardadoDTO[size];
        }
    };

    // —— Getters & Setters ——

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdContenidoGuardado() {
        return idContenidoGuardado;
    }

    public void setIdContenidoGuardado(int idContenidoGuardado) {
        this.idContenidoGuardado = idContenidoGuardado;
    }

    public String getTipoDeContenido() {
        return tipoDeContenido;
    }

    public void setTipoDeContenido(String tipoDeContenido) {
        this.tipoDeContenido = tipoDeContenido;
    }

    @Override
    public String toString() {
        return "ContenidoGuardadoDTO{" +
                "idUsuario=" + idUsuario +
                ", idContenidoGuardado=" + idContenidoGuardado +
                ", tipoDeContenido='" + tipoDeContenido + '\'' +
                '}';
    }
}
