package com.ruizhou.blueterminal;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.ruizhou.blueterminal.Data.BLE_Device;
import com.ruizhou.blueterminal.Data.UUID_status;
import com.ruizhou.blueterminal.Utils.Utils_functions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BLE_Service {

    private final String TAG = "BLE_Services";
    private final int SCANPERIOD = 7500;
    private Context context;
    private MainActivity ma;
    public boolean isConnecting;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    public BluetoothGatt mBluetoothGatt;
    private BluetoothGattCallback gattCallback;
    private UUID_status uuid_status;
   // private UUID read_UUID_chara;
    StringBuilder response;



    private boolean mScanning;
    private Handler mHandler;

    public BluetoothAdapter getmBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public BluetoothGattCallback getGattCallback() {
        return gattCallback;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public BLE_Service(Context context, MainActivity mainActivity){

        this.context = context;
        ma = mainActivity;
        isConnecting = false;

        uuid_status = new UUID_status();
        response = new StringBuilder();

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
        gattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                if(newState == BluetoothGatt.STATE_CONNECTED){
                    Log.e(TAG,"connection on, scanning");
                    mBluetoothGatt.discoverServices();
                    isConnecting = true;
                }
                else{
                    Log.e(TAG,"connection failed: " + status);
                    mBluetoothGatt.close();
                    isConnecting = false;
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                initServiceAndChara();
                Log.e(TAG,"onServicesDiscovered()");
                mBluetoothGatt.setCharacteristicNotification(mBluetoothGatt
                        .getService(uuid_status.notify_UUID_service).getCharacteristic(uuid_status.notify_UUID_chara),true);
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
                //Log.d(TAG,"onCharacteristicRead()");
                Log.d(TAG, "callBack characteristic read status: "+ status +" in thread" + Thread.currentThread());
                Log.d(TAG,"read value "+ characteristic.getValue());
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);

            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                Log.d(TAG,"onCharacteristicChanged()"+characteristic.getValue());
                final byte[] data=characteristic.getValue();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        response.append(bytes2hex(data));
                        response.append("\n");

                    }
                });
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void initServiceAndChara() {
        List<BluetoothGattService> bluetoothGattServices = mBluetoothGatt.getServices();
        for (BluetoothGattService bluetoothGattService : bluetoothGattServices) {
            List<BluetoothGattCharacteristic> characteristics = bluetoothGattService.getCharacteristics();
            for (BluetoothGattCharacteristic characteristic : characteristics) {
                int charaProp = characteristic.getProperties();
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                    Log.e(TAG,characteristic.getUuid().toString());
                    uuid_status.read_UUID_chara = characteristic.getUuid();
                    uuid_status.read_UUID_service = bluetoothGattService.getUuid();
                    Log.e(TAG, "read_chara=" + uuid_status.read_UUID_chara + "----read_service=" + uuid_status.read_UUID_service);
                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                    uuid_status.write_UUID_chara = characteristic.getUuid();
                    uuid_status.write_UUID_service = bluetoothGattService.getUuid();
                    Log.e(TAG, "write_chara=" + uuid_status.write_UUID_chara + "----write_service=" + uuid_status.write_UUID_service);
                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
                    uuid_status.write_UUID_chara = characteristic.getUuid();
                    uuid_status.write_UUID_service = bluetoothGattService.getUuid();
                    Log.e(TAG, "write_chara=" + uuid_status.write_UUID_chara + "----write_service=" + uuid_status.write_UUID_service);

                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                    uuid_status.notify_UUID_chara = characteristic.getUuid();
                    uuid_status.notify_UUID_service = bluetoothGattService.getUuid();
                    Log.e(TAG, "notify_chara=" + uuid_status.notify_UUID_chara + "----notify_service=" + uuid_status.notify_UUID_service);
                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
                    uuid_status.indicate_UUID_chara = characteristic.getUuid();
                    uuid_status.indicate_UUID_service = bluetoothGattService.getUuid();
                    Log.e(TAG, "indicate_chara=" + uuid_status.indicate_UUID_chara + "----indicate_service=" + uuid_status.indicate_UUID_service);

                }
            }
        }
    }
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void readData(){
            BluetoothGattCharacteristic characteristic=mBluetoothGatt.getService(uuid_status.read_UUID_service)
                    .getCharacteristic(uuid_status.read_UUID_chara);
            mBluetoothGatt.readCharacteristic(characteristic);
        }

    private static final String HEX = "0123456789abcdef";
    public static String bytes2hex(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes)
        {
            // 取出这个字节的高4位，然后与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
            sb.append(HEX.charAt((b >> 4) & 0x0f));
            // 取出这个字节的低位，与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
            sb.append(HEX.charAt(b & 0x0f));
        }
        return sb.toString();
    }
    }














