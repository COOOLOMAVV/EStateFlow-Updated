package com.EStateFlow;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
        Context context = holder.itemView.getContext();

        holder.txtMessageText.setText(msg.getText());
        holder.txtMessageTime.setText(msg.getTime());

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.bubbleContainer.getLayoutParams();

        if (msg.isFromMe()) {
            // Right-aligned — agent / me
            params.gravity = Gravity.END;
            holder.bubbleContainer.setBackgroundResource(R.drawable.bg_chat_bubble_me);
            holder.txtMessageText.setTextColor(Color.WHITE);
            holder.txtMessageTime.setTextColor(ContextCompat.getColor(context, R.color.primary_green_muted));
        } else {
            // Left-aligned — client
            params.gravity = Gravity.START;
            holder.bubbleContainer.setBackgroundResource(R.drawable.bg_chat_bubble_other);
            holder.txtMessageText.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
            holder.txtMessageTime.setTextColor(ContextCompat.getColor(context, R.color.text_muted));
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
