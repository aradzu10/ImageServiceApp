package com.biu.dombelskizulti.imageserviceapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.biu.dombelskizulti.imageserviceapp.ProgressBar.NotificationProgress;
import com.biu.dombelskizulti.imageserviceapp.ProgressBar.ProgressBar;
import com.biu.dombelskizulti.imageserviceapp.Transfer.Client;
import com.biu.dombelskizulti.imageserviceapp.Transfer.HandleClientCommunication;
import com.biu.dombelskizulti.imageserviceapp.Transfer.SendPhotos;

public class ImageService extends Service {

    private BroadcastReceiver onWifiConnect;

    public ImageService() { }

    @Nullable

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final IntentFilter theFilter = new IntentFilter();
        theFilter.addAction("android.net.wifi.supplicant.CONNECTION_CHANGE");
        theFilter.addAction("android.net.wifi.STATE_CHANGE");
        onWifiConnect = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (networkInfo != null) {
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        //get the different network states
                        if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                            startTransferImages();
                        }
                    }
                }
            }
        };

        this.registerReceiver(this.onWifiConnect, theFilter);
    }

    private void startTransferImages() {
        final Context context = this;
        new Thread() {
            @Override
            public void run() {
                ProgressBar progressBar = new ProgressBar();
                HandleClientCommunication handler = new SendPhotos(progressBar);
                NotificationProgress notificationProgress = new NotificationProgress(progressBar);
                Client client = new Client(6145, handler);
                if (!client.Connect()) return;
                client.Start();
                notificationProgress.DisplayProgressBar(context);
            }
        }.start();
    }
}
