package com.biu.dombelskizulti.imageserviceapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ImageService extends Service {

    private BroadcastReceiver onWifiConnect;
    private int count;

    public ImageService() {
        count = 0;
    }

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
                        // Starting the Transfer
                        }
                    }
                }
            }
        };
        // Registers the receiver so that your service will listen for
        // broadcasts
        this.registerReceiver(this.onWifiConnect, theFilter);
    }

    private void startTransferImages() {
        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName("10.0.0.2");
            //create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, 6145);

            try {
                //sends the message to the server
                OutputStream output = socket.getOutputStream();
                sendAllImages(output);
                output.close();
            } catch (Exception e) {
            } finally {
                socket.close();
            }
        } catch (Exception e) {
            int x = 5;
        }
    }

    private void sendAllImages(OutputStream output) throws Exception {
        File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (dcim == null) {
            return;
        }

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final int notify_id = 1;
        final File[] pics = dcim.listFiles();
        final int len = pics.length;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (count < len) {
                    builder.setProgress(len, count, false);
                    builder.setContentText("Transfer in progress\n" + count + "/" + len + "Transferred");
                    notificationManager.notify(notify_id, builder.build());
                }
                builder.setProgress(len, count, false);
                builder.setContentText("Transfer completed");
                notificationManager.notify(notify_id, builder.build());
            }
        }).start();

        if (pics != null) {
            for (File pic : pics) {
                try {

                    count++;
                    FileInputStream fis = new FileInputStream(pic);
                    Bitmap bm = BitmapFactory.decodeStream(fis);
                    byte[] imgbyte = getBytesFromBitmap(bm);
                    output.write(imgbyte);
                    output.flush();
                } catch (Exception e) {
                }
            }
        }

    }

    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

//    public void displayProgress(int count) {
//        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//        final int notify = 1;
//        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }).start();
//    }
}
