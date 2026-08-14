package com.syntax.dodua.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.syntax.dodua.R;

public final class ThemeSettings {

    private static final String PREFS = "dodua_prefs";
    private static final String KEY_MODE = "night_mode";

    private ThemeSettings() {}

    public static void applySaved(@NonNull Context context) {
        AppCompatDelegate.setDefaultNightMode(savedMode(context));
    }

    public static boolean isNight(@NonNull Context context) {
        int mask = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return mask == Configuration.UI_MODE_NIGHT_YES;
    }

    public static void toggle(@NonNull Context context) {
        setMode(context, isNight(context)
                ? AppCompatDelegate.MODE_NIGHT_NO
                : AppCompatDelegate.MODE_NIGHT_YES);
    }

    public static void setMode(@NonNull Context context, int mode) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_MODE, mode)
                .apply();
        AppCompatDelegate.setDefaultNightMode(mode);
    }

    public static int savedMode(@NonNull Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getInt(KEY_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public static String modeLabel(@NonNull Context context) {
        int mode = savedMode(context);
        if (mode == AppCompatDelegate.MODE_NIGHT_YES) {
            return context.getString(R.string.theme_dark);
        }
        if (mode == AppCompatDelegate.MODE_NIGHT_NO) {
            return context.getString(R.string.theme_light);
        }
        return context.getString(R.string.theme_system);
    }

    public static void bindToggle(@NonNull ImageButton button, boolean onTealHeader) {
        boolean night = isNight(button.getContext());
        button.setImageResource(night ? R.drawable.ic_theme_sun : R.drawable.ic_theme_moon);
        int tint = ContextCompat.getColor(
                button.getContext(),
                onTealHeader ? R.color.white : R.color.text_primary);
        button.setColorFilter(tint);
        button.setOnClickListener(v -> toggle(v.getContext()));
    }

}
