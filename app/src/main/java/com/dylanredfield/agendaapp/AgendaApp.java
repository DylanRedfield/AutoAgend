package com.dylanredfield.agendaapp;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by dylan_000 on 3/10/2015.
 */
public class AgendaApp extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
    }
}
