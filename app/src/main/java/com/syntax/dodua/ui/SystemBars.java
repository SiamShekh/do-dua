package com.syntax.dodua.ui;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.syntax.dodua.R;

public final class SystemBars {

    private SystemBars() {}

    /**
     * Transparent status bar that inherits the screen color.
     * {@code lightBackground} uses dark clock/network icons; otherwise white icons.
     */
    public static void apply(@NonNull Activity activity, boolean lightBackground) {
        Window window = activity.getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(ContextCompat.getColor(activity, R.color.surface));
        boolean night = ThemeSettings.isNight(activity);
        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(window, window.getDecorView());
        controller.setAppearanceLightStatusBars(lightBackground && !night);
        controller.setAppearanceLightNavigationBars(!night);
    }

    public static void addStatusBarPadding(@NonNull View view) {
        final int startTop = view.getPaddingTop();
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(v.getPaddingLeft(), startTop + bars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });
        ViewCompat.requestApplyInsets(view);
    }
}
