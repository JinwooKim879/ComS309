package com.example.cytinerary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.TextView;

import com.example.cytinerary.app.AppController;

import java.util.ArrayList;


/**
 * This class represents the activity displaying a single announcement post that has been selected.
 */
public class Announce_Post extends AppCompatActivity {

    /**
     * This method is called when a user is on the Announcements page and clicks on one of the Announcements in the
     * scrollable list there. This method then creates the layout for viewing a single announcement by parcelling the
     * selected announcement and unparcelling and using its contents to create the layout for this activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.announce_post_layout);

        AppController.setContext(this);

        TextView announceCC = findViewById(R.id.announceCC);
        TextView announceT = findViewById(R.id.announceT);
        TextView announceD = findViewById(R.id.announceD);
        TextView announceC = findViewById(R.id.announceC);

        Bundle extras = getIntent().getExtras();
        int position = extras.getInt("position");
        ArrayList<Announcements.Announcement> announcements = extras.getParcelableArrayList("announcements");

        Announcements.Announcement a = announcements.get(position);

        // String concatenations
        String a_course_code = "Course: " + a.course_code;
        String announce_title = "Title: " + a.title;
        String due_date = "Date: " + a.date;
        String description = "Content: " + a.content;

        // Setting the TextView values for each item of the announcement
        announceCC.setText(a_course_code);
        announceT.setText(announce_title);
        announceD.setText(due_date);
        announceC.setText(description);
    }
}

