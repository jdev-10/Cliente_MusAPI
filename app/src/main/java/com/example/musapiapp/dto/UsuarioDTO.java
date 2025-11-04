package com.example.musapiapp.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * Equivalente en Android de ClienteMusAPI.Modelo.UsuarioDTO (WPF),
 * implementando Parcelable para enviarlo por Intents.
 * El campo idUsuario se ignora en JSON, al igual que JsonIgnore.
 */
public class UsuarioDTO implements Parcelable {

    // Ignorado en JSON (transient evita que Gson lo serialice)
    private transient int idUsuario;

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

    // Constructor vacío para Gson
    public UsuarioDTO() { }

    // Constructor con todos los campos
    public UsuarioDTO(int idUsuario,
                      String nombre,
                      String correo,
                      String nombreUsuario,
                      String contrasenia,
                      String pais,
                      boolean esAdmin,
                      boolean esArtista) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.nombreUsuario = nombreUsuario;
        this.contrasenia = contrasenia;
        this.pais = pais;
        this.esAdmin = esAdmin;
        this.esArtista = esArtista;
    }

    // —— Parcelable implementation ——

    protected UsuarioDTO(Parcel in) {
        idUsuario     = in.readInt();
        nombre        = in.readString();
        correo        = in.readString();
        nombreUsuario = in.readString();
        contrasenia   = in.readString();
        pais          = in.readString();
        esAdmin       = in.readByte() != 0;
        esArtista     = in.readByte() != 0;
    }

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

    public static final Creator<UsuarioDTO> CREATOR = new Creator<UsuarioDTO>() {
        @Override
        public UsuarioDTO createFromParcel(Parcel in) {
            return new UsuarioDTO(in);
        }
        @Override
        public UsuarioDTO[] newArray(int size) {
            return new UsuarioDTO[size];
        }
    };

    // —— Getters & Setters ——

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
        return "UsuarioDTO{" +
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
