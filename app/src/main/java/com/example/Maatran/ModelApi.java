package com.example.Maatran;

import android.os.AsyncTask;
import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ModelApi extends AsyncTask<ArrayList<String>, String, String> {
    private static final String TAG = "ModelApi";
    StringBuilder response;
    BluetoothChatService mChatService;
    ModelApi(BluetoothChatService mChatService) {this.mChatService = mChatService;}
    public void sendRequest(String data) throws Exception {
        // The URL of the API endpoint
        URL url = new URL("https://zxv5hi.deta.dev/predict?sample="+data);
        // Open an HttpURLConnection to the API endpoint
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // Read the response data
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
    }

    @Override
    protected String doInBackground(ArrayList<String>... arrayLists) {
        ArrayList<String> dataPackets = arrayLists[0];
        JSONObject obj = new JSONObject();
        // In this case, it's an array of arrays
        JSONArray item1 = new JSONArray();
        for(int i=0;i<6;i++) {
            item1.add(Integer.parseInt(dataPackets.get(i)));
        }
        response = new StringBuilder();
        try{
            sendRequest(obj.toJSONString());
        }catch(Exception e)
        {
            Log.e(TAG,e.toString());
        }
        return response.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        mChatService.predictionReceived(s);
    }
}
