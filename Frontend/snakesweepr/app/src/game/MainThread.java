package game;

import android.view.SurfaceHolder;
import android.graphics.Canvas;

public class MainThread extends Thread {
    private SurfaceHolder surfaceHolder;
    private GameView gameView;
    private boolean running;
    public static Canvas canvas;

    public MainThread(SurfaceHolder surfaceHolder, GameView gameView) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gameView = gameView;
    }

    public void setRunning(boolean isRunning){
        running = isRunning;
    }

    @Override
    public void run(){
        while(running){
            //startTime=System.nanoTime();
            canvas = null;
        }
        try{
            canvas = this.surfaceHolder.lockCanvas();
            synchronized (surfaceHolder) {
               // this.gameView.update();
                this.gameView.draw(canvas);
            }
        }catch (Exception e){

        }
        finally {
            if (canvas !=null){
                try{
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }catch(Exception e){
                    System.out.println("Could not develop canvas");
                }
            }
        }
    }

}
