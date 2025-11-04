package com.example.musapiapp.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class BusquedaUsuarioDTO implements Parcelable {

    @SerializedName("idUsuario")
    private int idUsuario;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("correo")
    private String correo;

    @SerializedName("nombreUsuario")
    private String nombreUsuario;

    @SerializedName("contrasenia")
    private String contrasenia;

    @SerializedName("pais")
    private String pais;

    @SerializedName("esAdmin")
    private boolean esAdmin;

    @SerializedName("esArtista")
    private boolean esArtista;

    public BusquedaUsuarioDTO() {}

    protected BusquedaUsuarioDTO(Parcel in) {
        idUsuario = in.readInt();
        nombre = in.readString();
        correo = in.readString();
        nombreUsuario = in.readString();
        contrasenia = in.readString();
        pais = in.readString();
        esAdmin = in.readByte() != 0;
        esArtista = in.readByte() != 0;
    }

    public static final Creator<BusquedaUsuarioDTO> CREATOR = new Creator<BusquedaUsuarioDTO>() {
        @Override
        public BusquedaUsuarioDTO createFromParcel(Parcel in) {
            return new BusquedaUsuarioDTO(in);
        }

        @Override
        public BusquedaUsuarioDTO[] newArray(int size) {
            return new BusquedaUsuarioDTO[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idUsuario);
        dest.writeString(nombre);
        dest.writeString(correo);
        dest.writeString(nombreUsuario);
        dest.writeString(contrasenia);
        dest.writeString(pais);
        dest.writeByte((byte) (esAdmin ? 1 : 0));
        dest.writeByte((byte) (esArtista ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters y setters

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public boolean isEsAdmin() {
        return esAdmin;
    }

    public void setEsAdmin(boolean esAdmin) {
        this.esAdmin = esAdmin;
    }

    public boolean isEsArtista() {
        return esArtista;
    }

    public void setEsArtista(boolean esArtista) {
        this.esArtista = esArtista;
    }

    @Override
    public String toString() {
        return "BusquedaUsuarioDTO{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", correo='" + correo + '\'' +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", contrasenia='" + contrasenia + '\'' +
                ", pais='" + pais + '\'' +
                ", esAdmin=" + esAdmin +
                ", esArtista=" + esArtista +
                '}';
    }
}
