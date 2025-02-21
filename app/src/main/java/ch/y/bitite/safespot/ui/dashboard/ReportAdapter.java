package ch.y.bitite.safespot.ui.dashboard;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import javax.inject.Inject;

import ch.y.bitite.safespot.model.ReportValidated;
import ch.y.bitite.safespot.utils.ImageLoader;
import dagger.hilt.android.scopes.FragmentScoped;

/**
 * Adapter for displaying a list of validated reports in a RecyclerView.
 */
@FragmentScoped
public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private List<ReportValidated> reports;
    private final ImageLoader imageLoader;

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
    public void updateReports(List<ReportValidated> reports) {
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
        ch.y.bitite.safespot.databinding.ItemReportBinding binding = ch.y.bitite.safespot.databinding.ItemReportBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ReportViewHolder(binding);
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
        holder.textViewDescription.setText(currentReport.getDescription());
        holder.textViewLatitude.setText(String.valueOf(currentReport.getLatitude()));
        holder.textViewLongitude.setText(String.valueOf(currentReport.getLongitude()));
        holder.textViewDateTime.setText(String.valueOf(currentReport.getDateTimeString()));

        // Load the image using ImageLoader
        imageLoader.loadImage(currentReport.getImage(), holder.imageViewReport, new ImageLoader.ImageLoadCallback() {
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

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return reports == null ? 0 : reports.size();
    }

    /**
     * ViewHolder for a single report item.
     */
    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewDescription;
        private final TextView textViewLatitude;
        private final TextView textViewLongitude;
        private final TextView textViewDateTime;
        private final ImageView imageViewReport;

        /**
         * Constructor for ReportViewHolder.
         *
         * @param binding The ItemReportBinding for the report item.
         */
        public ReportViewHolder(@NonNull ch.y.bitite.safespot.databinding.ItemReportBinding binding) {
            super(binding.getRoot());
            textViewDescription = binding.textViewDescription;
            textViewLatitude = binding.textViewLatitude;
            textViewLongitude = binding.textViewLongitude;
            textViewDateTime = binding.textViewDateTime;
            imageViewReport = binding.imageViewReport;
        }
    }
}