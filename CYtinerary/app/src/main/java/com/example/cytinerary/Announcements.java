package com.example.cytinerary;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cytinerary.app.AppController;
import com.example.cytinerary.net_utils.Const;
import com.example.cytinerary.net_utils.fetchAnnouncements;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class represents the Announcements activity in which the list of all Announcements for the course selected
 * on the Dashboard will be listed/displayed. Announcements are only posted by instructors, but both student and
 * instructor users are able to view the list. Clicking on an announcement will take you to a new activity that
 * will only display information for that specific announcement.
 */
public class Announcements extends Activity implements View.OnClickListener {

    private static String TAG = Announcements.class.getSimpleName();
    private ProgressDialog pDialog;

    public static ListView announcements_list;

    public static String announcementtitle = "";
    public static String announcementcontent = "";

    public TextInputEditText text_edit_ann_title;
    public TextInputEditText text_edit_ann_content;

    // The array list holding loaded announcements
    ArrayList<Announcements.Announcement> announcements;


    /**
     * Class representing an Announcement object. Implements Parcelable to allow the movement of these
     * objects between activities (going from Announcements page to the single Announcement post page).
     * These objects can be constructed using either a JSON Object or a parcel containing the corresponding
     * fields.
     * Object contains 4 strings which account for the information of an Announcement post (course code, title,
     * due date of announcement, and a brief description).
     * These objects are only created by Instructor users when they are accessing the Announcements activity page.
     */
    // Class representing an Announcement object, should extend Parcelable eventually
    public static class Announcement implements Parcelable {
        String course_code, title, date, content, writerid;

        public static final Announcements.AnnouncementCreator CREATOR = new Announcements.AnnouncementCreator();

        /**
         * Announcement constructor that takes in a JSON Object and creates an Announcement object with fields
         * matching those found in the JSON Object.
         * @param post
         * @throws JSONException
         */
        // Constructor that takes a json object and makes an announcement object
        public Announcement(JSONObject post) throws JSONException {

            // Need to do an if-check, if an attribute is an empty string, the array shows null
            // This seems to cause crashes when loading the list of announcements (applies to discussions too)
            course_code = (String) post.get("coursecode");
            title = (String) post.get("announcementtitle");
            date = (String) post.get("announcementdate");
            content = (String) post.get("announcementcontent");
            writerid = (String) post.get("writerid");
        }

        /**
         * Announcement constructor that constructs an Announcement object using a source Parcel from another activity.
         * The created Announcement object will have its field set to those of the corresponding fields found in the
         * Parcel.
         * @param source
         */
        // Constructor for AnnouncementCreator, part of Parcelling
        public Announcement(Parcel source){
            Log.v(TAG, "ParcelData(Parcel source): time to put back parcel data");
            course_code = source.readString();
            title = source.readString();
            date = source.readString();
            content = source.readString();
            writerid = source.readString();
        }

        /**
         * Method that describes the contents of a Parcellable Announcement object (returning the hashcode).
         * @return int (hashcode)
         */
        // Is this right?
        @Override
        public int describeContents() {
            return hashCode();
        }

        /**
         * Method used to write an Announcement object into the Parcel format
         * @param dest
         * @param flags
         */
        // Parcelling elements of the Announcement object
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            Log.v(TAG, "writeToParcel..."+ flags);
            dest.writeString(course_code);
            dest.writeString(title);
            dest.writeString(date);
            dest.writeString(content);
            dest.writeString(writerid);
        }

        /**
         * method that gets the course code for this Announcement object. Returns a String.
         * @return String
         */
        String getCoursecode() {
            return course_code;
        }

        /**
         * method that gets the announcement title for this Announcement object. Returns a String.
         * @return String
         */
        String getAnnouncementtitle() {
            return title;
        }

        /**
         * method that gets the due date for this Announcement object. Returns a String.
         * @return String
         */
        String getDate() {
            return date;
        }

        /**
         * method that gets the description for this Announcement object. Returns a String.
         * @return String
         */
        String getContent() {
            return content;
        }

