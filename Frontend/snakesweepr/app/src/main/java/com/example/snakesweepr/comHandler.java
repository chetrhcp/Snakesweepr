package com.example.snakesweepr;

import android.content.Context;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.content.SharedPreferences;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class comHandler extends WebSocketClient {
    SharedPreferences sharedPref;
    SharedPreferences.Editor sharedEdit;

    public comHandler(String url, int port, Context context) throws URISyntaxException {
        super(new URI("ws://" + url + ":" + port));
        sharedPref = context.getSharedPreferences("userInfo",0);
        sharedEdit  = sharedPref.edit();
    }

    @Override
    public void onOpen(ServerHandshake handshake_data) {
        System.out.println("Connection Opened");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Message from Server" + message);
        try {
            JSONObject userInfo = new JSONObject(message);

            if(userInfo.get("type").equals("login") && userInfo.getBoolean("success")){
                sharedEdit.putBoolean("login", true);
                sharedEdit.putString("username", userInfo.getString("username"));
                sharedEdit.putString("firstName", userInfo.getString("firstname"));
                sharedEdit.putString("lastName", userInfo.getString("lastname"));
                sharedEdit.commit();
            }

            if(userInfo.get("type").equals("new_user") && userInfo.getBoolean("success")){
                sharedEdit.putBoolean("create", true);
                sharedEdit.commit();
            }

            if(userInfo.get("type").equals("join_lobby") && userInfo.getBoolean("success")){
                JSONArray chats = userInfo.getJSONArray("chats");
                for(int i =0; i<chats.length();++i){
                    sharedEdit.putString("chats" + (i+1), chats.getString(i));
                    if(chats.getString(i).equals("")){
                        sharedEdit.putString("chats" + (i+1), " ");
                    }
                }
                sharedEdit.commit();
            }

            if(userInfo.get("type").equals("new_lobby") && userInfo.getBoolean("success")){
                sharedEdit.putBoolean("chat", true);
                sharedEdit.commit();
            }

            if(userInfo.get("type").equals("lobby_chat")){
                sharedEdit.putString("chatUser", userInfo.getString("username"));
                sharedEdit.putString("message", userInfo.getString("message"));
                sharedEdit.putBoolean("waitingMsg", true);
                sharedEdit.commit();
            }
            if(userInfo.get("type").equals("new_game") || userInfo.get("type").equals("update_game")){//game logic
                   System.out.println("Got stuff back");
                ArrayList<String> grid = new ArrayList<String>();
                ArrayList<String> walls = new ArrayList<String>();
                ArrayList<String> newTile = new ArrayList<String>();
                ArrayList<String> mines = new ArrayList<String>();
                ArrayList<String> foods = new ArrayList<String>();
                Set<String> foodz = new HashSet<>();
                Set<String> gridz= new HashSet<>();
                Set<String> wallz= new HashSet<>();
                Set<String> newTilez= new HashSet<>();
                Set<String> minez= new HashSet<>();

                int x;
                int y;
                JSONObject data = userInfo.getJSONObject("data");
                JSONObject snake = data.getJSONObject("snake");
                JSONObject food = snake.getJSONObject("food_pos");
                x=food.getInt("x");
                y=food.getInt("y");
                foods.add( x +" "+ y);
                foodz.addAll(foods);
                JSONArray wall = snake.getJSONArray("walls");

                for (int i = 0; i < wall.length(); i++) {

                    JSONObject point = wall.getJSONObject(i);
                    x=point.getInt("x");
                    y=point.getInt("y");
                    walls.add( x +" "+ y);
                }
                wallz.addAll(walls);
                JSONArray snake_pos = new JSONArray(snake.getString("snake_pos"));
                for (int i = 0; i < snake_pos.length(); i++) {
                    JSONObject point = snake_pos.getJSONObject(i);
                    x=point.getInt("x");
                    y=point.getInt("y");
                    grid.add( x +" "+ y);
                }
                gridz.addAll(grid);
                JSONObject mine = new JSONObject(data.getString("minesweeper"));
                JSONArray mine_pos = new JSONArray(mine.getString("mine_pos"));
                for (int i = 0; i < mine_pos.length(); i++) {
                    JSONObject point = mine_pos.getJSONObject(i);
                    x=point.getInt("x");
                    y=point.getInt("y");
                    mines.add( x +" "+ y);
                }
                minez.addAll(mines);
                JSONArray new_tiles = new JSONArray(mine.getString("new_tiles"));
                for (int i = 0; i < new_tiles.length(); i++) {
                    JSONObject point = new_tiles.getJSONObject(i);
                    x=point.getInt("x");
                    y=point.getInt("y");
                    newTile.add( x +" "+ y);
                }
                newTilez.addAll(newTile);
                sharedEdit.putString("snakeScore", snake.getString("score"));
                sharedEdit.putString("mineScore", mine.getString("score"));
                sharedEdit.putBoolean("snakeDied", snake.getBoolean("died"));
                sharedEdit.putBoolean("mineDied", mine.getBoolean("died"));
                sharedEdit.putBoolean("snakeWon", snake.getBoolean("won"));
                sharedEdit.putBoolean("mineWon", mine.getBoolean("won"));
                sharedEdit.putStringSet("snake_start", gridz);
                sharedEdit.putStringSet("walls", wallz);
                sharedEdit.putStringSet("mines", minez);
                sharedEdit.putStringSet("new_tiles", newTilez);
                sharedEdit.putStringSet("food_pos", foodz);


                sharedEdit.commit();
                //System.out.println(sharedPref.getString("walls", "Hi"));
            }

            if(userInfo.get("type").equals("get_user_lobbies")) {
                int numLobbies = userInfo.getJSONArray("lobbies").length();

                String lobbyName;

                for (int j = 0; j < numLobbies; ++j) {
                   lobbyName = "lobby" + (j+1);
                   Set<String> set = sharedPref.getStringSet("info" + lobbyName, new HashSet<String>());
                   sharedEdit.putInt("hashkey", set.hashCode());
                   System.out.println(userInfo.getJSONArray("lobbies").getJSONArray(j).getJSONObject(0).toString(2));

                   set.add(userInfo.getJSONArray("lobbies").getJSONArray(j).getJSONObject(0).getString("lobbyid"));
                   set.add(userInfo.getJSONArray("lobbies").getJSONArray(j).getJSONObject(0).getString("name"));
                   set.add(userInfo.getJSONArray("lobbies").getJSONArray(j).getJSONObject(0).getString("creator"));
                   set.add(userInfo.getJSONArray("lobbies").getJSONArray(j).getJSONObject(0).getString("occupancy"));
                   set.add(userInfo.getJSONArray("lobbies").getJSONArray(j).getJSONObject(0).getString("isprivate"));

                    sharedEdit.putString(lobbyName, userInfo.getJSONArray("lobbies").getJSONArray(j).getJSONObject(0).getString("name"));
                    sharedEdit.putStringSet("info" + lobbyName, set);
                    sharedEdit.putString("id" + lobbyName, userInfo.getJSONArray("lobbies").getJSONArray(j).getJSONObject(0).getString("lobbyid"));
                    sharedEdit.commit();
                    System.out.println(sharedPref.getAll());
                }
            }
        } catch (JSONException e) {
           System.out.println("Com handler Error: "+e);

        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection Closed: " +"Code: "+code +" "+"Reason: " +reason+"Remote: "+remote);
    }

    @Override
    public void onError(Exception ex) {
       System.out.println("Com handler onError: " +ex);
    }

    public void sendMessage(String message){
        send(message);
    }
}
