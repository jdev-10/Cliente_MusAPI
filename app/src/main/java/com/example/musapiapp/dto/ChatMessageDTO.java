package com.example.musapiapp.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class ChatMessageDTO implements Parcelable {

    @SerializedName("nombreUsuario")
    private String nombreUsuario;

    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("idPerfilArtista")
    private Integer idPerfilArtista;

    public ChatMessageDTO() { }

    public ChatMessageDTO(String nombreUsuario, String mensaje, Integer idPerfilArtista) {
        this.nombreUsuario = nombreUsuario;
        this.mensaje = mensaje;
        this.idPerfilArtista = idPerfilArtista;
    }

    protected ChatMessageDTO(Parcel in) {
        nombreUsuario   = in.readString();
        mensaje         = in.readString();
        idPerfilArtista = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombreUsuario);
        dest.writeString(mensaje);
        dest.writeValue(idPerfilArtista);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ChatMessageDTO> CREATOR = new Creator<ChatMessageDTO>() {
        @Override
        public ChatMessageDTO createFromParcel(Parcel in) {
            return new ChatMessageDTO(in);
        }
        @Override
        public ChatMessageDTO[] newArray(int size) {
            return new ChatMessageDTO[size];
        }
    };

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Integer getIdPerfilArtista() {
        return idPerfilArtista;
    }

    public void setIdPerfilArtista(Integer idPerfilArtista) {
        this.idPerfilArtista = idPerfilArtista;
    }

    @Override
    public String toString() {
        return "ChatMessageDTO{" +
                "nombreUsuario='" + nombreUsuario + '\'' +
                ", mensaje='" + mensaje + '\'' +
                ", idPerfilArtista=" + idPerfilArtista +
                '}';
    }
}
