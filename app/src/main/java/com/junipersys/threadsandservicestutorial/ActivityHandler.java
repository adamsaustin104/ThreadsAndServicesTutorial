package com.junipersys.threadsandservicestutorial;

import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class ActivityHandler extends Handler {
    private MainActivity mMainActivity;
    public ActivityHandler(MainActivity mainActivity){
        mMainActivity = mainActivity;
    }

    @Override
    public void handleMessage(Message msg) {
        //returned from the handleMessage replyTo from player handler
        if(msg.arg1 == 0){
            if(msg.arg2 == 1){
                mMainActivity.changPlayButtonText("PLAY");
            } else {
                Message message = Message.obtain();
                message.arg1 = 0;
                try {
                    msg.replyTo.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                mMainActivity.changPlayButtonText("PAUSE");
            }
        } else if (msg.arg1 == 1){
            if(msg.arg2 == 1){
                mMainActivity.changPlayButtonText("PAUSE");
            } else {
                Message message = Message.obtain();
                message.arg1 = 1;
                try {
                    msg.replyTo.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                mMainActivity.changPlayButtonText("PLAY");
            }
        } else if (msg.arg1 ==3 ){
            mMainActivity.changPlayButtonText("PLAY");
        }
    }
}
