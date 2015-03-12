package com.smartlearning.ext;

import com.smartlearning.constant.ServerIP;
import com.smartlearning.ext.util.GetAction;
import com.smartlearning.ext.util.GetParam;
import com.smartlearning.ext.util.OnActionListener;
import com.smartlearning.ext.util.ResponseParam;


/**
 * 获取当前最新版本号操作
 * @author HalfmanHuang
 * @since SDK19 JDK7
 * @version 1.0.0
 */
public class ActionGetVersion {
	/** 设备类型：Android设备 */
	public final static int DEVICE_TYPE_ANDROID = 0;
	/** 设备类型：iOS设备 */
	public final static int DEVICE_TYPE_IOS = 1;
	/** 操作名 */
	private final static String ACTION_NAME = "getVersion";
	/** 设备类型参数 */
	private final static String PARAM_DEVICE = "device";
	/** 最新服务端版本 */
	private final static String PARAM_SERVER_VERSION = "serverVersion";
	/** 最新客户端版本 */
	private final static String PARAM_CLIENT_VERSION = "clientVersion";
	/** Get操作 */
	private GetAction action;
	/** Get输出参数 */
	private GetParam outParam;
	
	
	/**
	 * 构造方法
	 * @param actionId ActionID
	 */
	public ActionGetVersion(int actionId, String BASE_URL) {
		action = new GetAction(actionId, BASE_URL + ServerIP.SERVLET_GET_VERSION);
		outParam = new GetParam();
	}
	/**
	 * 设置Action返回监听器
	 * @param listener 监听器
	 */
	public void setOnActionListener(OnActionListener listener) {
		action.setOnActionListener(listener);
	}
	/**
	 * 启动操作
	 */
	public void startAction() {
		action.setParam(outParam);
		action.startAction();
	}
	/**
	 * 设置设备类型参数
	 * @param deviceType 设备类型参数：DEVICE_TYPE_ANDROID、DEVICE_TYPE_IOS
	 */
	public void setDevice(int deviceType) {
		switch (deviceType) {
		case DEVICE_TYPE_ANDROID:
			outParam.addParam(PARAM_DEVICE, ""+DEVICE_TYPE_ANDROID);
			break;
		case DEVICE_TYPE_IOS:
			outParam.addParam(PARAM_DEVICE, ""+DEVICE_TYPE_IOS);
			break;
		}
	}
	/**
	 * 获取最新接口版本
	 * @param ret 返回参数包
	 * @return 最新接口版本
	 */
	public static String getServerVersion(ResponseParam ret) {
		return ret.getStringObj(PARAM_SERVER_VERSION);
	}
	/**
	 * 获取最新客户端版本
	 * @param ret 返回参数包
	 * @return 最新客户端版本
	 */
	public static String getClientVersion(ResponseParam ret) {
		return ret.getStringObj(PARAM_CLIENT_VERSION);
	}
}
