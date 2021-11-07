package edu.upm.findme.activities;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.upm.findme.App;
import edu.upm.findme.R;
import edu.upm.findme.model.User;
import edu.upm.findme.utility.ApiClient;
import edu.upm.findme.utility.PermissionRequester;

public class RegisterActivity extends AppCompatActivity implements ApiClient.FailureHandler {

    static final String PERM_STEP_SENSOR = Manifest.permission.ACTIVITY_RECOGNITION;

    final ApiClient api = new ApiClient(this);
    PermissionRequester permissionRequester = new PermissionRequester(this);
    App app;

    TextView txtName;
    TextView txtPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        app = ((App) getApplicationContext()).init();

        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (app.userInfo.isUserIdSet()) {
            permissionRequester.askIfNotHeld(PERM_STEP_SENSOR, (isGranted) -> {
                if (isGranted)
                    jumpToMenu();
                else
                    goToPermissionSettings();
            });
        }
    }

    public void onBtnSubmit(View view) {
        permissionRequester.askIfNotHeld(PERM_STEP_SENSOR, (isGranted) -> {
            if (isGranted) {
                User user = new User(0, txtName.getText().toString(), txtPhone.getText().toString());

                api.registerUser(user, (id) -> {
                    app.userInfo.setUserId(id);
                    jumpToMenu();
                });
            } else
                goToPermissionSettings();
        });
    }

    void goToPermissionSettings() {
        Toast.makeText(RegisterActivity.this, "Permission required to proceed", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    public void onApiFailure(String errorDescription) {
        Toast.makeText(RegisterActivity.this, "API error: " + errorDescription, Toast.LENGTH_SHORT).show();
    }

    void jumpToMenu() {
        Intent intent = new Intent(this, MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}