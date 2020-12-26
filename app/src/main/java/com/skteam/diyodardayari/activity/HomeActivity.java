package com.skteam.diyodardayari.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.skteam.diyodardayari.R;
import com.skteam.diyodardayari.api.RetrofitApi;
import com.skteam.diyodardayari.api.RetrofitClient;
import com.skteam.diyodardayari.databinding.ActivityHomeBinding;
import com.skteam.diyodardayari.databinding.ActivityLoginBinding;
import com.skteam.diyodardayari.fragments.HomeFragment;
import com.skteam.diyodardayari.fragments.MyServiceFragment;
import com.skteam.diyodardayari.fragments.ProfileFragment;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivityTest";
    private Activity activity;
    private ActivityHomeBinding binding;
    private FirebaseAuth mAuth;
    private RetrofitApi mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        activity = this;
        mService = RetrofitClient.getAPI();
        FirebaseApp.initializeApp(activity);
        mAuth = FirebaseAuth.getInstance();
        setFragment(new HomeFragment(), "home_fragment");
        initViewsClicks();
    }

    private void initViewsClicks() {
        binding.bottomNav.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
    }

    public void setFragment(Fragment fragment, String name) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft = ft.replace(R.id.fragment_container, fragment, name);
        ft.commit();
    }




    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            item -> {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        setFragment(new HomeFragment(), "home_fragment");
                        break;
                    case R.id.nav_my_services:
                        setFragment(new MyServiceFragment(), "my_service_fragment");
                        break;
                    case R.id.nav_profile:
                        setFragment(new ProfileFragment(), "profile_fragment");
                        break;

                }
                return true;
            };

}