package com.biu.dombelskizulti.imageserviceapp.ProgressBar;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.biu.dombelskizulti.imageserviceapp.R;

public class NotificationProgress {

    private ProgressBar progressBar;

    public NotificationProgress(ProgressBar c) {
        progressBar = c;
    }

    public void DisplayProgressBar(Context context) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "transfer")
                .setSmallIcon(R.drawable.notify_icon)
                .setContentTitle("Backup Photos")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Add as notification
        final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        while (!progressBar.isDone()) {
            builder.setProgress(progressBar.getLimit(), progressBar.getCount(), false);
            builder.setContentText(progressBar.getCount() + "/" + progressBar.getLimit() + " Backed up");
            manager.notify(0, builder.build());
            try {
                Thread.sleep(500);
            } catch (Exception e) { }
        }
        builder.setProgress(0, 0, false);
        builder.setContentText("Backup completed");
        manager.notify(0, builder.build());
    }

}