        /**
         * method that gets the writer id for this Announcement object. Returns a String.
         * @return String
         */
        String getWriterid() {
            return writerid;
        }
    }

    /**
     * Creator used in the parcelling process
     */
    // Creator used in Parcelling process
    public static class AnnouncementCreator implements Parcelable.Creator<Announcements.Announcement> {
        /**
         * method that creates an Announcement object from a Parcel
         * @param source
         * @return Announcement
         */
        public Announcements.Announcement createFromParcel(Parcel source) {
            return new Announcements.Announcement(source);
        }
        public Announcements.Announcement[] newArray(int size) {
            return new Announcements.Announcement[size];
        }
    }

    /**
     * Class that represents the ArrayAdapter that converts an Announcement object into a View object.
     * This is used to construct the ListView seen on this page's layout.
     */
    // Class representing the adapter that converts a Announcement object into a View object
    // for use in the ListView on the page
    public static class AnnouncementAdapter extends ArrayAdapter<Announcements.Announcement> {
        public AnnouncementAdapter(Context context, ArrayList<Announcements.Announcement> announcements) {
            super(context, 0, announcements);
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
            Announcements.Announcement announcement = getItem(position);

            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_announcement, parent, false);
            }

            // CourseCode, PostTitle, PostDate, PostContent
            TextView tvACC = (TextView) convertView.findViewById(R.id.tvAnnCC);
            TextView tvAT = (TextView) convertView.findViewById(R.id.tvAnnT);
            TextView tvDD = (TextView) convertView.findViewById(R.id.tvAnnD);
            TextView tvD = (TextView) convertView.findViewById(R.id.tvAnnC);

            // String concatenations
            String course_code = "Course: " + announcement.course_code;
            String announcement_title = "Title: " + announcement.title;
            String due_date = "Date Posted: " + announcement.date;
            String description = "Content: " + announcement.content;

            // Setting the TextView values for each
            tvACC.setText(course_code);
            tvAT.setText(announcement_title);
            tvDD.setText(due_date);
            tvD.setText(description);

            return convertView;
        }
    }

    /**
     * This onCreate() methods creates the view for the Announcement activity when the Announcements button is pressed
     * when the user is in the Course Page activity.
     * The created view differs based on whether a student or an instructor is logged in. Only instructors will have
     * the ability to post announcements (giving them a title, due date, and a description). Students on this page
     * will simply see the list of all announcements. Clicking on an announcement item will load that a page displaying
     * that single announcement.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.announcements_layout);

        AppController.setContext(this);

        Button btnJsonObj_Post_Ann = findViewById(R.id.btnJsonObj_Post_Ann);

        text_edit_ann_title = findViewById(R.id.text_edit_ann_title);
        text_edit_ann_content = findViewById(R.id.text_edit_ann_content);

        announcements_list = findViewById(R.id.fetched_announcements);

        // Hide elements from non-instructor users (only instructor can post announcements)
        if(!AppController.instructor)
        {
            btnJsonObj_Post_Ann.setVisibility(View.GONE);
            text_edit_ann_title.setVisibility(View.GONE);
            text_edit_ann_content.setVisibility(View.GONE);
        }

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        btnJsonObj_Post_Ann.setOnClickListener(this);

        // Making an onClickListener for the announcement list, each individual post should be clickable
        announcements_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parentView, View v, int position, long id) {

                // Small text popup that displays Announcement + post's position in ListView
                Toast.makeText(getApplicationContext(),"Announcement " + position, Toast.LENGTH_LONG).show();

                // Get the announcement array list item that corresponds to the item that was clicked
                // Store that item into d
                announcements = fetchAnnouncements.getAL();

                // Putting announcementss and position into a bundle
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                bundle.putParcelableArrayList("announcements",announcements);

                // Create intent, store announcement array list in bundle, and start the individual post's activity
                Intent i = new Intent(Announcements.this, Announce_Post.class);
                i.putExtras(bundle);
                startActivity(i);
            }
        });

        // Loading the announcement array upon opening the Announcements page
        fetchAnnouncements process = new fetchAnnouncements(); // Async Task
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
     * This method sends an instructor's request to post an announcement to the server.
     * If accepted, the new announcement post will be added to the JSON Array and will be viewable on the
     * ListView displayed on this page's View.
     * */
    private void makeJsonObjReq_Post() {
        showProgressDialog();

        Map<String, String> params = new HashMap<>();

        Date date = new Date();
        String simple_date = new SimpleDateFormat("yyyy-MM-dd").format(date);

        announcementtitle = text_edit_ann_title.getText().toString();
        announcementcontent = text_edit_ann_content.getText().toString();

        params.put("coursecode", AppController.getCoursecode());
        params.put("announcementtitle", announcementtitle);
        params.put("announcementdate", simple_date);
        params.put("announcementcontent", announcementcontent);
        params.put("writerid", AppController.getId());

        JsonObjectRequest jsonObjReq_Post = new JsonObjectRequest(Request.Method.POST,
                Const.URL_ANNOUNCEMENTS, new JSONObject(params),
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
                params.put("announcementtitle", announcementtitle);
                params.put("announcementdate", simple_date);
                params.put("announcementcontent", announcementcontent);
                params.put("writerid", AppController.getId());
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
            case R.id.btnJsonObj_Post_Ann:
                makeJsonObjReq_Post(); // Volley

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // Refresh the announcement array 2 seconds after adding a new post
                        fetchAnnouncements process = new fetchAnnouncements(); // Async Task
                        process.execute();
                    }
                }, 2000);

                break;
        }
    }
}
