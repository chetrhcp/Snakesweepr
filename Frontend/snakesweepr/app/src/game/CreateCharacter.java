package game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
//A class for generating objects
/*
public class CreateCharacter {

        private Drawable image;

        public CreateCharacter(Drawable d) {
            image = d;
        }

        public void draw(Canvas canvas)
        {
            image.setBounds(30, 30,30,30);
            image.draw(canvas);
        }
}*/

public class CreateCharacter {
    private Bitmap image;

    public CreateCharacter(Bitmap bmp) {
        image = bmp;
    }

    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(image, 100, 100, null);
    }
}