package game;

        import android.content.Context;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.Matrix;
        import android.graphics.Paint;
        import android.graphics.drawable.BitmapDrawable;
        import android.graphics.drawable.Drawable;
        import android.util.Base64;
        import android.view.SurfaceHolder;
        import android.view.SurfaceView;
        import android.view.View;
        import android.widget.ImageView;

        import com.example.snakesweeper.snakesweeper.R;

public class GameView extends View {
    //public MainThread thread;
    private Bitmap head ,bod;
    int screenWidth;
    int screenHeight;
    int snakeWidth;
    int snakeHeight;
    int tileWidth;
    int tileHeight;
    int bodWidthOff;
    int bodHeightOff;
    int snakeInitH=4;//Random Number between 0-15
    int snakeInitW=5;//Random Number between 1-8


    public GameView(Context context, int width, int height) {
        super(context);
        screenWidth = width;
        screenHeight = height;
        System.out.println(height);
        System.out.println(width);
        snakeWidth = (width/10);
        snakeHeight = (height/16);
        bodHeightOff=(snakeHeight/10);
        bodWidthOff=4*(snakeWidth/8);
        tileHeight = (height/16);
        tileWidth = (width/10);

        head = BitmapFactory.decodeResource(getResources(), R.drawable.snakehead);
        bod = BitmapFactory.decodeResource(getResources(), R.drawable.snakebod);
        head = getResizedBitmap(head,snakeWidth,snakeHeight);
        bod = getResizedBitmap(bod, 3*(snakeWidth/4),3*(snakeHeight/4));

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

//        thread = new MainThread( getHolder(), this);
//        setFocusable(true);
//    }

//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//        thread.setRunning(true);
//        thread.start();
//        character = new CreateCharacter(BitmapFactory.decodeResource(getResources(), R.drawable.snakehead));
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
//
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder holder) {
//        boolean retry = true;
//        while(retry){
//            try{
//                thread.setRunning(false);
//                thread.join();
//            }catch (InterruptedException e){
//                e.printStackTrace();
//            }
//            retry = false;
//        }
//    }
//    public void update(){
//
//    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        int odd=0;

        canvas.drawColor(Color.BLACK);
        for(int i=0;i<10;i++){//creates the alternating tile grid to filll the whole screen based on the device size
            for(int j=0; j<16;j++){
                if(odd==0){
                    paint.setColor(Color.rgb(117, 232, 99));
                    canvas.drawRect(i*(tileWidth), j*(tileHeight),
                            i*(tileWidth)+(snakeWidth), j*(tileHeight)+(tileHeight), paint);
                    odd=1;
                }else{
                    paint.setColor(Color.rgb(70, 179, 53));
                    canvas.drawRect(i*(tileWidth), j*(tileHeight),
                            i*(tileWidth)+(snakeWidth), j*(tileHeight)+(tileHeight), paint);
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

        canvas.drawBitmap(head, snakeInitW*snakeWidth, snakeInitH*snakeHeight,null);//intial Snake Location
        canvas.drawBitmap(bod, snakeInitW*snakeWidth-bodWidthOff ,snakeInitH*snakeHeight+bodHeightOff ,null);

    }




}

