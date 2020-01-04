package game;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import com.example.snakesweepr.R;
import com.example.snakesweepr.comHandler;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    public int direct=0;
    private MainThread thread;
    private CreateCharacter character;
    float baseX=0;
    float baseY =0;
    public boolean eatten = false;
    public node snake = new node();
    //send these values to server
    public int foodX =0;//From Server
    public int foodY =0;//from Server
    public int headX =0;//send to server
    public int headY =0;//send to server
    public int length =1;//send to server
    public int walls[][];//from server
    public Bitmap food;
    public Bitmap logo;
    public Bitmap background;
    public int snakeLen =1;
    public int foods=-1;
    //
    private int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    private int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    static int yVal =10;// based on the size of the grid we want to make
    static int xVal =10;// based on the size of the grid we want to make
    int tileHeight = (screenHeight/yVal);//allows for the actual pixil count of a grid side to vary based on resolution
    int tileWidth = (screenWidth/xVal);//allows for the actual pixil count of a grid side to vary based on resolution
    public boolean init =false;//ture once initialized
    protected Canvas initCan;
    private SurfaceHolder holder;
    public Bitmap snakepiece;
    public Bitmap snakebod;
    public Bitmap wall;
    private Context game;
    public comHandler com;// = Game.com;
    public String username = Game.username;
    public String username1;
    public String lobby;
    public int lobbyId;
    public int highScore = 10;
    SharedPreferences sharedPref;
    SharedPreferences.Editor sharedEdit;
    ArrayList<String> grid = new ArrayList<String>();
    ArrayList<String> wallz = new ArrayList<String>();
    ArrayList<String> newTile = new ArrayList<String>();
    ArrayList<String> mines = new ArrayList<String>();
    ArrayList<String> foodz = new ArrayList<String>();


    public GameView(Context context) {
        super(context);
        game =context;
        sharedPref = context.getSharedPreferences("userInfo",0);
        sharedEdit  = sharedPref.edit();
        sharedEdit.putInt("snakeScore", highScore);
        sharedEdit.commit();
        lobbyId = sharedPref.getInt("lobbyid", 0);
        username1 = sharedPref.getString("username",username);

        getHolder().addCallback(this);
      //  lobbyId = sharedPref.getInt("lobby", lobbyId);
        thread = new MainThread(getHolder(), this);
        walls= new int[10][10];
//        walls[3][4]=1;
//        walls[9][9]=1;
        snake.initList();
        snakepiece = BitmapFactory.decodeResource(getResources(), R.drawable.snakehead);
        food= BitmapFactory.decodeResource(getResources(), R.drawable.food);
        logo= BitmapFactory.decodeResource(getResources(), R.drawable.logosnake);
        snakebod = BitmapFactory.decodeResource(getResources(), R.drawable.snakebod);
        background= BitmapFactory.decodeResource(getResources(), R.drawable.purple);
        wall= BitmapFactory.decodeResource(getResources(), R.drawable.wall);
        character = new CreateCharacter(this, snakepiece);
        init = true;

        snake.addNode(snake.head,snakeLen, character, 0);
        length++;
        SharedPreferences sharedGamePref = context.getSharedPreferences("userInfo",0);

        try {
            com = new comHandler("cs309-sd-1.misc.iastate.edu",5000, context);
            com.connect();
        } catch (URISyntaxException e) {
            System.out.println("Could not connect to server");
        }
//        com.sendMessage("Hi");
//        wallz = (ArrayList<String>) sharedPref.getStringSet("walls", null);
//        for(String elem : wallz){
//            System.out.println(elem+" ");
//        }

        //sharedGamePref.getStringSet("food_pos",(Set<String>));
        initHead();//place head

    }
    public void updateFood(){
        Random rand = new Random();
        foodX = rand.nextInt(9);
        foodY = rand.nextInt(9);
    }
    public void initHead(){
        Random rand = new Random();
        headX = rand.nextInt(9)+1;
        headY = rand.nextInt(9);
    }

    public static int getxVal() {
        return xVal;
    }

    public static int getyVal() {
        return yVal;
    }

    public void updateServer(){//uses comhandler to send data to server


        JSONObject user = new JSONObject();
        JSONObject data = new JSONObject();
        JSONObject head_pos = new JSONObject();

        try {
            user.put("type", "update_game");
            user.put("platform", "app");
            user.put("lobby_id", lobbyId);
            head_pos.put("x",headX);
            head_pos.put("y", headY);
            data.put("head_pos", head_pos);
            data.put("username",username1);
            user.put("data", data);

            com.sendMessage(user.toString());
            System.out.println(user.toString());

        } catch (JSONException e) {
            System.out.println("Could not send data to server");
        }


    }



    @Override
    public void onDraw(Canvas canvas) {
        node hold = snake.head;
        super.onDraw(canvas);
        //canvas.drawColor(Color.GREEN);

        //  while(hold.next != null) {
        hold.character.onDraw(canvas);
//            hold = hold.next;
//        }

        headX=hold.character.headX;
        headY=hold.character.headY;
        if((headY==foodY)&&(headX==foodX)){
            updateFood();
            foods++;
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        boolean result=false;
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //Touch detected, record base X-Y coordinates
                baseX = event.getX();
                baseY = event.getY();

                //As the event is consumed, return true
                result = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getX() - baseX > 0 && Math.abs(event.getY() - baseY)< Math.abs(event.getX() - baseX)) {
                    direct = 1;
                } else if (event.getX() - baseX <= 0 && Math.abs(event.getY() - baseY)< Math.abs(event.getX() - baseX)) {
                    direct = 3;
                } else if (event.getY() - baseY > 0 && Math.abs(event.getY() - baseY)> Math.abs(event.getX() - baseX)) {
                    direct = 2;
                } else if (event.getY() - baseY <= 0 && Math.abs(event.getY() - baseY)> Math.abs(event.getX() - baseX)) {
                    direct = 4;
                }
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:

                return true;
        }

        return result;
    }

    public void update(int direction) {
        node hold = snake.head;
        snake.head.direction = direction;//sets the new direction for the snake
        if (eatten) {
            character = new CreateCharacter(this, snakepiece);
            snake.addNode(snake.head, length, character,snake.findTail(snake.head).direction);
            length++;
            eatten = false;
        }

//       snake.head.showList(snake.head);//prints out snake to terminal for debugging
//        for(int i =0; i< length; i++) {
//            if(hold.prev ==null){
//                hold.character.update(direction);
//            }else {
//                hold.character.update(hold.prev.direction);
//            }
//            hold =hold.next;
//        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        thread.setRunning(true);
        thread.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;

            } catch (InterruptedException e) {

            }
        }
    }
}
