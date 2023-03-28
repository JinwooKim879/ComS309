package com.example.cytinerary.app;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import com.example.cytinerary.net_utils.LruBitmapCache;

/**
 * Class representing the AppController for the CYTinerary app.
 */
public class AppController extends Application {

    // Memory leak? Not sure how to fix. Using this to get context while calling AsyncTask fetch
    private static Context mContext;

    /**
     * Getter method for the context of the app which we are storing in the AppController
     * Used to get context while calling AsyncTask fetch classes / methods
     * Returns a Context object.
     * @return
     */
    public static Context getContext()
    {
        return mContext;
    }

    /**
     * Setter method for the context of the app which we are storing in the AppController
     * @return
     */
    public static void setContext(Context mContext) {
        AppController.mContext = mContext;
    }

    // Boolean used for checking if a login has been succesfully verified
    public static boolean loggedIn = false;

    // Potential way to mark a user as instructor for the duration of a session
    // Maybe can be used in the onCreate() method and can be used to decide if something
    // should or should not be created on the page (i.e., buttons etc)
    public static boolean instructor = false;

    // Way to track the id of the user who has logged in successfully
    public static String id = "";

    /**
     * Getter for the id in AppController.
     * This variable is used to keep track of the id of the user who is currently logged in
     * Used in Announcement posting.
     * Returns a string.
     * @return
     */
    public static String getId()
    {
        return id;
    }

    /**
     * Setter for the id in AppController.
     * This method is called in the Login activity.
     */
    public static void setId(String id) {AppController.id = id; }

    // Way to track the name of the user who has logged in successfully
    public static String name = "";

    /**
     * Getter for the user's name in AppController.
     * This variable is used to keep track of the name of the user who is currently logged in
     * Used in Profile activity.
     * Returns a string.
     * @return
     */
    public static String getName()
    {
        return name;
    }

    /**
     * Setter for the name in AppController.
     * This method is called in the Dashboard activity.
     */
    public static void setName(String name) {AppController.name = name; }

    // Way to track the title of the discussion post, used when dealing with replies to said post
    public static String discTitle = "";

    /**
     * Getter for the discussion title in AppController.
     * This variable is used to keep track of the discussion title that the user has clicked and
     * helps with loading in the replies array for the post with this title
     * Returns a string.
     * @return
     */
    public static String getDiscTitle()
    {
        return discTitle;
    }

    /**
     * Setter for the discTitle in AppController.
     * This method is called in the Disc_Post activity.
     */
    public static void setDiscTitle(String discTitle) {AppController.discTitle = discTitle; }

    // Way to track whether we are loading a list of discussions or a list of replies.
    // With replies we want to see less information (lots of redundancy: title is the same for all replies).
    // Default to 0, only set to 1 when loading Disc_Post page, reset on a backPress
    public static int replyCheck = 0;

    /**
     * Getter for the replyCheck value.
     * Used by the Discusssion Array Adapter to tell whether or not it is displaying a list of discussions or replies
     * @return
     */
    public static int getReplyCheck()
    {
        return replyCheck;
    }

    /**
     * Setter for the replyCheck in AppController.
     * This method is called before loading the  Disc_Post activity and after closing it (returning to Discussions).
     */
    public static void setReplyCheck(int val) {AppController.replyCheck = val; }


    // Way to track which course was selected on the dashboard
    // This will be used to affect the appearance of course page and all sub-pages
    // Posts will only be shown that have coursecodes matching the course selected
    public static String coursecode = "";

    /**
     * Getter for the coursecode in AppController.
     * This variable is used to keep track of which course was selected in the Dashboard.
     * Called when loading the Course Page. (Modifies display slightly)
     * Returns a string.
     * @return
     */
    public static String getCoursecode()
    {
        return coursecode;
    }

    /**
     * Setter for the coursecode in AppController.
     * Called while on the Dashboard based on which course the user selects in the course list.
     */
    public static void setCoursecode(String cc) {AppController.coursecode = cc; }

    public static final String TAG = AppController.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
    // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
