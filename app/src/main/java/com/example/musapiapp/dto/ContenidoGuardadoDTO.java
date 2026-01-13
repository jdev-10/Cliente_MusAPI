package com.example.musapiapp.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class ContenidoGuardadoDTO implements Parcelable {

    @SerializedName("idUsuario")
    private int idUsuario;

    @SerializedName("idContenidoGuardado")
    private int idContenidoGuardado;

    @SerializedName("tipoDeContenido")
    private String tipoDeContenido;

    public ContenidoGuardadoDTO() { }

    public ContenidoGuardadoDTO(int idUsuario, int idContenidoGuardado, String tipoDeContenido) {
        this.idUsuario = idUsuario;
        this.idContenidoGuardado = idContenidoGuardado;
        this.tipoDeContenido = tipoDeContenido;
    }

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
