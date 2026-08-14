package com.syntax.dodua.data;

import android.content.Context;

import com.syntax.dodua.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ContentRepository {

    public static final String ALL = "সব";
    public static final String TYPE_DHIKR = "dhikr";
    public static final String TYPE_DUA = "dua";

    private static final String[] DHIKR_SLUGS = {
            "morning-dhikr", "evening-dhikr", "dhikr-after-salah"
    };
    private static final String[] DUA_SLUGS = {
            "daily-dua", "selected-dua"
    };

    private static ContentRepository instance;

    private final List<ContentItem> dhikr = new ArrayList<>();
    private final List<ContentItem> duas = new ArrayList<>();
    private final Map<String, String> categoryNames = new LinkedHashMap<>();

    public static ContentRepository get(Context context) {
        if (instance == null) {
            instance = new ContentRepository(context.getApplicationContext());
        }
        return instance;
    }

    private ContentRepository(Context context) {
        loadCategories(context);
        for (String slug : DHIKR_SLUGS) {
            dhikr.addAll(loadItems(context, slug, TYPE_DHIKR));
        }
        for (String slug : DUA_SLUGS) {
            duas.addAll(loadItems(context, slug, TYPE_DUA));
        }
    }

    public List<ContentItem> dhikr() {
        return dhikr;
    }

    public List<ContentItem> duas() {
        return duas;
    }

    public ContentItem dhikrOfTheDay() {
        if (dhikr.isEmpty()) {
            return emptyItem();
        }
        int index = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % dhikr.size();
        return dhikr.get(index);
    }

    public List<ContentItem> todaysDhikr() {
        List<ContentItem> out = new ArrayList<>();
        if (dhikr.isEmpty()) return out;
        int start = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % dhikr.size();
        int limit = Math.min(3, dhikr.size());
        for (int i = 0; i < limit; i++) {
            out.add(dhikr.get((start + i) % dhikr.size()));
        }
        return out;
    }

    public List<ContentItem> recommended() {
        List<ContentItem> source = duas;
        List<ContentItem> out = new ArrayList<>();
        int limit = Math.min(4, source.size());
        for (int i = 0; i < limit; i++) {
            out.add(source.get(i));
        }
        return out;
    }

    public List<String> dhikrCategories() {
        return categoriesFor(DHIKR_SLUGS);
    }

    public List<String> duaCategories() {
        return categoriesFor(DUA_SLUGS);
    }

    public List<ContentItem> filter(List<ContentItem> source, String category) {
        if (category == null || ALL.equals(category)) return source;
        List<ContentItem> out = new ArrayList<>();
        for (ContentItem item : source) {
            if (category.equals(item.category)) out.add(item);
        }
        return out;
    }

    public ContentItem find(String id) {
        for (ContentItem item : dhikr) {
            if (item.id.equals(id)) return item;
        }
        for (ContentItem item : duas) {
            if (item.id.equals(id)) return item;
        }
        return dhikrOfTheDay();
    }

    public List<ContentItem> related(ContentItem item) {
        List<ContentItem> source = TYPE_DHIKR.equals(item.type) ? dhikr : duas;
        List<ContentItem> sameCategory = filter(source, item.category);
        return sameCategory.isEmpty() ? source : sameCategory;
    }

    public List<Shortcut> shortcuts() {
        return Arrays.asList(
                new Shortcut("ঘুম", "ঘুম", R.drawable.ic_sleep),
                new Shortcut("পায়খানা", "পায়খানা", R.drawable.ic_toilet),
                new Shortcut("খাবার", "খাওয়", R.drawable.ic_food),
                new Shortcut("সফর", "সফর", R.drawable.ic_tour),
                new Shortcut("মসজিদ", "মসজিদ", R.drawable.ic_mosque_minner)
        );
    }

    public List<ContentItem> searchTitles(String query) {
        List<ContentItem> out = new ArrayList<>();
        if (query == null || query.isEmpty()) return out;
        for (ContentItem item : duas) {
            if (item.title.contains(query)) out.add(item);
        }
        return out;
    }

    private List<String> categoriesFor(String[] slugs) {
        List<String> names = new ArrayList<>();
        names.add(ALL);
        for (String slug : slugs) {
            String name = categoryNames.get(slug);
            if (name != null) names.add(name);
        }
        return names;
    }

    private void loadCategories(Context context) {
        try {
            JSONObject root = new JSONObject(readAsset(context, "core/categories.json"));
            JSONArray list = root.getJSONArray("bn");
            for (int i = 0; i < list.length(); i++) {
                JSONObject obj = list.getJSONObject(i);
                categoryNames.put(obj.getString("slug"), obj.getString("name"));
            }
        } catch (Exception ignored) {
        }
    }

    private List<ContentItem> loadItems(Context context, String slug, String type) {
        List<ContentItem> items = new ArrayList<>();
        String category = categoryNames.containsKey(slug) ? categoryNames.get(slug) : slug;
        try {
            JSONArray array = new JSONArray(readAsset(context, "dua-dhikr/" + slug + "/bn.json"));
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String notes = text(obj, "notes");
                String benefits = text(obj, "benefits");
                if (benefits.isEmpty()) benefits = text(obj, "fawaid");
                items.add(new ContentItem(
                        slug + "-" + i,
                        type,
                        category,
                        text(obj, "title"),
                        text(obj, "arabic"),
                        text(obj, "latin"),
                        text(obj, "translation"),
                        notes,
                        benefits,
                        text(obj, "source"),
                        parseCount(notes)
                ));
            }
        } catch (Exception ignored) {
        }
        return items;
    }

    private static String text(JSONObject obj, String key) {
        if (!obj.has(key) || obj.isNull(key)) return "";
        return obj.optString(key, "").trim();
    }

    static int parseCount(String notes) {
        if (notes == null || notes.isEmpty()) return 0;
        StringBuilder digits = new StringBuilder();
        for (int i = 0; i < notes.length(); i++) {
            char c = notes.charAt(i);
            int n = digit(c);
            if (n >= 0) {
                digits.append(n);
            } else if (digits.length() > 0) {
                break;
            }
        }
        if (digits.length() == 0) return 0;
        try {
            return Integer.parseInt(digits.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static int digit(char c) {
        if (c >= '0' && c <= '9') return c - '0';
        switch (c) {
            case '০': return 0;
            case '১': return 1;
            case '২': return 2;
            case '৩': return 3;
            case '৪': return 4;
            case '৫': return 5;
            case '৬': return 6;
            case '৭': return 7;
            case '৮': return 8;
            case '৯': return 9;
            default: return -1;
        }
    }

    private static String readAsset(Context context, String path) throws Exception {
        try (InputStream in = context.getAssets().open(path);
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line).append('\n');
            }
            return out.toString();
        }
    }

    private static ContentItem emptyItem() {
        return new ContentItem("", TYPE_DHIKR, "", "", "", "", "", "", "", "", 0);
    }

    public static final class Shortcut {
        public final String label;
        public final String query;
        public final int iconRes;

        public Shortcut(String label, String query, int iconRes) {
            this.label = label;
            this.query = query;
            this.iconRes = iconRes;
        }
    }
}
