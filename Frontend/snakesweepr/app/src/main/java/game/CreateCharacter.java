package game;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.example.snakesweepr.R;


public class CreateCharacter extends Activity implements View.OnTouchListener {


    private Bitmap image;
    private Bitmap food;
    private int x, y;
    private int xVelocity = 5;
    private int yVelocity = 5;
    private int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    private int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    private boolean snakeLeft =false;
    private boolean snakeRight =false;
    private boolean snakeUp =false;
    private boolean snakeDown =false;
    private Bitmap original;
    private GameView gameView;
    private Bitmap bmp;
    private Canvas canvas;
    int xVal = GameView.getxVal();
    int yVal = GameView.getyVal();
    int width = screenWidth/xVal;
    int height = screenHeight/yVal;
    int padding = screenHeight- screenWidth;
    int tileWidth = screenWidth/10;
    int tileHeight = tileWidth;
    int headX =3;
    int headY=6;
    int foodX = 5;
    int foodY = 7;
    int score;
    private Bitmap bod;
    private Bitmap logo;
    private Bitmap background;
    private Bitmap wall;
    int logoScale;
    int walls[][];






    public CreateCharacter(GameView view, Bitmap bmp) {
        this.gameView=view;
        this.bmp=bmp;
        food = view.food;
        logo =view.logo;
        bod = view.snakebod;
        wall = view.wall;
        background=view.background;
        canvas =view.initCan;
        headX= gameView.headX;
        headY = gameView.headY;
        //logoScale = logo.getHeight()/logo.getWidth();

            bmp = getResizedBitmap(bmp, tileWidth, tileHeight);
            food = getResizedBitmap(food, tileWidth, tileHeight);
            logo =getResizedBitmap(logo, screenWidth/2, screenWidth/12);
            bod = getResizedBitmap(bod, tileWidth, tileHeight);
        wall = getResizedBitmap(wall, tileWidth, tileHeight);
            background = getResizedBitmap(background, screenWidth, screenHeight);
            x = headX * tileWidth;
            y = (headY*tileHeight + padding/2);



        image = bmp;
        original = image;

    }
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {//resizes the image to the size wanted
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public void onDraw(Canvas canvas) {
        update(gameView.direct);
        Paint paint = new Paint();
        int odd=0;
        int x1=x;
        int y1= y;
        if(snakeLeft){
            x1=x+tileWidth;
        }else if(snakeDown){
            y1 = y-tileHeight;
        }else if(snakeUp){
            y1 = y+tileHeight;
        }else{
            x1=x-tileWidth;
        }
//
        canvas.drawBitmap(background,0,0,null);
        canvas.drawBitmap(logo, 10, 40, null);

        for(int i=0;i<10;i++){//creates the alternating tile grid to fill the whole screen based on the device size
            for(int j=0; j<10;j++){
                if(odd==0){
                    paint.setColor(Color.rgb(117, 232, 99));
                    canvas.drawRect(i*tileWidth, ((j*tileHeight)+ (padding/2)),
                            (i*tileWidth)+ tileWidth, ((j*tileHeight)+(tileHeight)+ (padding/2)), paint);
                    odd=1;
                }else{
                    paint.setColor(Color.rgb(70, 179, 53));
                    canvas.drawRect(i*tileWidth, ((j*tileHeight)+ (padding/2)),
                            (i*tileWidth)+ tileWidth, ((j*tileHeight)+(tileHeight)+ (padding/2)), paint);
                    odd=0;
                }
            }
            if(odd==0) {
                odd = 1;
            }
            else {
                odd = 0;
            }
        }
        for(int xwall =0;xwall<10;xwall++){
            for(int ywall =0; ywall<10;ywall++){
                if( walls[xwall][ywall]==1){
                    canvas.drawBitmap(wall, xwall*tileWidth, ywall*tileHeight+padding/2, null);
                }
            }
        }
        canvas.drawBitmap(food, foodX*tileWidth, (tileHeight*foodY)+padding/2, null);
        canvas.drawBitmap(image, x, y, null);
        canvas.drawBitmap(bod, x1, y1, null);
        paint.setColor(Color.GREEN);
        paint.setTextSize(64);
        paint.setFakeBoldText(true);
      //  canvas.drawRect(12);
        canvas.drawText("Score: " +Integer.toString(score),screenWidth /2,12*tileHeight +padding/2,paint );


    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public void update(int direction){//updates the position of the character based on the swipe direction boolean
        foodX = gameView.foodX;
        foodY = gameView.foodY;
        score = gameView.foods;
        walls = gameView.walls;
        if(direction == 1 &&!snakeLeft){//right
            snakeRight=true;
            snakeLeft =false;
            snakeUp =false;
            snakeDown =false;
            image = RotateBitmap(original, 0);
        }else if(direction == 2 &&!snakeUp){//down
            snakeDown =true;
            snakeLeft =false;
            snakeRight =false;
            snakeUp =false;

            image = RotateBitmap(original, 90);
        }else if(direction == 3 &&!snakeRight){//left
            snakeLeft =true;
            snakeRight =false;
            snakeUp =false;
            snakeDown =false;
            image = RotateBitmap(original, 180);
        }else if(direction == 4 && !snakeDown){//up
            snakeUp =true;
            snakeLeft =false;
            snakeRight =false;
            snakeDown =false;
            image = RotateBitmap(original, -90);
        }else{
            snakeLeft =false;
            snakeRight =false;
            snakeUp =false;
            snakeDown =false;
        }
        if(snakeRight){
            if ((!(headX > 8)) && (walls[headX+1][headY]!=1)) {
                headX++;
                x = headX*tileWidth;
//                System.out.println("%y:"+headY);
//                System.out.println("%x:"+headX);
            }
        }
        else if(snakeLeft){

            if ((!(headX <1))&& (walls[headX-1][headY]!=1)) {
                headX--;
                x =headX*tileWidth;
//                System.out.println("%y:"+headY);
//                System.out.println("%x:"+headX);
            }

        }
        else if(snakeUp){
            if ((!(headY<1))&& (walls[headX][headY-1]!=1)) {
                headY--;
                y =(headY*tileHeight)+padding/2;
                //headY = ((y+tileHeight)-(padding/2))/tileHeight; //closest Xtile
            }
//            System.out.println("%y:"+headY);
//            System.out.println("%x:"+headX);
        }
        else if(snakeDown){
            if ((!(headY > 8))&& (walls[headX][headY+1]!=1)) {
                headY++;
                y =(headY*tileHeight)+padding/2;
               // headY = ((y+tileHeight)-(padding/2))/tileHeight; //closest Xtile
//                System.out.println("%y:"+headY);
//                System.out.println("%x:"+headX);
            }
        }

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

//    public void draw(Canvas canvas) {
//        canvas.drawBitmap(image, x, y, null);
//
//    }
}