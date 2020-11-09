package net.leelink.healthangelos.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.OrganBean;
import net.leelink.healthangelos.city.CityPicker;
import net.leelink.healthangelos.city.Cityinfo;
import net.leelink.healthangelos.city.FileUtil;
import net.leelink.healthangelos.city.ScrollerNumberPicker;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class EditInfoActivity extends BaseActivity implements View.OnClickListener {
    RelativeLayout rl_back,rl_local,rl_organ,rl_sex,rl_nation,rl_educate,rl_province,rl_city,rl_couny,img_add;
    TextView tv_local,tv_organ,tv_sex,tv_nation,tv_educate,tv_province,tv_city,tv_couny;
    private AlertDialog dialog;//城市选择
    private EditText ed_name,ed_card,ed_phone,ed_address,ed_tall,ed_weight,ed_contact;
    String province_id;//省ID
    String city_id;//市ID
    String couny_id;//区ID
    Context context;
    int organ_id,sex,nature,educate;
    private List<String> list_sex = new ArrayList<>();
    private List<String> list_nation = new ArrayList<>();
    private List<String> list_educate = new ArrayList<>();
    private List<Cityinfo> province_list = new ArrayList<Cityinfo>();
    private HashMap<String, List<Cityinfo>> city_map = new HashMap<String, List<Cityinfo>>();
    private HashMap<String, List<Cityinfo>> couny_map = new HashMap<String, List<Cityinfo>>();
    List<String> province = new ArrayList<>();
    List<String> city = new ArrayList<>();
    List<String> local = new ArrayList<>();
    CityPicker.JSONParser parser = new CityPicker.JSONParser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_edit_info);
        createProgressBar(this);
        context = this;
        init();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        rl_local = findViewById(R.id.rl_local);
        rl_local.setOnClickListener(this);
        tv_local = findViewById(R.id.tv_local);
        rl_organ = findViewById(R.id.rl_organ);
        rl_organ.setOnClickListener(this);
        tv_organ = findViewById(R.id.tv_organ);
        ed_name = findViewById(R.id.ed_name);
        rl_sex = findViewById(R.id.rl_sex);
        rl_sex.setOnClickListener(this);
        tv_sex = findViewById(R.id.tv_sex);
        rl_nation = findViewById(R.id.rl_nation);
        rl_nation.setOnClickListener(this);
        tv_nation = findViewById(R.id.tv_nation);
        ed_card = findViewById(R.id.ed_card);
        ed_phone = findViewById(R.id.ed_phone);
        rl_educate = findViewById(R.id.rl_educate);
        rl_educate.setOnClickListener(this);
        tv_educate = findViewById(R.id.tv_educate);
        rl_province = findViewById(R.id.rl_province);
        rl_province.setOnClickListener(this);
        tv_province = findViewById(R.id.tv_province);
        rl_city = findViewById(R.id.rl_city);
        rl_city.setOnClickListener(this);
        tv_city = findViewById(R.id.tv_city);
        rl_couny =  findViewById(R.id.rl_couny);
        rl_couny.setOnClickListener(this);
        tv_couny = findViewById(R.id.tv_couny);
        ed_address = findViewById(R.id.ed_address);
        ed_tall = findViewById(R.id.ed_tall);
        ed_weight =  findViewById(R.id.ed_weight);
        ed_contact = findViewById(R.id.ed_contact);
        img_add = findViewById(R.id.img_add);
        img_add.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_local:     //选择机构所在区域
                chooseLocal();
                break;
            case R.id.rl_organ:     //区域下机构列表
                getOrgan();
                break;
            case R.id.rl_sex:       //弹出性别选择
                showSex();
                break;
            case R.id.rl_nation:    //弹出民族列表
                showNation();
                break;
            case R.id.rl_educate:   //教育程度
                showEducate();
                break;
            case R.id.rl_province:      //省份
                province();
                break;
            case R.id.rl_city:      //城市
                city();
                break;
            case R.id.rl_couny:     //区域
                local();
                break;
            case R.id.img_add:
                edit();
                break;

        }
    }

    public void edit(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("address",ed_address.getText().toString().trim());
            jsonObject.put("areaId",couny_id);
            jsonObject.put("cityId",city_id);
            jsonObject.put("education",educate);
            jsonObject.put("elderlyName",ed_name.getText().toString().trim());
            jsonObject.put("height",ed_tall.getText().toString().trim());
            jsonObject.put("nation",tv_nation.getText().toString().trim());
            jsonObject.put("organId",organ_id);
            jsonObject.put("provinceId",province_id);
            jsonObject.put("sex",sex);
            jsonObject.put("telephone",ed_phone.getText().toString().trim());
            jsonObject.put("urgentPhone",ed_contact.getText().toString().trim());
            jsonObject.put("weight",ed_weight.getText().toString().trim());
            jsonObject.put("idCard",ed_card.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e( "edit: ", jsonObject.toString());
        showProgressBar();
        OkGo.<String>post(Urls.USERINFO)
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
                            Log.d("修改个人信息", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "修改完成", Toast.LENGTH_LONG).show();
                                finish();
                            }else if (json.getInt("status") == 505) {
                               reLogin(context);
                            }  else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    //选择省市区
    public void chooseLocal(){
        AlertDialog.Builder builder = new AlertDialog.Builder(EditInfoActivity.this, R.style.Dialog_FS);
        View view = LayoutInflater.from(EditInfoActivity.this).inflate(R.layout.addressdialog, null);
        builder.setView(view);
        TextView addressdialog_linearlayout = (TextView) view.findViewById(R.id.addressdialog_linearlayout);
        final ScrollerNumberPicker provincePicker = (ScrollerNumberPicker) view.findViewById(R.id.province);
        final ScrollerNumberPicker cityPicker = (ScrollerNumberPicker) view.findViewById(R.id.city);
        final ScrollerNumberPicker counyPicker = (ScrollerNumberPicker) view.findViewById(R.id.couny);
        dialog = builder.show();

        //设置弹窗在底部
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);

        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); //为获取屏幕宽、高
        WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = d.getWidth(); //宽度设置为屏幕
        dialog.getWindow().setAttributes(p); //设置生效
        addressdialog_linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String province = provincePicker.getSelectedText();
                String city = cityPicker.getSelectedText();
                String couny = counyPicker.getSelectedText();
                province_id = provincePicker.getSelected();
                city_id = cityPicker.getSelected();
                couny_id = counyPicker.getSelected();

                tv_local.setText(province + "/" + city + "/" + couny);
                dialog.dismiss();
            }
        });
    }

    //获取机构
    public void getOrgan(){
        OkGo.<String>get(Urls.ORGAN)
                .tag(this)
                .params("areaId", couny_id)
                //      .params("deviceToken", JPushInterface.getRegistrationID(LoginActivity.this))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询机构", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                Gson gson = new Gson();
                                List<OrganBean> list = gson.fromJson(jsonArray.toString(), new TypeToken<List<OrganBean>>() {
                                }.getType());
                                showOrgan(list);
                            } else {
                                Toast.makeText(context, json.getString("ResultValue"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //弹出机构列表
    public void showOrgan(final List<OrganBean> list) {
        List<String> organName = new ArrayList<>();
        for (OrganBean organBean : list) {
            organName.add(organBean.getOrganName());
        }
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                if(list.size()!=0) {
                    tv_organ.setText(list.get(options1).getOrganName());
                    organ_id = list.get(options1).getId();
                }
            }
        })
                .setDividerColor(Color.parseColor("#A0A0A0"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setContentTextSize(18)//设置滚轮文字大小
                .setOutSideCancelable(true)//点击外部dismiss default true
                .build();
        pvOptions.setPicker(organName);
        pvOptions.show();
    }

    //性别选择器
    public void showSex() {
        list_sex.clear();
        list_sex.add("男");
        list_sex.add("女");
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                tv_sex.setText(list_sex.get(options1));
                if (list_sex.get(options1).equals("男")) {
                    sex = 0;
                } else if (list_sex.get(options1).equals("女")) {
                    sex = 1;
                }
            }
        })
                .setDividerColor(Color.parseColor("#A0A0A0"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setContentTextSize(18)//设置滚轮文字大小
                .setOutSideCancelable(true)//点击外部dismiss default true
                .build();
        pvOptions.setPicker(list_sex);
        pvOptions.show();
    }

    //民族选择
    public void showNation() {
        list_nation = Arrays.asList(getResources().getStringArray(R.array.nation_list));
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                tv_nation.setText(list_nation.get(options1));
                nature = options1 + 1;
            }
        })
                .setDividerColor(Color.parseColor("#A0A0A0"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setContentTextSize(18)//设置滚轮文字大小
                .setOutSideCancelable(true)//点击外部dismiss default true
                .build();

        pvOptions.setPicker(list_nation);
        pvOptions.show();
    }
    //教育程度
    public void showEducate(){
        list_educate = Arrays.asList(getResources().getStringArray(R.array.educate_list));
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                tv_educate.setText(list_educate.get(options1));
                educate = options1 + 1;
            }
        })
                .setDividerColor(Color.parseColor("#A0A0A0"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setContentTextSize(18)//设置滚轮文字大小
                .setOutSideCancelable(true)//点击外部dismiss default true
                .build();

        pvOptions.setPicker(list_educate);
        pvOptions.show();
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
                tv_couny.setText(local.get(options1));
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
}
