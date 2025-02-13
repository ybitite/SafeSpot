package ch.y.bitite.safespot.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ch.y.bitite.safespot.utils.DateTypeAdapter;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.Date;

public class RetrofitClient {
    private static final String BASE_URL = "https://safespotapi20250207214631.azurewebsites.net/";
    private static Retrofit retrofit;
    private static final String TAG = "RetrofitClient";

    public static Retrofit getInstance() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    Log.d(TAG, "log: " + message);
                }
            });
            logging.setLevel(HttpLoggingInterceptor.Level.BODY); // Log request and response bodies

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, new DateTypeAdapter())
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client) // Add the OkHttpClient with the interceptor
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}