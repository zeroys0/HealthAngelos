package net.leelink.healthangelos.im.util;

import android.content.Context;
import android.widget.Toast;

public class Util {
    public static String id = "";
    public static String ws = "ws://192.168.16.82:8889/connectWebSocket/";//websocket测试地址

    public static void showToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }

    public static void setId(String id) {

        ws = ws+id;
    }
}
