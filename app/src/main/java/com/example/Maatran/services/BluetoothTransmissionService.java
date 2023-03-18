package com.example.Maatran.services;

import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Maatran.ui.BluetoothActivity;
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
    AppCompatActivity activity;
    ArrayList<String> dataPackets;
    ArrayList<String> dataPacketsTemp;
    ModelApi modelApi;
    private String and_id;

    public BluetoothTransmissionService(BluetoothChatService obj, String and_id, BluetoothActivity activity)
    {
        this.activity = activity;
        isSent = false;
        mChatService = obj;
        dataPackets = new ArrayList<>();
        this.and_id = and_id;
        db= FirebaseFirestore.getInstance().collection("UserDetails");
        user = FirebaseAuth.getInstance().getCurrentUser();
        ModelApi.ModelApiCallback modelApiCallback = result -> {
            Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
            Log.v(TAG, result);
            sendToDB(result);
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

    public void sendToDB(String risk)
    {
        HashMap<String, String> mp = new HashMap<>();
        for(int i=1;i<=6;i++)
        {
            mp.put(Integer.toString(i), dataPacketsTemp.get(i-1));
        }
        mp.put("risk", risk);
        assert user != null;
        String userId = user.getEmail();
        assert userId != null;
        db.document(userId)
                .set(mp, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.v(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.v(TAG, "Error writing document", e));
        dataPacketsTemp.clear();
    }

    public synchronized void responseReceived(String msg)
    {
        Log.v(TAG,"Response received...");
        Toast.makeText(activity, getValidData(msg), Toast.LENGTH_SHORT).show();
        dataPackets.add(getValidData(msg));
        isSent = false;
        if(dataPackets.size() == 8) {
            Log.v(TAG, "Sending to db");
            dataPacketsTemp = new ArrayList<>(dataPackets);
            dataPackets.clear();
            dataPacketsTemp.add("predict");
            modelApi.execute(dataPacketsTemp);
        }
    }
    public synchronized String getValidData(String msg)
    {
        StringBuilder ns= new StringBuilder();
        for(int i=0;i<msg.length();i++)
            ns.append(((msg.charAt(i) >= 48 && msg.charAt(i) <= 57) || msg.charAt(i)=='.') ? msg.charAt(i) : "");
        return ns.toString();
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
            while (i <= 8 && !stop) {
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
