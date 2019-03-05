package com.junipersys.threadsandservicestutorial;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;

public class PlayerService extends Service {
    private MediaPlayer mPlayer;
    public Messenger mMessenger = new Messenger(new PlayerHandler(this));
    public Messenger mActivityMessenger;

    @Override
    public void onCreate() {
        mPlayer = MediaPlayer.create(this, R.raw.jingle);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification.Builder notificationBuilder = new Notification.Builder(this);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        Notification notification = notificationBuilder.build();
        startForeground(11, notification);

        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopSelf();
                stopForeground(true);
                Message message = Message.obtain();
                message.arg1 = 3;
                try {
                    mActivityMessenger.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        });
        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public void onDestroy() {
        mPlayer.release();
    }

    public void play(){
        mPlayer.start();
    }

    public void pause(){
        mPlayer.pause();
    }

    public Boolean isPlaying(){
        return mPlayer.isPlaying();
    }
}
