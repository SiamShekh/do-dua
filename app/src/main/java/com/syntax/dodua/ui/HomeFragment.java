package com.syntax.dodua.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.syntax.dodua.ContentActivity;
import com.syntax.dodua.R;
import com.syntax.dodua.data.ContentItem;
import com.syntax.dodua.data.ContentRepository;

import java.time.chrono.HijrahDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SystemBars.addStatusBarPadding(view);
        ThemeSettings.bindToggle(view.findViewById(R.id.btn_theme), true);
        bindHijriDate(view);
        ContentRepository data = ContentRepository.get(requireContext());
        ContentItem hero = data.dhikrOfTheDay();
        ((TextView) view.findViewById(R.id.text_hero_arabic)).setText(hero.arabic);
        ((TextView) view.findViewById(R.id.text_hero_translation)).setText(hero.translation);
        ReadingSettings.applyArabic(view.findViewById(R.id.text_hero_arabic), false);
        ReadingSettings.applyBangla(view.findViewById(R.id.text_hero_translation), false);
        View.OnClickListener openHero = v -> open(hero.id, null);
        view.findViewById(R.id.text_hero_arabic).setOnClickListener(openHero);
        view.findViewById(R.id.text_hero_translation).setOnClickListener(openHero);

        LinearLayout shortcuts = view.findViewById(R.id.shortcuts_row);
        shortcuts.removeAllViews();
        for (ContentRepository.Shortcut shortcut : data.shortcuts()) {
            View tile = inflater().inflate(R.layout.item_header_stat, shortcuts, false);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            tile.setLayoutParams(params);
            ((ImageView) tile.findViewById(R.id.stat_icon)).setImageResource(shortcut.iconRes);
            ((TextView) tile.findViewById(R.id.stat_label)).setText(shortcut.label);
            tile.setOnClickListener(v -> openTopic(data, shortcut.query));
            shortcuts.addView(tile);
        }

        RecyclerView list = view.findViewById(R.id.list_recommended);
        list.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        list.setAdapter(new RecommendedAdapter(data.recommended(), item -> open(item.id, null)));
        list.setNestedScrollingEnabled(false);

        LinearLayout today = view.findViewById(R.id.todays_dhikr_list);
        today.removeAllViews();
        for (ContentItem item : data.todaysDhikr()) {
            View row = inflater().inflate(R.layout.item_home_dhikr, today, false);
            ((TextView) row.findViewById(R.id.row_title)).setText(item.title);
            ((TextView) row.findViewById(R.id.row_arabic)).setText(item.arabic);
            ((TextView) row.findViewById(R.id.row_translation)).setText(item.translation);
            ReadingSettings.applyBangla(row.findViewById(R.id.row_title), false);
            ReadingSettings.applyArabic(row.findViewById(R.id.row_arabic), false);
            ReadingSettings.applyBangla(row.findViewById(R.id.row_translation), false);
            ((TextView) row.findViewById(R.id.row_meta)).setText(item.category);
            TextView count = row.findViewById(R.id.row_count);
            if (item.targetCount > 0) {
                count.setVisibility(View.VISIBLE);
                count.setText(item.targetCount + "×");
            } else {
                count.setVisibility(View.GONE);
            }
            row.setOnClickListener(v -> open(item.id, null));
            today.addView(row);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        View view = getView();
        if (view != null) {
            bindHijriDate(view);
        }
    }

    private void bindHijriDate(View view) {
        HijrahDate today = HijrahDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);
        ((TextView) view.findViewById(R.id.text_date)).setText(today.format(formatter) + " H");
    }

    private LayoutInflater inflater() {
        return LayoutInflater.from(requireContext());
    }

    private void openTopic(ContentRepository data, String query) {
        List<ContentItem> matches = data.searchTitles(query);
        if (matches.isEmpty()) return;
        open(matches.get(0).id, query);
    }

    private void open(String id, String topic) {
        Intent intent = new Intent(requireContext(), ContentActivity.class);
        intent.putExtra(ContentActivity.EXTRA_ID, id);
        if (topic != null) {
            intent.putExtra(ContentActivity.EXTRA_TOPIC, topic);
        }
        startActivity(intent);
    }
}
