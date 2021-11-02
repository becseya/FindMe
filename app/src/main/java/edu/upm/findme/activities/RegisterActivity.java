package edu.upm.findme.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.upm.findme.R;
import edu.upm.findme.model.User;
import edu.upm.findme.utility.ApiClient;

public class RegisterActivity extends AppCompatActivity implements ApiClient.FailureHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onTestBtn(View v) {
        ApiClient client = new ApiClient(this);
        User user = new User("Jon Doe", "+34-00-00-00-00");

        client.addUser(user, (id) -> {
            Toast.makeText(RegisterActivity.this, "Id: " + id, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onApiFailure(String errorDescription) {
        Toast.makeText(RegisterActivity.this, "API error: " + errorDescription, Toast.LENGTH_SHORT).show();
    }
}