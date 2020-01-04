package com.example.snakesweepr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class recoverPinEnter extends AppCompatActivity {
    Button recovery;
    EditText password, checkPass, pin;
    comHandler com = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_pin_enter);

        final Context context = getApplicationContext();
        final SharedPreferences sharedPref = context.getSharedPreferences("userInfo",0);

        recovery = findViewById(R.id.ButtonRecovery);
        password = findViewById(R.id.editTextNewPass);
        pin = findViewById(R.id.editTextPin);

        try {
            com = new comHandler("cs309-sd-1.misc.iastate.edu",5000, context);
            com.connect();
        } catch (URISyntaxException e) {
            System.out.println("Could not connect to server");
        }

        recovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPass.getText().toString().isEmpty() || password.getText().toString().isEmpty()
                        || pin.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter user information", Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject user = new JSONObject();
                    try {
                        user.put("type","login");
                        user.put("platform", "app");
                        user.put("password",password.getText().toString());
                        user.put("pin", pin.getText().toString());

                        sendMessage(user.toString());
                        System.out.println(user.toString());

                        if(true){ //TODO if pin is correct
                            Intent home = new Intent(recoverPinEnter.this, homeLobby.class);
                            startActivity(home);
                        }

                    } catch (JSONException e) {
                        System.out.println("Could not send password reset");
                    }
                }//else
            }
        });//onClick
    }
    public void sendMessage(String message){
        com.sendMessage(message);
    }
}
