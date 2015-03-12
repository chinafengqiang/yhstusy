package com.smartlearning.ext.util;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

@SuppressLint({ "HandlerLeak", "HandlerLeak" })
public final class GetAction implements Runnable {
	/** 请求成功消息ID */
	private final static int MSG_TYPE_SUCCESS = 0x10;
	/** 请求失败消息ID */
	private final static int MSG_TYPE_FAILED = 0x20;
	/** 请求异常消息ID */
	private final static int MSG_TYPE_EXCEPTION = 0x30;
	/** 请求成功返回参数名 */
	private final static String MSG_KEY_PARAM = "param";
	/** 请求异常返回参数名 */
	private final static String MSG_KEY_EXCEPTION = "exception";
	/** Action ID */
	private int id;
	/** 请求目标URL */
	private String url;
	/** Action返回监听器实例 */
	private OnActionListener listener;
	/** 消息句柄 */
	private Handler handler;
	/** Get请求参数 */
	private GetParam param;
	/**
	 * 构造方法
	 * @param actionId 用于区分线程的ActionID
	 * @param baseUrl 基本URL，不推荐包含Get参数
	 */
	public GetAction(int actionId, String baseUrl) {
		this.url = baseUrl;
		this.id = actionId;
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == id) {
					switch (msg.arg1) {
					case MSG_TYPE_SUCCESS:
						// 获得成功消息的情况
						if (null != listener) {
							
							Log.i("halfman", "get (ResponseParam) msg.getData().getSerializable(MSG_KEY_PARAM)="+(ResponseParam) msg
									.getData().getSerializable(MSG_KEY_PARAM));
							
							listener.onActionSuccess(id, (ResponseParam) msg
									.getData().getSerializable(MSG_KEY_PARAM));
						}
						return;
					case MSG_TYPE_FAILED:
						// 获得失败消息的情况
						if (null != listener) {
							listener.onActionFailed(id, msg.arg2);
						}
						return;
					case MSG_TYPE_EXCEPTION:
						// 获得异常消息的情况
						if (null != listener) {
							listener.onActionException(id, msg.getData()
									.getString(MSG_KEY_EXCEPTION));
						}
						return;
					}
				}
				super.handleMessage(msg);
			}
		};
	}
	/**
	 * 设置参数
	 * @param param
	 */
	public void setParam(GetParam param) {
		this.param = param;
	}
	/**
	 * 设置监听器实例
	 * @param l
	 */
	public void setOnActionListener(OnActionListener l) {
		listener = l;
	}
	/**
	 * 启动Action
	 */
	public void startAction() {
		// 开启一个线程，执行本类
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		try {
			Log.d("halfman", "get param="+url + param.toString());
			HttpGet get = new HttpGet(url + param.toString());
			DefaultHttpClient client = new DefaultHttpClient();
			// 设置超时
			client.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
					10000);
			HttpResponse httpResponse = client.execute(get);
			int httpCode = httpResponse.getStatusLine().getStatusCode();
			// 判断HTTP请求是否成功返回
			if (httpCode == HttpStatus.SC_OK) {
				String result =  EntityUtils.toString(httpResponse.getEntity());
				Log.d("halfman", "ret="+result);
				ResponseParam param = new ResponseParam(result);
				// 发送返回参数至主线程
				Message msg = Message.obtain();
				msg.what = id;
				msg.arg1 = MSG_TYPE_SUCCESS;
				Bundle data = new Bundle();
				data.putSerializable(MSG_KEY_PARAM, param);
				msg.setData(data);
				handler.sendMessage(msg);
			} else {
				// HTTP状态字为失败的情况
				Message msg = Message.obtain();
				msg.what = id;
				msg.arg1 = MSG_TYPE_FAILED;
				msg.arg2 = httpCode;
				handler.sendMessage(msg);
			}
		} catch (Exception e) {
			// 请求过程中发生异常的情况
			Message msg = Message.obtain();
			msg.what = id;
			msg.arg1 = MSG_TYPE_EXCEPTION;
			Bundle data = new Bundle();
			data.putString(MSG_KEY_EXCEPTION, e.getMessage());
			msg.setData(data);
			handler.sendMessage(msg);
		}
	}

}
