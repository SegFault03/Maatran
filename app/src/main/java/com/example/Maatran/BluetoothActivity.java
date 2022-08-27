package com.example.Maatran;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class BluetoothActivity extends AppCompatActivity {

    //private data members corresponding to views in the layout
    private Button mBluetoothStateChangeBtn;                    //Controls changes to Bluetooth state [CONNECT/DISCONNECT]
    private ListView mBluetoothDeviceList;                      //Displays a list of remote devices
    private TextView mSelectDeviceText;                         //Displays 'Select a Device to connect to'
    private TextView mBluetoothStateText;                       //Displays the current state of Bluetooth [CONNECTED/DISCONNECTED]
    ProgressDialog progressDialog;

    //Handler for updating UI after a specific time interval
    private Handler handler;

    //For debugging
    private static final String TAG = "BluetoothActivity";

    //CODES TO INDICATE BLUETOOTH STATUS
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;             //This code is returned if request to turn on Bluetooth was granted

    //TODO Stores the name of the device to connect to. Will be different for each patient and
    //will be received from the calling Activity (to be implemented later)
    private String mConnectedDeviceName = null;

    //Global BluetoothAdapter class object to use Bluetooth features
    private BluetoothAdapter mBluetoothAdapter = null;

    //Global BluetoothService object
    //private BluetoothService mChatService = null;                   //TODO  service for handling data transmissions

    //Runnable for the handler
    private final Runnable runnable = new Runnable()
    {
        @Override
        public void run() {
            //Update UI
            updateUI();
            //call it again after 100ms
            handler.postDelayed(this, 100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_activity_screen);

        //Initializing data members
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        mBluetoothStateChangeBtn = findViewById(R.id.bluetooth_service_connect_btn);
        mBluetoothDeviceList = findViewById(R.id.device_list_lv);
        mSelectDeviceText = findViewById(R.id.select_device_text);
        mSelectDeviceText.setVisibility(View.INVISIBLE);
        mBluetoothStateText=findViewById(R.id.bluetooth_service_state_text);

        //Creating a new handler and binding it with a callback fn: runnable
        handler=new Handler();
        handler.postDelayed(runnable, 100);

        //Setting up bluetooth
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            super.finish();
        }

        //Chek if app has been granted permission to use Bluetooth
        checkForPermissions();

    }

    @Override
    public void onStart() {
        super.onStart();
        //Update UI depending upon Bluetooth state
        updateUI();
    }

    //Checks if the required permissions have been granted
    public void checkForPermissions()
    {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            //if permission has not been granted, check if it is necessary to explain why this permission is required..
            //shouldShowRequestPermissionRationale() returns true ONLY if user has refused to grant access to permission
            //Returns false otherwise
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.BLUETOOTH)) {
                showExplanation("Permission Needed", "Rationale", Manifest.permission.BLUETOOTH, REQUEST_ENABLE_BT);
            } else {
                //request for the permission to be granted
                requestPermission(Manifest.permission.BLUETOOTH, REQUEST_ENABLE_BT);
            }
        } else {
            Toast.makeText(this, "Permission to use Bluetooth (already) Granted!", Toast.LENGTH_SHORT).show();
        }
    }

    /**Overrides onRequestPermissionsResult defined in ActivityCompat interface
    Will be called automatically when a permission is requested using  ActivityCompat.requestPermissions()
     @param requestCode: Code passed to ActivityCompat.requestPermissions()
     @param permissions: Permission requested
     @param grantResults: codes indicating whether permission has been granted or not
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }


    /**
     creates and shows an AlertDialog explaining the need of a permission to be granted
     @param title: title of the message to be displayed
     @param message: message to be displayed
     @param permission: permission for which the message is displayed
     @param permissionRequestCode: request code for the permission
     */
    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //If user clicks 'OK', permission is asked again
        //(dialog, id) is a callback function to decide what happens if user clicks 'OK'
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialog, id) -> requestPermission(permission, permissionRequestCode));
        builder.create().show();
    }

    /**
     * requests user to grant a specific permission using  ActivityCompat.requestPermissions()
     * @param permissionName: Name of the permission to be granted
     * @param permissionRequestCode: request code for the permission
     */
    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }

    //Update UI according to Bluetooth State
    public void updateUI()
    {
        final String[] BLUETOOTH_STATE={"BLUETOOTH OFF","BLUETOOTH TURNING ON","BLUETOOTH ON","BLUETOOTH TURNING OFF"};
        switch (mBluetoothAdapter.getState())
        {
            case BluetoothAdapter.STATE_ON:
                mBluetoothStateText.setText(BLUETOOTH_STATE[2]);
                mBluetoothStateChangeBtn.setText("TURN "+BLUETOOTH_STATE[0]);
                mBluetoothStateChangeBtn.setOnClickListener(view->
                    mBluetoothAdapter.disable()
                );
                break;
            case BluetoothAdapter.STATE_OFF:
                mBluetoothStateText.setText(BLUETOOTH_STATE[0]);
                mBluetoothStateChangeBtn.setText("TURN "+BLUETOOTH_STATE[2]);
                mBluetoothStateChangeBtn.setOnClickListener(view->
                        mBluetoothAdapter.enable()
                );
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                mBluetoothStateText.setText(BLUETOOTH_STATE[3]);
                mBluetoothStateChangeBtn.setText(BLUETOOTH_STATE[3]);
                mBluetoothStateChangeBtn.setOnClickListener(view->
                        Toast.makeText(this,"not allowed",Toast.LENGTH_SHORT).show()
                );
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                mBluetoothStateText.setText(BLUETOOTH_STATE[1]);
                mBluetoothStateChangeBtn.setText(BLUETOOTH_STATE[1]);
                mBluetoothStateChangeBtn.setOnClickListener(view->
                        Toast.makeText(this,"not allowed",Toast.LENGTH_SHORT).show()
                );
                break;
        }
    }



    /*public void setUpBluetooth() {


        mSelectDeviceText.setVisibility(View.VISIBLE);
        String[] array = {"Niladri","Rahul","Abhishek"};
        ArrayAdapter<String> mListOfDevices = new ArrayAdapter<>(this, R.layout.listview_elements,R.id.device_list_item_lv, array);
        mBluetoothDeviceList.setAdapter(mListOfDevices);
    }*/
}