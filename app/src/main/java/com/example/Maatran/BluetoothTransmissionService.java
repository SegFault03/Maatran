package com.example.Maatran;

import android.util.Log;

import java.util.ArrayList;

public class BluetoothTransmissionService {
    public boolean isSent;
    private BluetoothChatService mChatService;
    private static final String TAG = "BTransmissionService";
    sendRequestThread mSendRequestThread;
    ArrayList<String> dataPackets;

    public BluetoothTransmissionService(BluetoothChatService obj)
    {
        isSent = false;
        mChatService = obj;
        dataPackets = new ArrayList<>();
    }

    public void startRequestThread()
    {
        if(mSendRequestThread!=null)
            mSendRequestThread = null;
        mSendRequestThread = new sendRequestThread();
        mSendRequestThread.start();
        Log.v(TAG,"Request Thread started...");
    }

    public void stopRequestThread()
    {
        if(mSendRequestThread!=null)
            mSendRequestThread.cancel();
        mSendRequestThread = null;
    }

    public synchronized void sendRequest(int requestCode)
    {
        while(isSent)
        {
            try{
                this.wait(100);
                Log.v(TAG,"Request Thread waiting...");
            }catch (InterruptedException e)
            {
                Log.e(TAG,"InterruptedException");
            }
        }
        Log.v(TAG,"Request Thread woke up...");
        String message = String.valueOf(requestCode);
        // Get the message bytes and tell the BluetoothChatService to write
        byte[] send = message.getBytes();
        mChatService.write(send);
        isSent = true;
        Log.v(TAG,"Request Thread sent a request...");
    }

    public synchronized void responseReceived(String msg)
    {
        Log.v(TAG,"Response received...");
        dataPackets.add(msg);
        isSent = false;
    }

    private class sendRequestThread extends Thread{
        private boolean stop;
        sendRequestThread()
        {
            stop=false;
        }

        @Override
        public void run()
        {
            int i = 1;
            while (i++ <= 4 && !stop)
                sendRequest(i);
        }

        public void cancel()
        {
            stop = true;
        }
    }
}
