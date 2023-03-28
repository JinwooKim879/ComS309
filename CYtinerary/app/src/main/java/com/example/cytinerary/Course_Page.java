package com.example.cytinerary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cytinerary.app.AppController;

/**
 * This class represents the activity displaying the course page. This page will be slightly different (displaying
 * course code for the course selected on the Dashboard). From here you can go to Announcements, Assignments,
 * or Discussions for the course that you are looking at.
 */
public class Course_Page extends AppCompatActivity {

    /**
     * This method is called when a user clicks the Course Page button on the Dashboard. The method creates the
     * view/layout for this activity. The layout here is fairly simple consisting of buttons for Announcements/
     * Assignments/Discussions.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_page_layout);

        Button ann = findViewById(R.id.btnAnnouncements);
        ann.setText(AppController.getCoursecode() + " Announcements");

        Button assign = findViewById(R.id.btnAssignments);
        assign.setText(AppController.getCoursecode() + " Assignments");

        Button disc = findViewById(R.id.btnDiscussions);
        disc.setText(AppController.getCoursecode() + " Discussions");

        AppController.setContext(this);
    }

    /**
     * Method that creates an intent for the Announcements activity and starts it. Called when the Announcements button
     * on the Course Page is clicked.
     * @param view
     */
    public void Announcements(android.view.View view) {
        Intent startNewActivity = new Intent(this, Announcements.class);
        startActivity(startNewActivity);
    }


    /**
     * Method that creates an intent for the Assignments activity and starts it. Called when the Assignments button
     * on the Course Page is clicked.
     * @param view
     */
    public void Assignments(android.view.View view) {
        Intent startNewActivity = new Intent(this, Assignments.class);
        startActivity(startNewActivity);
    }

    /**
     * Method that creates an intent for the Assignments activity and starts it. Called when the Assignments button
     * on the Course Page is clicked.
     * @param view
     */
    public void Discussions(android.view.View view) {
        Intent startNewActivity = new Intent(this, Discussions.class);
        startActivity(startNewActivity);
    }

    // Overriding the back press, somehow created an infinite loop stemming from Discussion Post
    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        startActivity(new Intent(Course_Page.this, Dashboard.class));
    }
}