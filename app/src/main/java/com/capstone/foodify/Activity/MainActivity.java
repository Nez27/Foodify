package com.capstone.foodify.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.capstone.foodify.API.FoodApiToken;
import com.capstone.foodify.BuildConfig;
import com.capstone.foodify.Common;
import com.capstone.foodify.Fragment.HomeFragment;
import com.capstone.foodify.Model.Response.CustomResponse;
import com.capstone.foodify.Model.User;
import com.capstone.foodify.R;
import com.capstone.foodify.ViewPagerAdapter;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.objectweb.asm.Handle;

import java.util.Arrays;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    ViewPager2 viewPager2;
    ViewPagerAdapter viewPagerAdapter;
    BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ConstraintLayout progressLayout;

    //Location
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private static final long MAX_WAIT_TIME_IN_MILLISECONDS = 1000;
    private static final int LOCATION_REQUEST_CODE = 100;
    private static final String TAG = "MainActivity";

    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallBack;
    private Location mCurrentLocation;
    private boolean mRequestingLocationUpdates = false;

    @Override
    public void onStart() {
        super.onStart();

        //Get user from Paper
//        user = Paper.book().read("user");

        // Check if user is signed in (non-null) and update UI accordingly.
        user = mAuth.getCurrentUser();

        if (user != null && Common.CURRENT_USER == null) {
            progressLayout.setVisibility(View.VISIBLE);
            user.getIdToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        @Override
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                Common.TOKEN = task.getResult().getToken();

                                //Get user from database
                                FoodApiToken.apiService.getUserFromEmail(user.getEmail()).enqueue(new Callback<User>() {
                                    @Override
                                    public void onResponse(Call<User> call, Response<User> response) {
                                        User userData = response.body();
                                        if (userData != null) {
                                            Common.CURRENT_USER = userData;
                                            HomeFragment.setNameUser();

                                            getTokenFCM();

                                            progressLayout.setVisibility(View.GONE);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<User> call, Throwable t) {
                                    }
                                });
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

        startPowerSaverIntent(this);
        checkNotificationPermission();

        if(Common.CURRENT_USER != null)
            getTokenFCM();


        if (Common.CURRENT_LOCATION == null) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mSettingsClient = LocationServices.getSettingsClient(this);

            mLocationCallBack = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    mCurrentLocation = locationResult.getLastLocation();
                    Common.CURRENT_LOCATION = mCurrentLocation;
                }
            };

            mLocationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, UPDATE_INTERVAL_IN_MILLISECONDS)
                    .setWaitForAccurateLocation(false)
                    .setMinUpdateIntervalMillis(1000)
                    .setMaxUpdateDelayMillis(MAX_WAIT_TIME_IN_MILLISECONDS)
                    .build();

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            mLocationSettingsRequest = builder.build();

            checkLocationPermission();

        }

        //Init location user
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mCurrentLocation != null){
                    Common.CURRENT_LOCATION = mCurrentLocation;
                    stopLocationUpdates();
                }

            }
        }, 5000);

    }

    private void getTokenFCM(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if(!task.isSuccessful()){
                            Log.d(TAG, "Failed to registration token!");
                            
                            return;
                        }

                        String token = task.getResult();

                        FoodApiToken.apiService.updateFCMToken(Common.CURRENT_USER.getId(), token).enqueue(new Callback<CustomResponse>() {
                            @Override
                            public void onResponse(Call<CustomResponse> call, Response<CustomResponse> response) {
                                if(response.code() != 200)
                                    Toast.makeText(MainActivity.this, "Không thể cập nhật FCM Token. Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<CustomResponse> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Đã có lỗi khi kết nối đến hệ thống!", Toast.LENGTH_SHORT).show();
                            }
                        });

                        Log.d(TAG, "Token: " + token);
                    }
                });
    }
    private void initComponent() {
        bottomNavigationView = findViewById(R.id.bottom_nav);
        viewPager2 = findViewById(R.id.viewPager);
        progressLayout = findViewById(R.id.progress_layout);
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
                        viewPager2.setCurrentItem(0, false);
                        break;
                    case R.id.activity_search:
                        viewPager2.setCurrentItem(1, false);
                        break;
                    case R.id.activity_basket:
                        if(Common.CURRENT_USER != null){
                            viewPager2.setCurrentItem(2, false);
                        } else {
                            startActivity(new Intent(MainActivity.this, SignInActivity.class));
                        }
                        break;
                    case R.id.activity_profile:
                        if(Common.CURRENT_USER != null){
                            viewPager2.setCurrentItem(3, false);
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
                        bottomNavigationView.getMenu().findItem(R.id.activity_profile).setChecked(true);
                        break;
                }
                super.onPageSelected(position);
            }
        });
    }

    //myringtone permission
    private void checkNotificationPermission(){
        if(!NotificationManagerCompat.from(this).areNotificationsEnabled()){
            showDialogPermission("Hãy bật quyền thông báo trên thiết bị của bạn để chúng thôi có thể cung cấp thông tin cho bạn một cách nhanh nhất!");
        }
    }


    //Location
    private void checkLocationPermission(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestForPermission();

        }else {
            //Get location
            mRequestingLocationUpdates = true;
            startLocationUpdates();
        }
    }

    private void requestForPermission() {
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == LOCATION_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Đã cấp quyền thành công!", Toast.LENGTH_SHORT).show();
            } else {
                showDialogPermission("Hãy cho ứng dụng truy cập vào vị trí của bạn để trải nghiệm tốt hơn!");
            }
        }
    }

    private void showDialogPermission(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openSettings();
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Để sau", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Thông báo!");
        alertDialog.show();
    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void startLocationUpdates() {
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest).addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallBack, Looper.myLooper());

            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade location settings");

                        try {
                            ResolvableApiException rae = (ResolvableApiException) e;
                            rae.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sie) {
                            Log.i(TAG, "PendingIntent unable to execute request");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        String errorMessage = "Location settings are inadequate, and cannot be fixed here. Fix in Settings";
                        Log.e(TAG, errorMessage);

                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallBack).addOnCompleteListener(this, task -> Log.d(TAG, "Location updates stopped!"));
    }

    private boolean checkPermission() {
        int permissionState = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates && checkPermission() && Common.CURRENT_LOCATION == null) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mRequestingLocationUpdates) {
            stopLocationUpdates();

            if(Common.CURRENT_LOCATION == null){
                if(mCurrentLocation != null)
                    Common.CURRENT_LOCATION = mCurrentLocation;
            }
        }

        if(Common.CURRENT_USER != null)
            getTokenFCM();
    }

    //Check auto start permission
    public static List<Intent> POWER_MANAGER_INTENTS = Arrays.asList(
            new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.entry.FunctionActivity")).setData(android.net.Uri.parse("mobilemanager://function/entry/AutoStart"))
    );


    public void startPowerSaverIntent(Context context) {
        SharedPreferences settings = context.getSharedPreferences("ProtectedApps", Context.MODE_PRIVATE);
        boolean skipMessage = settings.getBoolean("skipProtectedAppCheck", false);
        if (!skipMessage) {
            final SharedPreferences.Editor editor = settings.edit();
            boolean foundCorrectIntent = false;
            for (Intent intent : POWER_MANAGER_INTENTS) {
                if (isCallable(context, intent)) {
                    foundCorrectIntent = true;
                    final AppCompatCheckBox dontShowAgain = new AppCompatCheckBox(context);
                    dontShowAgain.setText("Không hiện hộp thoại này nữa!");
                    dontShowAgain.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryColor, null)));
                    dontShowAgain.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        editor.putBoolean("skipProtectedAppCheck", isChecked);
                        editor.apply();
                    });

                    new AlertDialog.Builder(context)
                            .setTitle("Phát hiện máy " + Build.MANUFACTURER + "!")
                            .setMessage(String.format("Vì một số dòng máy Trung Quốc tự động tắt chế độ chạy nền của app %s, nên cần bạn " +
                                    "cho phép ứng dụng luôn tự khởi chạy ở cài đặt ứng dụng để có thể không bỏ lỡ bất kỳ thông báo nào!%n", context.getString(R.string.app_name)))
                            .setView(dontShowAgain)
                            .setPositiveButton("Đi đến cài đặt", (dialog, which) -> context.startActivity(intent))
                            .setNegativeButton("Đóng", null)
                            .show();
                    break;
                }
            }
            if (!foundCorrectIntent) {
                editor.putBoolean("skipProtectedAppCheck", true);
                editor.apply();
            }
        }
    }


    private static boolean isCallable(Context context, Intent intent) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

}