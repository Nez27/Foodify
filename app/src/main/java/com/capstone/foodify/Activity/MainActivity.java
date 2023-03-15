package com.capstone.foodify.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.splashscreen.SplashScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.capstone.foodify.Common;
import com.capstone.foodify.R;
import com.capstone.foodify.ViewPagerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    ViewPager2 viewPager2;
    ViewPagerAdapter viewPagerAdapter;
    BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    @Override
    public void onStart() {
        super.onStart();

        //Get user from Paper
        user = Paper.book().read("user");

        // Check if user is signed in (non-null) and update UI accordingly.
        user = mAuth.getCurrentUser();
        if(user != null){
            user.getIdToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        @Override
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if(task.isSuccessful()){
                                Common.TOKEN = task.getResult().getToken();
                            } else {
                                Toast.makeText(MainActivity.this, "Error when taking token!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        //Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Paper Init
        Paper.init(this);

        initComponent();

        bottomNavigation();
    }
    private void initComponent() {
        bottomNavigationView = findViewById(R.id.bottom_nav);
        viewPager2 = findViewById(R.id.viewPager);
    }
    private void bottomNavigation() {
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);

        viewPager2.setUserInputEnabled(false);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.activity_home:
                        viewPager2.setCurrentItem(0);
                        break;
                    case R.id.activity_search:
                        viewPager2.setCurrentItem(1);
                        break;
                    case R.id.activity_basket:
                        viewPager2.setCurrentItem(2);
                        break;
                    case R.id.activity_favorite:
                        if(Common.TOKEN != null){
                            viewPager2.setCurrentItem(3);
                        } else {
                            startActivity(new Intent(MainActivity.this, SignInActivity.class));
                        }
                        break;
                    case R.id.activity_profile:
                        if(Common.TOKEN != null){
                            viewPager2.setCurrentItem(4);
                        } else {
                            startActivity(new Intent(MainActivity.this, SignInActivity.class));
                        }
                        break;
                }
                return false;
            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.activity_home).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.activity_search).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.activity_basket).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.activity_favorite).setChecked(true);
                        break;
                    case 4:
                        bottomNavigationView.getMenu().findItem(R.id.activity_profile).setChecked(true);
                        break;
                }
                super.onPageSelected(position);
            }
        });
    }
}