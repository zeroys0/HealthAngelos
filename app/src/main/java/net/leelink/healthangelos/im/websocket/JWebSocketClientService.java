package net.leelink.healthangelos.im.websocket;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.im.data.MessageDataHelper;
import net.leelink.healthangelos.im.data.MessageListHelper;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import static android.app.Notification.VISIBILITY_PUBLIC;
import static androidx.core.app.NotificationCompat.PRIORITY_MIN;


public class JWebSocketClientService extends Service {
    public JWebSocketClient client;
    private JWebSocketClientBinder mBinder = new JWebSocketClientBinder();
    String clientId;
    private final static int GRAY_SERVICE_ID = 1001;
    //灰色保活
    public static class GrayInnerService extends Service {

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(GRAY_SERVICE_ID, new Notification());
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }
    PowerManager.WakeLock wakeLock;//锁屏唤醒
    //获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
    @SuppressLint("InvalidWakeLockTag")
    private void acquireWakeLock()
    {
        if (null == wakeLock)
        {
            PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK| PowerManager.ON_AFTER_RELEASE, "PostLocationService");
            if (null != wakeLock)
            {
                wakeLock.acquire();
            }
        }
    }

    //用于Activity和service通讯
    public class JWebSocketClientBinder extends Binder {
        public JWebSocketClientService getService() {
            return JWebSocketClientService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //初始化websocket
        initSocketClient();
        mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//开启心跳检测

        //设置service为前台服务，提高优先级
        if (Build.VERSION.SDK_INT < 18) {
            //Android4.3以下 ，隐藏Notification上的图标
            startForeground(GRAY_SERVICE_ID, new Notification());
        } else if(Build.VERSION.SDK_INT>18 && Build.VERSION.SDK_INT<25){
            //Android4.3 - Android7.0，隐藏Notification上的图标
            Intent innerIntent = new Intent(this, GrayInnerService.class);
            startService(innerIntent);
            startForeground(GRAY_SERVICE_ID, new Notification());
        }else{
            String channelId = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                channelId = createNotificationChannel("my_service", "My Background Service");
            }
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,channelId);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(PRIORITY_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            //Android7.0以上app启动后通知栏会出现一条"正在运行"的通知
            startForeground(GRAY_SERVICE_ID, notification);
        }

        acquireWakeLock();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        closeConnect();
        super.onDestroy();
    }

    public JWebSocketClientService() {
    }


    /**
     * 初始化websocket连接
     */
    private void initSocketClient() {
        SharedPreferences sp = getSharedPreferences("sp",0);
        clientId = sp.getString("clientId","");
        String ws = sp.getString("ws","");
        String token =  sp.getString("secretKey","");
        URI uri = URI.create(ws+clientId+"/"+token);      //websocket连接地址

        client = new JWebSocketClient(uri) {
            @Override
            public void onMessage(String message) {
                Log.e("JWebSocketClientService", "收到的消息：" + message);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(message);
                    if (jsonObject.has("messageType")) {
                        if (jsonObject.getInt("messageType") == 4) {
                            String content = jsonObject.getString("textMessage");
                            MessageDataHelper messageDataHelper = new MessageDataHelper(getApplicationContext());
                            MessageListHelper messageListHelper = new MessageListHelper(getApplicationContext());
                            SQLiteDatabase db= messageDataHelper.getReadableDatabase();
                            SQLiteDatabase db_list = messageListHelper.getWritableDatabase();
                            ContentValues cv = new ContentValues();
                            cv.put("content",content);
                            cv.put("time",System.currentTimeMillis() + "");
                            cv.put("isMeSend",0);
                            cv.put("isRead",1);
                            cv.put("sendId", clientId);
                            cv.put("receiveId",jsonObject.getString("fromuserId"));
                            cv.put("type",jsonObject.getInt("type"));
                            cv.put("RecorderTime",0);
                            db.insert("MessageDataBase",null,cv);
                            db.close();
                            db_list.replace("MessageListDB",null,cv);
                            db_list.close();
                        }
                    }
                    //账号异地登录
                    if(jsonObject.getInt("status")==400) {


//                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                        intent.putExtra("type",9);
//
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.setAction("net.leelink.healthangelos");
                intent.putExtra("message", message);
                sendBroadcast(intent);
                checkLockAndShowNotification(message);
            }

            @Override
            public void onOpen(ServerHandshake handshakedata) {
                super.onOpen(handshakedata);
                Log.e("JWebSocketClientService", "websocket连接成功");
            }
        };
        connect();
    }

    /**
     * 连接websocket
     */
    private void connect() {
        new Thread() {
            @Override
            public void run() {
                try {
                    //connectBlocking多出一个等待操作，会先连接再发送，否则未连接发送会报错
                    client.connectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    /**
     * 发送消息
     *
     * @param msg
     */
    public void sendMsg(String msg) {
        if (null != client) {
            Log.e("JWebSocketClientService", "发送的消息：" + msg);
            client.send(msg);
        }
    }

    /**
     * 断开连接
     */
    private void closeConnect() {
        try {
            if (null != client) {
                client.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client = null;
        }
    }


//    -----------------------------------消息通知--------------------------------------------------------

    /**
     * 检查锁屏状态，如果锁屏先点亮屏幕
     *
     * @param content
     */
    private void checkLockAndShowNotification(String content) {
        //管理锁屏的一个服务
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (km.inKeyguardRestrictedInputMode()) {//锁屏
            //获取电源管理器对象
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            if (!pm.isScreenOn()) {
                @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
                wl.acquire();  //点亮屏幕
                wl.release();  //任务结束后释放
            }
            Log.e( "channelId: ","收到了消息" );
            sendNotification(content);
       } else {
            sendNotification(content);
            Log.e( "channelId: ","收到了消息" );
        }
    }

    /**
     * 发送通知
     *
     * @param content
     */
    private void sendNotification(String content) {
        Intent intent = new Intent();
        intent.setClass(this, net.leelink.healthangelos.im.ChatActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = createNotificationChannel("my_message", "Message Background Service");
            String channelName = "聊天消息";

            NotificationChannel notify = new NotificationChannel(channelId,channelName,NotificationManager.IMPORTANCE_HIGH);
            notify.enableVibration(true);
            notify.setVibrationPattern(new long[]{500});
            notifyManager.createNotificationChannel(notify);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,channelId);
            @SuppressLint("WrongConstant") Notification notification = notificationBuilder
                    .setAutoCancel(true)
                    // 设置该通知优先级
                    .setPriority(Notification.PRIORITY_MAX)
                    .setSmallIcon(R.drawable.icon)
                    .setContentTitle("服务器")
                    .setContentText(content)
                    .setVisibility(VISIBILITY_PUBLIC)
                    .setWhen(System.currentTimeMillis())
                    // 向通知添加声音、闪灯和振动效果
                    .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_ALL | Notification.DEFAULT_SOUND)
                    .setContentIntent(pendingIntent)
                    .build();
            notifyManager.notify(274, notification);//id要保证唯一

        } else {

            @SuppressLint("WrongConstant") Notification notification = new NotificationCompat.Builder(this)
                    .setAutoCancel(true)
                    // 设置该通知优先级
                    .setPriority(Notification.PRIORITY_MAX)
                    .setSmallIcon(R.drawable.icon)
                    .setContentTitle("服务器")
                    .setContentText(content)
                    .setVisibility(VISIBILITY_PUBLIC)
                    .setWhen(System.currentTimeMillis())
                    // 向通知添加声音、闪灯和振动效果
                    .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_ALL | Notification.DEFAULT_SOUND)
                    .setContentIntent(pendingIntent)
                    .build();
            notifyManager.notify(1, notification);//id要保证唯一
        }
    }

    /**
     * 设置通知channelId
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId,  String channelName){
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor( Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(chan);
        return channelId;
    }


    //    -------------------------------------websocket心跳检测------------------------------------------------
    private static final long HEART_BEAT_RATE = 10 * 1000;//每隔10秒进行一次对长连接的心跳检测
    private Handler mHandler = new Handler();
    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            Log.e("JWebSocketClientService", "心跳包检测websocket连接状态");
            if (client != null) {
                if (client.isClosed()) {
                    reconnectWs();
                    Log.e("JWebSocketClientService", "关闭,重新连接");
                }
            } else {
                //如果client已为空，重新初始化连接
                client = null;
                initSocketClient();
                Log.e("JWebSocketClientService", "空,重新初始化了");
            }
            //每隔一定的时间，对长连接进行一次心跳检测
            mHandler.postDelayed(this, HEART_BEAT_RATE);
        }
    };

    /**
     * 开启重连
     */
    private void reconnectWs() {
        mHandler.removeCallbacks(heartBeatRunnable);
        new Thread() {
            @Override
            public void run() {
                try {
                    Log.e("JWebSocketClientService", "开启重连");
                    client.reconnectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
