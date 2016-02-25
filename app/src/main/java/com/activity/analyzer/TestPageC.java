package com.activity.analyzer;

import android.app.Activity;
import android.os.Bundle;

public class TestPageC extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_c);

        // a long time
        String a = "";
        for (int i = 0; i < 5000; i++){
            a += i;
        }
    }
}
