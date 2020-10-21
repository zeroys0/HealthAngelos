package net.leelink.healthangelos.wxapi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Set;

import cn.jpush.android.api.TagAliasCallback;


public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI api;
    private BaseResp resp = null;
    private String WX_APP_ID = "wx91a949aa7912ff7c";
    // 获取第一步的code后，请求以下链接获取access_token
    private String GetCodeRequest = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
    // 获取用户个人信息
    private String GetUserInfo = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";
    private String WX_APP_SECRET = "";
    static int index_;
    static int status;
    static String nickname;
    Gson gson = new Gson();
//    SharedPreferences preferences = HousekeeperApplication.getInstance().getSharedPreferences();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, WX_APP_ID, false);
        api.handleIntent(getIntent(), this);
    }

    public static void callback(int index) {
        index_ = index;
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {
        finish();
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp resp) {
        String result = "";
        if (resp != null) {
            resp = resp;
        }
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = "发送成功";
//                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                if (index_ != -1) {
                    String code = ((SendAuth.Resp) resp).code;
            /*
             * 将你前面得到的AppID、AppSecret、code，拼接成URL 获取access_token等等的信息(微信)
             */
                    String get_access_token = getCodeRequest(code);

                    OkGo.<String>post(get_access_token)
                            .tag(this)
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(Response<String> response) {
                                    try {
                                        JSONObject jsonobject = new JSONObject(response.body());
                                        String access_token = jsonobject.getString("access_token");
                                        String openid = jsonobject.getString("openid");
                                        String get_user_info_url = getUserInfo(access_token, openid);
                                        getUserInfo(get_user_info_url);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                }

                finish();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "发送取消";
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "发送被拒绝";
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                finish();
                break;
            default:
                result = "发送返回";
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                finish();
                break;
        }
    }


    /**
     * 通过拼接的用户信息url获取用户信息
     *
     * @param user_info_url
     */
    private void getUserInfo(String user_info_url) {
        OkGo.<String>post(user_info_url)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonobject = new JSONObject(response.body());
                            String nickname = jsonobject.getString("nickname");
                            String openid = jsonobject.getString("openid");
                            String headimgurl = jsonobject.getString("headimgurl");
                            String unionid = jsonobject.getString("unionid");
                            if (index_ == 1) {
                                WXlogin(unionid, openid, nickname, headimgurl);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    private void WXlogin(final String unionid, final String openid, final String nickname, final String headimgurl) {
        // TODO Auto-generated method stub
        final String time_s = (System.currentTimeMillis() + "").substring(0, 10);
//        OkGo.<String>post(Urls.THIRDLOGINCHECK)
//                .tag(this)
//                .params("oauth", "wx")
//                .params("unionid", unionid)
//                .params("unixtime", time_s)
//                .execute(new StringCallback() {
//                    @Override
//                    public void onSuccess(Response<String> response) {
//                        try {
//                            JSONObject json = new JSONObject(response.body());
//                            if (json.getString("status").equals("1")) {
//                                String token = json.getString("token");
//                                String mobile = json.getString("mobile");
//                                JSONObject jsonObject = json.getJSONObject("userinfo");
//                                String userid = jsonObject.getString("userid");
//                                String auto_login = jsonObject.getString("auto_login");
//                                SharedPreferences.Editor editor = preferences.edit();
//                                editor.putString("time", time_s);
//                                editor.putString("mobile", mobile);
//                                editor.putString("password", "");
//                                editor.putString("auto_login", auto_login);
//                                editor.putString("oauth", "wx");
//                                editor.putString("openID", openid);
//                                editor.putString("unionid", unionid);
//                                editor.putString("nickname", nickname);
//                                editor.commit();
//                                HousekeeperApplication.getInstance().token = token;
//                                HousekeeperApplication.getInstance().userInfoModel.setMobile(mobile);
//                                HousekeeperApplication.getInstance().userInfoModel.setUserid(userid);
//
//                                Set<String> tagSet = new LinkedHashSet<String>();
//                                tagSet.add(mobile);
//                                JPushInterface.setAliasAndTags(getApplicationContext(), null, tagSet, mAliasCallback);
//
//                                Intent it = new Intent(WXEntryActivity.this, MainActivity.class);
//                                startActivity(it);
//                                finish();
//                            } else if (json.getString("status").equals("0")) {
//                                Intent it = new Intent(WXEntryActivity.this, WeixinBindAcitivity.class);
//                                it.putExtra("nickname", nickname);
//                                it.putExtra("headimgurl", headimgurl);
//                                it.putExtra("unionid", unionid);
//                                it.putExtra("openid", openid);
//                                it.putExtra("oauth", "wx");
//                                startActivity(it);
//                                finish();
//                            }
//                            ToastUtil.show(WXEntryActivity.this, json.getString("msg"));
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });

    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    break;
                case 6002:
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
            }

        }

    };

    @SuppressLint("NewApi")
    Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.arg1) {
                case 1:
                    nickname = (String) msg.obj;
                    break;
            }
            return false;
        }
    });


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
        finish();
    }

    /**
     * 获取access_token的URL（微信）
     *
     * @param code 授权时，微信回调给的
     * @return URL
     */
    private String getCodeRequest(String code) {
        String result = null;
        GetCodeRequest = GetCodeRequest.replace("APPID",
                urlEnodeUTF8(WX_APP_ID));
        GetCodeRequest = GetCodeRequest.replace("SECRET",
                urlEnodeUTF8(WX_APP_SECRET));
        GetCodeRequest = GetCodeRequest.replace("CODE", urlEnodeUTF8(code));
        result = GetCodeRequest;
        return result;
    }

    /**
     * 获取用户个人信息的URL（微信）
     *
     * @param access_token 获取access_token时给的
     * @param openid       获取access_token时给的
     * @return URL
     */
    private String getUserInfo(String access_token, String openid) {
        String result = null;
        GetUserInfo = GetUserInfo.replace("ACCESS_TOKEN", urlEnodeUTF8(access_token));
        GetUserInfo = GetUserInfo.replace("OPENID", urlEnodeUTF8(openid));
        result = GetUserInfo;
        return result;
    }

    private String urlEnodeUTF8(String str) {
        String result = str;
        try {
            result = URLEncoder.encode(str, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}