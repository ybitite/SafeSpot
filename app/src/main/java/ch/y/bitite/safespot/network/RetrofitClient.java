package ch.y.bitite.safespot.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ch.y.bitite.safespot.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String TAG = "RetrofitClient";

    public static Retrofit createRetrofit(OkHttpClient client, Gson gson) {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static OkHttpClient createOkHttpClient(HttpLoggingInterceptor logging) {
        return new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
    }

    public static HttpLoggingInterceptor createLoggingInterceptor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> Log.d(TAG, "log: " + message));
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return logging;
    }

    public static Gson createGson() {
        return new GsonBuilder()
                .create();
    }
}