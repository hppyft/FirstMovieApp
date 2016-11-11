package com.example.bridge.firstmovieapp.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.example.bridge.firstmovieapp.activities.SettingsActivity;
import com.example.bridge.firstmovieapp.interfaces.onSettingsChanged;


public class SettingsChangedBroadcastReceiver extends BroadcastReceiver {

    private onSettingsChanged mOnSettingsChanged;
    public boolean registered;

    public void register (Context context){
        if (registered==false) {
            context.registerReceiver(this, new IntentFilter(SettingsActivity.SORT_CHANGED));
            registered = true;
        }
        else{
            System.out.println("Tried to REGISTER an already REGISTERED Receiver");
        }
    }

    public void unregister (Context context){
        if (registered==true) {
            context.unregisterReceiver(this);
            registered = false;
        }
        else{
            System.out.println("Tried to UNREGISTER an already UNREGISTERED Receiver");
        }
    }

    public SettingsChangedBroadcastReceiver(onSettingsChanged onSettingsChanged) {
        mOnSettingsChanged = onSettingsChanged;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mOnSettingsChanged.resetMovieList();
    }

}
