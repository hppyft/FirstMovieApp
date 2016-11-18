package com.example.bridge.firstmovieapp.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.example.bridge.firstmovieapp.fragments.DetailFragment;
import com.example.bridge.firstmovieapp.interfaces.OnReviewListChanged;

/**
 * Created by bridge on 16/11/2016.
 */

public class ReviewListChangedBroadcastReceiver extends BroadcastReceiver {
    private OnReviewListChanged mOnReviewListChanged;
    public boolean registered;

    public void register (Context context){
        if (registered==false) {
            context.registerReceiver(this, new IntentFilter(DetailFragment.REVIEW_CHANGED));
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

    public ReviewListChangedBroadcastReceiver(OnReviewListChanged onReviewListChanged) {
        mOnReviewListChanged = onReviewListChanged;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mOnReviewListChanged.updateReviewList();
    }

}

