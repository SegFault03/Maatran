package com.example.Maatran;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


//Handles all BluetoothActivity
//NOTE: Ignore all 'errors' related to call 'requires permissions which may be rejected by the user..' and such..
//BLUETOOTH_ADMIN is declared in the manifest so there's no need to ask for permissions
public class BluetoothActivity extends AppCompatActivity {

    //private data members corresponding to views in the layout
    /**Controls changes to Bluetooth state [CONNECT/DISCONNECT]*/
    private Button mBluetoothStateChangeBtn;
    /**Displays a list of remote devices*/
    private ListView mBluetoothDeviceList;
    /**Displays 'Select a Device to connect to'*/
    private TextView mSelectDeviceDisplayText;
    /**Displays the current state of Bluetooth [CONNECTED/DISCONNECTED]*/
    private TextView mBluetoothStateText;
    /**Btn for enabling device discovery*/
    private Button mFindDevicesBtn;
    private ProgressDialog mProgressDialog;
    /**ArrayAdapter for ListView mBluetoothDeviceList*/
    private ArrayAdapter<String> mListOfDevices;
    /**ArrayList for ArrayAdapter mListOfDevices*/
    private ArrayList<String> mNameOfDevices;

    /**Handler for updating UI after a specific time interval*/
    private Handler handler;
    /**IntentFilter for handling Broadcasts*/
    IntentFilter mIntentFilter;

    //For debugging
    private static final String TAG = "BluetoothActivity";

    //CODES TO INDICATE BLUETOOTH STATUS
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    /**This code is returned if request to turn on Bluetooth was granted*/
    private static final int REQUEST_ENABLE_BT = 3;
    private static final int REQUEST_ENABLE_COARSE_LOCATION = 4;
    private static final int REQUEST_ENABLE_FINE_LOCATION = 5;

    //TODO Stores the name of the device to connect to. Will be different for each patient and
    /**will be received from the calling Activity (to be implemented later)*/
    private final String mDeviceToConnect = "Lenovo";
    /**
     * Stores the name and address of the paired device
     */
    private String mConnectedDeviceName = null;
    private String mConnectedDeviceHardwareAddress = null;

    /**
     * Global BluetoothAdapter class object to use Bluetooth features
     */
    private BluetoothAdapter mBluetoothAdapter = null;
    /**
     * Set containing BluetoothDevices that have been paired with in the past
     */
    Set<BluetoothDevice> mPairedDevices;
    /**
     * Set containing BluetoothDevices that have been discovered upon enabling device discovery
     */
    Set<BluetoothDevice> mDiscoveredDevices;
    //Global BluetoothService object
    //private BluetoothService mChatService = null;           //TODO  service for handling data transmissions

    /**
     * Runnable for the handler
     * Callback fn run() is called every 100ms
     */
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //Update UI
            updateUI();
            //call it again after 100ms
            handler.postDelayed(this, 100);
        }
    };

    /**
     * Create a BroadcastReceiver for ACTION_FOUND.
     * Broadcasts when a device is found upon device discovery
     */
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.S)
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int newSize,oldSize;
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                Toast.makeText(BluetoothActivity.this, "Device found!" + device.getName(), Toast.LENGTH_SHORT).show();
                oldSize=mDiscoveredDevices.size();
                mDiscoveredDevices.add(device);
                newSize=mDiscoveredDevices.size();
                if(oldSize!=newSize)
                {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress();
                    if(deviceName != null)
                        mNameOfDevices.add(deviceName);
                    else if(deviceHardwareAddress != null)
                        mNameOfDevices.add(deviceHardwareAddress);
                    else
                        mNameOfDevices.add("Unknown Device");
                    mListOfDevices.notifyDataSetChanged();
                }
