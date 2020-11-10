package com.ruizhou.blueterminal.Data;

import android.bluetooth.BluetoothDevice;


//Wrapper class for bluetooth devices
public class BLE_Device {
    private BluetoothDevice bluetoothDevice;
    private int rssi;

    public BLE_Device(BluetoothDevice bluetoothDevice, int rssi){
        this.bluetoothDevice = bluetoothDevice;
        this.rssi = rssi;
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
