package com.example.musapiapp.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Equivalente en Android de ClienteMusAPI.DTOs.CancionDTO (WPF),
 * implementando Parcelable para enviarlo por Intents.
 */
public class CancionDTO implements Parcelable {

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("archivoCancion")
    private String archivoCancion;

    @SerializedName("urlFoto")
    private String urlFoto;

    @SerializedName("duracionStr")
    private String duracionStr;

    @SerializedName("idCategoriaMusical")
    private int idCategoriaMusical;

    @SerializedName("idAlbum")
    private int idAlbum;

    @SerializedName("posicionEnAlbum")
    private int posicionEnAlbum;

    @SerializedName("idPerfilArtistas")
    private List<Integer> idPerfilArtistas;

    // Constructor vacío para Gson
    public CancionDTO() { }

    // Constructor con todos los campos
    public CancionDTO(String nombre,
                      String archivoCancion,
                      String urlFoto,
                      String duracionStr,
                      int idCategoriaMusical,
                      int idAlbum,
                      int posicionEnAlbum,
                      List<Integer> idPerfilArtistas) {
        this.nombre = nombre;
        this.archivoCancion = archivoCancion;
        this.urlFoto = urlFoto;
        this.duracionStr = duracionStr;
        this.idCategoriaMusical = idCategoriaMusical;
        this.idAlbum = idAlbum;
        this.posicionEnAlbum = posicionEnAlbum;
        this.idPerfilArtistas = idPerfilArtistas;
    }

    // —— Parcelable implementation ——

    protected CancionDTO(Parcel in) {
        nombre             = in.readString();
        archivoCancion     = in.readString();
        urlFoto            = in.readString();
        duracionStr        = in.readString();
        idCategoriaMusical = in.readInt();
        idAlbum            = in.readInt();
        posicionEnAlbum    = in.readInt();
        idPerfilArtistas   = new ArrayList<>();
        in.readList(idPerfilArtistas, Integer.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeString(archivoCancion);
        dest.writeString(urlFoto);
        dest.writeString(duracionStr);
        dest.writeInt(idCategoriaMusical);
        dest.writeInt(idAlbum);
        dest.writeInt(posicionEnAlbum);
        dest.writeList(idPerfilArtistas);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CancionDTO> CREATOR = new Creator<CancionDTO>() {
        @Override
        public CancionDTO createFromParcel(Parcel in) {
            return new CancionDTO(in);
        }
        @Override
        public CancionDTO[] newArray(int size) {
            return new CancionDTO[size];
        }
    };

    // —— Getters & Setters ——

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getArchivoCancion() {
        return archivoCancion;
    }

    public void setArchivoCancion(String archivoCancion) {
        this.archivoCancion = archivoCancion;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public String getDuracionStr() {
        return duracionStr;
    }

    public void setDuracionStr(String duracionStr) {
        this.duracionStr = duracionStr;
    }

    public int getIdCategoriaMusical() {
        return idCategoriaMusical;
    }

    public void setIdCategoriaMusical(int idCategoriaMusical) {
        this.idCategoriaMusical = idCategoriaMusical;
    }

    public int getIdAlbum() {
        return idAlbum;
    }

    public void setIdAlbum(int idAlbum) {
        this.idAlbum = idAlbum;
    }

    public int getPosicionEnAlbum() {
        return posicionEnAlbum;
    }

    public void setPosicionEnAlbum(int posicionEnAlbum) {
        this.posicionEnAlbum = posicionEnAlbum;
    }

    public List<Integer> getIdPerfilArtistas() {
        return idPerfilArtistas;
    }

    public void setIdPerfilArtistas(List<Integer> idPerfilArtistas) {
        this.idPerfilArtistas = idPerfilArtistas;
    }

    @Override
    public String toString() {
        return "CancionDTO{" +
                "nombre='" + nombre + '\'' +
                ", archivoCancion='" + archivoCancion + '\'' +
                ", urlFoto='" + urlFoto + '\'' +
                ", duracionStr='" + duracionStr + '\'' +
                ", idCategoriaMusical=" + idCategoriaMusical +
                ", idAlbum=" + idAlbum +
                ", posicionEnAlbum=" + posicionEnAlbum +
                ", idPerfilArtistas=" + idPerfilArtistas +
                '}';
    }
}
