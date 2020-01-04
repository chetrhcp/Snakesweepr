package mine;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.example.snakesweepr.R;

public class Tile extends android.support.v7.widget.AppCompatButton
{
    private boolean isCovered; // is tile covered yet
    private boolean isMined; // does the tile has a mine underneath
    private boolean isFlagged; // is tile flagged as a potential mine
    private boolean isQuestionMarked; // is tile question marked
    private boolean isClickable; // can tile accept click events
    private int numSurrounding; // number of mines in nearby tiles

    public Tile(Context context)
    {
        super(context);
    }

    public Tile(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public Tile(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    // set default properties for the tile
    public void setDefaults()
    {
        isCovered = true;
        isMined = false;
        isFlagged = false;
        isQuestionMarked = false;
        isClickable = true;
        numSurrounding = 0;

        this.setBackgroundResource(R.drawable.greentile);
        setBoldFont();
    }

    // mark the tile as disabled/opened
    // update the number of nearby mines
    public void setNumberOfSurroundingMines(int number)
    {
        this.setBackgroundResource(R.drawable.graytile);

        updateNumber(number);
    }

    // set mine icon for tile
    // set tile as disabled/opened if false is passed
    public void setMineIcon(boolean enabled)
    {
        this.setText("*");
        this.setTextSize(23);
        this.setTextColor(Color.BLACK);
        if (!enabled)
        {
            this.setBackgroundResource(R.drawable.redtile);

        }

    }

    // set mine as flagged
    // set tile as disabled/opened if false is passed
    public void setFlagIcon(boolean enabled)
    {
        //this.setText("F");

        if (!enabled)
        {
            this.setBackgroundResource(R.drawable.graytile);
          //  this.setTextColor(Color.RED);
        }
        else
        {
            this.setBackgroundResource(R.drawable.flag);
            //this.setTextColor(Color.BLACK);
        }
    }

    // set tile as disabled/opened if false is passed
    // else enable/close it
    public void setTileAsDisabled(boolean enabled)
    {
        if (!enabled)
        {
            this.setBackgroundResource(R.drawable.graytile);
        }
        else
        {
            this.setBackgroundResource(R.drawable.greentile);
        }
    }

    // clear all icons/text
    public void clearAllIcons()
    {
        this.setText("");
    }

    // set font as bold
    private void setBoldFont()
    {
        this.setTypeface(null, Typeface.BOLD);
    }

    // uncover this tile
    public void OpenTile()
    {
        // cannot uncover a mine which is not covered
        if (!isCovered)
            return;

        setTileAsDisabled(false);
        isCovered = false;

        // check if it has mine
        if (hasMine())
        {
            setMineIcon(false);
        }
        // update with the nearby mine count
        else
        {
            setNumberOfSurroundingMines(numSurrounding);
        }
    }

    // set text as nearby mine count
    public void updateNumber(int text)
    {
        if (text != 0)
        {
            this.setText(Integer.toString(text));

            // select different color for each number
            // we have already skipped 0 mine count
            switch (text)
            {
                case 1:
                    this.setTextColor(Color.BLUE);
                    break;
                case 2:
                    this.setTextColor(Color.rgb(0, 100, 0));
                    break;
                case 3:
                    this.setTextColor(Color.RED);
                    break;
                case 4:
                    this.setTextColor(Color.rgb(85, 26, 139));
                    break;
                case 5:
                    this.setTextColor(Color.rgb(139, 28, 98));
                    break;
                case 6:
                    this.setTextColor(Color.rgb(238, 173, 14));
                    break;
                case 7:
                    this.setTextColor(Color.rgb(47, 79, 79));
                    break;
                case 8:
                    this.setTextColor(Color.rgb(71, 71, 71));
                    break;
                case 9:
                    this.setTextColor(Color.rgb(205, 205, 0));
                    break;
            }
        }
    }

    // set tile as a mine underneath
    public void plantMine()
    {
        isMined = true;
    }

    // mine was opened
    // change the tile icon and color
    public void triggerMine()
    {
        setMineIcon(true);
        this.setTextColor(Color.BLACK);
    }

    // is tile still covered
    public boolean isCovered()
    {
        return isCovered;
    }

    // does the tile have any mine underneath
    public boolean hasMine()
    {
        return isMined;
    }

    // set number of nearby mines
    public void setNumSurrounding(int number)
    {
        numSurrounding = number;
    }

    // get number of nearby mines
    public int getNumberOfMinesInSorrounding()
    {
        return numSurrounding;
    }

    // is tile marked as flagged
    public boolean isFlagged()
    {
        return isFlagged;
    }

    // mark tile as flagged
    public void setFlagged(boolean flagged)
    {
        isFlagged = flagged;
    }

    // is tile marked as a question mark
    public boolean isQuestionMarked()
    {
        return isQuestionMarked;
    }

    public boolean isClickable()
    {
        return isClickable;
    }

    // disable tile for receive click events
    public void setClickable(boolean clickable)
    {
        isClickable = clickable;
    }
}
