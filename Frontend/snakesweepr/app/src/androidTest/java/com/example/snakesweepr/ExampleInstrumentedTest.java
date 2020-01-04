package com.example.snakesweepr;

import android.content.Intent;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class ExampleInstrumentedTest {
    @Rule
    public ActivityTestRule<userChat>
            muserChatRule = new ActivityTestRule<>(userChat.class, true,
            false);

    @Before
    public void intentBefore() {
        Intent startIntent = new Intent();
        muserChatRule.launchActivity(startIntent);

    }

    @Test
    public void chat_DisplayUi() throws Exception {
        onView(withId(R.id.btn)).check(matches(withText("Send")));
        onView(withId(R.id.chat_text)).check(matches(withText("")));
    }

    @Test
    public void chat_SendTest(){
        onView(withId(R.id.chat_text)).perform(typeText("howdy"), closeSoftKeyboard());
        onView(withId(R.id.btn)).perform(click());

        //onView(withId(R.id.chat_text)).check(matches(withText(" ")));
    }
}


