package com.smartlearning;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class TestActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_center);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_test, menu);
        return true;
    }
}
