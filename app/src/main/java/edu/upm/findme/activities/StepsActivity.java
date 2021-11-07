package edu.upm.findme.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.upm.findme.App;
import edu.upm.findme.R;

public class StepsActivity extends AppCompatActivity {

    App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        app = ((App) getApplicationContext()).init();

        Toast.makeText(this, "There are " + app.mqtt.getSteps().size() + " users in the database", Toast.LENGTH_SHORT).show();
    }
}
