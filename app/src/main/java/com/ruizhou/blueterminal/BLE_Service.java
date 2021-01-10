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
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.ruizhou.blueterminal.Data.BLE_Device;
import com.ruizhou.blueterminal.Data.UUID_status;
import com.ruizhou.blueterminal.Utils.Utils_functions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

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

    // FILE SYSTEM MANAGEMENT
    private String fileListName;



    private boolean mScanning;
    private Handler mHandler;

    public BluetoothAdapter getmBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public BluetoothGattCallback getGattCallback() {
        return gattCallback;
    }

    // FILE SYSTEM MANAGEMENT
    public String getFileListName() { return fileListName; }
    public void setFileListName(String newName) {fileListName = newName; }
    public void setFile(final Context context) {
        String filename = fileListName;

        File file = context.getFileStreamPath(filename);
        if (file.exists()) { // Delete if file already exists
            context.deleteFile(filename);
        }
        try {
            File.createTempFile(filename, null, context.getCacheDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public BLE_Service(final Context context, MainActivity mainActivity){

        this.context = context;

//        final String filename = fileListName;
//
//        File file = context.getFileStreamPath(filename);
//        if (file.exists()) {
//            context.deleteFile(filename);
//        }
//        try {
//            File.createTempFile(filename, null, context.getCacheDir());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


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
//                final byte[] data=characteristic.getValue();
//                String hex = bytes2hex(data);
//                StringBuilder output = new StringBuilder();
//                for (int i = 0; i < hex.length(); i = i + 2) {
//                    // Step-1 Split the hex string into two character group
//                    String s = hex.substring(i, i + 2);
//                    // Step-2 Convert the each character group into integer using valueOf method
//                    int n = Integer.valueOf(s, 16);
//                    // Step-3 Cast the integer value to char
//                    output.append((char)n);
//                }
//
//                final String out = output.toString();
//
//
//                FileOutputStream fos = null;
//                try {
//                    fos = context.openFileOutput(filename, context.MODE_APPEND);
//                    fos.write(out.getBytes(), 0, out.length()); // Need to convert string to bytes
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally { // This code is executed even if exception is thrown
//                    if (fos != null) {
//                        try {
//                            fos.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
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
                String hex = bytes2hex(data);
                StringBuilder output = new StringBuilder();
                for (int i = 0; i < hex.length(); i = i + 2) {
                    // Step-1 Split the hex string into two character group
                    String s = hex.substring(i, i + 2);
                    // Step-2 Convert the each character group into integer using valueOf method
                    int n = Integer.valueOf(s, 16);
                    // Step-3 Cast the integer value to char
                    output.append((char)n);
                }

                final String out = output.toString();
                String filename = fileListName;

                FileOutputStream fos = null;
                try {
                    fos = context.openFileOutput(filename, context.MODE_APPEND);
                    fos.write(out.getBytes(), 0, out.length()); // Need to convert string to bytes
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally { // This code is executed even if exception is thrown
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        response.append(out);
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

//        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public void writeData(String content) throws UnsupportedEncodingException {
            BluetoothGattService service=mBluetoothGatt.getService(uuid_status.write_UUID_service);
            BluetoothGattCharacteristic charaWrite=service.getCharacteristic(uuid_status.write_UUID_chara);
            String hex="7B46363941373237323532443741397D";
            byte[] data;
            //String content=testInput.getText().toString();
            if (!TextUtils.isEmpty(content)){
                //data=HexUtil.hexStringToBytes(content);
                data = content.getBytes("US-ASCII");


            }else{
                //data=HexUtil.hexStringToBytes(hex);
                data = hex.getBytes("US-ASCII");
            }
            if (data.length>20){//数据大于个字节 分批次写入
                Log.e(TAG, "writeData: length="+data.length);
                int num=0;
                if (data.length%20!=0){
                    num=data.length/20+1;
                }else{
                    num=data.length/20;
                }
                for (int i=0;i<num;i++){
                    byte[] tempArr;
                    if (i==num-1){
                        tempArr=new byte[data.length-i*20];
                        System.arraycopy(data,i*20,tempArr,0,data.length-i*20);
                    }else{
                        tempArr=new byte[20];
                        System.arraycopy(data,i*20,tempArr,0,20);
                    }
                    charaWrite.setValue(tempArr);
                    mBluetoothGatt.writeCharacteristic(charaWrite);
                }
            }else{
                charaWrite.setValue(data);
                mBluetoothGatt.writeCharacteristic(charaWrite);
            }
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














