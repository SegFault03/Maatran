package com.example.Maatran;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class BluetoothTransmissionService {
    public boolean isSent;
    private BluetoothChatService mChatService;
    private static final String TAG = "BTransmissionService";
    sendRequestThread mSendRequestThread;
    CollectionReference db;
    FirebaseUser user;
    ArrayList<String> dataPackets;
    ModelApi modelApi;
    private String and_id;

    public BluetoothTransmissionService(BluetoothChatService obj, String and_id)
    {
        isSent = false;
        mChatService = obj;
        dataPackets = new ArrayList<>();
        this.and_id = and_id;
        db= FirebaseFirestore.getInstance().collection("UserDetails");
        user = FirebaseAuth.getInstance().getCurrentUser();
        ModelApi.ModelApiCallback modelApiCallback = result -> {
            Log.v(TAG, result);
            dataPackets.add(result);
            sendToDB();
        };
        modelApi = new ModelApi(modelApiCallback);
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

    public void sendToDB()
    {
        HashMap<String, String> mp = new HashMap<>();
        for(int i=1;i<=6;i++)
        {
            mp.put(Integer.toString(i), dataPackets.get(i-1));
        }
        mp.put("risk", dataPackets.get(6));
        assert user != null;
        String userId = user.getEmail();
        assert userId != null;
        db.document(userId)
                .set(mp, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.v(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.v(TAG, "Error writing document", e));
        dataPackets.clear();
    }

    public synchronized void responseReceived(String msg)
    {
        Log.v(TAG,"Response received...");
        dataPackets.add(msg);
        isSent = false;
        if(dataPackets.size() == 6) {
            Log.v(TAG, "Sending to db");
            ArrayList<String> dataPacketsTemp = new ArrayList<>(dataPackets);
            dataPacketsTemp.add("predict");
            modelApi.execute(dataPacketsTemp);
        }
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
            while (i <= 6 && !stop) {
                sendRequest(i);
                i++;
            }
        }

        public void cancel()
        {
            stop = true;
        }
    }
}
