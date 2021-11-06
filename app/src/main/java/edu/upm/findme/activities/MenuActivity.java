package edu.upm.findme.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.upm.findme.R;
import edu.upm.findme.utility.ApiClient;
import edu.upm.findme.utility.PersistentSessionMqttClient;
import edu.upm.findme.utility.UserInfoManager;

public class MenuActivity extends AppCompatActivity implements PersistentSessionMqttClient.EventHandler {

    final UserInfoManager userInfo = new UserInfoManager(this);
    PersistentSessionMqttClient mqtt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mqtt = new PersistentSessionMqttClient(getApplicationContext(), this, userInfo.getUserId());
        mqtt.connect();
    }

    @Override
    public void onMessage(String topic, String payload) {
        toast("MSG: " + payload + " @ " + topic);
    }

    @Override
    public void onError(String errorDescription) {
        toast("MQTT error: " + errorDescription);
    }

    @Override
    public void onConnectionStateChange(boolean isConnected, String optionalInfo) {
        if (isConnected)
            mqtt.subscribe("messages/#", 2);
        else
            toast("MQTT connection error " + (optionalInfo != null ? optionalInfo : ""));
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
