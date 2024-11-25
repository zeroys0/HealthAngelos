package net.leelink.healthangelos.activity.ahaFit;

import android.app.Application;
import android.util.Log;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class BluetoothViewModel extends AndroidViewModel {
    private MutableLiveData<byte[]> characteristicData = new MutableLiveData<>();

    public BluetoothViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<byte[]> getCharacteristicData() {
        return characteristicData;
    }

    public void setCharacteristicData(byte[] data) {

        Log.d("BluetoothViewModel", "Setting characteristic data: " + Arrays.toString(data));
        characteristicData.postValue(data);
    }
}