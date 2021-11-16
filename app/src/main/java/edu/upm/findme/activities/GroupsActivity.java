package edu.upm.findme.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.upm.findme.R;
import edu.upm.findme.utility.ApiClient;

public class GroupsActivity extends AppCompatActivity implements ApiClient.FailureHandler {

    final ApiClient api = new ApiClient(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        api.listGroups((groups) -> {
            Toast.makeText(this, "Number of groups: " + groups.size(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onApiFailure(String errorDescription) {
        Toast.makeText(this, "API error: " + errorDescription, Toast.LENGTH_SHORT).show();
    }
}
