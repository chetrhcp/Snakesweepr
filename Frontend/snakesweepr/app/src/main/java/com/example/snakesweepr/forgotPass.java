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


public class forgotPass extends AppCompatActivity {
    Button submit;
    TextView content;
    EditText input;
    comHandler com = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        final Context context = getApplicationContext();
        final SharedPreferences sharedPref = context.getSharedPreferences("userInfo",0);

        submit = findViewById(R.id.buttonRecov);
        input = findViewById(R.id.editTextInput);

        try {
            com = new comHandler("cs309-sd-1.misc.iastate.edu",5000, context);
            com.connect();
        } catch (URISyntaxException e) {
            System.out.println("Could not connect to server");
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter user information", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject user = new JSONObject();

                        user.put("type", "forgot_password"); //check txt file
                        user.put("platform", "app");
                        user.put("username", input.getText()); //check with Collin on tags

                        sendMessage(user.toString());
                        System.out.println(user.toString()); //figure out how to update screen - specific call?

                        Intent recover = new Intent(forgotPass.this, recoverPinEnter.class);
                        startActivity(recover);

                    } catch (JSONException e) {
                        System.out.println("Could not recover password");
                    }
                }//else
            }
        });//setOnClickListener
    }//onCreate
    protected void sendMessage(String message){
        com.sendMessage(message);
    }
}