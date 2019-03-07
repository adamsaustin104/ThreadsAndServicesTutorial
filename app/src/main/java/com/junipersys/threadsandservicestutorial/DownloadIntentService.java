package com.junipersys.threadsandservicestutorial;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.junipersys.threadsandservicestutorial.models.Song;

public class DownloadIntentService extends IntentService {
    private static final String TAG = DownloadIntentService.class.getCanonicalName();
    private static final int NOTIFICATION_ID = 45;
    private NotificationManager mNotificationManager;

    public DownloadIntentService(){
        super("DownloadIntentService");
        setIntentRedelivery(true);
    }

    public DownloadIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Song song = intent.getParcelableExtra(MainActivity.EXTRA_SONG);

        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_queue_music_white)
                .setContentTitle("Downloading")
                .setContentText(song.getTitle());

        mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(NOTIFICATION_ID, builder.build());

        downloadSong(song.getTitle());
    }

    private void downloadSong(String song) {
        long endTime = System.currentTimeMillis() + 2*1000; //add 10 seconds
        while(System.currentTimeMillis() < endTime){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, song + " downloaded");
    }
}
