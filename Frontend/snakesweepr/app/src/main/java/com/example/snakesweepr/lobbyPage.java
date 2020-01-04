package com.example.snakesweepr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class lobbyPage extends AppCompatActivity {
    private ListView lobbyList;
    private Button lobbyCreate;
    private CheckBox isPrivate;
    private EditText lobbyChoice;
    comHandler com = null;
    private String[] lobbies = {"Lobby 1", "Lobby 2", "Lobby 3", "Lobby 4", "Lobby 5"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobbypage);

        final Context context = getApplicationContext();
        final SharedPreferences sharedPref = context.getSharedPreferences("userInfo",0);
        final SharedPreferences.Editor sharedEdit = sharedPref.edit();

        try {
            com = new comHandler("cs309-sd-1.misc.iastate.edu",5000, context);
            com.connect();
        } catch (URISyntaxException e) {
            System.out.println("Could not connect to server");
        }

        lobbyList = findViewById(R.id.lobbyList);
        lobbyCreate = findViewById(R.id.lobbyCreate);
        lobbyChoice = findViewById(R.id.lobbyChoice);
        isPrivate = findViewById(R.id.checkBox);

            lobbies[0] = sharedPref.getString("lobby1", " ");
            lobbies[1] = sharedPref.getString("lobby2", " ");
            lobbies[2] = sharedPref.getString("lobby3", " ");
            lobbies[3] = sharedPref.getString("lobby4", " ");
            lobbies[4] = sharedPref.getString("lobby5", " ");

        ArrayAdapter<String> lobbyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_selectable_list_item, lobbies);
        lobbyList.setAdapter(lobbyAdapter);
        lobbyList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lobbyList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

           @SuppressLint("ApplySharedPref")
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               if(!lobbies[position].equals(" ")){
                   sharedEdit.putString("lobby", sharedPref.getString("lobby" + (position+1), "lobby"));
                   sharedEdit.putInt("lobbykey", position + 1);
                   sharedEdit.putInt("lobbyid", Integer.parseInt(sharedPref.getString("idlobby" + (position + 1), "1")));
                   sharedEdit.commit();

                   Set<String> set = sharedPref.getStringSet("infolobby" + (position + 1), new HashSet<String>());
                    JSONObject lobby = new JSONObject();
                   try {
                       lobby.put("type", "join_lobby");
                       lobby.put("platform", "app");
                       lobby.put("lobby", sharedPref.getString("lobby", "Test"));
                       lobby.put("username", sharedPref.getString("username", "Username"));
                       sendMessage(lobby.toString());
                       System.out.println("Sending:" + lobby.toString());

                   } catch (JSONException e) {
                       System.out.println("Something is wrong with join lobbies.");
                   }

                   Intent chat = new Intent(lobbyPage.this, userChat.class);
                   startActivity(chat);
               }
           }
       });

        lobbyCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lobbyChoice.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter lobby information", Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject user = new JSONObject();
                    try {
                        user.put("type", "new_lobby");
                        user.put("platform", "app");
                        sendMessage(user.toString());
                        System.out.println(user.toString());
                        sharedEdit.putString("lobby", lobbyChoice.getText().toString());
                        sharedEdit.putString("lobbycreator", sharedPref.getString("username", "admin"));
                        if(isPrivate.isChecked()) {
                            sharedEdit.putBoolean("lobbyprivacy", true);
                        } else{
                            sharedEdit.putBoolean("lobbyprivacy", false);
                        }
                        sharedEdit.commit();

                        Intent chat = new Intent(lobbyPage.this, userChat.class);
                        startActivity(chat);

                    } catch (JSONException e) {
                        System.out.println("Cannot create a lobby");
                    }
                }
            }
        });//setOnClickListener
    }
    protected void sendMessage(String message){
        com.sendMessage(message);
    }
}