package com.example.cytinerary.net_utils;

import android.os.AsyncTask;

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

import static com.example.cytinerary.net_utils.Const.URL_USER;

public class fetchUser extends AsyncTask<Void, Void, Void> {

    private String users = "";

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL url = new URL(URL_USER);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";

            while (line != null) {
                line = bufferedReader.readLine();
                users = users + line;
            }

            JSONArray JA = new JSONArray(users);

            for (int i = 0; i < JA.length(); i++) {
                JSONObject JO = (JSONObject) JA.get(i);

                if (JO.get("regId").equals(AppController.getId())) {
                    AppController.setName((String) JO.get("name"));
                    break;
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
