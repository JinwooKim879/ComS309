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
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.example.cytinerary.app.AppController;
import com.example.cytinerary.net_utils.Const;
import com.example.cytinerary.net_utils.fetchDiscussions;

/**
 * This class represents the discussions activity where you can see a list of Discussions corresponding to the class
 * you selected on the dashboard. Any user is able to post here (student/instructor) and by clicking on an individual
 * post in the list you are taken to a separate page where you can reply and view replies to the selected post.
 */
public class Discussions extends Activity implements OnClickListener {

    private static String TAG = Discussions.class.getSimpleName();
    private ProgressDialog pDialog;

    public static ListView discussions_list;

    public static String discussion_title = "";
    public static String discussion_content = "";

    public TextInputEditText text_edit_title;
    public TextInputEditText text_edit_content;

    // The array list holding loaded discussions
    ArrayList<Discussion> discussions;

    /**
     * Class representing an Discussion object. Implements Parcelable to allow the movement of these
     * objects between activities (going from Discussions page to the single Discussion post page).
     * These objects can be constructed using either a JSON Object or a parcel containing the corresponding
     * fields.
     * Object contains 4 strings which account for the information of a Discussion post (course code, title,
     * date of the post, and the content of the post (i.e., whatever the user wanted to say in the post)).
     * These objects can be created by any user on the Discussions activity page or on the Disc_Post activity page.
     * The ones created on the single post view will be treated as replies (filtered out of the main Discussion
     * page, but shown on single post page).
     */
    // Class representing a Discussion object, should extend Parcelable eventually
    public static class Discussion implements Parcelable {
        String coursecode, discussiontitle, discussiondate, discussioncontent, writerid;

        public static final DiscussionCreator CREATOR = new DiscussionCreator();

        /**
         * Discussion constructor that takes in a JSON Object and creates a Discussion object with fields
         * matching those found in the JSON Object.
         * @param post
         * @throws JSONException
         */
        // Constructor that takes a json object and makes a discussion object
        public Discussion(JSONObject post) throws JSONException {

            coursecode = (String) post.get("coursecode");
            discussiontitle = (String) post.get("discussiontitle");
            discussiondate = (String) post.get("discussiondate");
            discussioncontent = (String) post.get("discussioncontent");
            writerid = (String) post.get("writerid");
        }

        /**
         * Discussion constructor that constructs a Discussion object using a source Parcel from another activity.
         * The created Discussion object will have its field set to those of the corresponding fields found in the
         * Parcel.
         * @param source
         */
        // Constructor for DiscussionCreator, part of Parcelling
        public Discussion(Parcel source){
            Log.v(TAG, "ParcelData(Parcel source): time to put back parcel data");
            coursecode = source.readString();
            discussiontitle = source.readString();
            discussiondate = source.readString();
            discussioncontent = source.readString();
            writerid = source.readString();
        }

        /**
         * Method that describes the contents of a Parcellable Discussion object (returning the hashcode).
         * @return
         */
        // Is this right?
        @Override
        public int describeContents() {
            return hashCode();
        }

        /**
         * Method used to write a Discussion object into the Parcel format
         * @param dest
         * @param flags
         */
        // Parcelling elements of the Discussion object
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            Log.v(TAG, "writeToParcel..."+ flags);
            dest.writeString(coursecode);
            dest.writeString(discussiontitle);
            dest.writeString(discussiondate);
            dest.writeString(discussioncontent);
            dest.writeString(writerid);
        }

        /**
         * method that gets the course code for this Discussion object. Returns a string.
         * @return
         */
        String getCoursecode() {
            return coursecode;
        }

        /**
         * method that gets the post title for this Discussion object. Returns a string.
         * @return
         */
        String getDiscussiontitle() {
            return discussiontitle;
        }

        /**
         * method that gets the post date for this Discussion object. Returns a string.
         * @return
         */
        String getDiscussiondate() {
            return discussiondate;
        }

        /**
         * method that gets the post content for this Discussion object. Returns a string.
         * @return
         */
        String getDiscussioncontent() {
            return discussioncontent;
        }

