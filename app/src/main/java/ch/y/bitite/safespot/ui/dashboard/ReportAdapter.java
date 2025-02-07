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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        ReportValidated report = reports.get(position);
        holder.description.setText(report.description);
        holder.comment.setText(report.comment);
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView description, comment;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.textDescription);
            comment = itemView.findViewById(R.id.textComment);
        }
    }
}