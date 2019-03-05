package com.junipersys.threadsandservicestutorial;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

public class DownloadIntentService extends IntentService {

    private static final String TAG = DownloadIntentService.class.getCanonicalName();

    public DownloadIntentService(){
        super("DownloadIntentService");
        setIntentRedelivery(true);
    }

    public DownloadIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String song = intent.getStringExtra(MainActivity.EXTRA_SONG);
        downloadSong(song);
    }

    private void downloadSong(String song) {
        long endTime = System.currentTimeMillis() + 5*1000; //add 10 seconds
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
