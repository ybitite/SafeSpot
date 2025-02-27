package ch.y.bitite.safespot.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import javax.inject.Inject;

import ch.y.bitite.safespot.R;
import ch.y.bitite.safespot.databinding.InfoWindowLayoutBinding;
import ch.y.bitite.safespot.databinding.ItemReportBinding;
import ch.y.bitite.safespot.model.ReportValidated;
import ch.y.bitite.safespot.ui.FullScreenImageActivity;
import ch.y.bitite.safespot.utils.ImageLoader;
import dagger.hilt.android.scopes.FragmentScoped;

/**
 * Adapter for displaying a list of validated reports in a RecyclerView.
 */
@FragmentScoped
public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private List<ReportValidated> reports;
    private final ImageLoader imageLoader;
    private static final int VIEW_TYPE_SIMPLE = 0;
    private static final int VIEW_TYPE_DETAIL = 1;
    private int expandedPosition = RecyclerView.NO_POSITION;
    private Context context;
    private RecyclerView recyclerView;

    /**
     * Constructor for ReportAdapter.
     *
     * @param imageLoader The ImageLoader for loading images.
     */
    @Inject
    public ReportAdapter(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    /**
     * Updates the list of reports and notifies the adapter of the data change.
     *
     * @param reports The new list of reports.
     */
    @SuppressLint("NotifyDataSetChanged")
    public void setReports(List<ReportValidated> reports) {
        this.reports = reports;
        notifyDataSetChanged();
    }

    /**
     * Called when RecyclerView needs a new {@link ReportViewHolder} of the given type to represent
     * an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ReportViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        recyclerView = (RecyclerView) parent;
        if (viewType == VIEW_TYPE_DETAIL) {
            InfoWindowLayoutBinding detailBinding = InfoWindowLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ReportViewHolder(detailBinding);
        } else {
            ItemReportBinding simpleBinding = ItemReportBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ReportViewHolder(simpleBinding);
        }
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder   The ReportViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        ReportValidated currentReport = reports.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_DETAIL) {
            holder.bindDetail(currentReport);
        } else {
            holder.bindSimple(currentReport);
        }
        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                if (expandedPosition == adapterPosition) {
                    expandedPosition = RecyclerView.NO_POSITION;
                } else {
                    expandedPosition = adapterPosition;
                    scrollToPosition(adapterPosition);
                }
                notifyItemChanged(adapterPosition);
            }
        });
        holder.setImageViewClickListener(currentReport.getImage());
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return reports == null ? 0 : reports.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position == expandedPosition ? VIEW_TYPE_DETAIL : VIEW_TYPE_SIMPLE;
    }

    private void scrollToPosition(int position) {
        if (recyclerView != null) {
            recyclerView.post(() -> {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    layoutManager.scrollToPositionWithOffset(position, 0);
                }
            });
        }
    }

    /**
     * ViewHolder for a single report item.
     */
    public class ReportViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewDescription;
        private TextView textViewLatitude;
        private TextView textViewLongitude;
        private TextView textViewDateTime;
        private ImageView imageViewReport;
        private TextView info_window_title;
        private TextView info_window_date;
        private ImageView info_window_image;

        /**
         * Constructor for ReportViewHolder.
         *
         * @param binding The ItemReportBinding for the report item.
         */
        public ReportViewHolder(@NonNull ItemReportBinding binding) {
            super(binding.getRoot());
            textViewDescription = binding.textViewDescription;
            textViewLatitude = binding.textViewLatitude;
            textViewLongitude = binding.textViewLongitude;
            textViewDateTime = binding.textViewDateTime;
            imageViewReport = binding.imageViewReport;
        }

        public ReportViewHolder(@NonNull InfoWindowLayoutBinding binding) {
            super(binding.getRoot());
            info_window_title = binding.infoWindowTitle;
            info_window_date = binding.infoWindowDate;
            info_window_image = binding.infoWindowImage;
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
}