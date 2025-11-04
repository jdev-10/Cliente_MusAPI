package com.example.musapiapp.util;

/**
 * Contenedor estático en memoria para los datos de sesión del usuario,
 * adaptado del patrón usado en WPF (SesionUsuario.cs).
 */
public class SesionUsuario {
    private static int    idUsuario;
    private static String nombreUsuario = "";
    private static String nombre        = "";
    private static String pais          = "";
    private static String correo        = "";
    private static boolean esAdmin;
    private static boolean esArtista;
    private static String  token        = "";

    // Constructor privado para evitar instanciación
    private void SessionUsuario() { }

    // Getters & Setters
    public static int getIdUsuario() {
        return idUsuario;
    }

    public static void setIdUsuario(int id) {
        idUsuario = id;
    }

    public static String getNombreUsuario() {
        return nombreUsuario;
    }

    public static void setNombreUsuario(String usuario) {
        nombreUsuario = usuario;
    }

    public static String getNombre() {
        return nombre;
    }

    public static void setNombre(String nom) {
        nombre = nom;
    }

    public static String getPais() {
        return pais;
    }

    public static void setPais(String p) {
        pais = p;
    }

    public static String getCorreo() {
        return correo;
    }

    public static void setCorreo(String c) {
        correo = c;
    }

    public static boolean isAdmin() {
        return esAdmin;
    }
    public static boolean getEsAdmin() {
        return esAdmin;
    }

    public static void setEsAdmin(boolean admin) {
        esAdmin = admin;
    }

    public static void setAdmin(boolean admin) {
        esAdmin = admin;
    }

    public static boolean isArtista() {
        return esArtista;
    }

    public static void setArtista(boolean artista) {
        esArtista = artista;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String t) {
        token = t;
    }

    /**
     * Limpia todos los datos de sesión, devolviéndolos a sus valores por defecto.
     */
    public static void clear() {
        idUsuario     = 0;
        nombreUsuario = "";
        nombre        = "";
        pais          = "";
        correo        = "";
        token         = "";
        esAdmin       = false;
        esArtista     = false;
    }
}
