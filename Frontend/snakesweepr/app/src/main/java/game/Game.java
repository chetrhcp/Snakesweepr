package game;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Window;
import android.view.WindowManager;

import com.example.snakesweepr.R;
import com.example.snakesweepr.comHandler;



public class Game extends Activity {

   // public static comHandler com;
    boolean value = true;
    public static String username="kevin";
    MediaPlayer player;
    SharedPreferences sharedPref;
    public int id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //value = getIntent().getExtras().getBoolean("isOn?");//from sound settings
        //username= getIntent().getExtras().getString("username");
        sharedPref = this.getSharedPreferences("userInfo",0);
        id=sharedPref.getInt("lobbyid", 1);

        if(value) {
            player = MediaPlayer.create(this, R.raw.tune2); //select music file
            player.start();
            player.setLooping(true); //set looping
            player.setVolume(100,100);
        }
        setContentView(new GameView(this));
        //setContentView(new GameView(this));

    }
    protected void onDestroy() {
        //stop service and stop music
        stopService(new Intent(Game.this, SoundService.class));
        super.onDestroy();
    }




}
