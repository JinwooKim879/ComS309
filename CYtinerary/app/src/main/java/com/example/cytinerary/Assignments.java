package com.example.cytinerary;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.example.cytinerary.app.AppController;
import com.example.cytinerary.net_utils.Const;
import com.example.cytinerary.net_utils.fetchAssignments;

/**
 * This class represents the activity displaying the list of all Assignments for the class selected on the Dashboard.
 * This page allows instructors to post, while students are only able to view the list of assignments.
 */
public class Assignments extends Activity implements OnClickListener {

    private static String TAG = Assignments.class.getSimpleName();
    private ProgressDialog pDialog;

    public static ListView assignments_list;

    public static String assignment_title = "";
    public static String assignment_due_date = "";
    public static String assignment_description = "";

    public TextInputEditText text_edit_a_title;
    public TextInputEditText text_edit_due_date;
    public TextInputEditText text_edit_description;

    // The array list holding loaded assignments
    ArrayList<Assignment> assignments;


    /**
     * Class representing an Assignment object. Implements Parcelable to allow the movement of these
     * objects between activities (going from Assignments page to the single Assignment post page).
     * These objects can be constructed using either a JSON Object or a parcel containing the corresponding
     * fields.
     * Object contains 4 strings which account for the information of an Assignment post (course code, title,
     * due date of assignment, and a brief description).
     * These objects are only created by Instructor users when they are accessing the Assignments activity page.
     */
    // Class representing an Assignment object, should extend Parcelable eventually
    public static class Assignment implements Parcelable {
        String course_code, assignment_title, due_date, description;

        public static final AssignmentCreator CREATOR = new AssignmentCreator();

        /**
         * Assignment constructor that takes in a JSON Object and creates an Assignment object with fields
         * matching those found in the JSON Object.
         * @param post
         * @throws JSONException
         */
        // Constructor that takes a json object and makes an assignment object
        public Assignment(JSONObject post) throws JSONException {

            // Need to do an if-check, if an attribute is an empty string, the array shows null
            // This seems to cause crashes when loading the list of assignments (applies to discussions too)
            course_code = (String) post.get("coursecode");
            assignment_title = (String) post.get("assignmenttitle");
            due_date = (String) post.get("duedate");
            description = (String) post.get("description");
        }

        /**
         * Assignment constructor that constructs an Assignment object using a source Parcel from another activity.
         * The created Assignment object will have its field set to those of the corresponding fields found in the
         * Parcel.
         * @param source
         */
        // Constructor for AssignmentCreator, part of Parcelling
        public Assignment(Parcel source){
            Log.v(TAG, "ParcelData(Parcel source): time to put back parcel data");

            course_code = source.readString();
            assignment_title = source.readString();
            due_date = source.readString();
            description = source.readString();
        }

        /**
         * Method that describes the contents of a Parcellable Assignment object (returning the hashcode).
         * @return int (hashcode)
         */
        // Is this right?
        @Override
        public int describeContents() {
            return hashCode();
        }

        /**
         * Method used to write an Assignment object into the Parcel format
         * @param dest
         * @param flags
         */
        // Parcelling elements of the Assignment object
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            Log.v(TAG, "writeToParcel..."+ flags);
            dest.writeString(course_code);
            dest.writeString(assignment_title);
            dest.writeString(due_date);
            dest.writeString(description);
        }

        /**
         * method that gets the course code for this Assignment object. Returns a String.
         * @return String
         */
        String getCoursecode() {
            return course_code;
        }

        /**
         * method that gets the assignment title for this Assignment object. Returns a String.
         * @return String
         */
        String getAssignmenttitle() {
            return assignment_title;
        }

        /**
         * method that gets the due date for this Assignment object. Returns a String.
         * @return String
         */
        String getDuedate() {
            return due_date;
        }

