package ch.y.bitite.safespot.di;

import ch.y.bitite.safespot.ui.dashboard.ReportAdapter;
import ch.y.bitite.safespot.utils.ImageLoader;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.FragmentComponent;
import dagger.hilt.android.scopes.FragmentScoped;

/**
 * This module provides dependencies related to adapters, specifically the ReportAdapter.
 * It uses Dagger Hilt for dependency injection.
 */
@Module
@InstallIn(FragmentComponent.class)
public class AdapterModule {

    /**
     * Provides an instance of ReportAdapter.
     *
     * @param imageLoader The ImageLoader instance to be injected into the ReportAdapter.
     * @return An instance of ReportAdapter.
     */
    @Provides
    @FragmentScoped
    public ReportAdapter provideReportAdapter(ImageLoader imageLoader) {
        return new ReportAdapter(imageLoader);
    }
}