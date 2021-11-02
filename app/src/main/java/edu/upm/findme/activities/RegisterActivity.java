package edu.upm.findme.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.upm.findme.R;
import edu.upm.findme.utility.AsyncHttpClient;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class RegisterActivity extends AppCompatActivity {

    TextView txtName;
    TextView txtPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
    }

    public void onBtnSubmit(View view) {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("name", txtName.getText().toString())
                .add("phone", txtPhone.getText().toString())
                .build();

        client.post("https://findme.becsengo.hu/api.php?command=user-add", requestBody, (success, payload) ->
                Toast.makeText(RegisterActivity.this, (success ? "OK " : "ERROR ") + payload, Toast.LENGTH_SHORT).show());
    }
}