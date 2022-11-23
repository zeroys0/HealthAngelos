package net.leelink.healthangelos.activity.Fit;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class FitConnectService extends Service {
    public FitConnectService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}