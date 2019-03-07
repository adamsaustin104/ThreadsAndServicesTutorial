package com.junipersys.threadsandservicestutorial;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.junipersys.threadsandservicestutorial.adapters.PlaylistAdapter;
import com.junipersys.threadsandservicestutorial.models.Song;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_LIST_POSITION = "EXTRA_POSITION";
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String EXTRA_SONG = "EXTRA_SONG";
    public static final String EXTRA_TITLE = "SONG_TITLE";
    public static final String EXTRA_FAVORITE = "IS_FAVORITE";
    public static final int REQUEST_FAVORITE = 0;

    private Button mDownloadButton;
    private Button mPlayButton;
    private Boolean mBound = false;
    private Messenger mServiceMessenger;
    private Messenger mActivityMessenger = new Messenger(new ActivityHandler(this));
    private PlaylistAdapter mAdapter;
    private RelativeLayout mRootLayout;
    private NetworkConnectionReceiver mReciever = new NetworkConnectionReceiver();

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
        mRootLayout = findViewById(R.id.rootLayout);

        mDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadSongs();


            }
        });

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBound){
                    Intent intent = new Intent(MainActivity.this, PlayerService.class);
                    intent.putExtra(EXTRA_SONG, Playlist.songs[0]);
                    startService(intent);
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

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        mAdapter = new PlaylistAdapter(this, Playlist.songs);
        recyclerView.setAdapter(mAdapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
    }

    private void testIntents() {
        //Intent intent = new Intent(this, DetailActivity.class);
        //intent.putExtra(EXTRA_TITLE, "Tribute");
        //startActivityForResult(intent, REQUEST_FAVORITE);

        //Implicit intent
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri geoLocation = Uri.parse("geo:0,0?q=41.682108, -111.856089(Home)");
        intent.setData(geoLocation);
        if (intent.resolveActivity(getPackageManager()) == null) {
            //handle the error
            Snackbar.make(mRootLayout, "Sorry, nothing found to handle maps", Snackbar.LENGTH_LONG).show();
        } else {
            startActivity(intent);
        }

    }

    private void downloadSongs() {
        Toast.makeText(MainActivity.this, "Downloading...", Toast.LENGTH_LONG).show();
        //Send Messages to Handler for processing
        for(Song song : Playlist.songs){
            Intent intent = new Intent(MainActivity.this, DownloadIntentService.class);
            intent.putExtra(EXTRA_SONG, song);
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

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "App is in the foreground");
        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(mReciever, filter);

        IntentFilter customFilter = new IntentFilter(NetworkConnectionReceiver.NOTIFY_NETWORK_CHANGE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalReceiver, customFilter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "App is in the background");
        unregisterReceiver(mReciever);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == REQUEST_FAVORITE){
            if(resultCode == RESULT_OK){
                boolean result = intent.getBooleanExtra(EXTRA_FAVORITE, false);
                Log.i(TAG, "Is favorite? " + result);
                int position = intent.getIntExtra(EXTRA_LIST_POSITION, 0);
                Playlist.songs[position].setIsFavorite(result);
                mAdapter.notifyItemChanged(position);
            }
        }
    }

    private BroadcastReceiver mLocalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnected = intent.getBooleanExtra(NetworkConnectionReceiver.EXTRA_IS_CONNECTED, false);
            if(isConnected){
                //Snackbar.make(mRootLayout, "Network is Connected", Snackbar.LENGTH_LONG).show();
            } else {
                //Snackbar.make(mRootLayout, "Network is Disconnected", Snackbar.LENGTH_LONG).show();
            }
        }
    };
}
