package com.ruizhou.blueterminal;

import android.bluetooth.BluetoothAdapter;

import com.ruizhou.blueterminal.Activity.MainActivity;

import java.util.logging.Handler;

public class BTLE_Scanner {
    private MainActivity ma;
    private BluetoothAdapter bluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;

    private long scanPerioid;
    private int signalStrength;
}
