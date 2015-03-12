package com.smartlearning.ext.util;

public interface OnActionListener {
	/**
	 * 请求成功
	 * @param actionId
	 * @param ret
	 */
	public void onActionSuccess(int actionId, ResponseParam ret);
	/**
	 * 请求http返回错误
	 * @param actionId
	 * @param httpStatus
	 */
	public void onActionFailed(int actionId, int httpStatus);
	/**
	 * 请求异常
	 * @param actionId
	 * @param exception
	 */
	public void onActionException(int actionId, String exception);
}
