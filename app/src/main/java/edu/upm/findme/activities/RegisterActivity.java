package edu.upm.findme.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import edu.upm.findme.R;
import edu.upm.findme.utility.AsyncHttpClient;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onTestBtn(View v) {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("name", "Jon Doe")
                .add("phone", "+34-00-00-00-00")
                .build();

        client.post("https://findme.becsengo.hu/api.php?command=user-add", requestBody, (success, payload) ->
                Toast.makeText(RegisterActivity.this, (success ? "OK " : "ERROR ") + payload, Toast.LENGTH_SHORT).show());
    }
}