package com.arhamnasir.i191962;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class bottomnav extends AppCompatActivity {

    BottomNavigationView navbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottomnav);

        navbar = findViewById(R.id.navbar); // Initialize the BottomNavigationView

        // Manually select the "Home" item to show the "HomeFragment" initially
        navbar.setSelectedItemId(R.id.home);

        navbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selected = null;
                int itemid = item.getItemId();

                if (itemid == R.id.home) {
                    selected = new HomeFragment();
                } else if (itemid == R.id.search) {
                    selected = new SearchFragment();
                } else if (itemid == R.id.add) {
                    selected = new AddFragment();
                } else if (itemid == R.id.chat) {
                    selected = new ChatFragment();
                } else if (itemid == R.id.profile) {
                    selected = new ProfileFragment();
                }

                if (selected != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, selected).commit();
                }
                return true;
            }
        });
    }
}
