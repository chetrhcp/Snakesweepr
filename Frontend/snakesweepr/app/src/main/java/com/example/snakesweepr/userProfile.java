package com.example.snakesweepr;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.net.URISyntaxException;

public class userProfile extends AppCompatActivity {
    TextView username, name, sweeprScore, snakeScore;
    comHandler com = null;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        username = findViewById(R.id.profileUser);
        name = findViewById(R.id.profileName);
        sweeprScore = findViewById(R.id.sweeprScore);
        snakeScore = findViewById(R.id.snakeScore);

        final Context context = getApplicationContext();
        final SharedPreferences sharedPref = context.getSharedPreferences("userInfo", 0);
       final SharedPreferences.Editor editor = sharedPref.edit();

        try {
            com = new comHandler("cs309-sd-1.misc.iastate.edu", 5000, context);
            com.connect();
        } catch (URISyntaxException e) {
            System.out.println("Could not connect to server");
        }

        editor.putString("snakeScore", "45");
        editor.putString("sweeprScore", "3");

        username.setText(sharedPref.getString("username", "User"));
        name.setText(sharedPref.getString("firstName", "First") + " " + sharedPref.getString("lastName", "Last"));
        snakeScore.setText(Integer.toString(sharedPref.getInt("snakeScore", 100)));
        sweeprScore.setText(Integer.toString(sharedPref.getInt("sweeprScore", 50)));

    }

    protected void sendMessage(String message) {
        com.sendMessage(message);
    }
}
