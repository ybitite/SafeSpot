package ch.y.bitite.safespot.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

public class ImageLoader {
    private final Context context;

    public ImageLoader(Context context) {
        this.context = context;
    }

    public void loadImage(String imageFileName, ImageView imageView) {
        String imageUrl = "https://safespotapi20250207214631.azurewebsites.net/uploads/";
        Glide.with(context)
                .asBitmap()
                .load(imageUrl+imageFileName)
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
                });
    }
}