package ch.y.bitite.safespot.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Utility class for file operations.
 */
public class FileUtils {

    /**
     * Retrieves a File object from a given URI.
     *
     * @param uri     The URI of the file.
     * @param context The application context.
     * @return The File object, or null if an error occurred.
     */
    public static File getFileFromUri(Uri uri, Context context) {
        File file = null;
        InputStream inputStream;
        try {
            String fileName = getFileName(uri, context);
            if (fileName == null) {
                fileName = "temp_image";
            }
            file = new File(context.getCacheDir(), fileName);
            inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            if (inputStream != null) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                outputStream.flush();
                outputStream.close();
            }
            Objects.requireNonNull(inputStream).close();
        } catch (IOException e) {
            Log.e("FileUtils", "Error reading file from URI", e);
        }
        return file;
    }

    /**
     * Gets the file name from a given URI.
     *
     * @param uri     The URI of the file.
     * @param context The application context.
     * @return The file name, or null if it could not be determined.
     */
    private static String getFileName(Uri uri, Context context) {
        String result = null;
        if (Objects.equals(uri.getScheme(), "content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            Objects.requireNonNull(result);
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}