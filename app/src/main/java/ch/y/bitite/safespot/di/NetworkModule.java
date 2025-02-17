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

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    @Provides
    @Singleton
    public Retrofit provideRetrofit(OkHttpClient client, Gson gson) {
        return RetrofitClient.createRetrofit(client, gson);
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(HttpLoggingInterceptor logging) {
        return RetrofitClient.createOkHttpClient(logging);
    }

    @Provides
    @Singleton
    public HttpLoggingInterceptor provideLoggingInterceptor() {
        return RetrofitClient.createLoggingInterceptor();
    }

    @Provides
    @Singleton
    public Gson provideGson() {
        return RetrofitClient.createGson();
    }
}