package com.smartlearning.utils;

import com.smartlearning.R;
import com.smartlearning.model.Request;
import com.smartlearning.model.Response;




public class SOAServiceUtils {
	
	private Response response;
	
	public SOAServiceUtils(){
		this.response = new Response();
	}
	
	public void getDataFromServer(Request request){
		boolean isHasNetWork = HttpUtil.hasNetwork(request.context);
		if(!isHasNetWork){
			response.status = -1;
			response.info = request.context.getString(R.string.net_error);
			return;
		}else{
			 HttpUtil.post(request,response);
		}
		
	}
	
    //从服务器端获取到的数据然后回调实现此处理数据的方法
    public abstract interface DataCallback<T> {
        public abstract void processData(T paramObject,
                                         int status,String info);
    }
    
    public void getDataFromServer(Request request,DataCallback callBack){
    	getDataFromServer(request);
    	callBack.processData(response.result, response.status,response.info);
    }
	
}
