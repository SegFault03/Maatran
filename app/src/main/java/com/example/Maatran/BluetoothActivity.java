package com.example.Maatran;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class BluetoothActivity extends AppCompatActivity {

    //private data members corresponding to views in the layout
    private Button mBluetoothStateChangeBtn;
    private ListView mBluetoothDeviceList;
    private TextView mSelectDeviceText;
    private TextView mBluetoothStateText;
    ProgressDialog progressDialog;

    //Handler for updating UI after a specific time interval
    private Handler handler;

    private static final String TAG = "BluetoothActivity";

    //CODES TO INDICATE BLUETOOTH STATUS
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    //Name of connected device
    private String mConnectedDeviceName = null;

    //Global BluetoothAdapter class object to use Bluetooth features
    private BluetoothAdapter mBluetoothAdapter = null;

    //Global BluetoothService object
    //private BluetoothService mChatService = null;                   //TODO

    @RequiresApi(api = Build.VERSION_CODES.M)
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

        //Setting an onClickListener to start Bluetooth

    }

    @Override
    public void onStart() {
        super.onStart();
        updateUI();
    }

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