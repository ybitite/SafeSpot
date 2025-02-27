package ch.y.bitite.safespot.ui;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import ch.y.bitite.safespot.R;
import ch.y.bitite.safespot.utils.ImageLoader;

public class FullScreenImageActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGE_URL = "image_url";
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        imageLoader = new ImageLoader(this);

        ImageView fullScreenImageView = findViewById(R.id.fullScreenImageView);

        // Récupérer l'URL de l'image depuis l'Intent
        String imageUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);

        // Charger l'image avec ImageLoader
        imageLoader.loadImage(imageUrl, fullScreenImageView, new ImageLoader.ImageLoadCallback() {
            @Override
            public void onImageLoaded() {
                // Image loaded successfully
            }

            @Override
            public void onImageLoadFailed() {
                // Image load failed
            }
        });
    }
}