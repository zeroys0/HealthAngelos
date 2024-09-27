package net.leelink.healthangelos.activity.ahaFit;

import android.bluetooth.BluetoothDevice;

public class BleManager {
    private static BleManager instance;
    private BluetoothDevice connectedDevice;

    private BleManager() {}

    public static BleManager getInstance() {
        if (instance == null) {
            synchronized (BleManager.class) {
                if (instance == null) {
                    instance = new BleManager();
                }
            }
        }
        return instance;
    }

    public void setConnectedDevice(BluetoothDevice device) {
        this.connectedDevice = device;
    }

    public BluetoothDevice getConnectedDevice() {
        return connectedDevice;
    }
}
