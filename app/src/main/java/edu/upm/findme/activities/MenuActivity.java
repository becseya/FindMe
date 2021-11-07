package edu.upm.findme.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.upm.findme.App;
import edu.upm.findme.AppEvent;
import edu.upm.findme.R;
import edu.upm.findme.utility.Locator;
import edu.upm.findme.utility.MenuManager;


public class MenuActivity extends AppCompatActivity implements App.MortalObserver {

    App app;
    MenuManager menuManager;
    TextView lblUnreadMessages;
    Locator locator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        app = ((App) getApplicationContext()).initWithObserver(this);
        lblUnreadMessages = findViewById(R.id.lblUnreadMessages);
        locator = new Locator(this, (location) -> {
            if (location != null)
                Toast.makeText(this, "LOC: " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_SHORT).show();
        });
        menuManager = new MenuManager(this, locator);

        // Services are protected again starting twice internally
        app.mqtt.start();
        app.stepSensor.start();
    }

    public void onBtnMessages(View view) {
        startActivity(new Intent(this, MessengerActivity.class));
    }

    public void onBtnSteps(View view) {
        startActivity(new Intent(this, StepsActivity.class));
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
}
