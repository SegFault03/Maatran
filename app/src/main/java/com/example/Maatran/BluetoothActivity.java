package com.example.Maatran;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class BluetoothActivity extends AppCompatActivity {

    private Button mBluetoothState;
    private ListView mBluetoothDeviceList;
    private TextView mSelectDeviceText;
    ProgressDialog progressDialog;
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
    private BluetoothService mChatService = null;                   //TODO

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_activity_screen);

        //Initializing data members
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        mBluetoothState = findViewById(R.id.bluetooth_service_connect_btn);
        mBluetoothDeviceList = findViewById(R.id.device_list_lv);
        mSelectDeviceText = findViewById(R.id.select_device_text);
        mSelectDeviceText.setVisibility(View.INVISIBLE);

        //Setting up bluetooth
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            super.finish();
        }

        //Setting an onClickListener to start Bluetooth
        mBluetoothState.setOnClickListener(v -> setUpBluetooth());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setUpBluetooth() {

        //Asking for user permission to turn on Bluetooth
        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Please turn on Bluetooth to proceed", Toast.LENGTH_SHORT).show();
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        //Makes this device discoverable for 300 seconds (5 minutes).
        progressDialog.setMessage("Setting up Bluetooth, please wait...");
        progressDialog.show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivity(discoverableIntent);
        }

        mSelectDeviceText.setVisibility(View.VISIBLE);
        String[] array = {"Niladri","Rahul","Abhishek"};
        ArrayAdapter<String> mListOfDevices = new ArrayAdapter<>(this, R.layout.listview_elements,R.id.device_list_item_lv, array);
        mBluetoothDeviceList.setAdapter(mListOfDevices);
    }
}