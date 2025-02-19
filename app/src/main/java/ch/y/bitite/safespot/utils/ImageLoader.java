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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import ch.y.bitite.safespot.BuildConfig;
import ch.y.bitite.safespot.R;

public class ImageLoader {
    private static final String TAG = "ImageLoader";
    private final Context context;

    public ImageLoader(Context context) {
        this.context = context;
    }

    public void loadImage(String imageFileName, ImageView imageView) {
        Log.d(TAG, "loadImage: " + imageFileName);
        if (imageFileName == null || imageFileName.trim().isEmpty()) {
            // Load the "not found" image
            Log.d(TAG, "loadImage: imageFileName is null or empty");
            loadNotFoundImage(imageView);
        } else {
            // Load the image from the server
            Glide.with(context)
                    .asBitmap()
                    .load(BuildConfig.BASE_URL_IMAGES + imageFileName)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            imageView.setImageBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // Handle placeholder if needed
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            Log.e(TAG, "onLoadFailed: Failed to load image");
                            loadNotFoundImage(imageView);
                        }
                    });
        }
    }

    private void loadNotFoundImage(ImageView imageView) {
        // Load the "not found" image from resources
        Drawable notFoundDrawable = ContextCompat.getDrawable(context, R.drawable.image_not_found);
        imageView.setImageDrawable(notFoundDrawable);
    }
}