package com.example.cytinerary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.cytinerary.app.AppController;
import com.example.cytinerary.net_utils.fetchUser;

/**
 * This class represents the activity displaying the Dashboard. On the Dashboard you can select a course from the
 * course list and then go to the course page corresponding to that course. There is also access to the Direct
 * Message activity for sending messages to individual users as well as access to the users profile. A user reaches
 * this page following a successful login and this is basically the "main hub" of the app.
 */
public class Dashboard extends AppCompatActivity {

    public static ListView course_list;

    /**
     * This method is called when a user opens the application. It first checks that if the user is logged
     * in properly, and if not it directs them to the Login activity. If they successfully log in
     * it then creates the Dashboard layout. The user should see a course page button that will take them to the
     * course page corresponding to the course that they select from the list below this button. The view created
     * here also contains buttons that take you to other activities (Direct Message and Profile).
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppController.setContext(this);

            if(!AppController.loggedIn) { //false
                startActivity(new Intent(this, LoginActivity.class));
            }else {

                setContentView(R.layout.dashboard_layout);

                Button b = (Button) findViewById(R.id.button_coursePage);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Dashboard.this, Course_Page.class));
                    }
                });

                Button b2 = (Button) findViewById(R.id.button_directMessage);
                b2.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        startActivity(new Intent(Dashboard.this, DirectMessageActivity.class));
                    }

                });

                Button profile = (Button) findViewById(R.id.button_profile);
                profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Dashboard.this, Profile.class));
                    }
                });

                Button logout = (Button) findViewById(R.id.button_Logout);
                logout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // reset all of the variables in the AppController to their default upon opening app
                        AppController.loggedIn = false;
                        AppController.instructor = false;
                        AppController.setId("");
                        AppController.setName("");
                        AppController.setDiscTitle("");
                        AppController.setReplyCheck(0);
                        AppController.setCoursecode("");

                        // then take user back to Dashboard, but because loggedIn = false
                        // the login activity should launch
                        startActivity(new Intent(Dashboard.this, Dashboard.class));
                    }
                });

                // loading course list
                loadCourseList();

                // Getting the user's name from the user array based on the id that they logged in with
                fetchUser process = new fetchUser();
                process.execute();
            }

    }

    /**
     * Void method that is used to retrieve the course list and fill the ListView on this page's
     * layout. After calling this method, the ListView on the page should have clickable items so
     * you can select which course page you would like to load when clicking the Course Page button.
     */
    // Method for retrieving/updating course list
    public void loadCourseList() {

        // setting course_list
        course_list = findViewById(R.id.course_list);

        // Just examples here for now, will actually load courselist from user eventually
        String[] list = new String[2];
        list[0] = "CS309";
        list[1] = "CS331";

        // Creating adapter for the list
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_selectable_list_item, android.R.id.text1, list);

        course_list.setAdapter(adapter);

        course_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppController.setCoursecode(list[position]);

                Button b = findViewById(R.id.button_coursePage);
                b.setText(list[position] + " Course Page");

            }
        });

        course_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                AppController.coursecode = "CS309";
            }
        });

    }

    // Overriding the back press, somehow created an infinite loop stemming from Discussion Post
    // Here I also added a self loop, because we don't want a user to go from Dashboard back to login
    // without logging out first
    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        startActivity(new Intent(Dashboard.this, Dashboard.class));
    }
}
