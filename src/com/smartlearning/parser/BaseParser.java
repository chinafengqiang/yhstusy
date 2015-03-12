package com.smartlearning.parser;


import org.json.JSONException;
import org.json.JSONObject;
/**
 * Created by Administrator on 14-3-13.
 */
public abstract class BaseParser<T> {
    public abstract T parseJSON(String paramString) throws JSONException;

    /**
     *
     * @param res
     * @throws org.json.JSONException
     */
    public String checkResponse(String paramString) throws JSONException{
        if(paramString==null){
            return null;
        }else{
            JSONObject jsonObject = new JSONObject(paramString);
            String result = jsonObject.getString("response");
            if(result!=null && !result.equals("error")){
                return result;
            }else{
                return null;
            }

        }
    }
}
