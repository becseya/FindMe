package edu.upm.findme.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.upm.findme.App;
import edu.upm.findme.AppEvent;
import edu.upm.findme.R;
import edu.upm.findme.model.User;
import edu.upm.findme.utility.ApiClient;
import edu.upm.findme.utility.UserInfoManager;

public class MenuActivity extends AppCompatActivity implements ApiClient.FailureHandler {

    final ApiClient api = new ApiClient(this);
    final UserInfoManager userInfo = new UserInfoManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        api.listUsers((users) -> {
            String name = "";
            for (User u : users) {
                if (u.getId() == userInfo.getUserId())
                    name = u.getName();
            }
            Toast.makeText(this, "Hello, " + name + "!\nNumber of users: " + users.size(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onApiFailure(String errorDescription) {
        Toast.makeText(this, "API error: " + errorDescription, Toast.LENGTH_SHORT).show();
    }
}
