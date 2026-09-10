package com.EStateFlow;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * ConversationAdapter — updated to use Inquiry model (from ES Old MessagesScreen).
 * Shows sender avatar initial, name, property title, last message, time, and unread dot.
 */
public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

    public interface OnInquiryClickListener {
        void onClick(Inquiry inquiry);
    }

    private final List<Inquiry> inquiries;
    private final OnInquiryClickListener listener;

    public ConversationAdapter(List<Inquiry> inquiries, OnInquiryClickListener listener) {
        this.inquiries = inquiries;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversation_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Inquiry inquiry = inquiries.get(position);

        holder.txtSenderInitial.setText(inquiry.getInitial());
        holder.txtSenderName.setText(inquiry.getSenderName());
        holder.txtPropertyTitle.setText(inquiry.getPropertyTitle());
        holder.txtLastMessage.setText(inquiry.getMessage());
        holder.txtTimeAgo.setText(inquiry.getTimeAgo());

        // Unread indicator
        if (holder.viewUnreadDot != null) {
            holder.viewUnreadDot.setVisibility(inquiry.isUnread() ? View.VISIBLE : View.GONE);
        }
        if (holder.txtSenderName != null) {
            holder.txtSenderName.setTextColor(inquiry.isUnread()
                    ? Color.parseColor("#1C1C1E")
                    : Color.parseColor("#555555"));
        }

        holder.itemView.setOnClickListener(v -> listener.onClick(inquiry));
    }

    @Override
    public int getItemCount() { return inquiries.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtSenderInitial, txtSenderName, txtPropertyTitle, txtLastMessage, txtTimeAgo;
        View viewUnreadDot;

        ViewHolder(View itemView) {
            super(itemView);
            txtSenderInitial  = itemView.findViewById(R.id.txtAvatarInitial);
            txtSenderName     = itemView.findViewById(R.id.txtSenderName);
            txtPropertyTitle  = itemView.findViewById(R.id.txtSubject);
            txtLastMessage    = itemView.findViewById(R.id.txtSnippet);
            txtTimeAgo        = itemView.findViewById(R.id.txtTimestamp);
            viewUnreadDot     = itemView.findViewById(R.id.dotUnread);
        }
    }
}
