package ch.y.bitite.safespot.repository;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://localhost:7095/";  // Remplacez par l'URL de base de votre API
    private static Retrofit retrofit;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()) // Utilisation de Gson pour la conversion JSON
                    .build();
        }
        return retrofit;
    }
}