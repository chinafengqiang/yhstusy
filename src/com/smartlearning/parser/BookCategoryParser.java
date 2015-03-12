package com.smartlearning.parser;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.smartlearning.model.BookCategoryVo;

public class BookCategoryParser extends BaseParser<List<BookCategoryVo>>{

	@Override
	public List<BookCategoryVo> parseJSON(String paramString)
			throws JSONException {
		if(paramString!=null){
            JSONObject jsonObject = new JSONObject(paramString);
            int rescode = jsonObject.getInt("code");
            if(rescode == 200){
                String infoList = jsonObject.getString("info");
                return JSON.parseArray(infoList,BookCategoryVo.class);
            }
        }
		return null;
	}

}
