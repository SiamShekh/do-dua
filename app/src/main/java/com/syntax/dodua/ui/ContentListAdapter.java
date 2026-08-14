package com.syntax.dodua.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.syntax.dodua.R;
import com.syntax.dodua.data.ContentItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class ContentListAdapter extends RecyclerView.Adapter<ContentListAdapter.Holder> {

    private final List<ContentItem> items = new ArrayList<>();
    private final Consumer<ContentItem> onClick;

    public ContentListAdapter(Consumer<ContentItem> onClick) {
        this.onClick = onClick;
    }

    public void submit(List<ContentItem> next) {
        items.clear();
        items.addAll(next);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_content_row, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        ContentItem item = items.get(position);
        holder.index.setText(String.format(Locale.US, "%02d", position + 1));
        holder.title.setText(item.title);
        holder.translation.setText(item.translation);
        ReadingSettings.applyBangla(holder.title, false);
        ReadingSettings.applyBangla(holder.translation, false);
        holder.meta.setText(item.category);
        if (item.targetCount > 0) {
            holder.count.setVisibility(View.VISIBLE);
            holder.count.setText(item.targetCount + "×");
        } else {
            holder.count.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(v -> onClick.accept(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final TextView index;
        final TextView title;
        final TextView translation;
        final TextView meta;
        final TextView count;

        Holder(@NonNull View itemView) {
            super(itemView);
            index = itemView.findViewById(R.id.row_index);
            title = itemView.findViewById(R.id.row_title);
            translation = itemView.findViewById(R.id.row_translation);
            meta = itemView.findViewById(R.id.row_meta);
            count = itemView.findViewById(R.id.row_count);
        }
    }
}
