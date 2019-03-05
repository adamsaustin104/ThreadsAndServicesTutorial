package com.junipersys.threadsandservicestutorial;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.junipersys.threadsandservicestutorial.adapters.PlaylistAdapter;
import com.junipersys.threadsandservicestutorial.models.Song;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_SONG = "song";
    public static final String EXTRA_TITLE = "SONG_TITLE";

    private Button mDownloadButton;
    private Button mPlayButton;
    private Boolean mBound = false;
    private Messenger mServiceMessenger;
    private Messenger mActivityMessenger = new Messenger(new ActivityHandler(this));
    private PlaylistAdapter mAdapter;

    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mBound = true;
            mServiceMessenger = new Messenger(binder);
            Message message = Message.obtain();
            message.arg1 = 2;
            message.arg2 = 1;
            message.replyTo = mActivityMessenger;
            try {
                mServiceMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDownloadButton = findViewById(R.id.downloadButton);
        mPlayButton = findViewById(R.id.playButton);

        mDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //downloadSongs();
                testIntents();
            }
        });

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBound){
                    startService(new Intent(MainActivity.this, PlayerService.class));
                    Message message = Message.obtain();
                    message.arg1 = 2;
                    message.replyTo = mActivityMessenger;
                    try {
                        mServiceMessenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        mAdapter = new PlaylistAdapter(this, Playlist.songs);
        recyclerView.setAdapter(mAdapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
    }

    private void testIntents() {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(EXTRA_TITLE, "Tribute");
        startActivity(intent);
    }

    private void downloadSongs() {
        Toast.makeText(MainActivity.this, "Downloading...", Toast.LENGTH_LONG).show();
        //Send Messages to Handler for processing
        for(Song song : Playlist.songs){
            Intent intent = new Intent(MainActivity.this, DownloadIntentService.class);
            intent.putExtra(KEY_SONG, song);
            startService(intent);
        }
    }

    public void changPlayButtonText(String text){
        mPlayButton.setText(text);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, PlayerService.class);
        bindService(intent, mServiceConn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mBound) unbindService(mServiceConn);
        mBound = false;
    }
}
