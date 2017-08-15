package com.hijamoya.rxlogin.sample;

import android.app.Application;

import com.twitter.sdk.android.core.Twitter;

public class App extends Application {

    @Override public void onCreate() {
        super.onCreate();
        Twitter.initialize(this);
    }

}