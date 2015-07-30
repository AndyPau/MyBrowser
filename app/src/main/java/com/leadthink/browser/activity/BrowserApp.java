package com.leadthink.browser.activity;

import android.app.Application;
import android.content.Context;

import com.flurry.android.FlurryAgent;
import com.squareup.leakcanary.LeakCanary;

public class BrowserApp extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        // Add LeakCanary Support
        LeakCanary.install(this);

        context = getApplicationContext();

        // configure Flurry
        FlurryAgent.setLogEnabled(false);

        // Used to allow/disallow Flurry SDK to report uncaught exceptions.
        // The feature is enabled by default and if you would like to disable this behavior,
        // this must be called before calling init
        FlurryAgent.setCaptureUncaughtExceptions(true);

        // init Flurry
        FlurryAgent.init(this, "MVSH55YG4CK6BSWX3SZC");
    }

    public static Context getAppContext() {
        return context;
    }
}
