package com.EStateFlow;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {

    private final List<ChatMessage> messages;

    public ChatMessageAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage msg = messages.get(position);

        holder.txtMessageText.setText(msg.getText());
        holder.txtMessageTime.setText(msg.getTime());

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.bubbleContainer.getLayoutParams();

        if (msg.isFromMe()) {
            // Right-aligned — agent/me
            params.gravity = Gravity.END;
            holder.bubbleContainer.setBackgroundColor(Color.parseColor("#2D6A4F"));
            holder.txtMessageText.setTextColor(Color.WHITE);
            holder.txtMessageTime.setTextColor(Color.parseColor("#AAFFCC"));
        } else {
            // Left-aligned — client
            params.gravity = Gravity.START;
            holder.bubbleContainer.setBackgroundColor(Color.parseColor("#F5F5F0"));
            holder.txtMessageText.setTextColor(Color.parseColor("#1C1C1E"));
            holder.txtMessageTime.setTextColor(Color.parseColor("#888888"));
        }
        holder.bubbleContainer.setLayoutParams(params);
    }

    @Override
    public int getItemCount() { return messages.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View bubbleContainer;
        TextView txtMessageText, txtMessageTime;

        ViewHolder(View itemView) {
            super(itemView);
            bubbleContainer  = itemView.findViewById(R.id.bubbleContainer);
            txtMessageText   = itemView.findViewById(R.id.txtMessageText);
            txtMessageTime   = itemView.findViewById(R.id.txtMessageTime);
        }
    }
}
