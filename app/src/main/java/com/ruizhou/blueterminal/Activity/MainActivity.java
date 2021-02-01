package com.ruizhou.blueterminal.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ruizhou.blueterminal.Adapter.BLE_DevicesAdapter;
import com.ruizhou.blueterminal.BLE_Service;
import com.ruizhou.blueterminal.Data.BroadcastReceiver_BTState;
import com.ruizhou.blueterminal.Data.BLE_Device;
import com.ruizhou.blueterminal.Data.UUID_status;
import com.ruizhou.blueterminal.R;
import com.ruizhou.blueterminal.Utils.Utils_functions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_ENABLE_BT = 1;
    private final String TAG = "MainActivity_TEST";

    public BLE_Service getBle() {
        return ble;
    }

    //private BluetoothAdapter ba;
    public static BLE_Service ble;

    public static Context context;
    public static String read_data;
//    public static ArrayList<String> nameList;
    private AlertDialog.Builder alert;

    private Button turnOn;
    private Button turnOff;
    private Button scan;

    private RecyclerView deviceView;
    private RecyclerView.Adapter deviceAdapter;
    private List<BLE_Device> listItems;
    private UUID_status uuid_status;


    private HashMap<String, BLE_Device> mBTDevicesHashMap;
    private ArrayList<BLE_Device> mBTDevicesArrayList;
    //private ListAdapter_BLE_Device listAdapter;

    private BroadcastReceiver_BTState mBTStateUpdateReceiver;





   @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Testing bluetooth low energy
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Utils_functions.toast(MainActivity.this, "This service does not support bluetooth energy" );

        }

        // Bluetooth backend adapter
       // ba = BluetoothAdapter.getDefaultAdapter();
        ble = new BLE_Service(MainActivity.this, this);
        alert = new AlertDialog.Builder(MainActivity.this);
        context = MainActivity.this;
        read_data = "DEADBEEF";
//        nameList = new ArrayList<String>();

        //UI setup
        UIsetup();
        //mBTDevicesArrayList = new ArrayList<>();
        listItems = new ArrayList<>();
        mBTDevicesHashMap = new HashMap<>();
        deviceAdapter = new BLE_DevicesAdapter(this,listItems, ble);
        deviceView.setAdapter(deviceAdapter);


        //Listener Setting
        turnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ble.getmBluetoothAdapter().enable()){
                    Toast.makeText(getApplicationContext(),
                            "Bluetooth Already On", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent turnOnApp = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(turnOnApp,0);
                    Toast.makeText(getApplicationContext(),
                            "Bluetooth Turn On", Toast.LENGTH_SHORT).show();
                }

            }
        });
        turnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ble.getmBluetoothAdapter().disable();
                Utils_functions.toast(MainActivity.this,"Bluetooth Turn Off");

            }
        });
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),
                        "Scanning", Toast.LENGTH_SHORT).show();
                ble.ScanningStart();
//                Set<BluetoothDevice> pairDevices = ble.getmBluetoothAdapter().getBondedDevices();
//                for(BluetoothDevice bt: pairDevices) addDevice(bt,1);
                int a = listItems.size();
                Log.d(TAG,""+a);
            }
        });



    }


    private void UIsetup(){
        turnOn = (Button) findViewById(R.id.button_main_turnOn);
        turnOff = (Button) findViewById(R.id.button_main_turnOff);
        scan = (Button) findViewById(R.id.button_main_scan);
        deviceView = (RecyclerView) findViewById(R.id.recyclerView_main);
        deviceView.setHasFixedSize(true);
        deviceView.setLayoutManager(new LinearLayoutManager(this));
    }
    public void addDevice(BluetoothDevice device, int rssi){
       if(device == null){
           Log.d(TAG,"wtf");
       }
       else{
           String address = device.getAddress();
           if(!mBTDevicesHashMap.containsKey(address)){
               BLE_Device bltDevice = new BLE_Device(device,rssi);
               listItems.add(bltDevice);
               mBTDevicesHashMap.put(address,bltDevice);
           }
           else{
               mBTDevicesHashMap.get(address).setRssi(rssi);
           }
           deviceAdapter.notifyDataSetChanged();


       }

    }




}