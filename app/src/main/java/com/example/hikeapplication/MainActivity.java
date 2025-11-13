package com.example.hikeapplication;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.hikeapplication.Fragment.AddHikeFragment;
import com.example.hikeapplication.Fragment.HomeFragment;
import com.example.hikeapplication.Fragment.SearchHikeFragment;
import com.example.hikeapplication.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Mặc định hiển thị Home khi khởi động
        replaceFragment(new HomeFragment(), false);
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.add) {
                replaceFragment(new AddHikeFragment(), true);
            } else if (itemId == R.id.home) {
                replaceFragment(new HomeFragment(), false);
            } else if (itemId == R.id.search) {
                replaceFragment(new SearchHikeFragment(), true);
            }
            return true;
        });
    }

    // Hàm thay thế fragment kèm hiển thị/ẩn nút back
    private void replaceFragment(Fragment fragment, boolean showBackButton) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.frame_layout, fragment);
        if (showBackButton) {
            transaction.addToBackStack(null); // Cho phép quay lại
        }
        transaction.commit();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(showBackButton);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
