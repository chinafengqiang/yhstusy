package com.smartlearning.ext.util;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ResponseParam implements Serializable {
	
	private static final long serialVersionUID = 9153994648139663603L;
	
	public JSONObject object;
	
	public ResponseParam(JSONObject obj) {
		object = obj;
	}
	
	public ResponseParam(String ret) throws JSONException {
		object = new JSONObject(ret);
	}
	
	public String getString(String key) {
		if (object.has(key)) {
			try {
				return object.getString(key);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public String getStringObj(String key) {
		
			try {
				String juser = object.getString("obj");
				Log.i("learning juser==", juser);
				JSONObject object1 = new JSONObject(juser);
				Log.i("learning object1==", object1.getString(key));
				return object1.getString(key);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return null;
	}
	
	public JSONObject getJSONObject(String key) {
		if (object.has(key)) {
			try {
				return object.getJSONObject(key);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public JSONArray getJSONArray(String key) {
		if (object.has(key)) {
			try {
				return object.getJSONArray(key);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
}
