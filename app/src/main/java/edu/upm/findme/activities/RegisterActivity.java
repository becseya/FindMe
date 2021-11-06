package edu.upm.findme.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.upm.findme.R;
import edu.upm.findme.App;
import edu.upm.findme.model.User;
import edu.upm.findme.utility.ApiClient;

public class RegisterActivity extends AppCompatActivity implements ApiClient.FailureHandler {

    final ApiClient api = new ApiClient(this);
    App app;

    TextView txtName;
    TextView txtPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        app = ((App) getApplicationContext()).init();

        if (app.userInfo.isUserIdSet())
            jumpToMenu();

        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
    }

    public void onBtnSubmit(View view) {
        User user = new User(0, txtName.getText().toString(), txtPhone.getText().toString());

        api.registerUser(user, (id) -> {
            app.userInfo.setUserId(id);
            jumpToMenu();
        });
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