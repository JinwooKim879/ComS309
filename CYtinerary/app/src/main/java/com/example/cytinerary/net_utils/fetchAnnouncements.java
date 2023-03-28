package com.example.cytinerary.net_utils;

import android.os.AsyncTask;

import com.example.cytinerary.Announcements;
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

import static com.example.cytinerary.net_utils.Const.URL_ANNOUNCEMENTS;

/**
 * This class represents the fetcher for Announcements. Built using AsyncTask. This is used to get the JSON Arrays from
 * the url related to Announcements. Used in the Announcements activity.
 */
public class fetchAnnouncements extends AsyncTask<Void, Void, Void> {
    private String announcements ="";
    private static ArrayList<Announcements.Announcement> announcements_a_list;
    private Announcements.AnnouncementAdapter adapter;
    private String cc = AppController.getCoursecode();

    /**
     * The AsyncTask request that gets the array and converts it into an ArrayList of parcellable Announcement objects.
     * @param voids
     * @return Void
     */
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL url = new URL(URL_ANNOUNCEMENTS);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";

            while(line != null) {
                line = bufferedReader.readLine();
                announcements = announcements + line;
            }

            JSONArray JA = new JSONArray(announcements);
            announcements_a_list = new ArrayList<>();

            for(int i = 0; i < JA.length(); i++) {
                JSONObject JO = (JSONObject) JA.get(i);

                // Check if coursecode is null (will be wrapped in type JSONObject.NULL) in JO
                if (JO.get("coursecode") != JSONObject.NULL) {

                    // Check if c matches the cc currently stored in the AppController
                    // i.e., we only want posts related to the currently selected course
                    if (JO.get("coursecode").equals(cc)) {

                        // Check all other attributes for null and if nothing is null, add it to ArrayList
                        if (JO.get("announcementtitle") != JSONObject.NULL &&
                                JO.get("announcementdate") != JSONObject.NULL &&
                                JO.get("announcementcontent") != JSONObject.NULL &&
                                JO.get("writerid") != JSONObject.NULL) {

                            // This is a JO that we want, add it to the ArrayList
                            Announcements.Announcement announcement = new Announcements.Announcement(JO);
                            announcements_a_list.add(announcement);
                        }
                    }
                }
            }

            adapter = new Announcements.AnnouncementAdapter(AppController.getContext(),announcements_a_list);

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

        Announcements.announcements_list.setAdapter(adapter);
    }

    /**
     * Getter for the ArrayList containing fetched Announcement objects
     * @return
     */
    // Get the array list of announcements
    public static ArrayList<Announcements.Announcement> getAL() {
        return announcements_a_list;
    }
}
