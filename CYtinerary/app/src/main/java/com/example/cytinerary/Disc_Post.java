package com.example.cytinerary;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cytinerary.app.AppController;
import com.example.cytinerary.net_utils.Const;
import com.example.cytinerary.net_utils.fetchDiscussions;
import com.example.cytinerary.net_utils.fetchReplies;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.cytinerary.Discussions.discussion_content;

public class Disc_Post extends Activity implements View.OnClickListener {

    private static String TAG = Disc_Post.class.getSimpleName();
    private ProgressDialog pDialog;

    public static ListView replies_list;

    public static String discussion_reply = "";

    public static String original_title = "";

    public TextView title;

    public TextInputEditText text_edit_reply;

    // Discussion object for the page
    Discussions.Discussion d;

    // The array list holding loaded discussions
    ArrayList<Discussions.Discussion> replies;

    /**
     * This method is called when a user is on the Discussions page and clicks on one of the Discussions in the
     * scrollable list there. This method then creates the layout for viewing a single assignment by parcelling the
     * selected assignment and unparcelling and using its contents to create the layout for this activity. On this page
     * users can reply to the selected discussion post and also view any replies that the selected post has.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discussions_post_layout);

        AppController.setContext(this);

        Button btnReply = findViewById(R.id.btnReply);

        replies_list = findViewById(R.id.fetched_replies);

        title = findViewById(R.id.discT);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        btnReply.setOnClickListener(this);

        TextView discCC = findViewById(R.id.discCC);
        TextView discT = findViewById(R.id.discT);
        TextView discD = findViewById(R.id.discD);
        TextView discC = findViewById(R.id.discC);

        text_edit_reply = findViewById(R.id.text_edit_reply);

        Bundle extras = getIntent().getExtras();
        int position = extras.getInt("position");
        ArrayList<Discussions.Discussion> discussions = extras.getParcelableArrayList("discussions");

        d = discussions.get(position);

        // Store the title in the AppController
        AppController.setDiscTitle(d.discussiontitle);

        // String concatenations
        String course_code = "Course: " + d.coursecode;
        String discussion_title = "Title: " + d.discussiontitle;
        String discussion_date = "Date: " + d.discussiondate;
        String discussion_content = "Content: " + d.discussioncontent;

        // Setting the TextView values for each
        discCC.setText(course_code);
        discT.setText(discussion_title);
        discD.setText(discussion_date);
        discC.setText(discussion_content);

        // Loading the reply array upon opening the Discussions page
        fetchReplies process = new fetchReplies(); // Async Task
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

        if(text_edit_reply.getText() != null) {
            discussion_reply = text_edit_reply.getText().toString();
        }

        params.put("coursecode", AppController.getCoursecode());
        params.put("discussiontitle", "RE: " + d.discussiontitle);
        params.put("discussiondate", simple_date);
        params.put("discussioncontent", discussion_reply);
        params.put("writerid", AppController.getId());

        JsonObjectRequest jsonObjReq_Post = new JsonObjectRequest(Request.Method.POST,
                Const.URL_DISCUSSION, new JSONObject(params),
                new Response.Listener<JSONObject>() {
            // Const.URL_DISCUSSION_REPLY + "/" + d.discussiontitle

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
             */
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
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnReply:
                makeJsonObjReq_Post(); // Volley

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // Refresh the reply array 2 seconds after adding a new post
                        fetchReplies process = new fetchReplies(); // Async Task
                        process.execute();
                    }
                }, 2000);

                break;
        }
    }

    // Overriding the back press, necessary for properly resetting the replyCheck in AppController
    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        AppController.setReplyCheck(0);
        startActivity(new Intent(Disc_Post.this, Discussions.class));
    }
}
