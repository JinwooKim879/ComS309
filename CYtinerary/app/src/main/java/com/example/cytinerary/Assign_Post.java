package com.example.cytinerary;

import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cytinerary.app.AppController;
import com.example.cytinerary.net_utils.Const;
import com.example.cytinerary.net_utils.fetchAssignments;
import com.example.cytinerary.net_utils.fetchUploads;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.mock.web.MockMultipartFile;

import static android.content.Intent.CATEGORY_OPENABLE;
import static android.content.Intent.makeMainActivity;
import static com.example.cytinerary.net_utils.Const.URL_DOWNLOAD;

/**
 * This class represents the activity displaying a single post that has been selected. The view on this page
 * varies based on the user type. Instructors here can see a list of uploaded files (submissions), while students
 * can select and upload a file.
 */
public class Assign_Post extends AppCompatActivity {

    public static ListView uploads;

    public static final int ACTIVITY_CHOOSE_FILE = 1;

    public File file;
    public String filePath, fileName = "";
    public boolean file_selected = false;

    /**
     * This method is called when a user is on the Assignments page and clicks on one of the Assignments in the
     * scrollable list there. This method then creates the layout for viewing a single assignment by parcelling the
     * selected assignment and unparcelling and using its contents to create the layout for this activity. On this page
     * students can upload their submission for the selected assignment.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assign_post_layout);

        AppController.setContext(this);

        TextView assignCC = findViewById(R.id.assignCC);
        TextView assignAT = findViewById(R.id.assignAT);
        TextView assignDD = findViewById(R.id.assignDD);
        TextView assignD = findViewById(R.id.assignD);

        Button select_file = findViewById(R.id.btn_select_file);

        Button submit_file = findViewById(R.id.btn_submit_file);

        TextView uploads_below = findViewById(R.id.uploads_below);
        TextView u_line1 = findViewById(R.id.u_line1);
        TextView u_line2 = findViewById(R.id.u_line2);
        TextView u_line3 = findViewById(R.id.u_line3);

        uploads = findViewById(R.id.fetched_uploads);

        Bundle extras = getIntent().getExtras();
        int position = extras.getInt("position");
        ArrayList<Assignments.Assignment> assignments = extras.getParcelableArrayList("assignments");

        Assignments.Assignment a = assignments.get(position);

        // String concatenations
        String a_course_code = "Course: " + a.course_code;
        String assign_title = "Title: " + a.assignment_title;
        String due_date = "Due Date: " + a.due_date;
        String description = "Description: " + a.description;

        // Setting the TextView values for each item of the assignment
        assignCC.setText(a_course_code);
        assignAT.setText(assign_title);
        assignDD.setText(due_date);
        assignD.setText(description);

        // On Click Listener for the select_file button
        select_file.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // begin working here, trying to prompt student user to select a file
                // once file is selected, the app should make a call to an uploadFile method of some sort
                // and upload that file to the download url + / <filename>

                file_selected = false;

                // Prompting the student to select a file and then getting that file
                search_for_file();
            }
        });

        // On Click Listener for the submit_file button
        submit_file.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // once file is selected, the app should make a call to an uploadFile method of some sort
                // and upload that file to the download url + / <filename>

                // should put file into a multipartfile, and then make a multipartrequest using volley
                //makeMultipartFileReq_Post()
            }
        });

        // On Item Click Listener for the ListView of uploaded files
        uploads.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parentView, View v, int position, long id) {
                ArrayList<String> uploads_a_list = fetchUploads.getAL();
                String url = uploads_a_list.get(position);

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });

        // Hide elements from instructor users (only student should be able to make a file submission)
        if (AppController.instructor) {
            select_file.setVisibility(View.GONE);
            submit_file.setVisibility(View.GONE);
        }

        // Hide elements from non-instructor users (only instructor can view list of files submitted)
        if (!AppController.instructor) {
            uploads_below.setVisibility(View.GONE);
            u_line2.setVisibility(View.GONE);
            uploads.setVisibility(View.GONE);
            u_line3.setVisibility(View.GONE);
        }

        // Run the fetch uploads process
        fetchUploads process = new fetchUploads();
        process.execute();
    }


    /**
     * This method has no parameters and is called when a user on the single assignment post's page clicks the
     * submit file button.
     * It creates an action get content intent and opens the device's local file storage for the user to go
     * through and select what they want to submit for the assignment.
     */
    // Method for prompting user to select a file
    public void search_for_file() {

        Intent chooseFile;
        Intent intent;

        chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("*/*");

        intent = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
    }

    /**
     * This method is called inside the search_for_file() method. It sets the filepath based on which
     * file the user clicked on in the file browser interface. It also stores its name and sets the file_selected
     * flag to true.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    // activity that is called to launch in search_for_file(), this will set the value of file
    // once file is selected, file_selected will be set to true
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTIVITY_CHOOSE_FILE: {
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    filePath = uri.getPath();
                    file = new File(filePath);
                    fileName = file.getName();
                    file_selected = true;
                }
            }
        }
    }
    /*
    // Request
    public class MultipartFileRequest extends Request<String> {

        MultipartFile multipartFile;
        {
            try {
                multipartFile = new MockMultipartFile(file.getName(), new FileInputStream(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private final Response.Listener<String> mListener;

        public MultipartFileRequest(int method, String url, Response.Listener listener, Response.ErrorListener errorListener, File filepart) {
            super(method, url, errorListener);

            mListener = listener;
            file = filepart;
        }

        @Override
        protected Response<String> parseNetworkResponse(NetworkResponse response) {
            return Response.success("Uploaded", getCacheEntry());
        }

        @Override
        protected void deliverResponse(String response) {
            mListener.onResponse(response);
        }
    }

    // Volley request
    private void makeMultipartFileReq_Post() {

        String tag_multipartFile_post_a = "multipartFile_p req";

        String TAG ="";

        MultipartFileRequest multipartFileReq_Post = new MultipartFileRequest(Request.Method.POST,
                Const.URL_DOWNLOAD,
                (Response.Listener<MultipartFile>) response -> {
                    Log.d(TAG, response.toString());
                }, Throwable::printStackTrace,file) {
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(multipartFileReq_Post,
                tag_multipartFile_post_a);

        // Cancelling request
        //AppController.getInstance().getRequestQueue().cancelAll(tag_json_obj);
    }*/
}
