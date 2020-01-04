package com.example.snakesweepr;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class settings extends AppCompatActivity {
    private Button more, sound;
    private TextView bio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Context context = getApplicationContext();
        final SharedPreferences sharedPref = context.getSharedPreferences("userInfo", 0);
        final SharedPreferences.Editor editor = sharedPref.edit();

        more = findViewById(R.id.buttonMore);
        sound = findViewById(R.id.buttonBack);

        bio = findViewById(R.id.settingsBio);
        bio.setVisibility(View.INVISIBLE);

        more.setOnClickListener(new View.OnClickListener() {//launches new account page

            @Override
            public void onClick(View v) {
                if (bio.getVisibility() == View.VISIBLE) {
                    bio.setVisibility(View.INVISIBLE);
                } else {
                    bio.setVisibility(View.VISIBLE);
                }
            }
        });

        sound.setOnClickListener(new View.OnClickListener() {//launches new account page

            @Override
            public void onClick(View v) {
               if(sound.getCurrentTextColor() == getResources().getColor(R.color.black)){
                   sound.setTextColor(getResources().getColor(R.color.colorAccent));
                   sharedPref.edit().putBoolean("soundVal", true);
                   editor.commit();
               }
               else {
                   sound.setTextColor(getResources().getColor(R.color.black));
                   sharedPref.edit().putBoolean("soundVal", false);
                   editor.commit();
               }
            }
        });
    }
}
