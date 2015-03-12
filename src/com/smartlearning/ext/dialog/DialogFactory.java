package com.smartlearning.ext.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;

import com.smartlearning.R;

public class DialogFactory {
	
	public static AlertDialog showNewVersion(final Activity activity,
			OnClickListener posListener) {
		return createAlert(activity, R.string.alert_new_version_title,
				R.string.alert_new_version_message,
				R.string.alert_new_version_pos, null,
				R.string.alert_new_version_neg, posListener);
	}
	
	public static AlertDialog showNoVersion(final Activity activity,
			OnClickListener posListener) {
		return createAlert(activity, R.string.alert_cur_version_title,
				R.string.alert_cur_version_message,
				R.string.alert_cur_version, posListener);
	}
	
	
	public static AlertDialog showMustUpdate(final Activity activity) {
		return createAlert(activity,
				R.string.alert_must_update_title,
				R.string.alert_must_update_message,
				R.string.alert_must_update_pos,null, R.string.alert_must_update_neg,
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						activity.finish();
					}

				});
	}
	
	public static AlertDialog showConnectError(Context context) {
		return createAlert(context,
				R.string.alert_connect_error_title,
				R.string.alert_connect_error_message,
				R.string.alert_connect_error_pos,null);
	}
	
	public static AlertDialog showConnectException(Context context) {
		return createAlert(context,
				R.string.alert_connect_exception_title,
				R.string.alert_connect_exception_message,
				R.string.alert_connect_exception_pos,null);
	}
	
	public static AlertDialog showNetworkSettingFailed(final Activity activity) {
		return createAlert(activity,
				R.string.alert_network_setting_failed_title,
				R.string.alert_network_setting_failed_message,
				R.string.alert_network_setting_failed_pos,
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (android.os.Build.VERSION.SDK_INT > 10) {
							// 3.0以上打开设置界面，也可以直接用ACTION_WIRELESS_SETTINGS打开到wifi界面
							activity.startActivity(new Intent(
									android.provider.Settings.ACTION_SETTINGS));
						} else {
							activity.startActivity(new Intent(
									android.provider.Settings.ACTION_WIRELESS_SETTINGS));
						}
					}

				}, R.string.alert_network_setting_failed_neg,
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						activity.finish();
					}

				});
	}
	
	public static ProgressDialog showProgress(Context context) {
		ProgressDialog dialog = new ProgressDialog(context);
		dialog.setMessage(context.getString(R.string.progress_message));
		dialog.show();
		return dialog;
	}
	
	public static AlertDialog createAlert(Context context, int title,
			int message, int posTitle, OnClickListener posListener) {
		return new AlertDialog.Builder(context).setTitle(title)
				.setMessage(message).setPositiveButton(posTitle, posListener)
				.show();
	}
	
	public static AlertDialog createAlert(Context context, int title,
			int message, int posTitle, OnClickListener posListener,
			int negTitle, OnClickListener negListener) {
		return new AlertDialog.Builder(context).setTitle(title)
				.setMessage(message).setPositiveButton(posTitle, posListener)
				.setNegativeButton(negTitle, negListener).show();
	}

}
