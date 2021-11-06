package edu.upm.findme.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import edu.upm.findme.AppEvent;
import edu.upm.findme.R;
import edu.upm.findme.model.Message;
import edu.upm.findme.utility.ApiClient;
import edu.upm.findme.utility.MqttTalker;
import edu.upm.findme.utility.UserInfoManager;

public class MenuActivity extends AppCompatActivity {

    final UserInfoManager userInfo = new UserInfoManager(this);
    MqttTalker mqtt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mqtt = new MqttTalker(getApplicationContext(), new AppEvent.Observer() {
            @Override
            public void onGlobalEvent(AppEvent.Type e) {
                if (e == AppEvent.Type.MEW_MESSAGE) {
                    List<Message> messages = mqtt.getMessages();
                    Message lastMessage = messages.get(messages.size() - 1);
                    toast("MSG: " + lastMessage.getContent() + " @ " + lastMessage.getSenderId());
                }
            }
        }, userInfo.getUserId());

        mqtt.start();
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
