package com.example.musapiapp.network;

import com.example.musapiapp.util.Constantes;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

/**
 * Cliente Retrofit configurado con:
 *  - Interceptor para cabecera Accept: application/json
 *  - Interceptor para Authorization: Bearer <token>
 *  - Logging de cuerpo request/response
 *  - Timeouts de conexión/lectura/escritura
 */
public class ApiCliente {
    private static final String baseUrl = Constantes.URL_API;
    private static Retrofit retrofit;
    private static String token; // guardado tras login

    /** Llamar tras hacer login: guarda el token y fuerza recreación del cliente **/
    public static void setToken(String t) {
        token = t;
        retrofit = null;
    }

    /** Devuelve el singleton de Retrofit, creándolo si es la primera vez **/
    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    // Cabeceras estándar
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request.Builder rb = original.newBuilder()
                                .header("Accept", "application/json");
                        if (token != null && !token.isEmpty()) {
                            rb.header("Authorization", "Bearer " + token);
                        }
                        return chain.proceed(rb.build());
                    })
                    // Logging detallado en desarrollo
                    .addInterceptor(new HttpLoggingInterceptor()
                            .setLevel(HttpLoggingInterceptor.Level.BODY))
                    // Configuración de timeouts
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(180, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static String getUrlArchivos() {
        return Constantes.URL_BASE;
    }
}
