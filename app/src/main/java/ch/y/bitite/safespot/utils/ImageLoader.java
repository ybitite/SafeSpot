package ch.y.bitite.safespot.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import ch.y.bitite.safespot.BuildConfig;
import ch.y.bitite.safespot.R;

/**
 * Utility class for loading images into ImageViews using Glide.
 */
public class ImageLoader {
    private static final String TAG = "ImageLoader";
    private final Context context;

    /**
     * Constructor for ImageLoader.
     *
     * @param context The application context.
     */
    public ImageLoader(Context context) {
        this.context = context;
    }

    /**
     * Loads an image into an ImageView.
     *
     * @param imageFileName The name of the image file to load.
     * @param imageView     The ImageView to load the image into.
     * @param callback      The callback to notify of image loading events.
     */
    public void loadImage(String imageFileName, ImageView imageView, ImageLoadCallback callback) {
        Log.d(TAG, "loadImage: " + imageFileName);
        if (imageFileName == null || imageFileName.trim().isEmpty()) {
            // Load the "not found" image
            Log.d(TAG, "loadImage: imageFileName is null or empty");
            loadNotFoundImage(imageView);
            if (callback != null) {
                callback.onImageLoadFailed();
            }
        } else {
            // Load the image from the server
            Glide.with(context)
                    .asBitmap()
                    .load(BuildConfig.BASE_URL_IMAGES + imageFileName)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, @NonNull Target<Bitmap> target, boolean isFirstResource) {
                            Log.e(TAG, "onLoadFailed: Failed to load image", e);
                            loadNotFoundImage(imageView);
                            if (callback != null) {
                                callback.onImageLoadFailed();
                            }
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(@NonNull Bitmap resource, @NonNull Object model, Target<Bitmap> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                            if (callback != null) {
                                callback.onImageLoaded();
                            }
                            return false;
                        }
                    })
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            imageView.setImageBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // Handle placeholder if needed
                        }
                    });
        }
    }

    /**
     * Loads the "not found" image into an ImageView.
     *
     * @param imageView The ImageView to load the image into.
     */
    private void loadNotFoundImage(ImageView imageView) {
        // Load the "not found" image from resources
        Drawable notFoundDrawable = ContextCompat.getDrawable(context, R.drawable.image_not_found);
        imageView.setImageDrawable(notFoundDrawable);
    }

    /**
     * Callback interface for image loading events.
     */
    public interface ImageLoadCallback {
        /**
         * Called when the image has been successfully loaded.
         */
        void onImageLoaded();

        /**
         * Called when the image loading has failed.
         */
        void onImageLoadFailed();
    }
}