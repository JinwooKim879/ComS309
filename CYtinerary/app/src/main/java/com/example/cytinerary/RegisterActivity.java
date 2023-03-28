package com.example.cytinerary;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
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
 * This class represents the register activity. Here users can choose to create a new student or instructor account.
 * After typing in the information for their new account, a request is sent to the server and the account is added
 * to the user array. The new account is then logged in and taken to the Dashboard.
 */
public class RegisterActivity extends AppCompatActivity {

    private static String TAG =  RegisterActivity.class.getSimpleName();
    private ProgressDialog pDialog;

    public static String user_name = "";
    public static String user_password = "";
    public static String user_type = "Student";
    public static String user_regID = "";

    public EditText u_register_name;
    public EditText u_register_password;
    public EditText u_register_regID;
    public CheckBox u_register_type;

    public static class User {
        String name;
        String password;
        String type;
        String regID;

        /**
         * User constructor that takes in a JSON Object and creates a User object with fields
         * matching those found in the JSON Object.
         * @param user
         * @throws JSONException
         */
        public User(JSONObject user) throws JSONException{
            name =  (String) user.get("name");
            password = (String) user.get("password");
            type =  (String) user.get("type");
            regID = (String) user.get("regId");
        }
    }

    /**
     * This onCreate() methods creates the view for the Registration page. A user can register as a student or an
     * instructor on this page. Following registration, a login is performed, and the user is taken to the Dashboard.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        AppController.setContext(this);

        Button regAcc = findViewById(R.id.btn_createAccount);
        final CheckBox isTeacher = findViewById(R.id.register_type);

        u_register_name = findViewById(R.id.register_name);
        u_register_password = findViewById(R.id.register_password);
        u_register_type = findViewById(R.id.register_type);
        u_register_regID = findViewById(R.id.register_regID);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        isTeacher.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (isTeacher.isChecked()) {
                    user_type = "Instructor";
                }//else {
                   // user_type += "Student";
                //}
            }

        });

        regAcc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // make the json request to add a new user
                makeJsonObjReq_NewUser();

                //delay after register
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //redirect back to login
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    }
                }, 2000);
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
     * This method sends an register request to the server which will then add the newly created user's information
     * to the user array.
     * */
    private void makeJsonObjReq_NewUser() {

        showProgressDialog();

        Map<String, String> params = new HashMap<>();

        user_name = u_register_name.getText().toString();
        user_password = u_register_password.getText().toString();
        user_regID = u_register_regID.getText().toString();
        //user type needs to be converted to "teacher or "student"
        //user_type = register_type.getText().toString();

        // Setting the id stored in the AppController to this id
        AppController.setId(user_regID);

        params.put("name", user_name);
        params.put("password", user_password);
        params.put("type", user_type);
        params.put("regId", user_regID);

        JsonObjectRequest jsonObjReq_NewUser = new JsonObjectRequest(Method.POST,
                Const.URL_USER, new JSONObject(params),
                new Listener<JSONObject>() {

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
        });

        String tag_json_obj_user = "j_obj_u req";
        AppController.getInstance().addToRequestQueue(jsonObjReq_NewUser,
               tag_json_obj_user);
    }
}
