package com.smartlearning.ui;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;

public class MediaScanner implements MediaScannerConnectionClient {

	private MediaScannerConnection mMs;
	private String mFile;

	public MediaScanner(Context context, String  f) {
		
		 //Log.v(TAG,	"SingleMediaScanner ---->path = " + f);
		 
	    mFile = f;
	    mMs = new MediaScannerConnection(context, this);
	    mMs.connect();
	}

	@Override
	public void onMediaScannerConnected() {
		
		 //Log.v(TAG,	"onMediaScannerConnected ---->");
		 
	    mMs.scanFile(mFile, null);
	}

	@Override
	public void onScanCompleted(String path, Uri uri) {
		
		//Log.v(TAG,	"onScanCompleted ---->");
		
	    mMs.disconnect();
	}

	}