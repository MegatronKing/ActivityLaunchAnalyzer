package com.activity.analyzer;

import android.app.Application;

import com.activity.analyzer.library.ActivityLaunchTimeAnalyzer;

/**
 * Custom application.
 *
 * @author Megatron King
 * @since 2016/2/25 14:19
 */
public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        ActivityLaunchTimeAnalyzer.install(this);
    }
}
