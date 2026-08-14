package com.syntax.dodua;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.syntax.dodua.ui.DhikrFragment;
import com.syntax.dodua.ui.DuaFragment;
import com.syntax.dodua.ui.HomeFragment;
import com.syntax.dodua.ui.SystemBars;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemBars.apply(this, false);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_nav);
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), bars.bottom);
            return insets;
        });

        if (savedInstanceState == null) {
            show(new HomeFragment());
        }
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                show(new HomeFragment());
                return true;
            } else if (id == R.id.nav_dhikr) {
                show(new DhikrFragment());
                return true;
            } else if (id == R.id.nav_dua) {
                show(new DuaFragment());
                return true;
            }
            return false;
        });
    }

    public void openTab(int menuId) {
        bottomNav.setSelectedItemId(menuId);
    }

    private void show(@NonNull Fragment fragment) {
        boolean lightBackground = !(fragment instanceof HomeFragment);
        SystemBars.apply(this, lightBackground);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
