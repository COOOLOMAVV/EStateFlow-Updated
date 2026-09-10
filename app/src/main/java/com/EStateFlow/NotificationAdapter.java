package com.EStateFlow;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    public interface OnNotificationClickListener {
        void onClick(NotificationItem notification);
    }

    private final List<NotificationItem> notifications;
    private final OnNotificationClickListener listener;

    public NotificationAdapter(List<NotificationItem> notifications, OnNotificationClickListener listener) {
        this.notifications = notifications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationItem notif = notifications.get(position);

        holder.txtNotifTitle.setText(notif.getTitle());
        holder.txtNotifMessage.setText(notif.getMessage());
        holder.txtNotifTime.setText(notif.getTimestampFormatted());

        // Unread indicator
        if (!notif.isRead()) {
            holder.viewUnreadDot.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundColor(Color.parseColor("#F0FAF4"));
        } else {
            holder.viewUnreadDot.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(Color.WHITE);
        }

        // Type icon emoji
        String typeIcon;
        switch (notif.getType()) {
            case VIEWING_BOOKED:  typeIcon = "📅"; break;
            case NEW_INQUIRY:     typeIcon = "💬"; break;
            case PRICE_DROP:      typeIcon = "💰"; break;
            case LISTING_UPDATE:  typeIcon = "🏡"; break;
            default:              typeIcon = "🔔";
        }
        holder.txtNotifIcon.setText(typeIcon);

        holder.itemView.setOnClickListener(v -> listener.onClick(notif));
    }

    @Override
    public int getItemCount() { return notifications.size(); }

    public void notifyListChanged() { notifyDataSetChanged(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNotifIcon, txtNotifTitle, txtNotifMessage, txtNotifTime;
        View viewUnreadDot;

        ViewHolder(View itemView) {
            super(itemView);
            txtNotifIcon    = itemView.findViewById(R.id.txtNotifIcon);
            txtNotifTitle   = itemView.findViewById(R.id.txtNotifTitle);
            txtNotifMessage = itemView.findViewById(R.id.txtNotifMessage);
            txtNotifTime    = itemView.findViewById(R.id.txtNotifTime);
            viewUnreadDot   = itemView.findViewById(R.id.viewUnreadDot);
        }
    }
}
