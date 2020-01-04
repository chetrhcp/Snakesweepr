package com.example.snakesweepr;

import android.content.ComponentName;
import android.content.Intent;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class SettingsTest {
    @Rule
    public ActivityTestRule<settings>
            mSettingsRule = new ActivityTestRule<>(settings.class, true,
            false);

    @Rule
    public ActivityTestRule<homeLobby>
            mLobbyRule = new ActivityTestRule<>(homeLobby.class, true,
            false);

    @Before
    public void intentBefore() {
        Intent startIntent = new Intent();
        mSettingsRule.launchActivity(startIntent);

    }

    @Test
    public void settings_DisplayUi() throws Exception {
        onView(withId(R.id.buttonUser)).check(matches(withText("User")));
        onView(withId(R.id.buttonGame)).check(matches(withText("Game")));
        onView(withId(R.id.buttonMore)).check(matches(withText("About the Game")));
        onView(withId(R.id.buttonBack)).check(matches(withText("Back")));
    }

    @Test
    public void settings_showMore(){
        onView(withId(R.id.settingsBio)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.buttonMore)).perform(click());
        onView(withId(R.id.settingsBio)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void settings_back(){
        onView(withId(R.id.buttonBack)).perform(click());
    }

    @After
    public void intentAfter(){
        Intent endIntent = new Intent();
        mLobbyRule.launchActivity(endIntent);
    }
}