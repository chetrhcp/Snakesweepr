package com.example.snakesweepr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class homeLobby extends AppCompatActivity {
    Button chat, settings, game, profile;
    comHandler com = null;
    boolean val;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homelobby);
        final Context context = getApplicationContext();
        final SharedPreferences sharedPref = context.getSharedPreferences("userInfo",0);

        chat = findViewById(R.id.buttonChat);
        settings = findViewById(R.id.buttonSettings);
        profile = findViewById(R.id.buttonFriends);

        try {
            com = new comHandler("cs309-sd-1.misc.iastate.edu",5000, context);
            com.connect();
        } catch (URISyntaxException e) {
            System.out.println("Could not connect to server");
        }

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject user = new JSONObject();

                try {
                    user.put("type","get_user_lobbies");
                    user.put("platform", "app");
                    user.put("username", sharedPref.getString("username", null));

                    sendMessage(user.toString());

                } catch (JSONException e) {
                    System.out.println("Cannot access lobby list.");
                }

                Intent chat = new Intent(homeLobby.this, lobbyPage.class);
                startActivity(chat);
            }
        });//onClick

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile = new Intent(homeLobby.this, userProfile.class);
                startActivity(profile);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {//launches new account page
            @Override
            public void onClick(View v) {
                Intent settings = new Intent(homeLobby.this, settings.class);
                startActivity(settings);
            }
        });
    }
    protected void sendMessage(String message){
        com.sendMessage(message);
    }
}