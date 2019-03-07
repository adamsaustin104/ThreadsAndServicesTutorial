package com.junipersys.threadsandservicestutorial;

import android.os.Looper;

public class DownloadThread extends Thread {
    // --Commented out by Inspection (3/6/2019 5:06 PM):private static final String TAG = DownloadThread.class.getCanonicalName();
    public DownloadHandler mHandler;

    @Override
    public void run() {
        Looper.prepare();
        mHandler = new DownloadHandler();
        Looper.loop();
    }


}
