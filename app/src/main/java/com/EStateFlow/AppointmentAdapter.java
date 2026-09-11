package com.EStateFlow;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    public interface OnAppointmentClickListener {
        void onStatusChange(ViewingAppointment appointment, AppointmentStatus newStatus);
        void onClick(ViewingAppointment appointment);
    }

    private final List<ViewingAppointment> appointments;
    private final OnAppointmentClickListener listener;

    public AppointmentAdapter(List<ViewingAppointment> appointments, OnAppointmentClickListener listener) {
        this.appointments = appointments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ViewingAppointment appt = appointments.get(position);

        holder.txtApptTitle.setText(appt.getPropertyTitle());
        holder.txtApptAddress.setText(appt.getPropertyAddress());
        holder.txtApptClient.setText(appt.getClientName());
        holder.txtApptDateTime.setText(appt.getDate() + " • " + appt.getTimeSlot());
        holder.txtApptType.setText(appt.getType());
        holder.txtApptNotes.setText(appt.getNotes());

        // Status badge
        android.content.Context context = holder.itemView.getContext();
        String statusLabel;
        int statusColor;
        switch (appt.getStatus()) {
            case UPCOMING:   statusLabel = "Upcoming";  statusColor = androidx.core.content.ContextCompat.getColor(context, R.color.primary_green); break;
            case COMPLETED:  statusLabel = "Completed"; statusColor = androidx.core.content.ContextCompat.getColor(context, R.color.text_muted); break;
            case CANCELLED:  statusLabel = "Cancelled"; statusColor = androidx.core.content.ContextCompat.getColor(context, R.color.badge_red); break;
            case PENDING:    statusLabel = "Pending";   statusColor = androidx.core.content.ContextCompat.getColor(context, R.color.rating_amber); break;
            default:         statusLabel = "Unknown";   statusColor = androidx.core.content.ContextCompat.getColor(context, R.color.text_muted);
        }
        holder.txtApptStatus.setText(statusLabel);
        holder.txtApptStatus.setTextColor(statusColor);

        // Confirm button (only shown when Upcoming)
        if (appt.getStatus() == AppointmentStatus.UPCOMING) {
            holder.btnConfirm.setVisibility(View.VISIBLE);
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnConfirm.setOnClickListener(v ->
                    listener.onStatusChange(appt, AppointmentStatus.COMPLETED));
            holder.btnCancel.setOnClickListener(v ->
                    listener.onStatusChange(appt, AppointmentStatus.CANCELLED));
        } else {
            holder.btnConfirm.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onClick(appt));
    }

    @Override
    public int getItemCount() { return appointments.size(); }

    public void notifyListChanged() { notifyDataSetChanged(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtApptTitle, txtApptAddress, txtApptClient,
                txtApptDateTime, txtApptType, txtApptNotes, txtApptStatus;
        View btnConfirm, btnCancel;

        ViewHolder(View itemView) {
            super(itemView);
            txtApptTitle    = itemView.findViewById(R.id.txtApptTitle);
            txtApptAddress  = itemView.findViewById(R.id.txtApptAddress);
            txtApptClient   = itemView.findViewById(R.id.txtApptClient);
            txtApptDateTime = itemView.findViewById(R.id.txtApptDateTime);
            txtApptType     = itemView.findViewById(R.id.txtApptType);
            txtApptNotes    = itemView.findViewById(R.id.txtApptNotes);
            txtApptStatus   = itemView.findViewById(R.id.txtApptStatus);
            btnConfirm      = itemView.findViewById(R.id.btnApptConfirm);
            btnCancel       = itemView.findViewById(R.id.btnApptCancel);
        }
    }
}
