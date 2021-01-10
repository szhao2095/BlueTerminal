package com.ruizhou.blueterminal;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
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
import com.ruizhou.blueterminal.Adapter.ListAdapter_BLE_Device;
import com.ruizhou.blueterminal.Data.BLE_Device;
import com.ruizhou.blueterminal.Data.UUID_status;
import com.ruizhou.blueterminal.Utils.Utils_functions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_ENABLE_BT = 1;
    private final String TAG = "MainActivity_TEST";

    //private BluetoothAdapter ba;
    private BLE_Service ble;
    private AlertDialog.Builder alert;

    private Button turnOn;
    private Button turnOff;
    private Button scan;
    private Button testReceive;
    private Button graphData;
    private Button sendButton;
    private Button dumpButton;
    private TextView testView;
    private EditText textInput;

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

        //UI setup
        UIsetup();
        //mBTDevicesArrayList = new ArrayList<>();
        listItems = new ArrayList<>();
        mBTDevicesHashMap = new HashMap<>();
        deviceAdapter = new BLE_DevicesAdapter(this,listItems, ble);
        deviceView.setAdapter(deviceAdapter);



        testReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ble.readData();
                testView.setText(ble.response.toString());
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                String content = textInput.getText().toString();
                try {
                    ble.writeData(content);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        dumpButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                ble.setFile(MainActivity.this); // Delete file if it exists and create new file
                try {
                    ble.writeData("DUMP#");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
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

        graphData.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                ble.setFile(MainActivity.this); // Delete file if it exists and create new file
                try {
                    ble.writeData("DUMP#");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String filename = ble.getFileListName();
                openGraph(filename);
            }
        });



    }


    private void UIsetup(){
        turnOn = (Button) findViewById(R.id.button_main_turnOn);
        turnOff = (Button) findViewById(R.id.button_main_turnOff);
        scan = (Button) findViewById(R.id.button_main_scan);
        graphData = (Button) findViewById(R.id.button_graph);
        deviceView = (RecyclerView) findViewById(R.id.recyclerView_main);
        deviceView.setHasFixedSize(true);
        deviceView.setLayoutManager(new LinearLayoutManager(this));

        testReceive = (Button) findViewById(R.id.buttonTest);
        testView = (TextView) findViewById(R.id.testTextVeiw);
        sendButton = (Button) findViewById(R.id.sendInput);
        dumpButton = (Button) findViewById(R.id.dump);
        textInput = (EditText)findViewById(R.id.testInput);

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

    public void openGraph(String filename) {
       Intent intent = new Intent(this, GraphData.class);
       intent.putExtra("filename", filename);
       startActivity(intent);
    }




}