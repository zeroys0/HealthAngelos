package net.leelink.healthangelos.activity.ahaFit;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

public class BleManager {
    private static BleManager instance;
    private BluetoothDevice connectedDevice;
    private BluetoothGatt mBluetoothGatt;

    private BluetoothGattCharacteristic mWritableCharacteristic;


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

    public BluetoothGatt getBluetoothGatt() {
        return mBluetoothGatt;
    }

    public void setBluetoothGatt(BluetoothGatt bluetoothGatt) {
        this.mBluetoothGatt = bluetoothGatt;
    }

    public void setConnectedDevice(BluetoothDevice device) {
        this.connectedDevice = device;
    }

    public BluetoothDevice getConnectedDevice() {
        return connectedDevice;
    }

    public BluetoothGattCharacteristic getWritableCharacteristic() {
        return mWritableCharacteristic;
    }

    public void setWritableCharacteristic(BluetoothGattCharacteristic writableCharacteristic) {
        this.mWritableCharacteristic = writableCharacteristic;
    }
}
