package net.leelink.healthangelos.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.HomeDoctorListAdapter;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.HomeDoctorBean;
import net.leelink.healthangelos.city.CityPicker;
import net.leelink.healthangelos.city.Cityinfo;
import net.leelink.healthangelos.city.FileUtil;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeDoctorListActivity extends BaseActivity implements View.OnClickListener, OnOrderListener {
    TextView tv_province, tv_city, tv_local;
    RelativeLayout rl_back;
    private List<Cityinfo> province_list = new ArrayList<Cityinfo>();
    private HashMap<String, List<Cityinfo>> city_map = new HashMap<String, List<Cityinfo>>();
    private HashMap<String, List<Cityinfo>> couny_map = new HashMap<String, List<Cityinfo>>();
    List<String> province = new ArrayList<>();
    List<String> city = new ArrayList<>();
    List<String> local = new ArrayList<>();
    CityPicker.JSONParser parser = new CityPicker.JSONParser();
    String province_id;//省ID
    String city_id;//市ID
    String couny_id;//区ID
    Context context;
    RecyclerView doctor_list;
    private EditText ed_search;
    private int page = 1;
    List<HomeDoctorBean> list = new ArrayList<>();
    HomeDoctorListAdapter homeDoctorListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_doctor_list);
        init();
        context = this;
        createProgressBar(context);
        initList();
    }

    public void init() {
        tv_province = findViewById(R.id.tv_province);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_province.setOnClickListener(this);
        tv_city = findViewById(R.id.tv_city);
        tv_city.setOnClickListener(this);
        tv_local = findViewById(R.id.tv_local);
        tv_local.setOnClickListener(this);
        doctor_list = findViewById(R.id.doctor_list);
        ed_search = findViewById(R.id.ed_search);
        ed_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    list.clear();
                    initList();
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_province:
                province();
                break;
            case R.id.tv_city:
                if (province_id != null) {
                    city();
                }
                break;
            case R.id.tv_local:
                if (city_id != null) {
                    local();
                }
                break;
        }
    }

    public void initList() {
        HttpParams httpParams = new HttpParams();
        httpParams.put("areaId", couny_id);
        httpParams.put("cityId", city_id);
        httpParams.put("content", ed_search.getText().toString().trim());
        httpParams.put("pageNum", page);
        httpParams.put("pageSize", 10);
        httpParams.put("provinceId", province_id);
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().FAMILY_DOCTOR)
                .tag(this)
                .headers("token", MyApplication.token)
                .params(httpParams)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询医生列表", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                JSONArray jsonArray = json.getJSONArray("list");
                                Gson gson = new Gson();
                                list = gson.fromJson(jsonArray.toString(),new TypeToken<List<HomeDoctorBean>>(){}.getType());
                                homeDoctorListAdapter = new HomeDoctorListAdapter(list,context,HomeDoctorListActivity.this);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                                doctor_list.setLayoutManager(layoutManager);
                                doctor_list.setAdapter(homeDoctorListAdapter);
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    //选择省份
    public void province() {
        province.clear();

        String area_str = FileUtil.readAssets(this, "area.json");
        province_list = parser.getJSONParserResult(area_str, "area0");
        city_map = parser.getJSONParserResultArray(area_str, "area1");
        couny_map = parser.getJSONParserResultArray(area_str, "area2");
        for (Cityinfo cityinfo : province_list) {
            province.add(cityinfo.getCity_name());
        }
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                tv_province.setText(province.get(options1));
                province_id = province_list.get(options1).getId();
                initList();
            }
        })
                .setDividerColor(Color.parseColor("#A0A0A0"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setContentTextSize(18)//设置滚轮文字大小
                .setOutSideCancelable(true)//点击外部dismiss default true
                .build();
        pvOptions.setPicker(province);
        pvOptions.show();

    }

    //城市选择
    public void city() {
        city.clear();
        final List<Cityinfo> cityinfoList = city_map.get(province_id);
        for (Cityinfo cityinfo : cityinfoList) {
            city.add(cityinfo.getCity_name());
        }
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                tv_city.setText(city.get(options1));
                city_id = cityinfoList.get(options1).getId();
                initList();
            }
        })
                .setDividerColor(Color.parseColor("#A0A0A0"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setContentTextSize(18)//设置滚轮文字大小
                .setOutSideCancelable(true)//点击外部dismiss default true
                .build();
        pvOptions.setPicker(city);
        pvOptions.show();
    }

    //地区选择
    public void local() {
        local.clear();
        final List<Cityinfo> cityinfoList = couny_map.get(city_id);
        for (Cityinfo cityinfo : cityinfoList) {
            local.add(cityinfo.getCity_name());
        }
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                tv_local.setText(local.get(options1));
                couny_id = cityinfoList.get(options1).getId();
                initList();
            }
        })
                .setDividerColor(Color.parseColor("#A0A0A0"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setContentTextSize(18)//设置滚轮文字大小
                .setOutSideCancelable(true)//点击外部dismiss default true
                .build();
        pvOptions.setPicker(local);
        pvOptions.show();
    }

    @Override
    public void onItemClick(View view) {

    }

    @Override
    public void onButtonClick(View view, int position) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("doctorId",list.get(position).getCareDoctorRegedit().getId());
            jsonObject.put("id",MyApplication.userInfo.getOlderlyId());
            jsonObject.put("type",1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkGo.<String>post(Urls.getInstance().APPLYDOCTOR)
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
                            Log.d("申请签约医生", json.toString());
                            if (json.getInt("status") == 200) {

                                Toast.makeText(context, "申请成功 请等待审核" , Toast.LENGTH_SHORT).show();
                                finish();
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
