package com.example.cytinerary;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;

@RunWith(AndroidJUnit4.class)
public class JamesAndroidMockito {

    // Starting on Course Page
    @Rule
    public ActivityTestRule<Course_Page> menuActivityTestRule =
            new ActivityTestRule<>(Course_Page.class, true, true);

    // Setting things up for testing, initializing Intents
    @Before
    public void setup() {
        Intents.init();
    }

    // Test 3 - Testing a button press to see if CoursePage's Discussions button takes it to the Discussion's page
    @Test
    public void test3() {
        Espresso.onView(ViewMatchers.withId(R.id.btnDiscussions)).perform(ViewActions.click());
        intended(hasComponent(Discussions.class.getName()));
    }
}