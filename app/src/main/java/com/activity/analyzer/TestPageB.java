package com.activity.analyzer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class TestPageB extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_b);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(TestPageB.this, TestPageC.class));
            }
        }, 2000);

        String a = "";
        for (int i = 0; i < 1000; i++){
            a += i;
        }
    }
}
