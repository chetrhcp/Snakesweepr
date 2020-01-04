package mine;

import java.net.URISyntaxException;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TableRow.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.snakesweepr.R;
import com.example.snakesweepr.comHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class MineSweep extends AppCompatActivity {

    TextView MineCount;
    TableLayout mineField;
    Tile tiles[][];
    int tileDimension = 100; // width of each tile
    int tilePadding = 2; // padding between tiles
    int numRows = 10;//the height of the game map
    int numCol = 10;//the width of the game map
    int GameMines = 10;
    boolean areMinesSet;//variable used to store game state
    boolean isGameOver;//decided by server false until set
    int minesToGo;//the number of mines left to flag
    Button signOut;
    Button returnChat;
    boolean value = true;
    MediaPlayer player;
    comHandler com;
    String username;
    int tileX;
    int tileY;
    String clicktype;
    public int highScore = 10;
    SharedPreferences sharedPref;
    SharedPreferences.Editor sharedEdit;
    int lobbyId;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        sharedPref = this.getSharedPreferences("userInfo",0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_sweep);
        MineCount = (TextView) findViewById(R.id.MineCount);
        mineField = (TableLayout)findViewById(R.id.MineField);

        final Context context = getApplicationContext();
        final SharedPreferences sharedPref = context.getSharedPreferences("userInfo",0);
        username= sharedPref.getString("username", "atobrien");

        if(value) {
            player = MediaPlayer.create(this, R.raw.tune); //select music file
            player.start();
            player.setLooping(true); //set looping
            player.setVolume(100,100);

        }
        try {
            com = new comHandler("cs309-sd-1.misc.iastate.edu",5000, context);
            com.connect();

        } catch (URISyntaxException e) {
            System.out.println("Could not connect to server");
        }


        signOut = (Button) findViewById(R.id.signOut);
        returnChat = (Button) findViewById(R.id.returnChat);
        signOut.setOnClickListener(new View.OnClickListener() {//launches main login

            @Override
            public void onClick(View v) {
                Intent create = new Intent(MineSweep.this, com.example.snakesweepr.MainActivity.class);
                player.stop();
                startActivity(create);
            }
        });
        returnChat.setOnClickListener(new View.OnClickListener() {//launches home lobby

            @Override
            public void onClick(View v) {
                Intent create = new Intent(MineSweep.this, com.example.snakesweepr.userChat.class);
                player.stop();
                startActivity(create);
            }
        });


        sharedEdit  = sharedPref.edit();
        sharedEdit.putInt("snakeScore", highScore);
        sharedEdit.commit();

        lobbyId = sharedPref.getInt("lobbyid", 0);

        startNewGame();
    }

    private void startNewGame()
    {

        editMineField();
        showGameMap();
        minesToGo = GameMines;
        isGameOver = false;
    }

    private void showGameMap()//sets up a grid to show the tiles and their values
    {

        for (int row = 1; row < numRows + 1; row++)
        {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new LayoutParams((tileDimension + tilePadding) * numCol, tileDimension + tilePadding));

            for (int column = 1; column < numCol + 1; column++)
            {
                tiles[row][column].setLayoutParams(new LayoutParams(
                        tileDimension + tilePadding,
                        tileDimension + tilePadding));
                tiles[row][column].setPadding(tilePadding, tilePadding, tilePadding, tilePadding);
                tableRow.addView(tiles[row][column]);
            }

            mineField.addView(tableRow,new TableLayout.LayoutParams(
                    (tileDimension + tilePadding) * numCol, tileDimension + tilePadding));
        }

    }

    private void endExistingGame()
    {
        MineCount.setText("000"); // revert mines count
        // remove all rows from mineField TableLayout
        mineField.removeAllViews();
        // set all variables to support end of game
        areMinesSet = false;
        isGameOver = false;
        minesToGo = 0;
        player.stop();
    }
    public void updateServer(){//uses comhandler to send data to server
        if( com.isClosed()) {

                com.reconnect();
        }
        JSONObject user = new JSONObject();
        JSONObject data = new JSONObject();
        JSONObject tile = new JSONObject();

        try {

            data.put("username",username);
            user.put("type", "update_game");
            user.put("platform", "app");
            tile.put("x",tileX);
            tile.put("y", tileY);
            data.put("tile", tile);
            data.put("click_type", clicktype );
            user.put("lobby_id", lobbyId);
            user.put("data", data);



            com.sendMessage(user.toString());
            System.out.println(user.toString());

        } catch (JSONException e) {
            System.out.println("Could not send data to server");
        }


    }
    private void editMineField()
    {

        tiles = new Tile[numRows + 2][numCol + 2];

        for (int row = 0; row < numRows + 2; row++)
        {
            for (int column = 0; column < numCol + 2; column++)
            {
                tiles[row][column] = new Tile(this);
                tiles[row][column].setDefaults();

                // pass current row and column number as final int's to event listeners
                // this way we can ensure that each event listener is associated to
                // particular instance of tile only
                final int currentRow = row;
                final int currentColumn = column;

                // add Click Listener
                // this is treated as Left Mouse click
                tiles[row][column].setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {

                        tileX = currentColumn;
                        tileY = currentRow;
                        clicktype = "normal";
                        updateServer();
                        System.out.println("Short");
                        // set mines on first click
                        if (!areMinesSet)
                        {
                            areMinesSet = true;
                            setMines(currentRow, currentColumn);
                        }
                        MineCount.setText(minesToGo+"");

                        // this is not first click
                        // check if current  is flagged
                        // if flagged the don't do anything
                        // as that operation is handled by LongClick
                        // if tile is not flagged then uncover nearby tiles
                        // till we get numbered mines
                        if (!tiles[currentRow][currentColumn].isFlagged())
                        {
                            // open nearby tiles till we get numbered tiles
                            uncoverTiles(currentRow, currentColumn);

                            // did we clicked a mine
                            if (tiles[currentRow][currentColumn].hasMine())
                            {
                                // Oops, game over
                                finishGame(currentRow,currentColumn);
                            }

                            // check if we win the game
                            if (checkGameWin())
                            {
                                // mark game as win
                                winGame();
                            }
                        }

                    }
                });

                // add Long Click listener
                // this is treated as right mouse click listener
                tiles[row][column].setOnLongClickListener(new OnLongClickListener()
                {
                    public boolean onLongClick(View view)
                    {
                        // simulate a left-right (middle) click
                        // if it is a long click on an opened mine then
                        // open all surrounding tiles
                        //sets data to send to the server
                        tileX = currentColumn;
                        tileY = currentRow;
                        clicktype = "flag";
                       updateServer();
                        System.out.println("Long");

                        if (!tiles[currentRow][currentColumn].isCovered() && (tiles[currentRow][currentColumn].getNumberOfMinesInSorrounding() > 0) && !isGameOver)
                        {
                            int nearbyFlaggedTiles = 0;
                            for (int previousRow = -1; previousRow < 2; previousRow++)
                            {
                                for (int previousColumn = -1; previousColumn < 2; previousColumn++)
                                {
                                    if (tiles[currentRow + previousRow][currentColumn + previousColumn].isFlagged())
                                    {
                                        nearbyFlaggedTiles++;
                                    }
                                }
                            }

                            // if flagged tile count is equal to nearby mine count
                            // then open nearby tiles
                            if (nearbyFlaggedTiles == tiles[currentRow][currentColumn].getNumberOfMinesInSorrounding())
                            {
                                for (int previousRow = -1; previousRow < 2; previousRow++)
                                {
                                    for (int previousColumn = -1; previousColumn < 2; previousColumn++)
                                    {
                                        // don't open flagged tiles
                                        if (!tiles[currentRow + previousRow][currentColumn + previousColumn].isFlagged())
                                        {
                                            // open tiles till we get numbered tile
                                            uncoverTiles(currentRow + previousRow, currentColumn + previousColumn);

                                            // did we clicked a mine
                                            if (tiles[currentRow + previousRow][currentColumn + previousColumn].hasMine())
                                            {
                                                // oops game over
                                                finishGame(currentRow + previousRow, currentColumn + previousColumn);
                                            }

                                            // did we win the game
                                            if (checkGameWin())
                                            {
                                                // mark game as win
                                                winGame();
                                            }
                                        }
                                    }
                                }
                            }


                            return true;
                        }

                        // if clicked tile is enabled, clickable or flagged
                        if (tiles[currentRow][currentColumn].isClickable() &&
                                (tiles[currentRow][currentColumn].isEnabled() || tiles[currentRow][currentColumn].isFlagged()))
                        {

                            // for long clicks set:
                            // 1. empty tiles to flagged
                            // 2. flagged to blank


                            // case 1. set blank tile to flagged
                            if (!tiles[currentRow][currentColumn].isFlagged() && !tiles[currentRow][currentColumn].isQuestionMarked())
                            {
                                tiles[currentRow][currentColumn].setTileAsDisabled(false);
                                tiles[currentRow][currentColumn].setFlagIcon(true);
                                tiles[currentRow][currentColumn].setFlagged(true);
                                minesToGo--; //reduce mine count
                                updateMineCountDisplay();
                                clicktype = "flag";
                            }
                            // case 2. change to blank square
                            else
                            {
                                tiles[currentRow][currentColumn].setTileAsDisabled(true);
                                tiles[currentRow][currentColumn].clearAllIcons();
                                // if it is flagged then increment mine count
                                if (tiles[currentRow][currentColumn].isFlagged())
                                {
                                    minesToGo++; // increase mine count
                                    updateMineCountDisplay();
                                }
                                // remove flagged status
                                tiles[currentRow][currentColumn].setFlagged(false);
                                clicktype = "unflag";
                            }

                            updateMineCountDisplay(); // update mine display
                        }


                        return true;
                    }
                });
            }
        }
    }
    //returns true on game win
    private boolean checkGameWin()
    {
        for (int row = 1; row < numRows + 1; row++)
        {
            for (int column = 1; column < numCol + 1; column++)
            {
                if (!tiles[row][column].hasMine() && tiles[row][column].isCovered())
                {
                    return false;
                }
            }
        }
        return true;
    }

    protected void updateMineCountDisplay()
    {
        if (minesToGo < 0)
        {
            MineCount.setText(Integer.toString(minesToGo));
        }
        else if (minesToGo < 10)
        {
            MineCount.setText("00" + Integer.toString(minesToGo));
        }
        else if (minesToGo < 100)
        {
            MineCount.setText("0" + Integer.toString(minesToGo));
        }
        else
        {
            MineCount.setText(Integer.toString(minesToGo));
        }
    }

    public void winGame()
    {
        isGameOver = true;
        minesToGo = 0; //set mine count to 0

        updateMineCountDisplay(); // update mine count

        for (int row = 1; row < numRows + 1; row++)
        {
            for (int column = 1; column < numCol + 1; column++)
            {
                tiles[row][column].setClickable(false);
                if (tiles[row][column].hasMine())
                {
                    tiles[row][column].setTileAsDisabled(false);
                    tiles[row][column].setFlagIcon(true);
                }
            }
        }
        Toast.makeText(getApplicationContext(), "YOU WIN!!.", Toast.LENGTH_SHORT).show();
        try {
            wait(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent create = new Intent(MineSweep.this, com.example.snakesweepr.userChat.class);
        player.stop();
        startActivity(create);

    }

    private void finishGame(int currentRow, int currentColumn)
    {
        isGameOver = true; // mark game as over
        player.stop();
        for (int row = 1; row < numRows + 1; row++)
        {
            for (int column = 1; column < numCol + 1; column++)
            {
                // disable tile
                tiles[row][column].setTileAsDisabled(false);

                // tile has mine and is not flagged
                if (tiles[row][column].hasMine() && !tiles[row][column].isFlagged())
                {
                    // set mine icon
                    tiles[row][column].setMineIcon(false);
                }

                // tile is flagged and doesn't not have mine
                if (!tiles[row][column].hasMine() && tiles[row][column].isFlagged())
                {
                    // set flag icon
                    tiles[row][column].setFlagIcon(false);
                }

                // tile is flagged
                if (tiles[row][column].isFlagged())
                {
                    // disable the tile
                    tiles[row][column].setClickable(false);
                }
            }
        }

        // trigger mine
        tiles[currentRow][currentColumn].triggerMine();
    }


    private void setMines(int currentRow, int currentColumn)
    {
        // set mines excluding the location where user clicked
        Random rand = new Random();
        int mineRow, mineColumn;

        for (int row = 0; row < GameMines; row++)
        {
            mineRow = rand.nextInt(numCol);
            mineColumn = rand.nextInt(numRows);
            if ((mineRow + 1 != currentColumn) || (mineColumn + 1 != currentRow))
            {
                if (tiles[mineColumn + 1][mineRow + 1].hasMine())
                {
                    row--; // mine is already there, don't repeat for same tile
                }
                // plant mine at this location
                tiles[mineColumn + 1][mineRow + 1].plantMine();
            }
            // exclude the user clicked location
            else
            {
                row--;
            }
        }

        int nearByMineCount;

        //keeps track o fthe number of mines touching each tile
        for (int row = 0; row < numRows + 2; row++)
        {
            for (int column = 0; column < numCol + 2; column++)
            {
                // for each tile find nearby mine count
                nearByMineCount = 0;
                if ((row != 0) && (row != (numRows + 1)) && (column != 0) && (column != (numCol + 1)))
                {
                    // check in all nearby tiles
                    for (int previousRow = -1; previousRow < 2; previousRow++)
                    {
                        for (int previousColumn = -1; previousColumn < 2; previousColumn++)
                        {
                            if (tiles[row + previousRow][column + previousColumn].hasMine())
                            {
                                // a mine was found so increment the counter
                                nearByMineCount++;
                            }
                        }
                    }

                    tiles[row][column].setNumSurrounding(nearByMineCount);
                }
                // for side rows (0th and last row/column)
                // set count as 9 and mark it as opened
                else
                {
                    tiles[row][column].setNumSurrounding(9);
                    tiles[row][column].OpenTile();
                }
            }
        }
    }

    public void uncoverTiles(int rowClicked, int columnClicked)
    {
        // don't open flagged or mined rows
        if (tiles[rowClicked][columnClicked].hasMine() || tiles[rowClicked][columnClicked].isFlagged())
        {
            return;
        }

        // open clicked tile
        tiles[rowClicked][columnClicked].OpenTile();

        // if clicked tile have nearby mines then don't open further
        if (tiles[rowClicked][columnClicked].getNumberOfMinesInSorrounding() != 0 )
        {
            return;
        }

        // open next 3 rows and 3 columns recursively
        for (int row = 0; row < 3; row++)
        {
            for (int column = 0; column < 3; column++)
            {
                // check all the above checked conditions
                // if met then open subsequent tiles
                if (tiles[rowClicked + row - 1][columnClicked + column - 1].isCovered()
                        && (rowClicked + row - 1 > 0) && (columnClicked + column - 1 > 0)
                        && (rowClicked + row - 1 < numRows + 1) && (columnClicked + column - 1 < numCol + 1))
                {
                    uncoverTiles(rowClicked + row - 1, columnClicked + column - 1 );
                }
            }
        }
        return;
    }

}