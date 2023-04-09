package com.example.Maatran.services;

import android.os.AsyncTask;
import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Utility class for calling the Maatran pregnancy risk classifier API.
 * <br></nr>Accepts an {@link ArrayList<String>} containing at least one argument,
 * whose inner argument of the same type must too contain at least 7 arguments.
 * The first 6 being the model params and the last being the API endpoint to invoke.
 * <br>Use:
 * <ul><li>"wakeup" to invoke the endpoint /sayhi</li>
 * <li>"predict" to invoke endpoint /predict</li>
 * </ul>
 * Needs the callback fn in {@link ModelApiCallback} to be overriden
 * in the caller activity as that is where the API response is
 * handled.
 */
public class ModelApi extends AsyncTask<ArrayList<String>, String, String> {
    private final ModelApiCallback mCallback;
    private static final String TAG = "ModelApi";
    // Define an interface for the callback

    /**
     * Interface containing the Callback function for {@link ModelApi}.
     * To be implemented by the class that wishes to call it.
     */
    public interface ModelApiCallback {
        /**
         * Callback function for {@link ModelApi}.
         * Called by onPostExecute() of {@link ModelApi}
         * @param result the result returned by the API
         */
        void onModelApiResult(String result);
    }

    // Constructor that accepts an instance of the callback interface

    /**
     * Constructor for ModelApi
     * @param callback an instance of {@link ModelApiCallback}
     */
    public ModelApi(ModelApiCallback callback) {
        this.mCallback = callback;
    }

    /**
     * fn invoked for calling the /predict endpoint
     * @param data the model params in JSON format
     * @return either the exception or the response. Exception returned
     * as "exception: exception-description"
     */
    public String sendRequest(String data){
        StringBuilder response = new StringBuilder();
        try{
            // The URL of the API endpoint
            URL url = new URL("https://maatranapi-1-c9699936.deta.app/predict?sample=" + data);

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
            return response.toString();
        }
        catch (Exception e)
        {
            Log.v(TAG,e.toString());
            return "exception: "+ e;
        }
    }

    /**
     * fn invoked for calling the /wakeup endpoint
     * @return "OK" if successful else the exception.
     * Exception returned
     * as "exception: exception-description"
     */
    public String wakeUpAPI()
    {
        StringBuilder response = new StringBuilder();
        try {

            // The URL of the API endpoint
            URL url = new URL("https://maatranapi-1-c9699936.deta.app/sayhi");

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
            return response.toString();
        }
        catch (Exception e)
        {
            Log.v(TAG,e.toString());
            return "exception: "+ e;
        }
    }

    /**
     * Tasks to be performed in the background
     * @param data {@link ArrayList<String>} array containing at least
     *              one inner array also of the same type. Inner array
     *              must contain 7 arguments.
     * @return API response or exception.
     * Exception returned
     * as "exception: exception-description"
     */
    @SafeVarargs
    @Override
    protected final String doInBackground(ArrayList<String>... data) {
        String requestType = "";
        if(data.length<1)
            return "exception: InputData must contain at least one argument";
        if(data[0].size()==1)
        {
            requestType = String.valueOf(data[0].get(0));
            if(requestType.equalsIgnoreCase("WakeUp"))
                return wakeUpAPI();
            else
                return "exception: incorrect API endpoint; expected 'wakeup'";
        }
        if(data[0].size()<9)
            return "exception: Inner array contains less than 7 arguments";
        else
        {
            requestType = String.valueOf(data[0].get(8));
            if(!requestType.equalsIgnoreCase("predict"))
                return "exception: incorrect API endpoint; expected 'predict'";
        }
        ArrayList<String> dataPackets = data[0];
        JSONObject obj = new JSONObject();
        // In this case, it's an array of arrays
        JSONArray item1 = new JSONArray();
        for(int i=0;i<6;i++) {
            try{
                item1.add(Double.parseDouble(dataPackets.get(i)));
            }
            catch (Exception e)
            {
                return "exception: "+e.toString();
            }
        }
        obj.put("data",item1);
        return sendRequest(obj.toJSONString());
    }

    /**
     * To be performed after task has finished. Calls the callback fn defined
     * in {@link ModelApiCallback} with the API response or the exception as
     * params.
     * @param result the API response or exception.
     * Exception returned
     * as "exception: exception-description"
     */
    @Override
    protected void onPostExecute(String result) {
        // Call the callback method with the result from the asynchronous task
        mCallback.onModelApiResult(result);
    }
}
