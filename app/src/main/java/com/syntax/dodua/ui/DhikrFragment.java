package com.syntax.dodua.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.syntax.dodua.ContentActivity;
import com.syntax.dodua.R;
import com.syntax.dodua.data.ContentItem;
import com.syntax.dodua.data.ContentRepository;

import java.util.List;

public class DhikrFragment extends Fragment {

    private ContentListAdapter adapter;
    private ContentRepository data;
    private String selected = ContentRepository.ALL;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SystemBars.addStatusBarPadding(view);
        ThemeSettings.bindToggle(view.findViewById(R.id.btn_theme), false);
        ((TextView) view.findViewById(R.id.list_title)).setText(R.string.daily_dhikr);
        adapter = new ContentListAdapter(this::open);
        RecyclerView list = view.findViewById(R.id.content_list);
        list.setLayoutManager(new LinearLayoutManager(requireContext()));
        list.setAdapter(adapter);
        data = ContentRepository.get(requireContext());
        bindTabs(view.findViewById(R.id.tabs_row), data.dhikrCategories());
        refresh();
    }

    private void bindTabs(LinearLayout row, List<String> categories) {
        row.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        for (String category : categories) {
            TextView tab = (TextView) inflater.inflate(R.layout.item_tab, row, false);
            tab.setText(category);
            tab.setSelected(category.equals(selected));
            tab.setOnClickListener(v -> {
                selected = category;
                bindTabs(row, categories);
                refresh();
            });
            row.addView(tab);
        }
    }

    private void refresh() {
        adapter.submit(data.filter(data.dhikr(), selected));
    }

    private void open(ContentItem item) {
        Intent intent = new Intent(requireContext(), ContentActivity.class);
        intent.putExtra(ContentActivity.EXTRA_ID, item.id);
        startActivity(intent);
    }
}
