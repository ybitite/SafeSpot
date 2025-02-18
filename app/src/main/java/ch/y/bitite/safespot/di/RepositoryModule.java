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

@Module
@InstallIn(SingletonComponent.class)
public class RepositoryModule {

    @Provides
    @Singleton
    public ApiService provideApiService(Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }

    @Provides
    @Singleton
    public ExecutorService provideExecutorService() {
        return Executors.newSingleThreadExecutor();
    }

    @Provides
    @Singleton
    public ReportLocalDataSource provideReportLocalDataSource(ReportDao reportDao, ExecutorService executorService) {
        return new ReportLocalDataSource(reportDao, executorService);
    }

    @Provides
    @Singleton
    public ReportRemoteDataSource provideReportRemoteDataSource(ApiService apiService) {
        return new ReportRemoteDataSource(apiService);
    }

    @Provides
    @Singleton
    public ReportFileDataSource provideReportFileDataSource(@ApplicationContext Context context) {
        return new ReportFileDataSource(context);
    }

    @Provides
    @Singleton
    public ReportRepository provideReportRepository(
            ReportLocalDataSource localDataSource,
            ReportRemoteDataSource remoteDataSource,
            ReportFileDataSource fileDataSource,
            @ApplicationContext Context context // Add this line
    ) {
        return new ReportRepository(localDataSource, remoteDataSource, fileDataSource, context); // Add context here
    }
}