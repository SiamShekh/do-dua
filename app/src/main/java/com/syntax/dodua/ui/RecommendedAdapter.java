package com.syntax.dodua.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.syntax.dodua.R;
import com.syntax.dodua.data.ContentItem;

import java.util.List;
import java.util.function.Consumer;

public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.Holder> {

    private final List<ContentItem> items;
    private final Consumer<ContentItem> onClick;

    public RecommendedAdapter(List<ContentItem> items, Consumer<ContentItem> onClick) {
        this.items = items;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recommended_card, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        ContentItem item = items.get(position);
        holder.arabic.setText(item.arabic);
        holder.title.setText(item.title);
        ReadingSettings.applyArabic(holder.arabic, false);
        ReadingSettings.applyBangla(holder.title, false);
        holder.itemView.setOnClickListener(v -> onClick.accept(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final TextView arabic;
        final TextView title;

        Holder(@NonNull View itemView) {
            super(itemView);
            arabic = itemView.findViewById(R.id.card_arabic);
            title = itemView.findViewById(R.id.card_title);
        }
    }
}
