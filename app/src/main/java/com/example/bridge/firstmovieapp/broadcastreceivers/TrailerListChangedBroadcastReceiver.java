package com.example.bridge.firstmovieapp.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.example.bridge.firstmovieapp.fragments.DetailFragment;
import com.example.bridge.firstmovieapp.interfaces.OnTrailerListChanged;

public class TrailerListChangedBroadcastReceiver extends BroadcastReceiver {
    private OnTrailerListChanged mOnTrailerListChanged;
    public boolean registered;

    public void register (Context context){
        if (registered==false) {
            context.registerReceiver(this, new IntentFilter(DetailFragment.TRAILER_CHANGED));
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

    public TrailerListChangedBroadcastReceiver(OnTrailerListChanged onTrailerListChanged) {
        mOnTrailerListChanged = onTrailerListChanged;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mOnTrailerListChanged.updateTrailerList();
    }

}
