package ch.y.bitite.safespot.ui.dashboard;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import javax.inject.Inject;

import ch.y.bitite.safespot.R;
import ch.y.bitite.safespot.model.ReportValidated;
import ch.y.bitite.safespot.utils.ImageLoader;
import dagger.hilt.android.scopes.FragmentScoped;

@FragmentScoped
public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private List<ReportValidated> reports;
    private final ImageLoader imageLoader;

    @Inject
    public ReportAdapter(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    public void updateReports(List<ReportValidated> reports) {
        this.reports = reports;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ch.y.bitite.safespot.databinding.ItemReportBinding binding = ch.y.bitite.safespot.databinding.ItemReportBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ReportViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        ReportValidated currentReport = reports.get(position);
        holder.textViewDescription.setText(currentReport.getDescription());
        holder.textViewLatitude.setText(String.valueOf(currentReport.getLatitude()));
        holder.textViewLongitude.setText(String.valueOf(currentReport.getLongitude()));
        holder.textViewDateTime.setText(String.valueOf(currentReport.getDateTimeString()));

        // Load the image using ImageLoader
        if (currentReport.getImage() != null && !currentReport.getImage().isEmpty()) {
            imageLoader.loadImage(currentReport.getImage(), holder.imageViewReport);
        } else {
            // If there's no image, you can set a placeholder or clear the ImageView
            holder.imageViewReport.setImageResource(R.drawable.image_not_found);
        }
    }

    @Override
    public int getItemCount() {
        return reports == null ? 0 : reports.size();
    }

    public class ReportViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewDescription;
        private final TextView textViewLatitude;
        private final TextView textViewLongitude;
        private final TextView textViewDateTime;
        private final ImageView imageViewReport;

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