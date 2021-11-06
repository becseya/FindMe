package edu.upm.findme.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import edu.upm.findme.App;
import edu.upm.findme.AppEvent;
import edu.upm.findme.R;
import edu.upm.findme.model.Message;
import edu.upm.findme.utility.ApiClient;

public class MenuActivity extends AppCompatActivity implements App.MortalObserver {

    App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        app = ((App) getApplicationContext()).initWithObserver(this);

        app.mqtt.start();
    }

    @Override
    public void onGlobalEvent(AppEvent.Type e) {
        if (e == AppEvent.Type.MEW_MESSAGE) {
            List<Message> messages = app.mqtt.getMessages();
            Message lastMessage = messages.get(messages.size() - 1);
            toast("MSG: " + lastMessage.getContent() + " @ " + lastMessage.getSenderId());
        }
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
