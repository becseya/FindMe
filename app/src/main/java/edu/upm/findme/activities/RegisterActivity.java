package edu.upm.findme.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
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
            permissionRequester.askAndGoToSettingIfDenied(PERM_STEP_SENSOR, () -> {
                jumpToNextActivity();
            });
        }
    }

    public void onBtnSubmit(View view) {
        permissionRequester.askAndGoToSettingIfDenied(PERM_STEP_SENSOR, () -> {
            User user = new User(0, txtName.getText().toString(), txtPhone.getText().toString());

            api.registerUser(user, (id) -> {
                app.userInfo.setUserId(id);
                jumpToNextActivity();
            });
        });
    }

    @Override
    public void onApiFailure(String errorDescription) {
        Toast.makeText(RegisterActivity.this, "API error: " + errorDescription, Toast.LENGTH_SHORT).show();
    }

    void jumpToNextActivity() {
        Intent intent = new Intent(this, GroupsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}