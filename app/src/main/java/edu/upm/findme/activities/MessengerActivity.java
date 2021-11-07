package edu.upm.findme.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import edu.upm.findme.App;
import edu.upm.findme.AppEvent;
import edu.upm.findme.R;
import edu.upm.findme.model.Message;

public class MessengerActivity extends AppCompatActivity implements App.MortalObserver {

    TextView txtMessage;
    Button btnSend;
    App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        app = ((App) getApplicationContext()).initWithObserver(this);
        txtMessage = findViewById(R.id.txtMessage);
        btnSend = findViewById(R.id.btnSend);

        txtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                btnSend.setEnabled(txtMessage.getText().toString().length() != 0);
            }
        });

        btnSend.setOnClickListener(view -> {
            Message msg = new Message(app.userInfo.getUserId(), txtMessage.getText().toString());
            app.mqtt.sendMessage(msg);
            txtMessage.setText("");
            view.setEnabled(false);
        });

        drawMessages();
    }

    @Override
    public void onGlobalEvent(AppEvent.Type e) {
        if (e == AppEvent.Type.MEW_MESSAGE)
            drawMessages();
    }

    void drawMessages() {
        List<Message> messages = app.mqtt.getMessages();
        Toast.makeText(this, "Got: " + messages.size(), Toast.LENGTH_SHORT).show();
    }
}