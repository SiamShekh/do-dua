package com.syntax.dodua;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.syntax.dodua.data.ContentItem;
import com.syntax.dodua.data.ContentRepository;
import com.syntax.dodua.ui.SystemBars;
import com.syntax.dodua.ui.ThemeSettings;

import java.util.List;

public class ContentActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "extra_id";
    public static final String EXTRA_TOPIC = "extra_topic";

    private ContentRepository repository;
    private String topic;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemBars.apply(this, true);
        setContentView(R.layout.activity_content);
        ThemeSettings.bindToggle(findViewById(R.id.btn_theme), false);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        repository = ContentRepository.get(this);
        topic = getIntent().getStringExtra(EXTRA_TOPIC);
        String id = getIntent().getStringExtra(EXTRA_ID);
        bind(repository.find(id));

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void bind(ContentItem item) {
        ((TextView) findViewById(R.id.toolbar_title)).setText(
                ContentRepository.TYPE_DHIKR.equals(item.type) ? "Dhikr" : "Dua");
        ((TextView) findViewById(R.id.text_item_title)).setText(item.title);
        String meta = item.category;
        if (item.targetCount > 0) {
            meta += " · " + item.targetCount + "×";
        }
        ((TextView) findViewById(R.id.meta_bar)).setText(meta);
        ((TextView) findViewById(R.id.text_arabic)).setText(item.arabic);
        setOptionalText(R.id.text_transliteration, item.transliteration);
        ((TextView) findViewById(R.id.text_translation)).setText(item.translation);
        setOptionalText(R.id.text_notes, item.notes);
        setOptionalText(R.id.text_benefits, item.benefits);
        View benefitsCard = findViewById(R.id.benefits_card);
        benefitsCard.setVisibility(
                item.benefits == null || item.benefits.isEmpty() ? View.GONE : View.VISIBLE);
        setOptionalText(R.id.text_source, item.source);

        LinearLayout tabs = findViewById(R.id.related_tabs);
        tabs.removeAllViews();
        List<ContentItem> related = topic != null
                ? repository.searchTitles(topic)
                : repository.related(item);
        for (ContentItem relatedItem : related) {
            TextView tab = (TextView) LayoutInflater.from(this)
                    .inflate(R.layout.item_tab, tabs, false);
            tab.setText(relatedItem.title);
            tab.setSelected(relatedItem.id.equals(item.id));
            tab.setOnClickListener(v -> bind(relatedItem));
            tabs.addView(tab);
        }

        View counter = findViewById(R.id.counter_block);
        TextView countText = findViewById(R.id.text_count);
        if (item.targetCount > 0) {
            counter.setVisibility(View.VISIBLE);
            count = 0;
            countText.setText("0 / " + item.targetCount);
            countText.setOnClickListener(v -> {
                if (count < item.targetCount) count++;
                else count = 0;
                countText.setText(count + " / " + item.targetCount);
            });
        } else {
            counter.setVisibility(View.GONE);
        }
    }

    private void setOptionalText(int viewId, String value) {
        TextView view = findViewById(viewId);
        if (value == null || value.isEmpty()) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
            view.setText(value);
        }
    }
}
