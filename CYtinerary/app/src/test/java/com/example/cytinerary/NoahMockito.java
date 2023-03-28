package com.example.cytinerary;

import  android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.cytinerary.app.AppController;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.Robolectric;
//import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
//@Config(manifest= Config.NONE)
public class NoahMockito {

    //@Rule
    //public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void isLoggedinTrue() throws Exception {
        Activity activity = Robolectric.setupActivity(LoginActivity.class);
        ((LoginActivity) activity).onLoginSuccess();
        assertTrue(AppController.loggedIn==true);
    }


    @Test
    public void successfulLoginOnUser() {
    Activity activity = Robolectric.setupActivity(LoginActivity.class);
    ((LoginActivity) activity).onLoginFailed();
    assertEquals(false, AppController.loggedIn);
    }

    @Test
    public void buttonClickNewActivity() {
        AppController.loggedIn=true;

        //Activity activityA = Robolectric.setupActivity(Dashboard.class);
        Activity activityA = Robolectric.buildActivity(Dashboard.class).setup().get();

        Button button = (Button) activityA.findViewById( R.id.button_coursePage );
        button.performClick();
        Intent intent = Shadows.shadowOf(activityA).peekNextStartedActivity();
        assertEquals(Course_Page.class.getCanonicalName(), intent.getComponent().getClassName());
    }

    @Test
    public void isInstructorCheckboxTest() throws Exception{
        Activity activityA =  Robolectric.setupActivity(RegisterActivity.class);
        CheckBox c =  (CheckBox) activityA.findViewById(R.id.register_type);
        c.performClick();
        assertEquals(((RegisterActivity) activityA).u_register_type.isChecked(), c.isChecked());
    }
}



