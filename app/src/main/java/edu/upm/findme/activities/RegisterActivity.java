package edu.upm.findme.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.upm.findme.R;

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
        Toast.makeText(this, txtName.getText().toString() + " " + txtPhone.getText().toString(), Toast.LENGTH_SHORT).show();
    }
}