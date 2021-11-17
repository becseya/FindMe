package edu.upm.findme.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.upm.findme.App;
import edu.upm.findme.AppEvent;
import edu.upm.findme.R;
import edu.upm.findme.adapters.UserAdapter;
import edu.upm.findme.model.UserDetails;
import edu.upm.findme.utility.ApiClient;
import edu.upm.findme.utility.MenuManager;


public class MenuActivity extends AppCompatActivity implements App.MortalObserver, ApiClient.FailureHandler, UserAdapter.UserClickListener {

    final ApiClient api = new ApiClient(this);
    App app;
    MenuManager menuManager;
    TextView lblUnreadMessages;
    RecyclerView userList;
    UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        app = ((App) getApplicationContext()).initWithObserver(this);
        lblUnreadMessages = findViewById(R.id.lblUnreadMessages);
        ((TextView) findViewById(R.id.lblGroupName)).setText(app.userInfo.getGroupName());
        menuManager = new MenuManager(this, app);

        userList = findViewById(R.id.listUsers);
        userAdapter = new UserAdapter(this);
        userList.setAdapter(userAdapter);
        userList.setLayoutManager(new LinearLayoutManager(this));

        api.listUsers(app.userInfo.getGroupId(), (fetchedUsers) -> {
            app.users = fetchedUsers;
            userAdapter.updateUsers(fetchedUsers, app.mqtt.getStatuses());
            // Services are protected against starting twice internally
            app.mqtt.start(app.userInfo.getGroupId());
            app.stepSensor.start();
        });
    }

    public void onBtnMessages(View view) {
        startActivity(new Intent(this, MessengerActivity.class));
    }

    public void onBtnSteps(View view) {
        startActivity(new Intent(this, StepsActivity.class));
    }

    public void onBtnMaps(View view) {
        startActivity(new Intent(this, MapsActivity.class));
    }

    public void onBtnLeaveGroup(View view) {
        app.userInfo.leaveGroup();
        app.mqtt.stop();

        // start Groups activity
        Intent intent = new Intent(this, GroupsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onGlobalEvent(AppEvent.Type e) {
        menuManager.onGlobalEvent(e);
        if (e == AppEvent.Type.MEW_MESSAGE)
            updateUnreadMessages();
        if (e == AppEvent.Type.STATUS_DATABASE_CHANGED)
            userAdapter.updateUserStatuses(app.mqtt.getStatuses());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUnreadMessages();
        userAdapter.updateUserStatuses(app.mqtt.getStatuses());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuManager.onCreateOptionsMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void updateUnreadMessages() {
        if (app.mqtt.getNumberOfUnreadMessages() > 0) {
            lblUnreadMessages.setVisibility(View.VISIBLE);
            lblUnreadMessages.setText(String.valueOf(app.mqtt.getNumberOfUnreadMessages()));
        } else
            lblUnreadMessages.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onApiFailure(String errorDescription) {
        Toast.makeText(this, "API error: " + errorDescription, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUserClick(UserDetails user) {
        Toast.makeText(this, user.getName() + " clicked", Toast.LENGTH_SHORT).show();
    }
}
