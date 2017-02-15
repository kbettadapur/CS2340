package com.example.kirin.cs2340.Controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.content.Intent;

import com.example.kirin.cs2340.Model.User;
import com.example.kirin.cs2340.Model.Users;
import com.example.kirin.cs2340.R;

import java.util.ArrayList;

/**
 * Created by Kirin on 2/14/2017.
 */

public class LoginActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

    }

    public void loginpressed(View view) {
        ArrayList<User> users = Users.getInstance().getUsers();
        if (username.getText().toString().equals(users.get(0).getUsername())
                && password.getText().toString().equals(users.get(1).getPassword())) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
        }
    }
}