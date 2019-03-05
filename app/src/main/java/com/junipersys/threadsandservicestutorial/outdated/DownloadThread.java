package com.junipersys.threadsandservicestutorial.outdated;

import android.os.Looper;

public class DownloadThread extends Thread {
    private static final String TAG = DownloadThread.class.getCanonicalName();
    public DownloadHandler mHandler;

    @Override
    public void run() {
        Looper.prepare();
        mHandler = new DownloadHandler();
        Looper.loop();
    }


}
