package com.example.bridge.firstmovieapp.entities;

import android.content.Context;
import android.net.ConnectivityManager;

import java.net.InetAddress;

public class Utility {
    public Context context;

    public Utility(Context context) {
        this.context = context;
    }

    public boolean isConnectionAvailable(){
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }

    }
}
