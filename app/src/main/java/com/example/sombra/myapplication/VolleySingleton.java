package com.example.sombra.myapplication;

import android.content.Context;

import com.android.volley.*;
import com.android.volley.toolbox.Volley;

/**
 * VolleySingleton handles the request queue for our HttpRequests.
 */
public class VolleySingleton {
    private static VolleySingleton mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    /**
     * The constructor for our Singleton(we know this is strange but we start the queue by creating
     * a instance of our Singleton, but only one ever.
     * That is also something we should take care of, so we can't accidently create several instances.
     * @param context Context of the Singleton.
     */
    private VolleySingleton(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    /**
     * getInstance() checks if we have an active instance of our VolleySingleton,
     * if not creating one.
     *
     * @param context Context of the VolleySingleton.
     * @return The VolleySingleton is returned for usage.
     */
    public static synchronized VolleySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleySingleton(context);
        }
        return mInstance;
    }

    /**
     * getRequestQueue() returns our request queue for usage with HttpRequests.
     * @return The request queue is returned.
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    /**
     * addToRequestQueue() adds the HttpRequest to our active request queue.
     *
     * @param req The request to add to the queue.
     * @param <T> The type of the request.
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}