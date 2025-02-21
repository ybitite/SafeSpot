package ch.y.bitite.safespot.di;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import ch.y.bitite.safespot.model.room.ReportDao;
import ch.y.bitite.safespot.network.ApiService;
import ch.y.bitite.safespot.repository.ReportFileDataSource;
import ch.y.bitite.safespot.repository.ReportLocalDataSource;
import ch.y.bitite.safespot.repository.ReportRemoteDataSource;
import ch.y.bitite.safespot.repository.ReportRepository;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;

/**
 * This module provides repository-related dependencies using Dagger Hilt.
 */
@Module
@InstallIn(SingletonComponent.class)
public class RepositoryModule {

    /**
     * Provides a singleton instance of ApiService.
     *
     * @param retrofit The Retrofit instance.
     * @return An ApiService instance.
     */
    @Provides
    @Singleton
    public ApiService provideApiService(Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }

    /**
     * Provides a singleton instance of ExecutorService.
     *
     * @return An ExecutorService instance.
     */
    @Provides
    @Singleton
    public ExecutorService provideExecutorService() {
        return Executors.newSingleThreadExecutor();
    }

    /**
     * Provides a singleton instance of ReportLocalDataSource.
     *
     * @param reportDao       The ReportDao instance.
     * @param executorService The ExecutorService instance.
     * @param context         The application context.
     * @return A ReportLocalDataSource instance.
     */
    @Provides
    @Singleton
    public ReportLocalDataSource provideReportLocalDataSource(ReportDao reportDao, ExecutorService executorService, Context context) {
        return new ReportLocalDataSource(reportDao, executorService, context);
    }

    /**
     * Provides a singleton instance of ReportRemoteDataSource.
     *
     * @param apiService The ApiService instance.
     * @return A ReportRemoteDataSource instance.
     */
    @Provides
    @Singleton
    public ReportRemoteDataSource provideReportRemoteDataSource(ApiService apiService, Context context) {
        return new ReportRemoteDataSource(apiService, context);
    }

    /**
     * Provides a singleton instance of ReportFileDataSource.
     *
     * @param context The application context.
     * @return A ReportFileDataSource instance.
     */
    @Provides
    @Singleton
    public ReportFileDataSource provideReportFileDataSource(@ApplicationContext Context context) {
        return new ReportFileDataSource(context);
    }

    /**
     * Provides a singleton instance of ReportRepository.
     *
     * @param localDataSource  The ReportLocalDataSource instance.
     * @param remoteDataSource The ReportRemoteDataSource instance.
     * @param fileDataSource   The ReportFileDataSource instance.
     * @return A ReportRepository instance.
     */
    @Provides
    @Singleton
    public ReportRepository provideReportRepository(
            ReportLocalDataSource localDataSource,
            ReportRemoteDataSource remoteDataSource,
            ReportFileDataSource fileDataSource
    ) {
        return new ReportRepository(localDataSource, remoteDataSource, fileDataSource);
    }
}