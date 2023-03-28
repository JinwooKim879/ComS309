package com.example.cytinerary;

import android.os.Bundle;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * This class represents the direct message activity. You arrive on this page by clicking the direct message button
 * on the dashboard. From here you can select a user to send a message to and type up a message to send them.
 */
public class DirectMessageActivity extends AppCompatActivity {

    public Button btn_send_dm, btn_connect;
    public EditText title, recpID;
    String senderID, senderPassword;
    public TextView txt1;


    private WebSocketClient cc;

    /**
     * This onCreate() methods creates the view for the Direct Message activity when the DM button is pressed
     * by a user in the Course Page activity.
     * The created view allows users to select an intended receiver and type up a message which is sent to that
     * user's inbox.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_message);
        btn_connect = (Button)findViewById(R.id.btn_connect);
        btn_send_dm = (Button)findViewById(R.id.btn_send_dm);
        title = findViewById(R.id.dmTitle);
        recpID = findViewById(R.id.dmToID);
        txt1 = (TextView)findViewById(R.id.txt1);
        txt1.setMovementMethod(new ScrollingMovementMethod());
        senderPassword = LoginActivity.user_password;
        senderID = LoginActivity.user_regID;


        btn_connect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Draft[] drafts = {new Draft_6455()};
                //http://cs309-rr-3.misc.iastate.edu:8080/websocket/userid/password
                //localhost - 127.0.0.1
               // String w = "ws://cs309-rr-3.misc.iastate.edu:8080/websocket/"+recpID;
                Log.d("user/password", ""+senderID+"/"+senderPassword);
                String w = "ws://cs309-rr-3.misc.iastate.edu:8080/websocket/"+senderID+"/"+senderPassword;
               // String w = "ws://localhost:8080/websocket/"+senderID+"/"+senderPassword;

                try{
                    Log.d("Socket:", "Trying socket");
                    cc = new WebSocketClient(new URI(w), (Draft) drafts[0]) {
                        @Override
                        public void onOpen(ServerHandshake serverHandshake) {
                            Log.d("OPEN", "run() returned: " + "is connecting");
                        }

                        @Override
                        public void onMessage(String s) {
                            Log.d("", "run() returned: " + s);
                                String str = txt1.getText().toString();
                                txt1.setText(str+" Server: " + s+"\n");




                        }

                        @Override
                        public void onClose(int code, String reason, boolean remote) {
                            Log.d("CLOSE", "onClose() returned: " +reason );

                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d("Exception: ", e.toString());

                        }
                    };
                }
                catch (URISyntaxException e){
                    Log.d("Exception:", e.getMessage());
                    e.printStackTrace();
                }
                cc.connect();

            }
        });
        btn_send_dm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                try{
                    //just the title +/d/+ description
                    //format works for dm??
                    String toSend = ""; //*
                    toSend += "DM";
                    toSend += recpID.getText().toString();

                    toSend += " ";
                    toSend += title.getText().toString();
                    Log.d("msg: " , toSend);
                    cc.send(toSend);
                   // cc.send(title.getText().toString());
                }
                catch (Exception e){
                    Log.d("ExceptionSendMessage:", e.getMessage());
                }


            }

        });
    }
}
