package com.example.snakesweepr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class createAccount extends AppCompatActivity {
    Button submit;
    EditText name, password, email, birth, firstName, lastName;
    comHandler com = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        final Context context = getApplicationContext();
        final SharedPreferences sharedPref = context.getSharedPreferences("userInfo",0);

        sharedPref.edit().putBoolean("create",false);
        sharedPref.edit().apply();

        name = findViewById(R.id.editTextUsername);
        password = findViewById(R.id.editTextPassword);
        email = findViewById(R.id.editTextEmail);
        birth = findViewById(R.id.editTextBdate);
        submit = findViewById(R.id.buttonSubmit);
        firstName = findViewById(R.id.editTextFirst);
        lastName = findViewById(R.id.editTextLast);

        try {
            com = new comHandler("cs309-sd-1.misc.iastate.edu",5000, context);
            com.connect();
        } catch (URISyntaxException e) {
            System.out.println("Could not connect to server");
        }

        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (name.getText().toString().isEmpty() || password.getText().toString().isEmpty() || email.getText().toString().isEmpty() || birth.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter user information", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject auth = new JSONObject();

                        auth.put("type", "new_user");
                        auth.put("platform", "app");
                        auth.put("firstname", firstName.getText());
                        auth.put("lastname", lastName.getText());
                        auth.put("username", name.getText());
                        auth.put("password", password.getText());
                        auth.put("email", email.getText());
                        auth.put("bdate", birth.getText());
                        auth.put("username", name.getText());
			            auth.put("type", "new_user");

                        sendMessage(auth.toString());
                        System.out.println(auth.toString());

                        if(sharedPref.getBoolean("success", true)){

                            Intent home = new Intent(createAccount.this, homeLobby.class);
                            startActivity(home);
                        }
                    } catch (JSONException e) {
                        System.out.println("Could not create an account");
                    }
                }//else
            }
        });//onClick
    }//onCreate

    protected void sendMessage(String message){
        com.sendMessage(message);
    }
}
