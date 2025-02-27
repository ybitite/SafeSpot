package ch.y.bitite.safespot.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ch.y.bitite.safespot.databinding.InfoWindowLayoutBinding;
import ch.y.bitite.safespot.databinding.ItemReportBinding;
import ch.y.bitite.safespot.model.ReportValidated;
import ch.y.bitite.safespot.ui.FullScreenImageActivity;
import ch.y.bitite.safespot.utils.ImageLoader;

public class ReportViewHolder extends RecyclerView.ViewHolder {
    private final ImageLoader imageLoader;
    private final Context context;
    private TextView textViewDescription;
    private TextView textViewLatitude;
    private TextView textViewLongitude;
    private TextView textViewDateTime;
    private ImageView imageViewReport;
    private TextView info_window_title;
    private TextView info_window_date;
    private ImageView info_window_image;

    public ReportViewHolder(@NonNull ItemReportBinding binding, ImageLoader imageLoader, Context context) {
        super(binding.getRoot());
        textViewDescription = binding.textViewDescription;
        textViewLatitude = binding.textViewLatitude;
        textViewLongitude = binding.textViewLongitude;
        textViewDateTime = binding.textViewDateTime;
        imageViewReport = binding.imageViewReport;
        this.imageLoader = imageLoader;
        this.context = context;
    }

    public ReportViewHolder(@NonNull InfoWindowLayoutBinding binding, ImageLoader imageLoader, Context context) {
        super(binding.getRoot());
        info_window_title = binding.infoWindowTitle;
        info_window_date = binding.infoWindowDate;
        info_window_image = binding.infoWindowImage;
        this.imageLoader = imageLoader;
        this.context = context;
    }

    public void bindSimple(ReportValidated report) {
        textViewDescription.setText(report.getDescription());
        textViewLatitude.setText(String.valueOf(report.getLatitude()));
        textViewLongitude.setText(String.valueOf(report.getLongitude()));
        String dateTimeFormatted = report.getDateTimeString().replace("T", " ");
        textViewDateTime.setText(dateTimeFormatted);

        // Load the image using ImageLoader
        imageLoader.loadImage(report.getImage(), imageViewReport, new ImageLoader.ImageLoadCallback() {
            @Override
            public void onImageLoaded() {
                Log.e("ReportAdapter", "Image loaded");

            }

            @Override
            public void onImageLoadFailed() {
                Log.e("ReportAdapter", "Failed to load image");

            }
        });
    }

    public void bindDetail(ReportValidated report) {
        info_window_title.setText(report.getDescription());
        String dateTimeFormatted = report.getDateTimeString().replace("T", " ");
        info_window_date.setText(dateTimeFormatted);
        // Load the image using ImageLoader
        imageLoader.loadImage(report.getImage(), info_window_image, new ImageLoader.ImageLoadCallback() {
            @Override
            public void onImageLoaded() {
                Log.e("ReportAdapter", "Image loaded");

            }

            @Override
            public void onImageLoadFailed() {
                Log.e("ReportAdapter", "Failed to load image");

            }
        });
    }

    public void setImageViewClickListener(String imageUrl) {
        if (imageViewReport != null) {
            imageViewReport.setOnClickListener(v -> {
                Intent intent = new Intent(context, FullScreenImageActivity.class);
                intent.putExtra(FullScreenImageActivity.EXTRA_IMAGE_URL, imageUrl);
                context.startActivity(intent);
            });
        }
        if (info_window_image != null) {
            info_window_image.setOnClickListener(v -> {
                Intent intent = new Intent(context, FullScreenImageActivity.class);
                intent.putExtra(FullScreenImageActivity.EXTRA_IMAGE_URL, imageUrl);
                context.startActivity(intent);
            });
        }
    }
}