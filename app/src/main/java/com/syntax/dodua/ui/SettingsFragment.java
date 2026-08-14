package com.syntax.dodua.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.syntax.dodua.R;

public class SettingsFragment extends Fragment {

    private TextView previewArabic;
    private TextView previewBangla;
    private View rowTheme;
    private View rowArabicFont;
    private View rowBanglaFont;
    private View rowArabicSize;
    private View rowTranslationSize;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SystemBars.addStatusBarPadding(view);
        previewArabic = view.findViewById(R.id.preview_arabic);
        previewBangla = view.findViewById(R.id.preview_bangla);
        rowTheme = view.findViewById(R.id.row_theme);
        rowArabicFont = view.findViewById(R.id.row_arabic_font);
        rowBanglaFont = view.findViewById(R.id.row_bangla_font);
        rowArabicSize = view.findViewById(R.id.row_arabic_size);
        rowTranslationSize = view.findViewById(R.id.row_translation_size);

        bindRow(rowTheme, R.drawable.ic_theme, getString(R.string.setting_theme));
        bindRow(rowArabicFont, R.drawable.ic_font, getString(R.string.setting_arabic_font));
        bindRow(rowBanglaFont, R.drawable.ic_font, getString(R.string.setting_bangla_font));
        bindRow(rowArabicSize, R.drawable.ic_text_size, getString(R.string.setting_arabic_size));
        bindRow(rowTranslationSize, R.drawable.ic_text_size, getString(R.string.setting_translation_size));

