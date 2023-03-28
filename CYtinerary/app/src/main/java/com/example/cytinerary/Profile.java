package com.example.cytinerary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.cytinerary.app.AppController;

public class Profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);
        //display user id
        TextView t = (TextView) findViewById(R.id.userID);
        String user = LoginActivity.user_regID;
        t.setText("User ID: "+user);
        //display user password
        TextView t2 = (TextView) findViewById(R.id.password);
        String pass =  LoginActivity.user_password;
        t2.setText("User Password: "+ pass);
        //display instructor
        TextView t3 = (TextView) findViewById(R.id.instructorIs);
        if(AppController.instructor) {
            t3.setText("Instructor");
        }
        else{
            t3.setText("Student");
        }
        TextView t4 = (TextView) findViewById(R.id.userName);
        String name = AppController.getName();
        t4.setText(""+ name);




    }
}
