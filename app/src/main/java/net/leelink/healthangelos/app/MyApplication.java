package net.leelink.healthangelos.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import android.webkit.WebView;

import com.inuker.bluetooth.library.BluetoothClient;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.view.CropImageView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.DBCookieStore;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.sinocare.multicriteriasdk.MulticriteriaSDKManager;
import com.sinocare.multicriteriasdk.auth.AuthStatusListener;
import com.sinocare.multicriteriasdk.utils.AuthStatus;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.UserInfo;
import net.leelink.healthangelos.im.util.Util;
import net.leelink.healthangelos.view.GlideImageLoader;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;

public class MyApplication extends Application {

    private static MyApplication instance;
    public static List<Activity> activityList = new LinkedList<Activity>();
    public static SharedPreferences preferences;
    public static String telephone;
    public static String token = "";
    public static UserInfo userInfo;
    public static String clientId = "";
    public static BluetoothClient mClient;
    private static final String PROCESSNAME = "com.zyb.webviewtest";
    public static String head = "";
    public static int changeFont = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

    }

    public void initImagePicker(){
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setMultiMode(true);    //设置单选多选模式
        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setSelectLimit(9);    //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素

    }


    public static MyApplication getContext() {
        return instance;
    }

//    public void setUserBean(UserBean userBean){
//        this.userBean = userBean;
//    }
//
//    public UserBean getUserBean(){
//        return  userBean;
//    }

    public void initIm(){
        SharedPreferences sp = getSharedPreferences("sp",0);
        String id = sp.getString("clientId","");
        Util.id = id;
        Util.setId(id);
    }


    public void initokGO() {
        HttpHeaders headers = new HttpHeaders();
        headers.put("commonHeaderKey1", "commonHeaderValue1");    //header不支持中文，不允许有特殊字符
        headers.put("commonHeaderKey2", "commonHeaderValue2");
        HttpParams params = new HttpParams();
        params.put("commonParamsKey1", "commonParamsValue1");     //param支持中文,直接传,不要自己编码
        params.put("commonParamsKey2", "这里支持中文参数");

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        //超时时间设置，默认60秒
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);      //全局的读取超时时间
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);     //全局的写入超时时间
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);   //全局的连接超时时间

        //自动管理cookie（或者叫session的保持），以下几种任选其一就行
        //builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));            //使用sp保持cookie，如果cookie不过期，则一直有效
        builder.cookieJar(new CookieJarImpl(new DBCookieStore(this)));              //使用数据库保持cookie，如果cookie不过期，则一直有效
        //builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));            //使用内存保持cookie，app退出后，cookie消失


        OkGo.getInstance().init(this)                       //必须调用初始化
                .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置将使用默认的
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(3)                               //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
                .addCommonHeaders(headers)                      //全局公共头
                .addCommonParams(params);                       //全局公共参数

    }
    public void initJPush(){
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }
    @SuppressLint("StringFormatInvalid")
    private void initWebView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = getProcessName();
            if (!PROCESSNAME.equals(processName)) {
                WebView.setDataDirectorySuffix(getString(R.string.app_name,processName));
            }
        }
    }

    public void initSdk(){
        initokGO();
        initJPush();
//        Glide.init(this,);
        initImagePicker();
//        NineGridView.setImageLoader(new PicassoImageLoader());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        ZXingLibrary.initDisplayOpinion(this);
        MulticriteriaSDKManager.init(this); //初始化
        MulticriteriaSDKManager.authentication(new AuthStatusListener() { //鉴权
            @Override
            public void onAuthStatus(AuthStatus authStatus) {
                Log.e( "onAuthStatus: ",authStatus.getCode()+"" );
            }
        });
        mClient = new BluetoothClient(this);
        initIm();
        initWebView();
    }

    // 遍历所有Activity并finish
    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        System.exit(0);
    }

}
