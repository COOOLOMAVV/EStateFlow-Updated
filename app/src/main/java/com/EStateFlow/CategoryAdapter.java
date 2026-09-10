package com.EStateFlow;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    private final List<Category> categories;
    private final OnCategoryClickListener listener;
    private int selectedPosition = 0;

    public CategoryAdapter(List<Category> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_chip, parent, false);
        return new CategoryViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bind(categories.get(position), position == selectedPosition);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void selectPosition(int newPos) {
        if (newPos != RecyclerView.NO_POSITION && newPos != selectedPosition && newPos < categories.size()) {
            int oldPos = selectedPosition;
            selectedPosition = newPos;
            notifyItemChanged(oldPos);
            notifyItemChanged(selectedPosition);
            if (listener != null) {
                listener.onCategoryClick(categories.get(selectedPosition));
            }
        }
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout container;
        private final ImageView icon;
        private final TextView label;

        public CategoryViewHolder(@NonNull View itemView, CategoryAdapter adapter) {
            super(itemView);
            container = itemView.findViewById(R.id.chipContainer);
            icon = itemView.findViewById(R.id.chipIcon);
            label = itemView.findViewById(R.id.chipLabel);

            itemView.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                adapter.selectPosition(pos);
            });
        }

        public void bind(Category category, boolean isSelected) {
            label.setText(category.getName());
            icon.setImageResource(category.getIconResId());

            if (isSelected) {
                container.setBackgroundResource(R.drawable.bg_chip_selected);
                label.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.white));
                ImageViewCompat.setImageTintList(icon, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.white)));
            } else {
                container.setBackgroundResource(R.drawable.bg_chip_unselected);
                label.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_secondary));
                ImageViewCompat.setImageTintList(icon, ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.text_secondary)));
            }
        }
    }
}
