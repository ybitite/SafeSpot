package ch.y.bitite.safespot.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ch.y.bitite.safespot.R;
import ch.y.bitite.safespot.model.ReportValidated;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private List<ReportValidated> reports = new ArrayList<>();

    public void setReports(List<ReportValidated> reports) {
        this.reports = reports;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        ReportValidated currentReport = reports.get(position);
        holder.textViewDescription.setText(currentReport.description);
        holder.textViewLatitude.setText(String.valueOf(currentReport.latitude));
        holder.textViewLongitude.setText(String.valueOf(currentReport.longitude));
        holder.textViewDateTime.setText(String.valueOf(currentReport.date_time));
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewDescription;
        private final TextView textViewLatitude;
        private final TextView textViewLongitude;
        private final TextView textViewDateTime;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewLatitude = itemView.findViewById(R.id.textViewLatitude);
            textViewLongitude = itemView.findViewById(R.id.textViewLongitude);
            textViewDateTime = itemView.findViewById(R.id.textViewDateTime);
        }
    }
}