package mine;

import android.content.Context;
import android.content.SharedPreferences;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class gameCom extends WebSocketClient {
    SharedPreferences sharedPref;
    SharedPreferences.Editor sharedEdit;
    public gameCom(String url, int port, Context context) throws URISyntaxException {
        super(new URI("ws://" + url + ":" + port));
        sharedPref = context.getSharedPreferences("userInfo",0);
        sharedEdit  = sharedPref.edit();
    }
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connection Opened");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Message from Server" + message);
        try {
            JSONObject userInfo = new JSONObject(message);

            if(userInfo.get("type").equals("login") && userInfo.getBoolean("success")){
                sharedEdit.putBoolean("login", true);
                sharedEdit.putString("u", userInfo.getString("username"));
                sharedEdit.putInt("sessionid", userInfo.getInt("sessionid"));
                sharedEdit.commit();
            }

            if(userInfo.get("type").equals("new_user") && userInfo.getBoolean("success")){
                sharedEdit.putBoolean("create", true);
                sharedEdit.commit();
            }

            if(userInfo.get("type").equals("new_lobby") && userInfo.getBoolean("success")){
                sharedEdit.putBoolean("chat", true);
                sharedEdit.commit();
            }

            if(userInfo.get("type").equals("lobby_chat")){
                sharedEdit.putString("message", userInfo.getString("message"));
                sharedEdit.commit();
            }

            if(userInfo.get("type").equals("lobbies")){ //double check type name
                //sharedEdit.putString("lobby1", get lobbies);
                // Set <String> lobbyList = null; //need to figure out how to initialize
                //sharedEdit.putStringSet("lobbies", lobbyList);
                // sharedEdit.commit();
            }

        } catch (JSONException e) {
            System.out.println("This is not a JSON Object");
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection Closed");
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }
}
