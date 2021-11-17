package edu.upm.findme.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.upm.findme.App;
import edu.upm.findme.R;
import edu.upm.findme.adapters.GroupAdapter;
import edu.upm.findme.model.Group;
import edu.upm.findme.utility.ApiClient;

public class GroupsActivity extends AppCompatActivity implements ApiClient.FailureHandler, GroupAdapter.GroupClickListener {

    final ApiClient api = new ApiClient(this);
    RecyclerView groupList;
    GroupAdapter groupAdapter;
    App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        app = ((App) getApplicationContext()).init();

        groupList = findViewById(R.id.listGroups);
        groupAdapter = new GroupAdapter(this, app.userInfo.getUserId());
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
        api.joinGroup(app.userInfo.getUserId(), group.getId(), (answer) -> {
            if (answer.equals("OK")) {
                // save new group
                app.userInfo.setGroupInfo(group);

                // start menu
                Intent intent = new Intent(this, MenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onGroupRemove(Group group, int position) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.are_you_sure)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    api.RemoveGroup(group.getId(), (answer) -> {
                        if (answer.equals("OK"))
                            groupAdapter.removeAtPosition(position);
                    });
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void onBtnNewGroup(View view) {
        final EditText taskEditText = new EditText(this);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.enter_new_group_name)
                .setView(taskEditText)
                .setPositiveButton(R.string.ok, (dialog1, which) -> {
                    addNewGroup(String.valueOf(taskEditText.getText()));
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.show();
    }

    void addNewGroup(String name) {
        api.addNewGroup(name, app.userInfo.getUserId(), (groups) -> {
            groupAdapter.updateGroups(groups);
        });
    }
}
