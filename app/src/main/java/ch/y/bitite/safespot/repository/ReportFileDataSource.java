package ch.y.bitite.safespot.repository;

import android.content.Context;
import android.net.Uri;

import java.io.File;

import javax.inject.Inject;

import ch.y.bitite.safespot.utils.FileUtils;
import dagger.hilt.android.qualifiers.ApplicationContext;

public class ReportFileDataSource {
    private final Context context;

    /**
     * Constructor for ReportFileDataSource.
     *
     * @param context The application context.
     */
    @Inject
    public ReportFileDataSource(@ApplicationContext Context context) {
        this.context = context;
    }

    /**
     * Get the file from the uri.
     *
     * @param imageUri The uri of the image.
     * @return The file.
     */
    public File getFileFromUri(Uri imageUri) {
        return imageUri != null ? FileUtils.getFileFromUri(imageUri, context) : null;
    }
}