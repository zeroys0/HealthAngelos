package net.leelink.healthangelos.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.pattonsoft.pattonutil1_0.util.MapUtil;
import com.pattonsoft.pattonutil1_0.util.Mytoast;
import com.pattonsoft.pattonutil1_0.util.NetWorkStatus;
import com.pattonsoft.pattonutil1_0.util.SPUtils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.SettingService;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.Contact;
import net.leelink.healthangelos.util.HanziToPinyin;
import net.leelink.healthangelos.util.Urls;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddContactActivity extends BaseActivity implements View.OnClickListener {
    Context mContext;
    ArrayList<HashMap<String, String>> constactsList = new ArrayList<>();//获取手机本地的通讯录列表
    List<Map<String, Object>> linkList = new ArrayList<>();//健康大使请求回来的联系人列表
    List<Map<String, Object>> chooseLinkList = new ArrayList<>();//用来存储选中的记录
    List<String> keyList;
    List<Integer> stateList = new ArrayList<>();
    ListView lv;
    RelativeLayout rl_back;
    TextView tv_create;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        createProgressBar(this);
        init();
        mContext = this;
        initData();

    }

    public void init() {
        lv = findViewById(R.id.lv);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_create = findViewById(R.id.tv_create);
        tv_create.setOnClickListener(this);
    }

    //获取权限 并扫描
    void doGetPermission() {
        AndPermission.with(mContext)
                .permission(
                        Permission.READ_CONTACTS
                )
                .rationale(new Rationale() {
                    @Override
                    public void showRationale(Context context, List<String> permissions, final RequestExecutor executor) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage("需要获取通讯录,是否同意开启通讯录权限");
                        builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 如果用户同意去设置：
                                executor.execute();
                            }
                        });
                        //设置点击对话框外部区域不关闭对话框
                        builder.setCancelable(false);
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 如果用户不同意去设置：
                                executor.cancel();
                                Mytoast.show(mContext, "无法获取通讯录");

                            }
                        });
                        builder.show();
                    }
                })
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        showProgressBar();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                constactsList = readContact();
                                handler.sendEmptyMessage(0);
                            }
                        }).start();

                    }
                })
                .onDenied(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {

                        if (AndPermission.hasAlwaysDeniedPermission(mContext, permissions)) {
                            // 这里使用一个Dialog展示没有这些权限应用程序无法继续运行，询问用户是否去设置中授权。
                            final SettingService settingService = AndPermission.permissionSetting(mContext);
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setMessage("通讯录权限已被禁止,无法获取通讯录信息,请到\"设置\"开启");
                            builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 如果用户同意去设置：
                                    settingService.execute();

                                }
                            });
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 如果用户不同意去设置：
                                    settingService.cancel();
                                }
                            });
                            //设置点击对话框外部区域不关闭对话框
                            builder.setCancelable(false);
                            builder.show();
                        }
                    }

                })
                .start();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SPUtils.put(mContext, "MyContacts", new Gson().toJson(constactsList));
            for (int i = 0; i < constactsList.size(); i++) {
                HashMap<String, String> map = constactsList.get(i);
                String name = map.get("p_name");
                String key = HanziToPinyin.getPinYinFirst(name);
                map.put("key", key);
                constactsList.set(i, map);
                stateList.add(0);
            }
            lv.setAdapter(new ContactsAdapter());
            stopProgressBar();
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_create:
                //批量新增

                List<Map<String, Object>> list = new ArrayList<>();

                for (int i = 0; i < stateList.size(); i++) {
                    int state = stateList.get(i);
                    if (state == 1) {
                        HashMap<String, String> sMap = constactsList.get(i);
                        Map<String, Object> map = new HashMap<>();
                        String p_name = sMap.get("p_name");
                        String p_phone = sMap.get("p_phone");
                        map.put("name", p_name);
                        map.put("phone", p_phone);
                        list.add(map);
                    }
                }

                if (list != null && list.size() > 0) {
                    try {
                        upLinks(list);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Mytoast.show(mContext, "请选择需要上传的联系人");
                }

                break;


        }
    }

    class ContactsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return constactsList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            View v = view;
            final Holder holder;
            if (v == null) {
                v = getLayoutInflater().inflate(R.layout.item_contacts_check, viewGroup, false);
                holder = new Holder(v);
                v.setTag(holder);
            } else {
                holder = (Holder) v.getTag();
            }

            //判断是否选中
            int state = 0;
            if(linkList!=null &&linkList.size()>0) {
                for (Map<String, Object> linkMap : linkList) {
                    String phone = MapUtil.getString(linkMap, "phone");
                    String p_phone = constactsList.get(i).get("p_phone");
                    if (phone.equals(p_phone)) {
                        state = 1;
                        break;
                    }
                }
            }

            if (state == 1) {
                holder.create.setVisibility(View.GONE);
                holder.tvCheck.setVisibility(View.GONE);
                holder.vHide.setVisibility(View.VISIBLE);
            } else {
                holder.vHide.setVisibility(View.GONE);
                holder.create.setVisibility(View.VISIBLE);
                holder.tvCheck.setVisibility(View.VISIBLE);
                holder.create.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            upLink(constactsList.get(i).get("p_name"),constactsList.get(i).get("p_phone"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                if (stateList.get(i) == 1) {
                    holder.tvCheck.setImageResource(R.drawable.check_yes);
                } else {
                    holder.tvCheck.setImageResource(R.drawable.check_no);
                }
            }

            holder.tvName.setText(constactsList.get(i).get("p_name"));
            holder.tvPhone.setText(constactsList.get(i).get("p_phone"));

            holder.tvCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int state = stateList.get(i);
                    if (state == 1) {
                        stateList.set(i, 0);
                        holder.tvCheck.setImageResource(R.drawable.check_no);
                    } else {
                        stateList.set(i, 1);
                        holder.tvCheck.setImageResource(R.drawable.check_yes);
                    }
                }
            });
            return v;
        }

        class Holder {
            ImageView tvCheck;
            TextView tvName;
            TextView tvPhone;
            TextView create;
            View vHide;

            Holder(View view) {
                tvCheck = view.findViewById(R.id.tv_check);
                tvName = view.findViewById(R.id.tv_name);
                tvPhone = view.findViewById(R.id.tv_phone);
                create = view.findViewById(R.id.create);
                vHide = view.findViewById(R.id.v_hide);
            }
        }
    }


    /**
     * 得到联系人
     **/
    private ArrayList<HashMap<String, String>> readContact() {
        constactsList = new ArrayList<>();
        String[] cols = {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
        try {
            Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    cols, null, null, null);
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                // 取得联系人名字
                int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                int numberFieldColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String name = cursor.getString(nameFieldColumnIndex);
                String number = cursor.getString(numberFieldColumnIndex);

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("p_name", name);
                map.put("p_phone", number);
                if (!constactsList.contains(map) && map.get("p_phone") != null && map.get("p_name") != null) {
                    constactsList.add(map);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return constactsList;
    }

    /**
     * 获取健康大使的联系人列表
     */
    public void initData(){
        showProgressBar();
        OkGo.<String>get(Urls.URGENTPEOPLE)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("imei",MyApplication.userInfo.getJwotchImei())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取紧急联系人", json.toString());
                            if (json.getInt("status") == 200) {
                                Map<String, Object> data = new Gson().fromJson(body, new TypeToken<Map<String, Object>>() {}.getType());
                                linkList = (List<Map<String, Object>>) data.get("data");
                            } else if (json.getInt("status") == 505) {
                                reLogin(mContext);
                            }  else {
                                Toast.makeText(AddContactActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                            doGetPermission();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }



    /**
     * 批量上传
     */
    void upLinks(List<Map<String, Object>> contact) throws JSONException {
        try {
            if (!NetWorkStatus.isNetworkAvailable(mContext)) {
                Mytoast.show(mContext, "网络未连接");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        for (Map<String, Object> map1 : contact) {
            JSONObject j = new JSONObject();
            j.put("name", map1.get("name"));
            j.put("phone", map1.get("phone"));
            jsonArray.put(j);
        }

        jsonObject.put("imei", MyApplication.userInfo.getJwotchImei());
        jsonObject.put("urgentVos",jsonArray);

        Log.e( "upLinks: ", jsonObject.toString());
        showProgressBar();
        OkGo.<String>post(Urls.URGENTPEOPLE)
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("上传紧急联系人", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(mContext, "成功", Toast.LENGTH_LONG).show();
                                finish();
                            } else if (json.getInt("status") == 505) {
                                reLogin(mContext);
                            }  else {
                                Toast.makeText(mContext, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


    }

    /**
     * 单个上传
     */
    void upLink(String contactName,String contactPhone) throws JSONException {
        try {
            if (!NetWorkStatus.isNetworkAvailable(mContext)) {
                Mytoast.show(mContext, "网络未连接");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject j = new JSONObject();
            j.put("name",contactName);
            j.put("phone",contactPhone);
            jsonArray.put(j);

        jsonObject.put("imei", MyApplication.userInfo.getJwotchImei());
        jsonObject.put("urgentVos",jsonArray);

        Log.e( "upLinks: ", jsonObject.toString());
        showProgressBar();
        OkGo.<String>post(Urls.URGENTPEOPLE)
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("上传单个紧急联系人", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(mContext, "成功", Toast.LENGTH_SHORT).show();
                                EventBus.getDefault().post(new Contact());
                                finish();
                            } else {
                                Toast.makeText(mContext, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }
}
