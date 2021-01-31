package com.ruizhou.blueterminal.Data;

import android.bluetooth.BluetoothDevice;

import java.io.Serializable;

//Wrapper class for bluetooth devices
public class BLE_Device implements Serializable {
    private BluetoothDevice bluetoothDevice;
    private int rssi;
    private boolean connection;

    public boolean isConnection() {
        return connection;
    }

    public void setConnection(boolean connection) {
        this.connection = connection;
    }


    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public BLE_Device(BluetoothDevice bluetoothDevice, int rssi){
        this.bluetoothDevice = bluetoothDevice;
        this.rssi = rssi;
        this.connection = false;
    }
    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
    public String getName(){
        return bluetoothDevice.getName();
    }
    public String getAddress(){
        return bluetoothDevice.getAddress();
    }




}