        rowTheme.setOnClickListener(v -> showThemeSheet());
        rowArabicFont.setOnClickListener(v -> showFontSheet(true));
        rowBanglaFont.setOnClickListener(v -> showFontSheet(false));
        rowArabicSize.setOnClickListener(v -> showSizeSheet(true));
        rowTranslationSize.setOnClickListener(v -> showSizeSheet(false));
        refresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) refresh();
    }

    private void refresh() {
        ReadingSettings.applyArabic(previewArabic, true);
        ReadingSettings.applyBangla(previewBangla, true);
        setValue(rowTheme, ThemeSettings.modeLabel(requireContext()));
        setValue(rowArabicFont, ReadingSettings.arabicFont(requireContext()).label);
        setValue(rowBanglaFont, ReadingSettings.banglaFont(requireContext()).label);
        setValue(rowArabicSize, ReadingSettings.arabicSizeSp(requireContext()) + " sp");
        setValue(rowTranslationSize, ReadingSettings.translationSizeSp(requireContext()) + " sp");
    }

    private void bindRow(View row, @DrawableRes int icon, String label) {
        ((ImageView) row.findViewById(R.id.row_icon)).setImageResource(icon);
        ((TextView) row.findViewById(R.id.row_label)).setText(label);
    }

    private void setValue(View row, String value) {
        ((TextView) row.findViewById(R.id.row_value)).setText(value);
    }

    private BottomSheetDialog newSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext(), R.style.Theme_DoDua_BottomSheet);
        dialog.setOnShowListener(d -> {
            FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                bottomSheet.setBackgroundResource(android.R.color.transparent);
            }
        });
        return dialog;
    }

    private void showThemeSheet() {
        BottomSheetDialog dialog = newSheet();
        View sheet = LayoutInflater.from(requireContext()).inflate(R.layout.sheet_choice, null);
        ((TextView) sheet.findViewById(R.id.sheet_title)).setText(R.string.setting_theme);
        ((TextView) sheet.findViewById(R.id.sheet_subtitle)).setText(R.string.sheet_theme_hint);
        LinearLayout options = sheet.findViewById(R.id.sheet_options);
        addThemeOption(dialog, options, getString(R.string.theme_light), getString(R.string.theme_light_hint),
                R.drawable.ic_theme_sun, AppCompatDelegate.MODE_NIGHT_NO);
        addThemeOption(dialog, options, getString(R.string.theme_dark), getString(R.string.theme_dark_hint),
                R.drawable.ic_theme_moon, AppCompatDelegate.MODE_NIGHT_YES);
        addThemeOption(dialog, options, getString(R.string.theme_system), getString(R.string.theme_system_hint),
                R.drawable.ic_system, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        dialog.setContentView(sheet);
        dialog.show();
    }

    private void addThemeOption(BottomSheetDialog dialog, LinearLayout parent, String label, String hint,
                                @DrawableRes int icon, int mode) {
        boolean selected = ThemeSettings.savedMode(requireContext()) == mode;
        View option = inflateOption(parent, icon, label, hint, selected, false);
        option.setOnClickListener(v -> {
            ThemeSettings.setMode(requireContext(), mode);
            dialog.dismiss();
            refresh();
        });
        parent.addView(option);
    }

    private void showFontSheet(boolean arabic) {
        BottomSheetDialog dialog = newSheet();
        View sheet = LayoutInflater.from(requireContext()).inflate(R.layout.sheet_choice, null);
        ((TextView) sheet.findViewById(R.id.sheet_title)).setText(
                arabic ? R.string.setting_arabic_font : R.string.setting_bangla_font);
        ((TextView) sheet.findViewById(R.id.sheet_subtitle)).setText(
                arabic ? R.string.sheet_arabic_font_hint : R.string.sheet_bangla_font_hint);
        LinearLayout options = sheet.findViewById(R.id.sheet_options);
        ReadingSettings.FontOption[] fonts = arabic
                ? ReadingSettings.ARABIC_FONTS
                : ReadingSettings.BANGLA_FONTS;
        String current = arabic
                ? ReadingSettings.arabicFont(requireContext()).id
                : ReadingSettings.banglaFont(requireContext()).id;
        String sample = arabic
                ? getString(R.string.preview_arabic)
                : getString(R.string.preview_bangla);
        for (ReadingSettings.FontOption font : fonts) {
            boolean selected = font.id.equals(current);
            View option = inflateOption(options, R.drawable.ic_font, font.label, sample, selected, true);
            TextView sampleView = option.findViewById(R.id.option_sample);
            ReadingSettings.applyFont(option.findViewById(R.id.option_label), font.fontRes);
            ReadingSettings.applyFont(sampleView, font.fontRes);
            option.setOnClickListener(v -> {
                if (arabic) {
                    ReadingSettings.setArabicFont(requireContext(), font.id);
                } else {
                    ReadingSettings.setBanglaFont(requireContext(), font.id);
                }
                dialog.dismiss();
                refresh();
            });
            options.addView(option);
        }
        dialog.setContentView(sheet);
        dialog.show();
    }

    private View inflateOption(ViewGroup parent, @DrawableRes int icon, String label, String sample,
                               boolean selected, boolean showSample) {
        View option = LayoutInflater.from(requireContext()).inflate(R.layout.item_sheet_option, parent, false);
        ImageView iconView = option.findViewById(R.id.option_icon);
        iconView.setImageResource(icon);
        iconView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.teal_primary));
        ((TextView) option.findViewById(R.id.option_label)).setText(label);
        TextView sampleView = option.findViewById(R.id.option_sample);
        if (showSample) {
            sampleView.setVisibility(View.VISIBLE);
            sampleView.setText(sample);
        } else if (sample != null && !sample.isEmpty()) {
            sampleView.setVisibility(View.VISIBLE);
            sampleView.setText(sample);
        }
        option.setBackgroundResource(selected
                ? R.drawable.bg_sheet_option_selected
                : R.drawable.bg_sheet_option);
        option.findViewById(R.id.option_check).setVisibility(selected ? View.VISIBLE : View.GONE);
        return option;
    }

    private void showSizeSheet(boolean arabic) {
        BottomSheetDialog dialog = newSheet();
        View sheet = LayoutInflater.from(requireContext()).inflate(R.layout.sheet_size, null);
        TextView title = sheet.findViewById(R.id.sheet_title);
        TextView preview = sheet.findViewById(R.id.sheet_preview);
        TextView value = sheet.findViewById(R.id.sheet_size_value);
        SeekBar seek = sheet.findViewById(R.id.sheet_seek);
        int min = arabic ? ReadingSettings.ARABIC_SIZE_MIN : ReadingSettings.TRANSLATION_SIZE_MIN;
        int max = arabic ? ReadingSettings.ARABIC_SIZE_MAX : ReadingSettings.TRANSLATION_SIZE_MAX;
        int current = arabic
                ? ReadingSettings.arabicSizeSp(requireContext())
                : ReadingSettings.translationSizeSp(requireContext());
        title.setText(arabic ? R.string.setting_arabic_size : R.string.setting_translation_size);
        preview.setText(arabic ? R.string.preview_arabic : R.string.preview_bangla);
        if (arabic) {
            ReadingSettings.applyArabic(preview, false);
        } else {
            ReadingSettings.applyBangla(preview, false);
        }
        seek.setMax(max - min);
        seek.setProgress(current - min);
        Runnable applyPreview = () -> {
            int size = min + seek.getProgress();
            preview.setTextSize(size);
            value.setText(size + " sp");
        };
        applyPreview.run();
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                applyPreview.run();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        sheet.findViewById(R.id.btn_save).setOnClickListener(v -> {
            int size = min + seek.getProgress();
            if (arabic) {
                ReadingSettings.setArabicSize(requireContext(), size);
            } else {
                ReadingSettings.setTranslationSize(requireContext(), size);
            }
            dialog.dismiss();
            refresh();
        });
        dialog.setContentView(sheet);
        dialog.show();
    }
}
