package edu.upm.findme.utility;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

public class PermissionRequester {

    ActivityResultLauncher<String> requestPermissionLauncher;
    PermissionResultHandler handler;
    FragmentActivity activity;

    public PermissionRequester(FragmentActivity activity) {
        this.activity = activity;

        requestPermissionLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted -> {
                    handler.onPermissionResult(isGranted);
                });
    }

    public void ask(String permission, PermissionResultHandler handler) {
        int permissionStatus = ContextCompat.checkSelfPermission(activity, permission);

        if (permissionStatus == PackageManager.PERMISSION_GRANTED)
            handler.onPermissionResult(true);
        else {
            this.handler = handler;
            requestPermissionLauncher.launch(permission);
        }
    }

    public void askAndDisplayToastIfDenied(String permission, PermissionGrantedHandler handler) {
        ask(permission, (isGranted) -> {
            if (isGranted)
                handler.onPermissionGranted();
            else
                Toast.makeText(activity, "Permission denied", Toast.LENGTH_SHORT).show();
        });
    }

    public void askAndGoToSettingIfDenied(String permission, PermissionGrantedHandler handler) {
        ask(permission, (isGranted) -> {
            if (isGranted)
                handler.onPermissionGranted();
            else {
                Toast.makeText(activity, "Permission required to proceed", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivity(intent);
            }
        });
    }

    public interface PermissionGrantedHandler {
        void onPermissionGranted();
    }

    public interface PermissionResultHandler {
        void onPermissionResult(boolean granted);
    }
}
