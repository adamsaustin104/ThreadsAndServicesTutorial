package com.junipersys.threadsandservicestutorial;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.junipersys.threadsandservicestutorial.models.Song;

public class PlayerService extends Service {
    private static final int REQUEST_OPEN = 99;
    private MediaPlayer mPlayer;
    public Messenger mMessenger = new Messenger(new PlayerHandler(this));
    public Messenger mActivityMessenger;

    @Override
    public void onCreate() {
        mPlayer = MediaPlayer.create(this, R.raw.jingle);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String title = "";
        String artist = "";

        if(intent.getParcelableExtra(MainActivity.EXTRA_SONG) != null){
            Song song = intent.getParcelableExtra(MainActivity.EXTRA_SONG);
            title = song.getTitle();
            artist = song.getArtist();
        }
        Intent mainIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,REQUEST_OPEN, mainIntent, 0);

        NotificationManager notification_manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notification_builder;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String chanel_id = "3000";
            CharSequence name = "Channel Name";
            String description = "Chanel Description";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(chanel_id, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.BLUE);
            notification_manager.createNotificationChannel(mChannel);
            notification_builder = new NotificationCompat.Builder(this, chanel_id);
        } else {
            notification_builder = new NotificationCompat.Builder(this);
        }

        notification_builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(artist)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Notification notification = notification_builder.build();

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
