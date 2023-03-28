package com.example.cytinerary.net_utils;

import android.os.AsyncTask;

import com.example.cytinerary.Assignments;
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

import static com.example.cytinerary.net_utils.Const.URL_ASSIGNMENTS;

/**
 * This class represents the fetcher for Assignments. Built using AsyncTask. This is used to get the JSON Arrays from
 * the url related to Assignments. Used in the Assignments activity.
 */
public class fetchAssignments extends AsyncTask<Void, Void, Void> {

    private String assignments ="";
    private static ArrayList<Assignments.Assignment> assignments_a_list;
    private Assignments.AssignmentAdapter adapter;
    private String cc = AppController.getCoursecode();

    /**
     * The AsyncTask request that gets the array and converts it into an ArrayList of parcellable Assignment objects.
     * @param voids
     * @return
     */
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL url = new URL(URL_ASSIGNMENTS);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";

            while(line != null) {
                line = bufferedReader.readLine();
                assignments = assignments + line;
            }

            JSONArray JA = new JSONArray(assignments);
            assignments_a_list = new ArrayList<>();

            for(int i = 0; i < JA.length(); i++) {
                JSONObject JO = (JSONObject) JA.get(i);

                // Check if coursecode is null (will be wrapped in type JSONObject.NULL) in JO
                if (JO.get("coursecode") != JSONObject.NULL) {

                    // Check if c matches the cc currently stored in the AppController
                    // i.e., we only want posts related to the currently selected course
                    if (JO.get("coursecode").equals(cc)) {

                        // Check all other attributes for null and if nothing is null, add it to ArrayList
                        if (JO.get("assignmenttitle") != JSONObject.NULL &&
                                JO.get("duedate") != JSONObject.NULL &&
                                JO.get("description") != JSONObject.NULL) {

                            // This is a JO that we want, add it to the ArrayList
                            Assignments.Assignment assignment = new Assignments.Assignment(JO);
                            assignments_a_list.add(assignment);
                        }
                    }
                }
            }

            adapter = new Assignments.AssignmentAdapter(AppController.getContext(),assignments_a_list);

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

        Assignments.assignments_list.setAdapter(adapter);
    }

    /**
     * Getter for the ArrayList containing fetched Assignment objects
     * @return
     */
    // Get the array list of assignments
    public static ArrayList<Assignments.Assignment> getAL() {
        return assignments_a_list;
    }
}
