package com.example.musapiapp.network;

import com.example.musapiapp.util.Constantes;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class ApiCliente {
    private static final String baseUrl = Constantes.URL_API;
    private static Retrofit retrofit;
    private static String token;

    public static void setToken(String t) {
        token = t;
        retrofit = null;
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request.Builder rb = original.newBuilder()
                                .header("Accept", "application/json");
                        if (token != null && !token.isEmpty()) {
                            rb.header("Authorization", "Bearer " + token);
                        }
                        return chain.proceed(rb.build());
                    })
                    .addInterceptor(new HttpLoggingInterceptor()
                            .setLevel(HttpLoggingInterceptor.Level.BODY))
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
