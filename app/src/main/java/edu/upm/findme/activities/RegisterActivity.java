package edu.upm.findme.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.upm.findme.R;
import edu.upm.findme.model.User;
import edu.upm.findme.utility.ApiClient;
import edu.upm.findme.utility.UserInfoManager;

public class RegisterActivity extends AppCompatActivity implements ApiClient.FailureHandler {

    final ApiClient api = new ApiClient(this);
    final UserInfoManager userInfo = new UserInfoManager(this);

    TextView txtName;
    TextView txtPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
    }

    public void onBtnSubmit(View view) {
        if (userInfo.isUserIdSet()) {
            Toast.makeText(RegisterActivity.this, "ALREADY REGISTERED! " + userInfo.getUserId(), Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User(0, txtName.getText().toString(), txtPhone.getText().toString());

        api.registerUser(user, (id) -> {
            userInfo.setUserId(id);
            Toast.makeText(RegisterActivity.this, "User added with id: " + id, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onApiFailure(String errorDescription) {
        Toast.makeText(RegisterActivity.this, "API error: " + errorDescription, Toast.LENGTH_SHORT).show();
    }
}