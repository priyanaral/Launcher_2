package com.android.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity  extends AppCompatActivity implements SliderAdapter.AppItemClickListener {

    private ViewPager2 viewPager2;
    private List<AppInfo> appList;
    private AutoScrollRunnable autoScrollRunnable;
    private Handler sliderHandler = new Handler();
    private static final int REQUEST_PERMISSION_QUERY_ALL_PACKAGES = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager2 = findViewById(R.id.viewPager2);
        appList = getInstalledApps();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                if (!hasQueryAllPackagesPermission()) {
                    requestQueryAllPackagesPermission();
                } else {
                    setupLauncher();
                }
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            setupLauncher();
        }
        //SliderAdapter adapter = new SliderAdapter(appList, viewPager2,this);
        SliderAdapter adapter = new SliderAdapter(appList, viewPager2, (SliderAdapter.AppItemClickListener) this);
        viewPager2.setAdapter(adapter);

        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1-Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });

        viewPager2.setPageTransformer(compositePageTransformer);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 2000); // slide duration 2 seconds
            }
        });


        // Auto-scroll the ViewPager2
        autoScrollRunnable = new AutoScrollRunnable(viewPager2, appList.size(), 3000);
        autoScrollRunnable.start();



    }

    private void setupLauncher() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        autoScrollRunnable.stop();
    }

    private List<AppInfo> getInstalledApps()
    {
        List<AppInfo> apps = new ArrayList<>();
        PackageManager packageManager = getPackageManager();
        List<ApplicationInfo> applicationInfos = packageManager.getInstalledApplications(0);

        for (ApplicationInfo appInfo : applicationInfos) {
            // Filter out system apps if needed
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                ///  Drawable appIcon = appInfo.loadIcon(packageManager);
                //  String appName = appInfo.loadLabel(packageManager).toString();
                //  apps.add(new AppInfo(appName, appIcon));
                Drawable appIcon = appInfo.loadIcon(packageManager);
                String appName = appInfo.loadLabel(packageManager).toString();
                String packageName = appInfo.packageName;
                apps.add(new AppInfo(appName,appIcon,packageName));
            }
        }

        return apps;
    }


    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };

    @Override
    public void onAppItemClick(String packageName) {
        // Launch the corresponding app using the package name
        launchApp(packageName);
    }
    private void launchApp(String packageName) {
        try {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
            if (launchIntent != null) {
                startActivity(launchIntent);
            } else {
                Toast.makeText(this, "App cannot be launched.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error launching the app.", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private boolean hasQueryAllPackagesPermission() throws PackageManager.NameNotFoundException {
        return PackageManager.PERMISSION_GRANTED ==
                getPackageManager().getApplicationInfo(getPackageName(), 0).targetSdkVersion;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void requestQueryAllPackagesPermission() throws PackageManager.NameNotFoundException {
        if (!hasQueryAllPackagesPermission()) {
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.QUERY_ALL_PACKAGES)) {
                // Show an explanation to the user, if needed.
            }
            requestPermissions(new String[]{Manifest.permission.QUERY_ALL_PACKAGES},
                    REQUEST_PERMISSION_QUERY_ALL_PACKAGES);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_QUERY_ALL_PACKAGES) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               // setupLauncher();
            } else {
                // Permission denied. Show a message or take appropriate action.
                Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
                // You can also direct the user to the app settings to grant the permission manually
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.fromParts("package", getPackageName(), null));
                startActivity(intent);
            }
        }
    }
}

