package edu.upm.findme.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.upm.findme.R;
import edu.upm.findme.adapters.GroupAdapter;
import edu.upm.findme.model.Group;
import edu.upm.findme.utility.ApiClient;

public class GroupsActivity extends AppCompatActivity implements ApiClient.FailureHandler, GroupAdapter.GroupClickListener {

    final ApiClient api = new ApiClient(this);
    RecyclerView groupList;
    GroupAdapter groupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        groupList = findViewById(R.id.listGroups);
        groupAdapter = new GroupAdapter(this);
        groupList.setAdapter(groupAdapter);
        groupList.setLayoutManager(new LinearLayoutManager(this));

        api.listGroups((groups) -> {
            groupAdapter.updateGroups(groups);
        });
    }

    @Override
    public void onApiFailure(String errorDescription) {
        Toast.makeText(this, "API error: " + errorDescription, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGroupClick(Group group) {
        Toast.makeText(this, "Clicked: " + group.getName(), Toast.LENGTH_SHORT).show();
    }
}
