package com.example.bridge.firstmovieapp;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MoviesApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/caviarDreams.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
