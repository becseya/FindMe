package edu.upm.findme.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import edu.upm.findme.App;
import edu.upm.findme.AppEvent;
import edu.upm.findme.R;
import edu.upm.findme.model.User;
import edu.upm.findme.utility.ApiClient;
import edu.upm.findme.utility.MenuManager;


public class MenuActivity extends AppCompatActivity implements App.MortalObserver, ApiClient.FailureHandler {

    final ApiClient api = new ApiClient(this);
    App app;
    MenuManager menuManager;
    TextView lblUnreadMessages;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        app = ((App) getApplicationContext()).initWithObserver(this);
        lblUnreadMessages = findViewById(R.id.lblUnreadMessages);
        menuManager = new MenuManager(this, app);
        listView = findViewById(R.id.listViewUser);

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("user1");
        arrayList.add("user2");
        arrayList.add("user3");
        arrayList.add("user4");
        arrayList.add("user5");
        arrayList.add("user6");
        arrayList.add("user7");
        arrayList.add("user8");
        arrayList.add("user9");
        arrayList.add("user10");


        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);

        api.listUsers((users) -> {
            String name = "";
            for (User u : users) {
                if (u.getId() == app.userInfo.getUserId())
                    name = u.getName();
            }
            Toast.makeText(this, "Hello, " + name + "!\nNumber of users: " + users.size(), Toast.LENGTH_SHORT).show();

            // Services are protected against starting twice internally
            app.mqtt.start();
            app.stepSensor.start();
        });
    }

    public void onBtnMessages(View view) {
        startActivity(new Intent(this, MessengerActivity.class));
    }

    public void onBtnSteps(View view) {
        startActivity(new Intent(this, StepsActivity.class));
    }

    @Override
    public void onGlobalEvent(AppEvent.Type e) {
        menuManager.onGlobalEvent(e);
        if (e == AppEvent.Type.MEW_MESSAGE)
            updateUnreadMessages();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUnreadMessages();
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
}
