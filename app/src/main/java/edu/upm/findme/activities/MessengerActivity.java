package edu.upm.findme.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.upm.findme.App;
import edu.upm.findme.AppEvent;
import edu.upm.findme.R;
import edu.upm.findme.adapters.MessageAdapter;
import edu.upm.findme.model.Message;
import edu.upm.findme.utility.MenuManager;

public class MessengerActivity extends AppCompatActivity implements App.MortalObserver {

    MenuManager menuManager;
    TextView txtMessage;
    Button btnSend;
    App app;
    RecyclerView messageList;
    MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        app = ((App) getApplicationContext()).initWithObserver(this);
        txtMessage = findViewById(R.id.txtMessage);
        btnSend = findViewById(R.id.btnSend);
        menuManager = new MenuManager(this, app);

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

        messageList = findViewById(R.id.messageList);
        messageAdapter = new MessageAdapter();
        messageList.setAdapter(messageAdapter);
        messageList.setLayoutManager(new LinearLayoutManager(this));

        drawMessages();
    }

    @Override
    public void onGlobalEvent(AppEvent.Type e) {
        menuManager.onGlobalEvent(e);
        if (e == AppEvent.Type.MEW_MESSAGE)
            drawMessages();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuManager.onCreateOptionsMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }

    void drawMessages() {
        messageAdapter.updateMessages(app.mqtt.getMessages());
    }
}