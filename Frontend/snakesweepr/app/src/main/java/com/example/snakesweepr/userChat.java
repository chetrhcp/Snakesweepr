package com.example.snakesweepr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashSet;

public class userChat extends AppCompatActivity {
    private Button send, game;
    private EditText input;
    boolean val;
    comHandler com = null;
    private TextView chat;
    String toChat = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userchat);

        //this.mHandler = new Handler();
        //this.mHandler.postDelayed(m_Runnable,5000);

        input = findViewById(R.id.chat_text);
        send = findViewById(R.id.btn);
        chat = findViewById(R.id.chat);
        game = findViewById(R.id.gameStart);
        final Context context = getApplicationContext();
        final SharedPreferences sharedPref = context.getSharedPreferences("userInfo",0);
        final SharedPreferences.Editor sharedEdit = sharedPref.edit();

        chat.setText("");

        for(int i=0; i<10;++i) {
            if (!sharedPref.getString("chats" + (i+1), " ").equals(" ")) {
                toChat += "\n" + sharedPref.getString("chats" + (i+1), " ");
                sharedEdit.putString("chats" + (i+1), " ");
                sharedEdit.commit();
            }
            else{
                sharedEdit.putString("chats" + (i+1), " ");
                sharedEdit.commit();
            }
        }

        chat.setText(toChat);

        newMessage(com);

        try {
            com = new comHandler("cs309-sd-1.misc.iastate.edu",5000, context);
            com.connect();
        } catch (URISyntaxException e) {
            System.out.println("Could not connect to server");
        }

        newMessage(com);

        send.setOnClickListener(new View.OnClickListener() {//launches new account page
            @Override
            public void onClick(View v) {
                JSONObject message = new JSONObject();

                try {
                    message.put("type","lobby_chat");
                    message.put("platform", "app");
                    message.put("username", sharedPref.getString("username", null));
                    message.put("lobby",sharedPref.getString("lobby", "Test"));
                    message.put("message", input.getText().toString());

                    sendMessage(message.toString());
                    System.out.println("Sending:" + message.toString());

                   if(toChat.equals("")) {
                       toChat = sharedPref.getString("username", "User") + ": " + message.get("message").toString();
                       chat.setText(toChat);
                   }
                   else{
                       toChat += "\n" + sharedPref.getString("username", "User") + ": " + message.get("message").toString();
                       chat.setText(toChat);
                   }

                    input.setText("");
                } catch (JSONException e) {
                    System.out.println("Something is wrong with chat.");
                }
            }
        });

        game.setOnClickListener(new View.OnClickListener() {//launches new account page
            @Override
            public void onClick(View v) {
                String username = sharedPref.getString("username", "user");
                String lobby = "infolobby" + sharedPref.getInt("lobbykey", 1);
                if(sharedPref.getStringSet(lobby, new HashSet<String>()).contains(username)){
                    Intent game = new Intent(userChat.this, game.Game.class);
                    sharedPref.getBoolean("soundVal", val);
                    game.putExtra("isOn?", val);
                    startActivity(game);
                }
                else{
                    Intent game = new Intent(userChat.this, mine.MineSweep.class);
                    sharedPref.getBoolean("soundVal", val);
                    game.putExtra("isOn?", val);
                    startActivity(game);
                }
            }
        });
    }
    protected void sendMessage(String message){
        com.sendMessage(message);
    }

    protected void newMessage(comHandler com){
        final Context context = getApplicationContext();
        final SharedPreferences sharedPref = context.getSharedPreferences("userInfo",0);
        final SharedPreferences.Editor sharedEdit = sharedPref.edit();

        if(sharedPref.getBoolean("waitingMsg", false)){
            sharedEdit.putBoolean("waitingMsg", false);
            if(toChat.equals("")) {
                toChat += sharedPref.getString("chatUser", "User") + ": " + sharedPref.getString("message", "message");
                chat.setText(toChat);
            }
            else{
                toChat += "\n" + sharedPref.getString("chatUser", "User") + ": " + sharedPref.getString("message", "message");
                chat.setText(toChat);
            }
        }
    }
    private final Runnable m_Runnable = new Runnable()
    {
        public void run()

        {
       //     Toast.makeText(refresh.this,"in runnable",Toast.LENGTH_SHORT).show();
       //     refresh.this.mHandler.postDelayed(m_Runnable, 5000);
        }

    };//runnable
}