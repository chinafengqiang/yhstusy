package com.feng.volley;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class FRestClient {
	public static final String TAG = FRestClient.class.getSimpleName();
	
	private static FRestClient mInstance;
	private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private Context mCtx;
    
    private FRestClient(Context context){
    	this.mCtx = context;
    }
    
    public static synchronized FRestClient getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new FRestClient(context);
        }
        return mInstance;
    }
    
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext()是关键, 它会避免
            // Activity或者BroadcastReceiver带来的缺点.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
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
