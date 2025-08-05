package com.example.forecastfable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Window;
import android.widget.Toast;

public class SplashScreen extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        setContentView(R.layout.activity_splash_screen);

        // Check if the required permissions are granted
        if (checkLocationPermissions()) {
            navigateToMainActivity();
        } else {
            requestLocationPermissions();
        }
    }

    private boolean checkLocationPermissions() {
        int coarseLocationPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int fineLocationPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);

        return coarseLocationPermission == PackageManager.PERMISSION_GRANTED &&
                fineLocationPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                },
                LOCATION_PERMISSION_REQUEST_CODE
        );
    }

    private void navigateToMainActivity() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashScreen.this, MainActivity.class));
            finish();
        }, 4000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            boolean coarseGranted = false;
            boolean fineGranted = false;

            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    coarseGranted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                } else if (permissions[i].equals(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    fineGranted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                }
            }

            if (coarseGranted && fineGranted) {
                navigateToMainActivity();
            } else {
                Toast.makeText(this, "Location permissions are required to use the app.", Toast.LENGTH_SHORT).show();

                // Check if we should show rationale or direct to settings
                boolean showRationaleCoarse = ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
                boolean showRationaleFine = ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION);

                if (!showRationaleCoarse && !showRationaleFine) {
                    // Permissions denied permanently
                    new Handler().postDelayed(() -> {
                        new AlertDialog.Builder(this)
                                .setTitle("Permissions Required")
                                .setMessage("Location permissions are required to proceed. Please enable them in App Settings.")
                                .setPositiveButton("Go to Settings", (dialog, which) -> {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    }, 4000);
                } else {
                    // Ask again after 4 seconds
                    new Handler().postDelayed(this::requestLocationPermissions, 4000);
                }
            }
        }
    }
}