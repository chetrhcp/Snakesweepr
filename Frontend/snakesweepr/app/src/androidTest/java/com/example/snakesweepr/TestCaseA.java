package com.example.snakesweepr;

import android.support.test.filters.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import game.Game;

@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestCaseA {

//    @Rule
//    public IntentsTestRule mActivityRule =
//            new IntentsTestRule<>(homeLobby.class);
//
//
//    @Before
//    public void setUp() throws Exception {
//
//    }
//
//    @Test
//    public void testEnsureIntentStarted() throws InterruptedException {
//        Espresso.onView(ViewMatchers.withId(R.id.buttonGame)).perform(ViewActions.click());
//        assert( (Game.class.getName()).equals("GameView2"));
//        System.out.print("TEST");
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        //After Test case Execution
//    }
}