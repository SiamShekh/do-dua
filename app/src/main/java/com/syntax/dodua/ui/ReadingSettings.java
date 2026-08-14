package com.syntax.dodua.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.widget.TextView;

import androidx.annotation.FontRes;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.syntax.dodua.R;

public final class ReadingSettings {

    private static final String PREFS = "dodua_prefs";
    private static final String KEY_ARABIC_FONT = "arabic_font";
    private static final String KEY_BANGLA_FONT = "bangla_font";
    private static final String KEY_ARABIC_SIZE = "arabic_size_sp";
    private static final String KEY_TRANSLATION_SIZE = "translation_size_sp";

    public static final int ARABIC_SIZE_MIN = 18;
    public static final int ARABIC_SIZE_MAX = 40;
    public static final int ARABIC_SIZE_DEFAULT = 24;
    public static final int TRANSLATION_SIZE_MIN = 12;
    public static final int TRANSLATION_SIZE_MAX = 28;
    public static final int TRANSLATION_SIZE_DEFAULT = 16;

    public static final FontOption[] ARABIC_FONTS = {
            new FontOption("naskh", "Noto Naskh", R.font.noto_naskh_arabic),
            new FontOption("scheherazade", "Scheherazade New", R.font.scheherazade_new_regular),
            new FontOption("amiri", "Amiri", R.font.amiri_regular),
            new FontOption("lateef", "Lateef", R.font.lateef_regular)
    };

    public static final FontOption[] BANGLA_FONTS = {
            new FontOption("hind", "Hind Siliguri", R.font.hind_siliguri_regular),
            new FontOption("noto_bn", "Noto Sans Bengali", R.font.noto_sans_bengali),
            new FontOption("tiro", "Tiro Bangla", R.font.tiro_bangla_regular),
            new FontOption("baloo", "Baloo Da 2", R.font.baloo_da2)
    };

    private ReadingSettings() {}

    public static FontOption arabicFont(@NonNull Context context) {
        return find(ARABIC_FONTS, prefs(context).getString(KEY_ARABIC_FONT, ARABIC_FONTS[0].id));
    }

    public static FontOption banglaFont(@NonNull Context context) {
        return find(BANGLA_FONTS, prefs(context).getString(KEY_BANGLA_FONT, BANGLA_FONTS[0].id));
    }

    public static int arabicSizeSp(@NonNull Context context) {
        return clamp(prefs(context).getInt(KEY_ARABIC_SIZE, ARABIC_SIZE_DEFAULT),
                ARABIC_SIZE_MIN, ARABIC_SIZE_MAX);
    }

    public static int translationSizeSp(@NonNull Context context) {
        return clamp(prefs(context).getInt(KEY_TRANSLATION_SIZE, TRANSLATION_SIZE_DEFAULT),
                TRANSLATION_SIZE_MIN, TRANSLATION_SIZE_MAX);
    }

    public static void setArabicFont(@NonNull Context context, @NonNull String id) {
        prefs(context).edit().putString(KEY_ARABIC_FONT, id).apply();
    }

    public static void setBanglaFont(@NonNull Context context, @NonNull String id) {
        prefs(context).edit().putString(KEY_BANGLA_FONT, id).apply();
    }

    public static void setArabicSize(@NonNull Context context, int sizeSp) {
        prefs(context).edit().putInt(KEY_ARABIC_SIZE, clamp(sizeSp, ARABIC_SIZE_MIN, ARABIC_SIZE_MAX)).apply();
    }

    public static void setTranslationSize(@NonNull Context context, int sizeSp) {
        prefs(context).edit()
                .putInt(KEY_TRANSLATION_SIZE, clamp(sizeSp, TRANSLATION_SIZE_MIN, TRANSLATION_SIZE_MAX))
                .apply();
    }

    public static void applyArabic(@NonNull TextView view, boolean useReadingSize) {
        Context context = view.getContext();
        applyFont(view, arabicFont(context).fontRes);
        if (useReadingSize) {
            view.setTextSize(arabicSizeSp(context));
        }
    }

    public static void applyBangla(@NonNull TextView view, boolean useReadingSize) {
        Context context = view.getContext();
        applyFont(view, banglaFont(context).fontRes);
        if (useReadingSize) {
            view.setTextSize(translationSizeSp(context));
        }
    }

    public static void applyFont(@NonNull TextView view, @FontRes int fontRes) {
        Typeface typeface = ResourcesCompat.getFont(view.getContext(), fontRes);
        if (typeface != null) {
            view.setTypeface(typeface);
        }
    }

    private static FontOption find(FontOption[] options, String id) {
        for (FontOption option : options) {
            if (option.id.equals(id)) return option;
        }
        return options[0];
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static final class FontOption {
        public final String id;
        public final String label;
        @FontRes
        public final int fontRes;

        FontOption(String id, String label, @FontRes int fontRes) {
            this.id = id;
            this.label = label;
            this.fontRes = fontRes;
        }
    }
}
