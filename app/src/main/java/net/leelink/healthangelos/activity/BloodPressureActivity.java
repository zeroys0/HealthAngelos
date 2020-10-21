package net.leelink.healthangelos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import net.leelink.healthangelos.view.HeartView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingDeque;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;
import static java.lang.Float.NaN;

public class BloodPressureActivity extends BaseActivity {
    RelativeLayout rl_back;
    public static final String serviceUUID = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String characterUUID = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String getCharacterUUID = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";
    byte[] bytes = {(byte) 0xaa, 0x55, (byte) 0x01, 0x10, 0x0f, (byte) 0x20}; //开始测量
    byte[] end_bytes = {(byte) 0xaa, 0x55, (byte) 0x02, 0x11, 0x00, 0x0f, (byte) 0x22};  //结束测量
    byte[] woshou_byte = {(byte) 0xaa, 0x55,0x01,0x13,0x00,0x14};
    String mac;
    TextView tv_end,tv_heart_rate;
    private boolean buffer = false;
    String sb = "";
    String type;
    private int pm_size = 0;
    private boolean target = true;
    StringBuilder stringBuilder = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_pressure);
        init();
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

                String[] strings = s.split("AA55");
                for (int i = 0; i < strings.length; i++) {
                    String string = strings[i];
                    if (string.length() > 0) {
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
//            case "A0":
//                for (int n = 0; n < data.length(); n=n+2) {
//                    heartView.offer(hexToDecimal((data.charAt(n)) + String.valueOf(data.charAt(n + 1))));
//                }
//                break;
            /**
             * 血压标定数据
             */
            case "91":
                Log.e( "血压标定数据: ", data);
                int total = data.charAt(0);
                int now = data.charAt(1);
                if(pm_size<6) {
                    if (total == now) {
                        pm_size++;
                    }
                    stringBuilder.append(data.substring(2));
                } else {
                    if(target) {
                        updata();
                    }
                }
                break;
            /**
             * 握手响应
             */
            case "92":
                woshou();
                break;
            /**
             * 心率实时数据
             */
//            case "A2":
//                if(data.equals("0000")|| data.equals("ffff")) {
//                    return;
//                } else {
//                    data = data.substring(2, 4);
//                    tv_heart_rate.setText(hexToDecimal(data)+"次/分钟");
//                }
//                break;
            /**
             * 血压数据
             */
            case "B0":
                Log.e( "血压数据: ", data);
                tv_heart_rate.setText(hexToDecimal(data.substring(6))+"/"+hexToDecimal(data.substring(2,4)));
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

                    Toast.makeText(BloodPressureActivity.this, "sucess", Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(BloodPressureActivity.this, "sucess", Toast.LENGTH_SHORT).show();
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

    public void updata(){
        String s  = String.valueOf(stringBuilder);
        String pm = "";
        int si = 1;
        for(int i=0;i<s.length();i=i+8) {
//                byte[] b = new byte[]{(byte) (s.charAt(i)+s.charAt(i+1)), (byte) (s.charAt(i+2)+s.charAt(i+3)), (byte) (s.charAt(i+4)+s.charAt(i+5)), (byte) (s.charAt(i+6)+s.charAt(i+7))};
            String byt = s.substring(i,i+8);
            byte[] b = hexStrToByteArray(byt);
            float  f = byte2float(b,0);
//            pm = pm+f;
            if(f != NaN) {
                BigDecimal bigDecimal = new BigDecimal(Float.toString(f));
                pm = pm+bigDecimal;
            } else {
                pm = pm+f;
            }
//            pm = pm+big2(f);


            if(i == si*1024-8) {

                pm = pm+";";
                si++;
            } else {
                pm = pm+",";
            }
        }
        long time = System.currentTimeMillis();
        String UPDATELOAD = "https://test-health.vita-course.com/gromit/account/"+getIntent().getStringExtra("pid")+"/multi/calib/updateload";
        SharedPreferences sp = getSharedPreferences("sp",0);
        String vtoken = sp.getString("vtoken","");
        HttpParams httpParams =new HttpParams();
        httpParams.put("sys",120);
        httpParams.put("dia",80);
        httpParams.put("pm_size",128);
        httpParams.put("pm",pm);
        httpParams.put("size",6);
        httpParams.put("data_source",421);
        httpParams.put("bptag","asdojetoiejgnogr0y");
        httpParams.put("age",79);
        httpParams.put("drug",0);
        httpParams.put("posture",0);
        httpParams.put("sts",524496584);
        httpParams.put("ets",524759281);
        httpParams.put("height",178);
        httpParams.put("weight",70);
        httpParams.put("agent",107);


        Log.e( "上传标定参数: ", pm);
        Log.e( "上传标定参数: ",vtoken );
        Log.e( "上传标定参数: ",getIntent().getStringExtra("pid")+"" );
        OkGo.<String>post(UPDATELOAD)
                .tag(this)
                .headers("vtoken", vtoken)
                .params(httpParams)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("上传标定参数", json.toString());
                            if (json.getInt("code") == 0) {
                                target = false;
                                Toast.makeText(BloodPressureActivity.this, "标定成功", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(BloodPressureActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     * 字节转换为浮点
     *
     * @param b 字节（至少4个字节）
     * @param index 开始位置
     * @return
     */
    public static float byte2float(byte[] b, int index) {
        int l;
        l = b[index];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 2] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 3] << 24);
        return Float.intBitsToFloat(l);
    }


    /**
     * 浮点转换为字节
     *
     * @param f
     * @return
     */
    public static byte[] float2byte(float f) {

        // 把float转换为byte[]
        int fbit = Float.floatToIntBits(f);

        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (fbit >> (24 - i * 8));
        }

        // 翻转数组
        int len = b.length;
        // 建立一个与源数组元素类型相同的数组
        byte[] dest = new byte[len];
        // 为了防止修改源数组，将源数组拷贝一份副本
        System.arraycopy(b, 0, dest, 0, len);
        byte temp;
        // 将顺位第i个与倒数第i个交换
        for (int i = 0; i < len / 2; ++i) {
            temp = dest[i];
            dest[i] = dest[len - i - 1];
            dest[len - i - 1] = temp;
        }

        return dest;

    }

    public static byte[] hexStrToByteArray(String str)
    {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return new byte[0];
        }
        byte[] byteArray = new byte[str.length() / 2];
        for (int i = 0; i < byteArray.length; i++){
            String subStr = str.substring(2 * i, 2 * i + 2);
            byteArray[i] = ((byte)Integer.parseInt(subStr, 16));
        }
        return byteArray;
    }

//    private static String big2(float f) {
//        f = Math.round(f * 100) / (float) 100 ;
//
//        BigDecimal decimal= new BigDecimal(f);
//        return String.valueOf(decimal);
//
//    }

    //方法二： BigDecimal
    private static String big2(float d) {
        BigDecimal d1 = new BigDecimal(Double.toString(d));
        BigDecimal d2 = new BigDecimal(Integer.toString(1));
        // 四舍五入,保留2位小数
        return d1.divide(d2,40,BigDecimal.ROUND_HALF_UP).toString();

    }

}
