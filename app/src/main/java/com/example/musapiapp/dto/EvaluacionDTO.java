package com.example.musapiapp.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * Equivalente en Android de ClienteMusAPI.DTOs.EvaluacionDTO (WPF),
 * implementando Parcelable para enviarlo por Intents.
 */
public class EvaluacionDTO implements Parcelable {

    @SerializedName("idUsuario")
    private int idUsuario;

    @SerializedName("idArtista")
    private int idArtista;

    @SerializedName("comentario")
    private String comentario;

    @SerializedName("calificacion")
    private int calificacion;

    // Constructor vacío para Gson
    public EvaluacionDTO() { }

    // Constructor con todos los campos
    public EvaluacionDTO(int idUsuario, int idArtista, String comentario, int calificacion) {
        this.idUsuario = idUsuario;
        this.idArtista = idArtista;
        this.comentario = comentario;
        this.calificacion = calificacion;
    }

    // —— Parcelable implementation ——

    protected EvaluacionDTO(Parcel in) {
        idUsuario   = in.readInt();
        idArtista   = in.readInt();
        comentario  = in.readString();
        calificacion = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idUsuario);
        dest.writeInt(idArtista);
        dest.writeString(comentario);
        dest.writeInt(calificacion);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EvaluacionDTO> CREATOR = new Creator<EvaluacionDTO>() {
        @Override
        public EvaluacionDTO createFromParcel(Parcel in) {
            return new EvaluacionDTO(in);
        }

        @Override
        public EvaluacionDTO[] newArray(int size) {
            return new EvaluacionDTO[size];
        }
    };

    // —— Getters & Setters ——

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdArtista() {
        return idArtista;
    }

    public void setIdArtista(int idArtista) {
        this.idArtista = idArtista;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }

    @Override
    public String toString() {
        return "EvaluacionDTO{" +
                "idUsuario=" + idUsuario +
                ", idArtista=" + idArtista +
                ", comentario='" + comentario + '\'' +
                ", calificacion=" + calificacion +
                '}';
    }
}
