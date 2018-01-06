package com.sfotakos.themovielist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.io.Serializable;

/**
 * Created by spyridion on 06/01/18.
 */

// Deal with connectivity changes
public class ConnectivityReceiver extends BroadcastReceiver implements Serializable {

    private IConnectivityChange listener;

    public ConnectivityReceiver(IConnectivityChange listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        listener.connectivityChanged();
    }


    public interface IConnectivityChange {
        void connectivityChanged();
    }
}
