package net.leelink.healthangelos.activity.Fit.mock;

import android.content.Context;
import android.content.SharedPreferences;

public class AppFakeDataProvider {

    public static final boolean ENABLED = false;

    private static SharedPreferences openSp(Context context) {
        return context.getSharedPreferences("AppFakeDataProvider", Context.MODE_PRIVATE);
    }


}