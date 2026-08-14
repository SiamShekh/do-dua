package com.syntax.dodua;

import android.app.Application;

import com.syntax.dodua.ui.ThemeSettings;

public class DoDuaApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ThemeSettings.applySaved(this);
    }
}