        /**
         * method that gets the description for this Assignment object. Returns a String.
         * @return String
         */
        String getDescription() {
            return description;
        }
    }

    /**
     * Creator used in the parcelling process
     */
    // Creator used in Parcelling process
    public static class AssignmentCreator implements Parcelable.Creator<Assignment> {
        /**
         * method that creates an Assignment object from a Parcel
         * @param source
         * @return Assignment
         */
        public Assignment createFromParcel(Parcel source) {
            return new Assignment(source);
        }
        public Assignment[] newArray(int size) {
            return new Assignment[size];
        }
    }

    /**
     * Class that represents the ArrayAdapter that converts an Assignment object into a View object.
     * This is used to construct the ListView seen on this page's layout.
     */
    // Class representing the adapter that converts a Assignment object into a View object
    // for use in the ListView on the page
    public static class AssignmentAdapter extends ArrayAdapter<Assignment> {
        public AssignmentAdapter(Context context, ArrayList<Assignment> assignments) {
            super(context, 0, assignments);
        }

        /**
         * Method that returns the View corresponding to the item that was selected from the list.
         * @param position
         * @param convertView
         * @param parent
         * @return
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            Assignment assignment = getItem(position);

            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_assignment, parent, false);
            }

            // CourseCode, PostTitle, PostDate, PostContent
            TextView tvACC = (TextView) convertView.findViewById(R.id.tvACC);
            TextView tvAT = (TextView) convertView.findViewById(R.id.tvAT);
            TextView tvDD = (TextView) convertView.findViewById(R.id.tvDD);
            TextView tvD = (TextView) convertView.findViewById(R.id.tvD);

            // String concatenations
            String course_code = "Course: " + assignment.course_code;
            String assignment_title = "Title: " + assignment.assignment_title;
            String due_date = "Due Date: " + assignment.due_date;
            String description = "Description: " + assignment.description;

            // Setting the TextView values for each
            tvACC.setText(course_code);
            tvAT.setText(assignment_title);
            tvDD.setText(due_date);
            tvD.setText(description);

            return convertView;
        }
    }

    /**
     * This onCreate() methods creates the view for the Assignment activity when the Assignments button is pressed
     * when the user is in the Course Page activity.
     * The created view differs based on whether a student or an instructor is logged in. Only instructors will have
     * the ability to post assignments (giving them a title, due date, and a description). Students on this page
     * will simply see the list of all assignments. Clicking on an assignment item will load that a page displaying
     * that single assignment.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assignments_layout);

        AppController.setContext(this);

        Button btnJsonObj_Post_A = findViewById(R.id.btnJsonObj_Post_A);

        text_edit_a_title = findViewById(R.id.text_edit_a_title);
        text_edit_due_date = findViewById(R.id.text_edit_due_date);
        text_edit_description = findViewById(R.id.text_edit_description);

        assignments_list = findViewById(R.id.fetched_assignments);

        // Hide elements from non-instructor users (only instructor can post assignments)
        if(!AppController.instructor)
        {
            btnJsonObj_Post_A.setVisibility(View.GONE);
            text_edit_a_title.setVisibility(View.GONE);
            text_edit_due_date.setVisibility(View.GONE);
            text_edit_description.setVisibility(View.GONE);
        }

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        btnJsonObj_Post_A.setOnClickListener(this);

        // Making an onClickListener for the assignment list, each individual post should be clickable
        assignments_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parentView, View v, int position, long id) {

                // Small text popup that displays Assignment + post's position in ListView
                Toast.makeText(getApplicationContext(),"Assignment " + position, Toast.LENGTH_LONG).show();

                // Get the assignment array list item that corresponds to the item that was clicked
                // Store that item into d
                assignments = fetchAssignments.getAL();

                // Putting assignmentss and position into a bundle
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                bundle.putParcelableArrayList("assignments",assignments);

                // Create intent, store assignment array list in bundle, and start the individual post's activity
                Intent i = new Intent(Assignments.this, Assign_Post.class);
                i.putExtras(bundle);
                startActivity(i);
            }
        });

        // Loading the assignment array upon opening the Assignments page
        fetchAssignments process = new fetchAssignments(); // Async Task
        process.execute();
    }

    private void showProgressDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideProgressDialog() {
        if (pDialog.isShowing())
            pDialog.hide();
    }

    /**
     * Making JSON object request (Disc_Post method).
     * This method sends an instructor's request to post an assignment to the server.
     * If accepted, the new assignment post will be added to the JSON Array and will be viewable on the
     * ListView displayed on this page's View.
     * */
    private void makeJsonObjReq_Post() {
        showProgressDialog();

        Map<String, String> params = new HashMap<>();

        assignment_title = text_edit_a_title.getText().toString();
        assignment_due_date = text_edit_due_date.getText().toString();
        assignment_description = text_edit_description.getText().toString();

        params.put("coursecode", AppController.getCoursecode());
        params.put("assignmenttitle", assignment_title);
        params.put("duedate", assignment_due_date);
        params.put("description", assignment_description);

        JsonObjectRequest jsonObjReq_Post = new JsonObjectRequest(Method.POST,
                Const.URL_ASSIGNMENTS, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                error.printStackTrace();
                hideProgressDialog();
            }
        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("coursecode", AppController.getCoursecode());
                params.put("assignmenttitle", "test");
                params.put("duedate", "2019-2-19");
                params.put("description", "test");
                return params;
            }
        };

        // Adding request to request queue
        String tag_json_obj_post_a = "j_obj_p req";
        AppController.getInstance().addToRequestQueue(jsonObjReq_Post,
                tag_json_obj_post_a);

        // Cancelling request
        //AppController.getInstance().getRequestQueue().cancelAll(tag_json_obj);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnJsonObj_Post_A:
                makeJsonObjReq_Post(); // Volley

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // Refresh the assignment array 2 seconds after adding a new post
                        fetchAssignments process = new fetchAssignments(); // Async Task
                        process.execute();
                    }
                }, 2000);

                break;
        }
    }
}

