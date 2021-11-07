package edu.upm.findme.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import edu.upm.findme.App;
import edu.upm.findme.AppEvent;
import edu.upm.findme.R;
import edu.upm.findme.model.User;
import edu.upm.findme.utility.ApiClient;
import edu.upm.findme.utility.UserInfoManager;

public class MenuActivity extends AppCompatActivity implements ApiClient.FailureHandler {

    final ApiClient api = new ApiClient(this);
    final UserInfoManager userInfo = new UserInfoManager(this);

    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        listView=(ListView)findViewById(R.id.listViewUser);

        ArrayList<String> arrayList=new ArrayList<>();
        arrayList.add("user1");
        arrayList.add("user2");
        arrayList.add("user3");
        arrayList.add("user4");
        arrayList.add("user5");
        arrayList.add("user6");
        arrayList.add("user7");
        arrayList.add("user8");
        arrayList.add("user9");
        arrayList.add("user10");


        ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);

        api.listUsers((users) -> {
            String name = "";
            for (User u : users) {
                if (u.getId() == userInfo.getUserId())
                    name = u.getName();
            }
            Toast.makeText(this, "Hello, " + name + "!\nNumber of users: " + users.size(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onApiFailure(String errorDescription) {
        Toast.makeText(this, "API error: " + errorDescription, Toast.LENGTH_SHORT).show();
    }
}
