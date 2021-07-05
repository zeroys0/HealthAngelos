package net.leelink.healthangelos.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.bean.StreetBean;
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

public class ChooseAddressActivity extends BaseActivity implements View.OnClickListener {
    RelativeLayout rl_province,rl_back,rl_city,rl_local,rl_town;
    List<String> province = new ArrayList<>();
    CityPicker.JSONParser parser = new CityPicker.JSONParser();
    private List<Cityinfo> province_list = new ArrayList<Cityinfo>();
    List<String> city = new ArrayList<>();
    List<String> local = new ArrayList<>();
    private HashMap<String, List<Cityinfo>> city_map = new HashMap<String, List<Cityinfo>>();
    private HashMap<String, List<Cityinfo>> couny_map = new HashMap<String, List<Cityinfo>>();
    String province_id;//省ID
    String city_id;//市ID
    String couny_id;//区ID
    String town_id;//街道id
    Context context;
    EditText ed_address;
    private TextView tv_province,tv_city,tv_local,tv_town;
    private Button btn_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_address);
        init();
        context =this;
    }

    public void init(){
        rl_province = findViewById(R.id.rl_province);
        rl_province.setOnClickListener(this);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        tv_province = findViewById(R.id.tv_province);
        rl_city = findViewById(R.id.rl_city);
        rl_city.setOnClickListener(this);
        tv_city = findViewById(R.id.tv_city);
        rl_local = findViewById(R.id.rl_local);
        rl_local.setOnClickListener(this);
        tv_local = findViewById(R.id.tv_local);
        ed_address = findViewById(R.id.ed_address);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
        rl_town = findViewById(R.id.rl_town);
        rl_town.setOnClickListener(this);
        tv_town = findViewById(R.id.tv_town);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_province:
                province();
                break;
            case R.id.rl_city:
                if(province_id!=null) {
                    city();
                }else {
                    Toast.makeText(context, "请选择省份", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.rl_local:
                if(city_id!=null) {
                    local();
                }else {
                    Toast.makeText(context, "请选择城市", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_confirm:
                confirm();
                break;
            case R.id.rl_town:
                street();
                break;

        }
    }

    public void confirm(){
        if(tv_local.getText().toString().equals("")){
            Toast.makeText(context, "填写正确的地址", Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject json_address = new JSONObject();
        try {
            json_address.put("province", tv_province.getText().toString());
            json_address.put("city", tv_city.getText().toString());
            json_address.put("countyId", couny_id);
            json_address.put("county", tv_local.getText().toString());
            json_address.put("townId", town_id);
            json_address.put("cityId", city_id);
            json_address.put("provinceId", province_id);
            json_address.put("town", tv_town.getText().toString());
            json_address.put("address", ed_address.getText().toString());
            json_address.put("fullAddress", tv_province.getText().toString() + tv_city.getText().toString() + tv_local.getText().toString() + tv_town.getText().toString() + ed_address.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String address = tv_province.getText().toString()+tv_city.getText().toString()+tv_local.getText().toString()+ed_address.getText().toString().trim();
        Intent intent = new Intent();
        intent.putExtra("local_id",couny_id);
        intent.putExtra("local_address",ed_address.getText().toString().trim());
        intent.putExtra("address",address);
        intent.putExtra("json",json_address.toString());
        setResult(2,intent);
        finish();
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

    //街道选择
    public void street() {
        OkGo.<String>get(Urls.getInstance().GETTOWN)
                .tag(this)
                .params("id", couny_id)
                //      .params("deviceToken", JPushInterface.getRegistrationID(LoginActivity.this))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询街道", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                Gson gson = new Gson();
                                List<StreetBean> list = gson.fromJson(jsonArray.toString(), new TypeToken<List<StreetBean>>() {
                                }.getType());
                                showStreet(list);
                            } else {
                                Toast.makeText(context, json.getString("ResultValue"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
    //弹出街道列表
    public void showStreet(final List<StreetBean> list) {
        List<String> streetName = new ArrayList<>();
        for (StreetBean streetBean : list) {
            streetName.add(streetBean.getTown());
        }
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                if (list.size() != 0) {
                    tv_town.setText(list.get(options1).getTown());
                    town_id = list.get(options1).getId();
                }
            }
        })
                .setDividerColor(Color.parseColor("#A0A0A0"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setContentTextSize(18)//设置滚轮文字大小
                .setOutSideCancelable(true)//点击外部dismiss default true
                .build();
        pvOptions.setPicker(streetName);
        pvOptions.show();
    }
}
