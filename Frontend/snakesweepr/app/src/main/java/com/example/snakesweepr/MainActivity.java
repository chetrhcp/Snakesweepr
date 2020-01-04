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

public class MainActivity extends AppCompatActivity {
    private Button login, create, forgot;
    private EditText name, password;
    comHandler com = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       final Context context = getApplicationContext();
       final SharedPreferences sharedPref = context.getSharedPreferences("userInfo",0);
       final SharedPreferences.Editor sharedEdit = sharedPref.edit();

        name = findViewById(R.id.editTextName); //Name input on screen
        password = findViewById(R.id.editTextPass); //Password input on screen
        create = findViewById(R.id.buttonCreate);
        forgot = findViewById(R.id.buttonForgot);
        login = findViewById(R.id.buttonLogin);

        //sharedEdit.clear(); //Used for removing preferences no longer needed
        //sharedEdit.commit();

        try {
            com = new comHandler("cs309-sd-1.misc.iastate.edu",5000, context);
            com.connect();
        } catch (URISyntaxException e) {
            System.out.println("Could not connect to server");
        }

        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){

                if(name.getText().toString().isEmpty() || password.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter login information.", Toast.LENGTH_SHORT).show();
                }//if
                else{

                    JSONObject user = new JSONObject();
                    try {
                        user.put("type","login");
                        user.put("platform", "app");
                        user.put("username",name.getText().toString());
                        user.put("password", password.getText().toString());
                        sharedEdit.putString("password", password.getText().toString());

                        sendMessage(user.toString());
                        System.out.println("Sending:" + user.toString());

                        if(sharedPref.getBoolean("success", true)){

                            Intent home = new Intent(MainActivity.this, homeLobby.class);
                            startActivity(home);
                        }

                    } catch (JSONException e) {
                        System.out.println("Could not log in to server");
                    }
                }//else
            }
        });//setOnClickListener

        create.setOnClickListener(new View.OnClickListener() {//launches new account page

            @Override
            public void onClick(View v) {

               // sharedPref.edit().putString("success", "false");
                Intent create = new Intent(MainActivity.this, createAccount.class);
                startActivity(create);
            }
        });//create button
        forgot.setOnClickListener(new View.OnClickListener() {//launches new account page
            @Override
            public void onClick(View v) {
                Intent forgot = new Intent(MainActivity.this, forgotPass.class);
                startActivity(forgot);
            }
        });//forgot button
    }//onCreate

    protected void sendMessage(String message){
        com.sendMessage(message);
    }
}//class
