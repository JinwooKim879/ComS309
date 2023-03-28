package com.example.cytinerary;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cytinerary.app.AppController;
import com.example.cytinerary.net_utils.Const;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class represents the login activity. Users begin here in the app and must provide valid login credentials to
 * access the Dashboard and other features of the app. Users that have not already made an account can click register
 * and will be taken to the registration activity to create a new account.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private ProgressDialog pDialog;

    // static?
    public static String user_regID = "";
    public static String user_password = "";

    public EditText l_password;
    public EditText l_regID;

    /**
     * This onCreate() methods creates the view for the Login page. The Login page should be the first thing a user
     * sees when opening the app. On this page a user will also have a chance to go to the registration page if they
     * aren't currently in the user list. A successful login on this page will take you to the Dashboard.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.login_layout);

            AppController.setContext(this);

            Button logB = findViewById(R.id.btn_login);
            Button regB = findViewById(R.id.btn_register);

            l_password = findViewById(R.id.input_password);
            l_regID = findViewById(R.id.input_ID);

             pDialog = new ProgressDialog(this);
             pDialog.setMessage("Loading...");
             pDialog.setCancelable(false);

            logB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // verify that login is valid;
                    makeJsonObjReq_LogIn();

                    // delay
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {

                        }
                    }, 30000);
                }
            });

            regB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                }
            });
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
     * This method sends an login request to the server which will then authenticate whether or not the login
     * information provided was valid. If it was valid, the server also says whether the login was for a student
     * or an instructor.
     * */
    public void makeJsonObjReq_LogIn() {

        showProgressDialog();

        Map<String, String> params = new HashMap<>();

        user_password = l_password.getText().toString();
        user_regID = l_regID.getText().toString();

        // Setting the id stored in the AppController to this id
        AppController.setId(user_regID);

        params.put("regId", user_regID);
        params.put("password", user_password);

        JsonObjectRequest jsonObjReq_LogIn = new JsonObjectRequest(Request.Method.POST,
                Const.URL_LOGIN, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        pDialog.hide(); //maybe at end?
                        String r ="";

                        try {
                            r += response.get("name").toString();
                            //"type"?
                            Log.d("name",r);

                        }catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                        if(r.equals("student")|| r.equals("instructor") || r.equals("ta")){
                            if(r.equals("instructor") ||  r.equals("ta")){
                                AppController.instructor = true;
                            }

                            else {
                                AppController.instructor = false;
                            }

                            onLoginSuccess();

                        }else{
                            onLoginFailed();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                error.printStackTrace();
                hideProgressDialog();

            }
        });

        String tag_json_obj_user = "j_obj_u req";
        AppController.getInstance().addToRequestQueue(jsonObjReq_LogIn,
                tag_json_obj_user);
    }

    @Override
    public void onBackPressed(){
        moveTaskToBack(true);
    }

    /**
     * Void method that sets boolean loggedIn, located in the AppController, to true signalling that the login
     * was valid (this allows the user to move on to the Dashboard after leaving the login activity).
     * */
    public void onLoginSuccess(){
        AppController.loggedIn = true;
        startActivity(new Intent(LoginActivity.this, Dashboard.class));
    }

    /**
     * Void method that causes a toast popup to appear notifying the user that the login information they provided
     * was invalid and therefore their login attempt failed. This will not allow the user to move on to the
     * Dashboard until correct login info is provided.
     */
    public void onLoginFailed(){
        Toast.makeText(getBaseContext(), "Login Failed", Toast.LENGTH_LONG).show();
    }
}
