package com.example.Maatran;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;


//This Activity handles all BluetoothActivity
//NOTE: Ignore all 'errors' related to call 'requires permissions which may be rejected by the user..' and such..
//BLUETOOTH_ADMIN is declared in the manifest so there's no need to ask for permissions
public class BluetoothActivity extends AppCompatActivity {

    //private data members corresponding to views in the layout

    /**Start data transmission*/
    private Button mStartDeviceTransmissionBtn;

    /**Controls changes to Bluetooth state [CONNECT/DISCONNECT]*/
    private Button mBluetoothStateChangeBtn;

    /**Displays a list of remote devices*/
    private ListView mBluetoothDeviceList;

    /**Displays 'Select a Device to connect to'*/
    private TextView mSelectDeviceDisplayText;

    /**Displays the current state of Bluetooth [CONNECTED/DISCONNECTED]*/
    private TextView mBluetoothStateText;

    /**Opens Bluetooth settings for changing the name of the device to connect. Modifies: {@link BluetoothActivity#mDeviceToConnect} */
    private ImageView mOpenBluetoothSettings;

    /**Displays status (device discovery, connected, etc.)*/
    private TextView mStatusBarText;

    /**Btn for enabling device discovery*/
    private Button mFindDevicesBtn;

    /**ArrayAdapter for ListView mBluetoothDeviceList*/
    private ArrayAdapter<String> mListOfDevices;

    /**ArrayList for ArrayAdapter mListOfDevices*/
    private ArrayList<String> mNameOfDevices;

    /**Handler for updating UI after a specific time interval*/
    private Handler mHandlerForBluetoothBroadcasts;

    /**IntentFilter for handling Broadcasts*/
    IntentFilter mIntentFilter;

    //For debugging
    private static final String TAG = "BluetoothActivity";

    //CODES TO INDICATE BLUETOOTH STATUS
    /**Codes for requesting permissions, is used in onActivityResult*/
    private static final int REQUEST_ENABLE_BT = 3;
    private static final int REQUEST_ENABLE_FINE_LOCATION = 5;

    /**0 if bluetooth is not enabled, 1 otherwise*/
    private static int BLUETOOTH_STATUS;

    /**2 if some device is connected, 1 if attempting to connect to some device, 0 otherwise*/
    private static int BLUETOOTH_CONNECTION_STATUS;

    /**1 if device discovery is in action, 0 if not, 2 if device discovery has finished**/
    private static int BLUETOOTH_DISCOVERY_STATUS;

    // Message types sent from the BluetoothChatService Handler
    static final int MESSAGE_STATE_CHANGE = 1;
    static final int MESSAGE_READ = 2;
    static final int MESSAGE_WRITE = 3;
    static final int MESSAGE_DEVICE_NAME = 4;
    static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    static final String DEVICE_NAME = "device_name";
    static final String TOAST = "toast";

    //TODO Stores the name of the device to connect to. Will be different for each patient and

    /**will be received from the calling Activity (to be implemented later)*/
    private String mDeviceToConnect;

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

    ArrayList<BluetoothDevice> mDiscoveredBluetoothDevices;

    /**
     * Global BluetoothChatService instance
     */
    BluetoothChatService mBluetoothChatService;
    BluetoothTransmissionService mBtService;

    /**
     * File stored in cache memory containing user data, including the name of the device to connect
     */
    File mCachedUserDataFile;

