package edu.upm.findme.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.upm.findme.App;
import edu.upm.findme.AppEvent;
import edu.upm.findme.R;
import edu.upm.findme.model.User;
import edu.upm.findme.utility.ApiClient;

public class MenuActivity extends AppCompatActivity implements App.MortalObserver, ApiClient.FailureHandler {

    final ApiClient api = new ApiClient(this);
    App app;
    TextView lblUnreadMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        app = ((App) getApplicationContext()).initWithObserver(this);
        lblUnreadMessages = findViewById(R.id.lblUnreadMessages);

        api.listUsers((users) -> {
            String name = "";
            for (User u : users) {
                if (u.getId() == app.userInfo.getUserId())
                    name = u.getName();
            }
            toast("Hello, " + name + "!\nNumber of users: " + users.size());
            app.mqtt.start();
        });
    }

    public void onBtnMessages(View view) {
        startActivity(new Intent(this, MessengerActivity.class));
    }

    @Override
    public void onGlobalEvent(AppEvent.Type e) {
        if (e == AppEvent.Type.MEW_MESSAGE)
            updateUnreadMessages();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUnreadMessages();
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void updateUnreadMessages() {
        if (app.mqtt.getNumberOfUnreadMessages() > 0) {
            lblUnreadMessages.setVisibility(View.VISIBLE);
            lblUnreadMessages.setText("" + app.mqtt.getNumberOfUnreadMessages());
        } else
            lblUnreadMessages.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onApiFailure(String errorDescription) {
        toast("API error: " + errorDescription);
    }
}
