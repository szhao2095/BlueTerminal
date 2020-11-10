package com.ruizhou.blueterminal;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.ruizhou.blueterminal.Data.BLE_Device;
import com.ruizhou.blueterminal.Utils.Utils_functions;

import java.util.ArrayList;
import java.util.List;

public class BLE_Service {

    private final String TAG = "BLE_Services";
    private final int SCANPERIOD = 7500;
    private Context context;
    private MainActivity ma;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;

    private boolean mScanning;
    private Handler mHandler;

    public BluetoothAdapter getmBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public BLE_Service(Context context, MainActivity mainActivity){

        this.context = context;
        ma = mainActivity;

        mHandler = new Handler();
        final BluetoothManager bluetoothManager= (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);;
        mBluetoothAdapter = bluetoothManager.getAdapter();;
        mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
                final int new_rssi = rssi;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        ma.addDevice(device, new_rssi);
                        Log.d(TAG, "add:"+rssi);
                    }
                });
                Log.d(TAG, "run:scanning......");


            }
        };

    }




    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void ScanningStart(){
        final List<BLE_Device> ble_devices = new ArrayList<>();
        //Ensure Bluetooth working
        if(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()){
            Utils_functions.toast(context,"Bluetooth Not Enabled");
            return ;
        }
        Log.d(TAG, "process");
//        final BluetoothAdapter.LeScanCallback callback = new BluetoothAdapter.LeScanCallback(){
//            @Override
//            public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
//                ma.addDevice(device,rssi);
//                Log.d(TAG, "run:scanning......");
//            }
//
//        };
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils_functions.toast(context,"Stopping BLE Scanning");
                mScanning = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);

            }
        },SCANPERIOD);
        mScanning = true;
        mBluetoothAdapter.startLeScan(mLeScanCallback);
        return;

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void ScanningStop(){
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }













}