//                String deviceName = device.getName();
//                String deviceHardwareAddress = device.getAddress(); // MAC address
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Toast.makeText(BluetoothActivity.this, "Device Discovery started...", Toast.LENGTH_SHORT).show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(BluetoothActivity.this, "Device Discovery finished...", Toast.LENGTH_SHORT).show();
            }
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_activity_screen);

        //Initializing data members
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mBluetoothStateChangeBtn = findViewById(R.id.bluetooth_service_connect_btn);
        mBluetoothDeviceList = findViewById(R.id.device_list_lv);
        mSelectDeviceDisplayText = findViewById(R.id.select_device_text);
        mSelectDeviceDisplayText.setVisibility(View.INVISIBLE);
        mBluetoothStateText = findViewById(R.id.bluetooth_service_state_text);
        mFindDevicesBtn = findViewById(R.id.find_devices_btn);
        mFindDevicesBtn.setVisibility(View.INVISIBLE);

        mFindDevicesBtn.setOnClickListener(v->setUpBluetooth());
        mDiscoveredDevices=new HashSet<>();
        mPairedDevices=null;
        mNameOfDevices=new ArrayList<>();
        mListOfDevices = new ArrayAdapter<>(getApplicationContext(), R.layout.listview_elements,R.id.device_list_item_lv,mNameOfDevices);
        mBluetoothDeviceList.setAdapter(mListOfDevices);
        mBluetoothDeviceList.setOnItemClickListener(
                (parent, view, position, id) -> {
                    String deviceName = mBluetoothDeviceList.getItemAtPosition(position).toString();
                    Toast.makeText(BluetoothActivity.this, "Connecting to " + deviceName, Toast.LENGTH_SHORT).show();
                    //TODO connect to the device
                    ConnectThread connectThread = new ConnectThread(mDiscoveredDevices.stream().filter(device -> device.getName().equals(deviceName)).findFirst().get());
                    connectThread.run();
                }
        );

        //Initialize default BluetoothAdapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            super.finish();
        }
    }


    /**
     * Overrides onActivityResult() in AppCompatActivity class. Is called automatically after startActivityForResult() is called
     * @param requestCode: request code for turning on Bluetooth
     * @param resultCode: returned by startActivityForResult; indicates whether request was granted or rejected
     * @param data: TODO Intent object to be passed (to be implemented in future)
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //            case REQUEST_CONNECT_DEVICE_SECURE:
        //                // When DeviceListActivity returns with a device to connect
        //                if (resultCode == AppCompatActivity.RESULT_OK) {
        //                    connectDevice(data, true);
        //                }
        //                break;
        //            case REQUEST_CONNECT_DEVICE_INSECURE:
        //                // When DeviceListActivity returns with a device to connect
        //                if (resultCode == AppCompatActivity.RESULT_OK) {
        //                    connectDevice(data, false);
        //                }
        //                break;
        if (requestCode == REQUEST_ENABLE_BT) {// When the request to enable Bluetooth returns
            if (resultCode != AppCompatActivity.RESULT_OK) {
                // User did not enable Bluetooth or an error occurred
                Toast.makeText(this, "Bluetooth is essential for this functionality to work!", Toast.LENGTH_LONG).show();
                super.finish();
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this,"Fine Location Permission not available! Please grant it to continue!",Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ENABLE_FINE_LOCATION);
        }

        //Creating a new handler and binding it with a callback fn: runnable
        handler = new Handler();
        handler.postDelayed(runnable, 100);

        // Register for broadcasts when a device is discovered.
        mIntentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        mIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        BluetoothActivity.this.registerReceiver(receiver, mIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Stop the handler
        handler.removeCallbacks(runnable);
        //unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }

    @Override
    public void onPause() {

        super.onPause();
        if(mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Update UI depending upon Bluetooth state
        updateUI();
    }

    /**
     * Update UI according to Bluetooth State
     */
    public void updateUI() {
        final String[] BLUETOOTH_STATE = {"BLUETOOTH OFF", "BLUETOOTH TURNING ON", "BLUETOOTH ON", "BLUETOOTH TURNING OFF"};
        switch (mBluetoothAdapter.getState()) {
            case BluetoothAdapter.STATE_ON:
                mBluetoothStateText.setText(BLUETOOTH_STATE[2]);
                mBluetoothStateChangeBtn.setText("TURN " + BLUETOOTH_STATE[0]);
                mBluetoothStateChangeBtn.setOnClickListener(view ->
                        mBluetoothAdapter.disable()
                );
                mSelectDeviceDisplayText.setVisibility(View.VISIBLE);
                mFindDevicesBtn.setVisibility(View.VISIBLE);
                break;

            case BluetoothAdapter.STATE_OFF:
                mBluetoothStateText.setText(BLUETOOTH_STATE[0]);
                mBluetoothStateChangeBtn.setText("TURN " + BLUETOOTH_STATE[2]);
                mNameOfDevices.clear();
                mListOfDevices.notifyDataSetChanged();
                mBluetoothStateChangeBtn.setOnClickListener(view -> {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                });
                mSelectDeviceDisplayText.setVisibility(View.INVISIBLE);
                mFindDevicesBtn.setVisibility(View.INVISIBLE);
                break;

            case BluetoothAdapter.STATE_TURNING_OFF:
                mBluetoothStateText.setText(BLUETOOTH_STATE[3]);
                mBluetoothStateChangeBtn.setText(BLUETOOTH_STATE[3]);
                mBluetoothStateChangeBtn.setOnClickListener(view ->
                        Toast.makeText(this, "not allowed", Toast.LENGTH_SHORT).show()
                );
                mSelectDeviceDisplayText.setVisibility(View.INVISIBLE);
                mFindDevicesBtn.setVisibility(View.INVISIBLE);
                break;

            case BluetoothAdapter.STATE_TURNING_ON:
                mBluetoothStateText.setText(BLUETOOTH_STATE[1]);
                mBluetoothStateChangeBtn.setText(BLUETOOTH_STATE[1]);
                mBluetoothStateChangeBtn.setOnClickListener(view ->
                        Toast.makeText(this, "not allowed", Toast.LENGTH_SHORT).show()
                );
                mSelectDeviceDisplayText.setVisibility(View.INVISIBLE);
                mFindDevicesBtn.setVisibility(View.INVISIBLE);
                break;
        }
    }

    /**
     * Main function for managing Bluetooth-related activities. Performs discovery, makes connection, etc.
     * Calls checkForBondedDevices and startDeviceDiscovery
     */

    public void setUpBluetooth() {
        boolean deviceFound = checkForBondedDevices();
        if (!deviceFound)
            startDeviceDiscovery();
    }

    /**Called from setUpBluetooth(). Checks if paired devices are available. Returns true if device
     * to connect to has already been paired with in the past. Returns false if no paired devices
     * are found or if the required device is not found.
     * @return Boolean
     */

    public boolean checkForBondedDevices()
    {
        mPairedDevices = mBluetoothAdapter.getBondedDevices();
        if (mPairedDevices.size() > 0) {
            mProgressDialog.setMessage("Checking if the device to connect has already been paired with in the past...");
            mProgressDialog.show();
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : mPairedDevices) {
                String deviceHardwareAddress = device.getAddress(); // MAC address
                String deviceName = device.getName();
                if (deviceName.equals(mDeviceToConnect)) {
                    mConnectedDeviceName = deviceName;
                    mConnectedDeviceHardwareAddress = deviceHardwareAddress;
                    Toast.makeText(this, "Device Found!", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                    return true;
                }
            }

            if (mProgressDialog.isShowing())
                mProgressDialog.dismiss();
            Toast.makeText(this, "Device required not found among paired devices, moving on to device discovery...",
                        Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    public void startDeviceDiscovery()
    {
        Toast.makeText(this, "Starting Device Discovery, please do not close this app now...", Toast.LENGTH_SHORT).show();
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
//        {
//            Toast.makeText(this,"Coarse Location Permission not available! Please grant it to continue!",Toast.LENGTH_SHORT).show();
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ENABLE_COARSE_LOCATION);
//        }

        if (mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery();
        mBluetoothAdapter.startDiscovery();

    }

    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;
        private BluetoothDevice mmDevice;
        private final UUID MY_UUID =  UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //This is random. Change as necessary.

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            runOnUiThread(new Runnable() {
                public void run() {
                    // Cancel discovery because it otherwise slows down the connection.
                    mBluetoothAdapter.cancelDiscovery();

                    try {
                        // Connect to the remote device through the socket. This call blocks
                        // until it succeeds or throws an exception.
                        mmSocket.connect();

                        Toast.makeText(BluetoothActivity.this, "Device connected",
                                Toast.LENGTH_SHORT).show();
                    } catch (IOException connectException) {
                        // Unable to connect; move on to fallback socket.
                        //Log.e(TAG, "Connect Exception", connectException);
                        try {
                            Log.e("","trying fallback...");

                            mmSocket =(BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(mmDevice,1);
                            mmSocket.connect();

                            Toast.makeText(BluetoothActivity.this, "Device connected",
                                    Toast.LENGTH_SHORT).show();
                            Log.e("","Connected");
                        } catch (Exception connectException2) {
                            Log.e(TAG, "Connect Exception", connectException2);
                            Toast.makeText(BluetoothActivity.this, "Unable to connect",
                                    Toast.LENGTH_SHORT).show();

                            try {
                                mmSocket.close();
                            }
                            catch(Exception closeException) {
                                Log.e(TAG, "Could not close the client socket", closeException);
                            }
                            return;
                        }
                    }
                }
            });

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            //manageMyConnectedSocket(mmSocket);

        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

}