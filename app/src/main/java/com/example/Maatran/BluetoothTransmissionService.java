package com.example.Maatran;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class BluetoothTransmissionService {
    public boolean isSent;
    private BluetoothChatService mChatService;
    private static final String TAG = "BTransmissionService";
    sendRequestThread mSendRequestThread;
    ArrayList<String> dataPackets;
    ModelApi modelApi;
    private String and_id;

    public BluetoothTransmissionService(BluetoothChatService obj, String and_id)
    {
        isSent = false;
        mChatService = obj;
        dataPackets = new ArrayList<>();
        ModelApi.ModelApiCallback modelApiCallback = result -> Log.v(TAG,result);
        modelApi = new ModelApi(modelApiCallback);
        this.and_id = and_id;
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

    public void sentToDB()
    {
        HashMap<String, String> mp = new HashMap<>();
        for(int i=1;i<=6;i++)
        {
            mp.put(Integer.toString(i), dataPackets.get(i-1));
        }
        CollectionReference db= FirebaseFirestore.getInstance().collection("UserDetails");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userId = user.getEmail(); // use UID instead of email as the document ID
        assert userId != null;
        db.document(userId)
                .collection("Patients")
                .whereEqualTo("android_id", and_id)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String id="";
                        for(DocumentSnapshot ds : task.getResult())
                        {
                            id=ds.getId();
                        }
                        db.document(userId) // use the user's UID here as well
                                .collection("Patients")
                                .document(id)
                                .set(mp, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));

                    }
                });
    }

    public synchronized void responseReceived(String msg)
    {
        Log.v(TAG,"Response received...");
        dataPackets.add(msg);
        isSent = false;
        if(dataPackets.size() == 6) {
            sentToDB();
            ArrayList<String> dataPacketsTemp = new ArrayList<>(dataPackets);
            dataPacketsTemp.add("predict");
            modelApi.execute(dataPacketsTemp);
            dataPackets.clear();
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
