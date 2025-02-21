package ch.y.bitite.safespot.di;

import com.google.gson.Gson;

import javax.inject.Singleton;

import ch.y.bitite.safespot.network.RetrofitClient;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

/**
 * This module provides network-related dependencies using Dagger Hilt.
 */
@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    /**
     * Provides a singleton instance of Retrofit.
     *
     * @param client The OkHttpClient instance.
     * @param gson   The Gson instance.
     * @return A Retrofit instance.
     */
    @Provides
    @Singleton
    public Retrofit provideRetrofit(OkHttpClient client, Gson gson) {
        return RetrofitClient.createRetrofit(client, gson);
    }

    /**
     * Provides a singleton instance of OkHttpClient.
     *
     * @param logging The HttpLoggingInterceptor instance.
     * @return An OkHttpClient instance.
     */
    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(HttpLoggingInterceptor logging) {
        return RetrofitClient.createOkHttpClient(logging);
    }

    /**
     * Provides a singleton instance of HttpLoggingInterceptor.
     *
     * @return An HttpLoggingInterceptor instance.
     */
    @Provides
    @Singleton
    public HttpLoggingInterceptor provideLoggingInterceptor() {
        return RetrofitClient.createLoggingInterceptor();
    }

    /**
     * Provides a singleton instance of Gson.
     *
     * @return A Gson instance.
     */
    @Provides
    @Singleton
    public Gson provideGson() {
        return RetrofitClient.createGson();
    }
}