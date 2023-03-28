package com.example.cytinerary.net_utils;

import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import com.example.cytinerary.Assign_Post;
import com.example.cytinerary.R;

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

import static com.example.cytinerary.app.AppController.getContext;
import static com.example.cytinerary.net_utils.Const.URL_UPLOAD;

/**
 * This class represents the fetcher for student Uploads. Built using AsyncTask. This is used to get the JSON Arrays
 * from the url related to Uploads. Used in the single Assignment post activity when in instructor view.
 */
public class fetchUploads extends AsyncTask<Void, Void, Void> {

    private String uploads = "";
    private static ArrayList<String> uploads_a_list;
    private ArrayAdapter<String> adapter;

    /**
     * The AsyncTask request that gets the array and converts it into an ArrayList of strings corresponding to urls.
     * @param voids
     * @return
     */
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL url = new URL(URL_UPLOAD);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";

            while (line != null) {
                line = bufferedReader.readLine();
                uploads = uploads + line;
            }

            JSONArray JA = new JSONArray(uploads);
            uploads_a_list = new ArrayList<>();

            for (int i = 0; i < JA.length(); i++) {
                JSONObject JO = (JSONObject) JA.get(i);
                String upload = JO.get("fileUrl").toString();
                uploads_a_list.add(upload);
            }

            adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,uploads_a_list);
            //adapter = new ArrayAdapter<>(getContext(), R.layout.item_upload,uploads_a_list);

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

        Assign_Post.uploads.setAdapter(adapter);
    }

    /**
     * Getter for the ArrayList containing fetched strings corresponding to links for uploads
     * @return
     */
    // Get the array list of dimensions
    public static ArrayList<String> getAL() {
        return uploads_a_list;
    }
}