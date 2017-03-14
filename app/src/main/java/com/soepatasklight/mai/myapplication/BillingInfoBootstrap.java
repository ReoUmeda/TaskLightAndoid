package com.soepatasklight.mai.myapplication;

import android.app.Application;
import com.beardedhen.androidbootstrap.TypefaceProvider;

public class BillingInfoBootstrap extends Application {
    @Override public void onCreate() {
        super.onCreate();
        TypefaceProvider.registerDefaultIconSets();
    }
}