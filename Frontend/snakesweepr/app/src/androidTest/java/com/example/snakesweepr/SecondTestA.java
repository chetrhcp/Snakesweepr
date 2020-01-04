package com.example.snakesweepr;



import android.support.test.filters.LargeTest;

import android.support.v4.app.Fragment;
import android.widget.FrameLayout;


import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;



import androidx.test.rule.ActivityTestRule;
import game.CreateCharacter2;
import game.Game;
import game.GameView2;

@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SecondTestA {
    GameView2 t3;
    Bitmap snakepiece ;
    @Rule public final ActivityTestRule<Game> main = new ActivityTestRule<>(Game.class);
    private Game mActivity = null;

    @Before
    public void setUp() throws Exception {
        t3 = new GameView2(mActivity);
        mActivity =main.getActivity();
    }
    @Test
    public void testLaunchSingUpScreen(){
        CreateCharacter2 car = new CreateCharacter2(t3, snakepiece );

       }
    @After
    public void tearDown() throws Exception {
        mActivity= null;
    }

}