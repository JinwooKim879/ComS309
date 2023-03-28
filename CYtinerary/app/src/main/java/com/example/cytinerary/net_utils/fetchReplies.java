package com.example.cytinerary.net_utils;

import android.os.AsyncTask;

import com.example.cytinerary.Disc_Post;
import com.example.cytinerary.Discussions;
import com.example.cytinerary.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.example.cytinerary.net_utils.Const.URL_DISCUSSION_REPLY;

/**
 * This class represents the fetcher for Discussions. Built using AsyncTask. This is used to get the JSON Arrays from
 * the url related to Discussions. Used in the Discussions activity.
 */
public class fetchReplies extends AsyncTask<Void, Void, Void> {

    private String replies ="";
    private static ArrayList<Discussions.Discussion> replies_a_list;
    private Discussions.DiscussionAdapter adapter;
    private String cc = AppController.getCoursecode();

    /**
     * The AsyncTask request that gets the array and converts it into an ArrayList of parcellable Discussion objects.
     * @param voids
     * @return
     */
    @Override
    protected Void doInBackground(Void... voids) {

        try {
            URL url = new URL(URL_DISCUSSION_REPLY + "/" + AppController.getDiscTitle());
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";

            while(line != null) {
                line = bufferedReader.readLine();
                replies = replies + line;
            }

            JSONArray JA = new JSONArray(replies);
            replies_a_list = new ArrayList<>();

            for(int i = 0; i < JA.length(); i++) {
                JSONObject JO = (JSONObject) JA.get(i);

                // Check if coursecode is null (will be wrapped in type JSONObject.NULL) in JO
                if (JO.get("coursecode") != JSONObject.NULL) {

                    // Check if c matches the cc currently stored in the AppController
                    // i.e., we only want posts related to the currently selected course
                    if (JO.get("coursecode").equals(cc)) {

                        // Check all other attributes for null and if nothing is null
                        if (JO.get("discussiontitle") != JSONObject.NULL &&
                                JO.get("discussiondate") != JSONObject.NULL &&
                                JO.get("discussioncontent") != JSONObject.NULL &&
                                JO.get("writerid") != JSONObject.NULL) {

                            // This is a JO that we want, add it to the ArrayList
                            Discussions.Discussion reply = new Discussions.Discussion(JO);
                            replies_a_list.add(reply);
                        }
                    }
                }
            }

            adapter = new Discussions.DiscussionAdapter(AppController.getContext(),replies_a_list);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Execute method for AsyncTask fetching.
     * @param aVoid
     */
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        Disc_Post.replies_list.setAdapter(adapter);
    }

    /**
     * Getter for the ArrayList containing fetched Discussion objects
     * @return
     */
    // Get the array list of discussions
    public static ArrayList<Discussions.Discussion> getAL() {
        return replies_a_list;
    }
}