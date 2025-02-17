package ch.y.bitite.safespot.di;

import com.bumptech.glide.RequestManager;

import ch.y.bitite.safespot.ui.dashboard.ReportAdapter;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.FragmentComponent;
import dagger.hilt.android.scopes.FragmentScoped;

@Module
@InstallIn(FragmentComponent.class)
public class AdapterModule {

    @Provides
    @FragmentScoped
    public ReportAdapter provideReportAdapter(RequestManager glide) {
        return new ReportAdapter(glide);
    }
}