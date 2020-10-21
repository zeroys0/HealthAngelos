package net.leelink.healthangelos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.utils.HexUtil;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.view.HeartView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingDeque;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;

public class VitaHeartRateActivity extends BaseActivity {
    RelativeLayout rl_back;
    public static final String serviceUUID = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String characterUUID = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String getCharacterUUID = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";
    byte[] bytes = {(byte) 0xaa, 0x55, (byte) 0x01, 0x10, 0x0f, (byte) 0x20}; //开始测量
    byte[] end_bytes = {(byte) 0xaa, 0x55, (byte) 0x02, 0x11, 0x00, 0x0f, (byte) 0x22};  //结束测量
    byte[] woshou_byte = {(byte) 0xaa, 0x55,0x01,0x13,0x00,0x14};
    private boolean buffer = false;
    String sb = "";
    private LinkedBlockingDeque<Integer> dataQueue = new LinkedBlockingDeque<>();
    HeartView heartView;
    String type;
    private int pm_size = 0;
    private boolean target = true;
    StringBuilder stringBuilder = new StringBuilder();
    String mac;
    TextView tv_end,tv_heart_rate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vita_heart_rate);
        init();
//        getFileContent(new File("/sdcard/Gyt/Data.txt"));

    }

    public void init() {
        mac = getIntent().getStringExtra("mac");
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_end = findViewById(R.id.tv_end);
        tv_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTest();
            }
        });
        tv_heart_rate = findViewById(R.id.tv_heart_rate);
        heartView = findViewById(R.id.hv);
        heartView.setHeartSpeed(2f);
        heartView.setShowSeconds(5f);
        heartView.setBaseLine(200);
        startTest();

        MyApplication.mClient.readDescriptor(mac, UUID.fromString(serviceUUID), UUID.fromString(characterUUID), UUID.fromString(getCharacterUUID), new BleReadResponse() {
            @Override
            public void onResponse(int code, byte[] data) {

            }
        });

        MyApplication.mClient.notify(mac, UUID.fromString(serviceUUID), UUID.fromString(getCharacterUUID), new BleNotifyResponse() {
            @Override
            public void onNotify(UUID service, UUID character, byte[] value) {


                String s = byteArrayToHexStr(value);
//                writeData(s);
                heartView.offer(90);
                String[] strings = s.split("AA55");
                for (int i = 0; i < strings.length; i++) {
                    String string = strings[i];
                    if (string.length() > 0) {
                        Log.e("onResponse_s: ", string);
                        //字符串的长度
                        int length;
                        //从字符串获取的data长度
                        int data_length;
                        //数据应有长度
                        int standard;
                        //判断是否有上一条需要拼接的数据 默认false
                        if (buffer) {
                            //有需要拼接的数据 则拼接数据
                            string = sb + string;
                            if (string.startsWith("AA55")) {
                                string = string.substring(4);
                            }
                            length = string.length();
                            data_length = hexToDecimal(string.substring(0, 2));
                            standard = data_length * 2 + 6;
                            sb = "";
                            buffer = false;
                            //拼接后判断数据是否合格  0AA000080008010807080208DC
                            if (checkString(length, standard, string)) {    //合格
                                String data = getValue(length, standard, string);
                                type = data.substring(2, 4);
                                data = data.substring(4, data.length() - 2);
                                //截取类型字段
                                handleData(type,data);
                            }
                        } else {
                            length = string.length();
                            data_length = hexToDecimal(string.substring(0, 2));
                            standard = data_length * 2 + 6;
                            //没有需要拼接的数据 直接判断
                            if (checkString(length, standard, string)) {
                                String data = getValue(length, standard, string);
                                type = data.substring(2, 4);
                                data = data.substring(4, data.length() - 2);
                                //截取类型字段
                                handleData(type,data);
                            }
                        }
                    }
                }
            }

            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {

                }
            }
        });
    }

    //处理腕表返回数据
    public void handleData(String type,String data){
        switch (type) {
            /**
             * 心率图数据
             */
            case "A0":
                for (int n = 0; n < data.length(); n=n+2) {
                    heartView.offer(hexToDecimal((data.charAt(n)) + String.valueOf(data.charAt(n + 1))));
                }
                break;
            /**
             * 血压标定数据
//             */
//            case "91":
//                Log.e( "血压标定数据: ", data);
//                int total = data.charAt(0);
//                int now = data.charAt(1);
//                if(pm_size<1) {
//                    if (total == now) {
//                        pm_size = 1;
//                    }
//                    stringBuilder.append(data.substring(2));
//                } else {
//                    if(target) {
//                        updata();
//                    }
//                }
//                break;
            /**
             * 握手响应
             */
            case "92":
                woshou();
                break;
            /**
             * 心率实时数据
             */
            case "A2":
                if(data.equals("0000")|| data.equals("ffff")) {
                    return;
                } else {
                    data = data.substring(2, 4);
                    tv_heart_rate.setText(hexToDecimal(data)+"次/分钟");
                }
                break;
            /**
             * 血压数据
             */
            case "B0":
                Log.e( "血压数据: ", data);
                break;
        }
    }

    //检查string是否符合规格
    public boolean checkString(int length, int standard, String string) {

        //如果长度恰好等于数据
        if (length >= standard) {
            //合格 可以获取数据
            return true;
        } else {
            //字符串长度小于应有
            //继续拼接
            sb = string;
            //数据应该拼接
            buffer = true;
            return false;
        }
    }

    public String getValue(int length, int standard, String string) {
        if (length == standard) { //恰好符合
            return string;
        } else if (length > standard) {
            //字符串大于应有长度  则末尾有aa55的不完整头数据
            //把aa55的头留给下一个字符串
            sb = string.substring(standard, length);
            buffer = true;
            //只返回需要的
            string = string.substring(0, standard);
            return string;
        } else {
            return "";
        }
    }

    //开始测量
    public void startTest() {

        MyApplication.mClient.write(mac, UUID.fromString(serviceUUID), UUID.fromString(characterUUID), bytes, new BleWriteResponse() {
            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {

                    Toast.makeText(VitaHeartRateActivity.this, "sucess", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //结束测量
    public void endTest() {
        MyApplication.mClient.write(mac, UUID.fromString(serviceUUID), UUID.fromString(characterUUID), end_bytes, new BleWriteResponse() {
            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {
                    Toast.makeText(VitaHeartRateActivity.this, "sucess", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //响应握手包
    public void woshou(){
        MyApplication.mClient.write(mac, UUID.fromString(serviceUUID), UUID.fromString(characterUUID), woshou_byte, new BleWriteResponse() {
            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {
                    Log.e( "onResponse: ","握手成功" );
                }
            }
        });
    }

    public static String byteArrayToHexStr(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[byteArray.length * 2];
        for (int j = 0; j < byteArray.length; j++) {
            int v = byteArray[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private void writeData(String s) {
        String filePath = "/sdcard/Gyt/";
        String fileName = "data.txt";
        writeTxtToFile(s, filePath, fileName);
    }

    // 将字符串写入到文本文件中
    private void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

//生成文件

    private File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

//生成文件夹

    private static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }

    //hex转int
    public static int hexToDecimal(String hex) {
        int decimalValue = 0;
        for (int i = 0; i < hex.length(); i++) {
            char hexChar = hex.charAt(i);
            decimalValue = decimalValue * 16 + hexCharToDecimal(hexChar);
        }
        return decimalValue;
    }


//hex转byte
    public static int hexCharToDecimal(char hexChar) {
        if (hexChar >= 'A' && hexChar <= 'F')
            return 10 + hexChar - 'A';
        else
            return hexChar - '0';//切记不能写成int类型的0，因为字符'0'转换为int时值为48
    }



}