        /**
         * method that gets the post content for this Discussion object. Returns a string.
         * @return
         */
        String getWriterid() {
            return writerid;
        }
    }

    /**
     * Creator used in the parcelling process
     */
    // Creator used in Parcelling process
    public static class DiscussionCreator implements Parcelable.Creator<Discussion> {
        public Discussion createFromParcel(Parcel source) {
            return new Discussion(source);
        }
        public Discussion[] newArray(int size) {
            return new Discussion[size];
        }
    }

    /**
     * Class that represents the ArrayAdapter that converts a Discussion object into a View object.
     * This is used to construct the ListView seen on this page's layout.
     */
    // Class representing the adapter that converts a Discussion object into a View object
    // for use in the ListView on the page
    public static class DiscussionAdapter extends ArrayAdapter<Discussion> {
        public DiscussionAdapter(Context context, ArrayList<Discussion> discussions) {
            super(context, 0, discussions);
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
            Discussion discussion = getItem(position);

            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_discussion, parent, false);
            }

            // CourseCode, PostTitle, PostDate, PostContent
            TextView tvDiscCC = (TextView) convertView.findViewById(R.id.tvDiscCC);
            TextView tvDiscT = (TextView) convertView.findViewById(R.id.tvDiscT);
            TextView tvDiscD = (TextView) convertView.findViewById(R.id.tvDiscD);
            TextView tvDiscC = (TextView) convertView.findViewById(R.id.tvDiscC);

            // String concatenations
            String course_code = "Course: " + discussion.coursecode;
            String discussion_title = "Title: " + discussion.discussiontitle;
            String discussion_date = "Date: " + discussion.discussiondate;
            String discussion_content = "Content: " + discussion.discussioncontent;

            // Setting the TextView values for each
            tvDiscCC.setText(course_code);
            tvDiscT.setText(discussion_title);
            tvDiscD.setText(discussion_date);
            tvDiscC.setText(discussion_content);

            // Hide redundant cc and title (all of them are RE: {title}) when looking at reply list
            // Don't want to hide them if looking at normal discussions though
            if(AppController.getReplyCheck() == 1) {
                tvDiscCC.setVisibility(View.GONE);
                tvDiscT.setVisibility(View.GONE);
            }

            return convertView;
        }
    }

    /**
     * This onCreate() methods creates the view for the Discussions activity when the Discussions button is pressed
     * when the user is in the Course Page activity.
     * Users will see a list of all discussions corresponding to the course they selected on the dashboard.
     * All users are able to post discussions here and are able to select a single Discussion from the ListView on
     * the page in order to reply/view replies to it.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discussions_layout);

        AppController.setContext(this);

        Button btnJsonObj_Post = findViewById(R.id.btnJsonObj_Post);

        discussions_list = findViewById(R.id.fetched_list);

        text_edit_title = findViewById(R.id.text_edit_title);
        text_edit_content = findViewById(R.id.text_edit_content);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        btnJsonObj_Post.setOnClickListener(this);

        // Making an onClickListener for the discussion list, each individual post should be clickable
        discussions_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parentView, View v, int position, long id) {

                // Small text popup that displays Discussion + post's position in ListView
                Toast.makeText(getApplicationContext(),"Discussion " + position, Toast.LENGTH_LONG).show();

                // Get the discussion array list item that corresponds to the item that was clicked
                // Store that item into d
                discussions = fetchDiscussions.getAL();

                // Putting discussions and position into a bundle
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                bundle.putParcelableArrayList("discussions",discussions);

                // Going to be looking at reply list, not discussion list, so set ReplyCheck
                AppController.setReplyCheck(1);

                // Create intent, store discussion array list in bundle, and start the individual post's activity
                Intent i = new Intent(Discussions.this, Disc_Post.class);
                i.putExtras(bundle);
                startActivity(i);
            }
        });

        // Loading the discussion array upon opening the Discussions page
        fetchDiscussions process = new fetchDiscussions(); // Async Task
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
     * This method sends a user's request to post an Discussion to the server.
     * If accepted, the new Discussion post will be added to the JSON Array and will be viewable on the
     * ListView displayed on this page's View.
     * */
    private void makeJsonObjReq_Post() {
        showProgressDialog();

        Map<String, String> params = new HashMap<>();

        Date date = new Date();
        String simple_date = new SimpleDateFormat("yyyy-MM-dd hh:mm::ss").format(date);

        discussion_title = text_edit_title.getText().toString();
        discussion_content = text_edit_content.getText().toString();

        params.put("coursecode", AppController.getCoursecode());
        params.put("discussiontitle", discussion_title);
        params.put("discussiondate", simple_date);
        params.put("discussioncontent", discussion_content);
        params.put("writerid", AppController.getId());

        JsonObjectRequest jsonObjReq_Post = new JsonObjectRequest(Method.POST,
                Const.URL_DISCUSSION, new JSONObject(params),
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
                params.put("coursecode", "CS309");
                params.put("discussiontitle", "test");
                params.put("discussiondate", "2019-2-19");
                params.put("discussioncontent", "test");
                params.put("writerid", "000000000");
                return params;
            }
        };

        // Adding request to request queue
        String tag_json_obj_post = "j_obj_p req";
        AppController.getInstance().addToRequestQueue(jsonObjReq_Post,
                tag_json_obj_post);

        // Cancelling request
        //AppController.getInstance().getRequestQueue().cancelAll(tag_json_obj);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnJsonObj_Post:
                makeJsonObjReq_Post(); // Volley

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // Refresh the discussion array 2 seconds after adding a new post
                        fetchDiscussions process = new fetchDiscussions(); // Async Task
                        process.execute();
                    }
                    }, 2000);

                break;
        }
    }

    // Overriding the back press, somehow created an infinite loop stemming from Discussion Post
    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        startActivity(new Intent(Discussions.this, Course_Page.class));
    }
}