    /**
     * Runnable for the mHandlerForBluetoothBroadcasts
     * Callback fn run() is called every 100ms
     */
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {

            //Update UI
            updateUI();

            //Update Status Bar text
            updateStatusText();

            //call it again after 100ms
            mHandlerForBluetoothBroadcasts.postDelayed(this, 100);
        }
    };

    /**
     * Create a BroadcastReceiver for ACTION_FOUND.
     * Broadcasts when a device is found upon device discovery
     */

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.S)
        public void onReceive(Context context, Intent intent) {

            //Broadcast device discovery is in action
            BLUETOOTH_DISCOVERY_STATUS = 1;

            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                if(!mDiscoveredBluetoothDevices.contains(device)) {
                    mDiscoveredBluetoothDevices.add(device);
                    if(deviceName!=null)
                        mNameOfDevices.add(deviceName);
                    else
                        mNameOfDevices.add(device.getAddress());
                    mListOfDevices.notifyDataSetChanged();
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Toast.makeText(BluetoothActivity.this, "Device Discovery started...", Toast.LENGTH_SHORT).show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(BluetoothActivity.this, "Device Discovery finished...", Toast.LENGTH_SHORT).show();

                //Broadcast that device discovery has finished
                BLUETOOTH_DISCOVERY_STATUS = 2;
            }
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_activity_screen);

        //Initializing data members
        mBluetoothStateChangeBtn = findViewById(R.id.bluetooth_service_connect_btn);
        mBluetoothDeviceList = findViewById(R.id.device_list_lv);
        mSelectDeviceDisplayText = findViewById(R.id.select_device_text);
        mSelectDeviceDisplayText.setVisibility(View.INVISIBLE);
        mBluetoothStateText = findViewById(R.id.bluetooth_service_state_text);
        mStatusBarText = findViewById(R.id.status_bar_text);
        mStatusBarText.setVisibility(View.INVISIBLE);

        //Initializing Bluetooth status codes
        BLUETOOTH_CONNECTION_STATUS = 0;
        BLUETOOTH_DISCOVERY_STATUS = 0;
        BLUETOOTH_STATUS = 0;

        mFindDevicesBtn = findViewById(R.id.find_devices_btn);
        mFindDevicesBtn.setVisibility(View.INVISIBLE);

        //TODO change code
        //Initializing the Bluetooth settings btn and binding it with a callback:
        mOpenBluetoothSettings = findViewById(R.id.bluetooth_settings);
        mOpenBluetoothSettings.setOnClickListener(this::changeDeviceToConnect);

        //TODO This is the button for starting data transmissions.
        mStartDeviceTransmissionBtn = findViewById(R.id.start_transmission_btn);
        mStartDeviceTransmissionBtn.setVisibility(View.GONE);
        mStartDeviceTransmissionBtn.setOnClickListener(this::startDeviceTransmissions);

        mFindDevicesBtn.setOnClickListener(v->checkForDeviceToConnect());
        mDiscoveredBluetoothDevices = new ArrayList<>();
        mNameOfDevices = new ArrayList<>();
        mListOfDevices = new ArrayAdapter<>(getApplicationContext(), R.layout.listview_elements,R.id.device_list_item_lv,mNameOfDevices);
        mBluetoothDeviceList.setAdapter(mListOfDevices);
        mBluetoothDeviceList.setOnItemClickListener(
                (parent, view, position, id) -> {
                    String deviceName = mBluetoothDeviceList.getItemAtPosition(position).toString();
                    Toast.makeText(BluetoothActivity.this, "Connecting to " + deviceName, Toast.LENGTH_SHORT).show();
                    if(mBluetoothChatService.getState() != BluetoothChatService.STATE_NONE)
                        mBluetoothChatService.stop();
                    String deviceToConnectAddress = mDiscoveredBluetoothDevices.get(position).getAddress();
                    BluetoothDevice deviceToConnect = mBluetoothAdapter.getRemoteDevice(deviceToConnectAddress);
                      mBluetoothChatService.connect(deviceToConnect);
                }
        );

        //Creating a new mHandlerForBluetoothBroadcasts and binding it with a callback fn: runnable
        mHandlerForBluetoothBroadcasts = new Handler();
        mHandlerForBluetoothBroadcasts.postDelayed(runnable, 100);


        //Register for broadcasts for device discovery
        mIntentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        mIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        BluetoothActivity.this.registerReceiver(receiver, mIntentFilter);

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
        if (requestCode == REQUEST_ENABLE_BT) {// When the request to enable Bluetooth returns
            if (resultCode != AppCompatActivity.RESULT_OK) {

                // User did not enable Bluetooth or an error occurred
                Toast.makeText(this, "Bluetooth is essential for this functionality to work!", Toast.LENGTH_LONG).show();
                super.finish();
            }

            //Bluetooth has been turned on. Start the Chat service and initialize other data members as necessary.
            BLUETOOTH_STATUS = 1;
            BLUETOOTH_CONNECTION_STATUS = 0;
            BLUETOOTH_DISCOVERY_STATUS = 0;
            startBluetoothChatService();
            if(mBluetoothAdapter.isDiscovering())
                mBluetoothAdapter.cancelDiscovery();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        checkForPermissions();
        //Check if Bluetooth is turned on or not, THIS IS ESSENTIAL AS THE APP WILL CRASH IF THIS IS NOT THE CASE
        if(mBluetoothAdapter.getState()!=BluetoothAdapter.STATE_ON) {
            turnOnBluetooth();
        }
        else{
            if(mBluetoothChatService==null)
                startBluetoothChatService();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //Stop doing discovery
        if(mBluetoothAdapter!=null)
            mBluetoothAdapter.cancelDiscovery();

        //Stop Chat Service
        if (mBluetoothChatService != null) {
            mBluetoothChatService.stop();
        }

        //Stop the mHandlerForBluetoothBroadcasts
        mHandlerForBluetoothBroadcasts.removeCallbacks(runnable);

        //unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }

    /**Checks if the required permissions are available. Is only called after enabling Bluetooth or when the app was resumed*/
    @Override
    public void onResume() {
        super.onResume();
        if(mBluetoothChatService != null)
        {
            if(mBluetoothChatService.getState() == BluetoothChatService.STATE_NONE)
                mBluetoothChatService.start();
        }
        //Update UI depending upon Bluetooth state
        updateUI();
        if(mDeviceToConnect==null)
            manageCachedFiles(false);
    }

    public void checkForPermissions()
    {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
//        {
//            Log.d(TAG,"Access Fine location permission not available");
//            Toast.makeText(this,"Fine Location Permission not available! Please grant it to continue!",Toast.LENGTH_SHORT).show();
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ENABLE_FINE_LOCATION);
//        }
        if (ContextCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_DENIED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            {
                Log.d(TAG,"Bl scan location permission not available");
                ActivityCompat.requestPermissions(BluetoothActivity.this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 9);
                return;
            }
        }
        if (ContextCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            {
                Log.d(TAG,"Bl connect permission not available");
                ActivityCompat.requestPermissions(BluetoothActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 8);
                return;
            }
        }
    }

    /*Turns on Bluetooth*/
    public void turnOnBluetooth()
    {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    /**Initializes and starts BluetoothChatService*/
    public void startBluetoothChatService()
    {
        if(mBluetoothChatService==null)
            mBluetoothChatService = new BluetoothChatService(BluetoothActivity.this,mChatServiceHandler);
        mBluetoothChatService.start();
    }


    /**
     * Manages file operations - creation, updation
     * @param update boolean object that indicates whether updation is to be performed or not
     * */
    //TODO Implement caching
    public void manageCachedFiles(boolean update)
    {
        //Create objects for file handling
        Scanner fileScanner = null;
        StringBuffer fileContents = null;
        FileWriter fileWriter = null;
        String tempDeviceName = null;

        //Try creating a file object, do it only once
        if(mCachedUserDataFile==null){
            mCachedUserDataFile = new File(this.getFilesDir(), "maatran_user_data_final.tmp");
        }

        //Check if file exists or not
        if(!mCachedUserDataFile.exists())
        {

            //File does not exist, create the file and write to it
            try{
//                mCachedUserDataFile=File.createTempFile("maatran_user_data.tmp", null, this.getCacheDir());
                boolean check = mCachedUserDataFile.createNewFile();
                if(mCachedUserDataFile.exists())
                {
                    Log.v(TAG,"File created successfully");
                }
                else {
                    Log.v(TAG, "File doesn't exist");
                    return;
                }
            }
            catch (Exception e)
            {
                Log.e(TAG,"Failed to create the file");
                return;
            }

        }

        //Instantiate the objects needed for file handling
        try {
            fileScanner = new Scanner(mCachedUserDataFile);
            fileScanner.useDelimiter("\n");
        }
        catch (Exception e)
        {
            Log.e(TAG,"Could not create Scanner object!");
            return;
        }
        try {
            fileWriter = new FileWriter(mCachedUserDataFile, true);
        }
        catch (Exception e)
        {
            Log.e(TAG,"Could not create FileWriter object!");
        }
        fileContents = new StringBuffer();


        //File exists
        //Check if the required field exists or not
        while(fileScanner.hasNext())
        {
            String temp = fileScanner.next();
            if(temp.contains("device_to_connect"))
            {
                //field exists
                tempDeviceName = temp.substring(temp.indexOf(":")+1);
                //exit func
            }
        }


        //No such field present
        if(tempDeviceName==null)
        {
            //Create a temporary field
            assert fileWriter != null;
            try{
                fileWriter.append("\ndevice_to_connect:PLACEHOLDER\n");
            }
            catch (Exception e)
            {
                Log.e(TAG,"Failed to write to file");
            }
            //Open bluetooth Settings to ask user to enter a device name
//            changeDeviceToConnect(mOpenBluetoothSettings);
            tempDeviceName = "PLACEHOLDER";
        }


        //TODO TESTING
        //This block will only execute when the device name needs to be changed
        //It assumes that both the file as well as the field exists.
        if(update)
        {
            try {
                String oldDeviceName = null;
                fileScanner = new Scanner(mCachedUserDataFile);
                fileScanner.useDelimiter("\n");
                while(fileScanner.hasNext())
                {
                    String temp = fileScanner.next();
                    if(temp.contains("device_to_connect")) {
                        temp+="\n";
                        oldDeviceName = temp;
                    }
                    fileContents.append(temp);
                }
                String oldFileContents = fileContents.toString();
                if(oldDeviceName==null)
                {
                    //oldDeviceName will be null iff file does not contain field
                    Log.v(TAG,"Failed to update! Field does not exist!");
                    fileScanner.close();
                    return;
                }
                String newFileContents = oldFileContents.replace(oldDeviceName,"device_to_connect:"+mDeviceToConnect+"\n");
                try (FileOutputStream fos = this.openFileOutput("maatran_user_data_final.tmp", Context.MODE_PRIVATE)) {
                    fos.write(newFileContents.getBytes());
                }
            }
            catch (Exception e)
            {
                Log.e(TAG,"Failed to update the file!");
                return;
            }

        }
        else
        {
            //We only need the name of the device to connect
            mDeviceToConnect = tempDeviceName;
        }

        //Close all the streams
        try {
            if (fileWriter != null)
                fileWriter.close();
            fileScanner.close();
        }
        catch (Exception e)
        {
            Log.e(TAG,"Failed to close streams"+e);
        }
    }

    /**
     * Update UI according to Bluetooth State
     */
    public void updateUI() {
        final String[] BLUETOOTH_STATE = {"BLUETOOTH OFF", "BLUETOOTH TURNING ON", "BLUETOOTH ON", "BLUETOOTH TURNING OFF"};
        switch (mBluetoothAdapter.getState()) {
            case BluetoothAdapter.STATE_ON:
                mBluetoothStateText.setText(BLUETOOTH_STATE[2]);
                mBluetoothStateText.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                BLUETOOTH_STATUS=1;
                mBluetoothStateChangeBtn.setText("TURN " + BLUETOOTH_STATE[0]);
                mBluetoothStateChangeBtn.setOnClickListener(view ->
                        {
                            mBluetoothChatService.stop();
                            mBluetoothAdapter.disable();
                            BLUETOOTH_STATUS = 0;
                        }
                );
                mStatusBarText.setVisibility(View.VISIBLE);
                mSelectDeviceDisplayText.setVisibility(View.VISIBLE);
                mFindDevicesBtn.setVisibility(View.VISIBLE);
                break;

            case BluetoothAdapter.STATE_OFF:
                mBluetoothStateText.setText(BLUETOOTH_STATE[0]);
                mBluetoothStateText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                mBluetoothStateChangeBtn.setText("TURN " + BLUETOOTH_STATE[2]);
                mNameOfDevices.clear();
                mListOfDevices.notifyDataSetChanged();
                mBluetoothStateChangeBtn.setOnClickListener(view -> turnOnBluetooth());
                mStatusBarText.setVisibility(View.INVISIBLE);
                mSelectDeviceDisplayText.setVisibility(View.INVISIBLE);
                mFindDevicesBtn.setVisibility(View.INVISIBLE);
                break;

            case BluetoothAdapter.STATE_TURNING_OFF:
                mBluetoothStateText.setText(BLUETOOTH_STATE[3]);
                mBluetoothStateText.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                mBluetoothStateChangeBtn.setText(BLUETOOTH_STATE[3]);
                mBluetoothStateChangeBtn.setOnClickListener(view ->
                        Toast.makeText(this, "not allowed", Toast.LENGTH_SHORT).show()
                );
                BLUETOOTH_STATUS = 0;
                BLUETOOTH_CONNECTION_STATUS = 0;
                BLUETOOTH_DISCOVERY_STATUS = 0;
                mStatusBarText.setVisibility(View.INVISIBLE);
                mSelectDeviceDisplayText.setVisibility(View.INVISIBLE);
                mFindDevicesBtn.setVisibility(View.INVISIBLE);
                break;

            case BluetoothAdapter.STATE_TURNING_ON:
                mBluetoothStateText.setText(BLUETOOTH_STATE[1]);
                mBluetoothStateText.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                mBluetoothStateChangeBtn.setText(BLUETOOTH_STATE[1]);
                mBluetoothStateChangeBtn.setOnClickListener(view ->
                        Toast.makeText(this, "not allowed", Toast.LENGTH_SHORT).show()
                );
                BLUETOOTH_STATUS = 1;
                BLUETOOTH_CONNECTION_STATUS = 0;
                BLUETOOTH_DISCOVERY_STATUS = 0;
                mStatusBarText.setVisibility(View.INVISIBLE);
                mSelectDeviceDisplayText.setVisibility(View.INVISIBLE);
                mFindDevicesBtn.setVisibility(View.INVISIBLE);
                break;
        }
    }

    //TODO STATUS BAR NOT VISIBLE POST CONNECTION
    /**Controls Status Bar text and other views corresponding to device connections status*/
    private void updateStatusText()
    {
        //BLUETOOTH IS NOT ON
        if (BLUETOOTH_STATUS == 0) {
            mSelectDeviceDisplayText.setVisibility(View.GONE);
            mStatusBarText.setVisibility(View.GONE);
        }
        else {

            //BLUETOOTH IS ON
            mStatusBarText.setVisibility(View.VISIBLE);

            //CHECK IF ANY DEVICES ARE CONNECTED
            switch (BLUETOOTH_CONNECTION_STATUS)
            {
                case 0:
                    mStartDeviceTransmissionBtn.setVisibility(View.GONE);
                    switch (BLUETOOTH_DISCOVERY_STATUS)
                    {
                        case 0:
                            mSelectDeviceDisplayText.setVisibility(View.GONE);
                            mStatusBarText.setVisibility(View.VISIBLE);
                            mStatusBarText.setText("Not connected to any devices!");
                            mStatusBarText.setTextColor(getResources().getColor(android.R.color.darker_gray));
                            break;

                        case 1:
                            mSelectDeviceDisplayText.setVisibility(View.VISIBLE);
                            mStatusBarText.setText("Device Discovery started...");
                            mStatusBarText.setTextColor(getResources().getColor(android.R.color.darker_gray));
                            break;

                        case 2:
                            //Check if devices are found or not
                            if (mListOfDevices.getCount() == 0) {
                                mSelectDeviceDisplayText.setVisibility(View.GONE);
                                mStatusBarText.setText("Device Discovery has ended, No device found...");
                                mStatusBarText.setTextColor(getResources().getColor(android.R.color.darker_gray));
                            } else {
                                mStatusBarText.setText("Device Discovery has ended, No device connected...");
                                mSelectDeviceDisplayText.setVisibility(View.VISIBLE);
                                mStatusBarText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                            }
                            break;
                    }
                    break;

                case 1:
                    mSelectDeviceDisplayText.setVisibility(View.GONE);
                    mStartDeviceTransmissionBtn.setVisibility(View.GONE);
                    mStatusBarText.setText("Attempting to Connect...");
                    mStatusBarText.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                    break;

                case 2:
                    mSelectDeviceDisplayText.setVisibility(View.GONE);
                    mStartDeviceTransmissionBtn.setVisibility(View.VISIBLE);
                    mStatusBarText.setText("Connected with "+mConnectedDeviceName);
                    mStatusBarText.setTextColor(getResources().getColor(android.R.color.holo_green_light));
            }
        }
    }


    public void checkForDeviceToConnect(){
        //Call manageCachedFiles to initialize mDeviceToConnect
        if(mDeviceToConnect.equals("PLACEHOLDER"))
            changeDeviceToConnect(mOpenBluetoothSettings);
        else
            setUpBluetooth();
    }

    /**
     * Main function for managing Bluetooth-related activities. Performs discovery, makes connection, etc.
     * Calls checkForBondedDevices and startDeviceDiscovery
     */

    public void setUpBluetooth() {
        //clear the existing list of devices
        mDiscoveredBluetoothDevices.clear();
        mNameOfDevices.clear();
        mListOfDevices.notifyDataSetChanged();

        if(mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery();

        //Ensure that the current device is discoverable to begin listening to connection attempts
        ensureDiscoverable();
        boolean deviceFound = checkForBondedDevices();
        if (!deviceFound)
            startDeviceDiscovery();
    }

    /**
     * Opens a popup window that allows the user to change the device name that is to be located.
     * @param view: The view that was clicked
     * */
    public void changeDeviceToConnect(View view)
    {
        View popupChangeDeviceToConnect = getLayoutInflater().inflate(R.layout.popupview_editdevicetoconnect,null);
        EditText editText = popupChangeDeviceToConnect.findViewById(R.id.edited_device_name);
        if(mDeviceToConnect.equals("PLACEHOLDER"))
            ((TextView) popupChangeDeviceToConnect.findViewById(R.id.text_dialog)).setText("Enter the name of the device to connect:");
        else {
            ((TextView) popupChangeDeviceToConnect.findViewById(R.id.text_dialog)).setText("Change the name of the device to connect:");
            editText.setText(mDeviceToConnect);
        }

        PopupWindow popupWindow = new PopupWindow(popupChangeDeviceToConnect, LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,true);
        popupWindow.showAtLocation(view, Gravity.CENTER,0,0);
        popupChangeDeviceToConnect.findViewById(R.id.btn_ok).setOnClickListener(v-> {
            mDeviceToConnect = editText.getText().toString().trim();
            if(mDeviceToConnect.trim().length()>0)
            manageCachedFiles(true);
            else
            {
                Toast.makeText(BluetoothActivity.this,"Device name field is empty!",Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this,"Successfully changed device name",Toast.LENGTH_SHORT).show();
            popupWindow.dismiss();
            setUpBluetooth();
        });
        popupChangeDeviceToConnect.findViewById(R.id.btn_cancel).setOnClickListener(v->popupWindow.dismiss());
    }


    /**Called from setUpBluetooth(). Checks if paired devices are available. Returns true if device
     * to connect to has already been paired with in the past. Returns false if no paired devices
     * are found or if the required device is not found.
     * @return Boolean
     */

    public boolean checkForBondedDevices() {
        mStatusBarText.setVisibility(View.VISIBLE);
        mStatusBarText.setText("Checking for bonded devices, please wait...");
        mStatusBarText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        Set<BluetoothDevice> pairedDevices;
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceHardwareAddress = device.getAddress(); // MAC address
                String deviceName = device.getName();
                if (deviceName.equals(mDeviceToConnect)) {
                    mConnectedDeviceName = deviceName;
                    mConnectedDeviceHardwareAddress = deviceHardwareAddress;
                    Toast.makeText(this, "Device Found!", Toast.LENGTH_SHORT).show();
                    if (!mNameOfDevices.contains(deviceName)) {
                          mDiscoveredBluetoothDevices.add(device);
                          mNameOfDevices.add(deviceName);
                    }
                    mListOfDevices.notifyDataSetChanged();
                    return true;
                }
            }
            Toast.makeText(this, "Device required not found among paired devices, moving on to device discovery...",
                    Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /**
     * Starts Device Discovery
     * */
    public void startDeviceDiscovery()
    {
        mStatusBarText.setVisibility(View.VISIBLE);
        mStatusBarText.setText("Performing device discovery, please wait...");
        mStatusBarText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        Toast.makeText(this, "Starting Device Discovery, please do not close this app now...", Toast.LENGTH_SHORT).show();
        if (mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery();
        mBluetoothAdapter.startDiscovery();

    }

    /**
     * Makes this device discoverable for 300 seconds (5 minutes).
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    //TODO Implement data transmissions here:
    /**
     * Starts device transmissions (read/write)
     * @param view: view that is clicked
     * */
    public void startDeviceTransmissions(View view) {

        mBtService = new BluetoothTransmissionService(mBluetoothChatService);
        mBtService.startRequestThread();
//        String message="Hi!This is ";
//        // Check that we're actually connected before trying anything
//        if (mBluetoothChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
//            Toast.makeText(this, "not_connected", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Get the message bytes and tell the BluetoothChatService to write
//        byte[] send = message.getBytes();
//        mBluetoothChatService.write(send);
    }


    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    Handler mChatServiceHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            //update status codes
                            BLUETOOTH_CONNECTION_STATUS=2;
                            if(mBluetoothAdapter.isDiscovering())
                                mBluetoothAdapter.cancelDiscovery();
                            BLUETOOTH_DISCOVERY_STATUS = 0;
                            Toast.makeText(BluetoothActivity.this,"DEVICE CONNECTED",Toast.LENGTH_SHORT).show();
                            mListOfDevices.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            BLUETOOTH_CONNECTION_STATUS=1;
                            BLUETOOTH_DISCOVERY_STATUS = 0;
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Toast.makeText(BluetoothActivity.this,mConnectedDeviceName + ":  " + readMessage,Toast.LENGTH_SHORT).show();
                    if(mBtService!=null)
                        mBtService.responseReceived(readMessage);
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(BluetoothActivity.this, "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(BluetoothActivity.this, msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    if(Objects.equals(msg.getData().getString(TOAST), "Unable to connect device") || Objects.equals(msg.getData().getString(TOAST), "Device connection was lost"))
                    {
                        //reset status codes
                        BLUETOOTH_CONNECTION_STATUS = 0;
                        BLUETOOTH_DISCOVERY_STATUS = 0;
                    }

                    break;
            }
            return false;
        }
    });
}