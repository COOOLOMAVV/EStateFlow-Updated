package com.EStateFlow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder> {

    public interface OnPropertyClickListener {
        void onPropertyClick(Property property);
        void onFavoriteClick(Property property, int position);
    }

    private final List<Property> properties = new ArrayList<>();
    private final OnPropertyClickListener listener;

    public PropertyAdapter(List<Property> initialList, OnPropertyClickListener listener) {
        this.listener = listener;
        setHasStableIds(true);
        if (initialList != null) {
            this.properties.addAll(initialList);
        }
    }

    public void updateList(List<Property> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new PropertyDiffCallback(this.properties, newList));
        this.properties.clear();
        if (newList != null) {
            this.properties.addAll(newList);
        }
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public long getItemId(int position) {
        return properties.get(position).getId().hashCode();
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_property_card, parent, false);
        return new PropertyViewHolder(view, listener, properties);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
        holder.bind(properties.get(position));
    }

    @Override
    public int getItemCount() {
        return properties.size();
    }

    static class PropertyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgProperty;
        private final TextView txtCategoryTag;
        final ImageButton btnFavorite;
        private final TextView txtPrice;
        private final TextView txtTitle;
        private final TextView txtLocation;
        private final TextView txtBeds;
        private final TextView txtBaths;
        private final TextView txtSqft;

        public PropertyViewHolder(@NonNull View itemView, OnPropertyClickListener listener, List<Property> properties) {
            super(itemView);
            imgProperty = itemView.findViewById(R.id.imgProperty);
            txtCategoryTag = itemView.findViewById(R.id.txtCategoryTag);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            txtBeds = itemView.findViewById(R.id.txtBeds);
            txtBaths = itemView.findViewById(R.id.txtBaths);
            txtSqft = itemView.findViewById(R.id.txtSqft);

            itemView.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null && pos < properties.size()) {
                    listener.onPropertyClick(properties.get(pos));
                }
            });

            btnFavorite.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null && pos < properties.size()) {
                    Property prop = properties.get(pos);
                    prop.setFavorite(!prop.isFavorite());
                    btnFavorite.setImageResource(prop.isFavorite() ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);
                    listener.onFavoriteClick(prop, pos);
                }
            });
        }

        public void bind(Property property) {
            imgProperty.setImageResource(property.getImageResId());
            txtCategoryTag.setText(property.getTagText());
            txtPrice.setText(property.getPrice());
            txtTitle.setText(property.getTitle());
            txtLocation.setText(property.getLocation());
            txtBeds.setText(property.getBeds() + " Beds");
            txtBaths.setText(property.getBaths() + " Baths");
            txtSqft.setText(String.format("%,d sqft", property.getSqft()));

            btnFavorite.setImageResource(property.isFavorite() ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);
        }
    }

    private static class PropertyDiffCallback extends DiffUtil.Callback {
        private final List<Property> oldList;
        private final List<Property> newList;

        public PropertyDiffCallback(List<Property> oldList, List<Property> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList != null ? oldList.size() : 0;
        }

        @Override
        public int getNewListSize() {
            return newList != null ? newList.size() : 0;
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return Objects.equals(oldList.get(oldItemPosition).getId(), newList.get(newItemPosition).getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Property oldP = oldList.get(oldItemPosition);
            Property newP = newList.get(newItemPosition);
            return oldP.isFavorite() == newP.isFavorite()
                    && Objects.equals(oldP.getPrice(), newP.getPrice())
                    && Objects.equals(oldP.getTitle(), newP.getTitle());
        }
    }
}
