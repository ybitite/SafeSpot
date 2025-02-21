package ch.y.bitite.safespot.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ch.y.bitite.safespot.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This class provides utility methods for creating Retrofit, OkHttpClient,
 * HttpLoggingInterceptor, and Gson instances.
 */
public class RetrofitClient {
    private static final String TAG = "RetrofitClient";

    /**
     * Creates a Retrofit instance.
     *
     * @param client The OkHttpClient to use.
     * @param gson   The Gson instance to use for JSON conversion.
     * @return A Retrofit instance.
     */
    public static Retrofit createRetrofit(OkHttpClient client, Gson gson) {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    /**
     * Creates an OkHttpClient instance.
     *
     * @param logging The HttpLoggingInterceptor to add to the client.
     * @return An OkHttpClient instance.
     */
    public static OkHttpClient createOkHttpClient(HttpLoggingInterceptor logging) {
        return new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
    }

    /**
     * Creates an HttpLoggingInterceptor instance.
     *
     * @return An HttpLoggingInterceptor instance.
     */
    public static HttpLoggingInterceptor createLoggingInterceptor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> Log.d(TAG, "log: " + message));
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return logging;
    }

    /**
     * Creates a Gson instance.
     *
     * @return A Gson instance.
     */
    public static Gson createGson() {
        return new GsonBuilder()
                .create();
    }
}