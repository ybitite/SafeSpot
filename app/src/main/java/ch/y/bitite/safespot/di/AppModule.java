package ch.y.bitite.safespot.di;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import javax.inject.Singleton;

import ch.y.bitite.safespot.utils.ImageLoader;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

/**
 * This module provides application-wide dependencies using Dagger Hilt.
 */
@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    /**
     * Provides a singleton instance of RequestManager for image loading with Glide.
     *
     * @param context The application context.
     * @return A RequestManager instance.
     */
    @Provides
    @Singleton
    public RequestManager provideGlide(@ApplicationContext Context context) {
        return Glide.with(context);
    }

    /**
     * Provides the application context.
     *
     * @param context The application context.
     * @return The application context.
     */
    @Provides
    @Singleton
    public Context provideContext(@ApplicationContext Context context) {
        return context;
    }

    /**
     * Provides a singleton instance of ImageLoader.
     *
     * @param context The application context.
     * @return An ImageLoader instance.
     */
    @Provides
    @Singleton
    public ImageLoader provideImageLoader(@ApplicationContext Context context) {
        return new ImageLoader(context);
    }
}