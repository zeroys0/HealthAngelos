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
import net.leelink.healthangelos.bean.ProvinceBean;
import net.leelink.healthangelos.bean.StreetBean;
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
    RelativeLayout rl_back, rl_local, rl_organ, rl_sex, rl_nation, rl_educate, rl_province, rl_city, rl_couny, img_add, rl_street;
    TextView tv_local, tv_organ, tv_sex, tv_nation, tv_educate, tv_province, tv_city, tv_couny, tv_street;
    private AlertDialog dialog;//城市选择
    private EditText ed_name, ed_card, ed_phone, ed_address, ed_tall, ed_weight, ed_contact;
    String province_id;//省ID
    String city_id;//市ID
    String couny_id;//区ID
    String town_id; //街道ID
    String province_id_user; //个人住址省id
    String city_id_user; //个人住址市id
    String county_id_user; //个人住址区id
    String town_id_user; //个人区id
    Context context;
    int organ_id, sex, nature, educate;
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
        initData();
    }

    public void init() {
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
        rl_couny = findViewById(R.id.rl_couny);
        rl_couny.setOnClickListener(this);
        tv_couny = findViewById(R.id.tv_couny);
        ed_address = findViewById(R.id.ed_address);
        ed_tall = findViewById(R.id.ed_tall);
        ed_weight = findViewById(R.id.ed_weight);
        ed_contact = findViewById(R.id.ed_contact);
        img_add = findViewById(R.id.img_add);
        img_add.setOnClickListener(this);
        rl_street = findViewById(R.id.rl_street);
        rl_street.setOnClickListener(this);
        tv_street = findViewById(R.id.tv_street);
        organ_id = getIntent().getIntExtra("organId", 0);

//        if (getIntent().getStringExtra("address") != null) {
//            try {
//                JSONObject address = new JSONObject(getIntent().getStringExtra("address"));
//                couny_id = address.getString("countyId");
//                city_id = address.getString("cityId");
//                town_id = address.getString("townId");
//                province_id = address.getString("provinceId");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
                if (province_id == null) {
                    Toast.makeText(context, "请先选择省市", Toast.LENGTH_SHORT).show();
                } else {
                    city();
                }
                break;
            case R.id.rl_couny:     //区域
                if (city_id == null) {
                    Toast.makeText(context, "请先选择城市", Toast.LENGTH_SHORT).show();
                } else {
                    local();
                }
                break;
            case R.id.img_add:
                if(organ_id == 0) {
                    Toast.makeText(context, "请重新选择您的所属机构", Toast.LENGTH_SHORT).show();
                    return;
                }
                edit();
                break;
            case R.id.rl_street:
                street();
                break;
            case R.id.above:

                break;



        }
    }

    public void edit() {
        JSONObject json_address = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            json_address.put("province", tv_province.getText().toString());
            json_address.put("city", tv_city.getText().toString());
            json_address.put("countyId", county_id_user);
            json_address.put("county", tv_couny.getText().toString());
            json_address.put("townId", town_id_user);
            json_address.put("cityId", city_id_user);
            json_address.put("provinceId", province_id_user);
            json_address.put("town", tv_street.getText().toString());
            json_address.put("address", ed_address.getText().toString());
            json_address.put("fullAddress", tv_province.getText().toString() + tv_city.getText().toString() + tv_couny.getText().toString() + tv_street.getText().toString() + ed_address.getText().toString());

            jsonObject.put("address", json_address.toString());
            jsonObject.put("areaId", couny_id);
            jsonObject.put("cityId", city_id);
            jsonObject.put("education", educate);
            jsonObject.put("elderlyName", ed_name.getText().toString().trim());
            jsonObject.put("height", ed_tall.getText().toString().trim());
            jsonObject.put("nation", tv_nation.getText().toString().trim());
            if (organ_id != 0) {
                jsonObject.put("organId", organ_id);
            }
            jsonObject.put("provinceId", province_id);
            jsonObject.put("sex", sex);
            jsonObject.put("telephone", ed_phone.getText().toString().trim());
            jsonObject.put("urgentPhone", ed_contact.getText().toString().trim());
            jsonObject.put("weight", ed_weight.getText().toString().trim());
            jsonObject.put("idCard", ed_card.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("提交修改: ", jsonObject.toString());
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().USERINFO)
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
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        stopProgressBar();
                        Toast.makeText(context, "请确认信息完全", Toast.LENGTH_SHORT).show();
                    }
                });

    }
//    public void sub(String ...strings){
//        for(int i =0;i<strings.length;i++){
//            Log.e( "sub: ", strings[i]);
//        }
//    }

    public void initData() {
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().INFO)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {

                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("个人信息", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                ed_name.setText(json.getString("name"));
                                if (json.has("sex")) {
                                    sex = json.getInt("sex");
                                    switch (sex) {
                                        case 0:
                                            tv_sex.setText("男");
                                            break;
                                        case 1:
                                            tv_sex.setText("女");
                                            break;
                                    }
                                }
                                tv_organ.setText(json.getString("organName"));
                                JSONObject jsonObject = json.getJSONObject("elderlyUserInfo");
                                tv_nation.setText(jsonObject.getString("nation"));
                                if(jsonObject.has("organId")&&!jsonObject.isNull("organId")) {
                                    organ_id = jsonObject.getInt("organId");
                                }
                                province_id = jsonObject.getString("organProvinceId");
                                city_id = jsonObject.getString("organCityId");
                                couny_id = jsonObject.getString("organAreaId");
                                ed_card.setText(jsonObject.getString("idCard"));
                                ed_phone.setText(jsonObject.getString("telephone"));
                                String[] ed = new String[]{"小学", "初中", "高中", "技工学校", "中专/中技", "大专", "本科", "硕士", "博士", "其他"};
                                if (!jsonObject.getString("education").equals("null") && jsonObject.getInt("education") >0 && jsonObject.getInt("education")<10) {
                                    educate = jsonObject.getInt("education");
                                    tv_educate.setText(ed[jsonObject.getInt("education")]);
                                }
                                tv_local.setText(jsonObject.getString("organProvinceName")+jsonObject.getString("organCityName")+jsonObject.getString("organAreaName"));
                                String address = jsonObject.getString("address");
                                JSONObject j = new JSONObject(address);
                                ed_address.setText(j.getString("address"));
                                tv_province.setText(j.getString("province"));
                                tv_city.setText(j.getString("city"));
                                tv_couny.setText(j.getString("county"));
                                tv_street.setText(j.getString("town"));
                                province_id_user = j.getString("provinceId");
                                city_id_user = j.getString("cityId");
                                county_id_user = j.getString("countyId");
                                town_id_user = j.getString("townId");
                                ed_tall.setText(jsonObject.getString("height"));
                                ed_weight.setText(jsonObject.getString("weight"));
                                ed_contact.setText(jsonObject.getString("urgentPhone"));
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

    //选择省市区
    public void chooseLocal() {
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
    public void getOrgan() {
        OkGo.<String>get(Urls.getInstance().ORGAN)
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
                if (list.size() != 0) {
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
                    tv_street.setText(list.get(options1).getTown());
                    town_id_user = list.get(options1).getId();
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
    public void showEducate() {
        list_educate = Arrays.asList(getResources().getStringArray(R.array.educate_list));
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                tv_educate.setText(list_educate.get(options1));
                educate = options1;
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
        OkGo.<String>get(Urls.getInstance().GETPROVINCE)
                .tag(this)
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
                                List<ProvinceBean> list = gson.fromJson(jsonArray.toString(), new TypeToken<List<ProvinceBean>>() {
                                }.getType());
                                showProvince(list);
                            } else {
                                Toast.makeText(context, json.getString("ResultValue"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

//        province.clear();
//        String area_str = FileUtil.readAssets(this, "area.json");
//        province_list = parser.getJSONParserResult(area_str, "area0");
//        city_map = parser.getJSONParserResultArray(area_str, "area1");
//        couny_map = parser.getJSONParserResultArray(area_str, "area2");
//        for (Cityinfo cityinfo : province_list) {
//            province.add(cityinfo.getCity_name());
//        }
//        //条件选择器
//        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
//            @Override
//            public void onOptionsSelect(int options1, int option2, int options3, View v) {
//                tv_province.setText(province.get(options1));
//                province_id_user = province_list.get(options1).getId();
//            }
//        })
//                .setDividerColor(Color.parseColor("#A0A0A0"))
//                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
//                .setContentTextSize(18)//设置滚轮文字大小
//                .setOutSideCancelable(true)//点击外部dismiss default true
//                .build();
//        pvOptions.setPicker(province);
//        pvOptions.show();

    }

    public void showProvince(List<ProvinceBean> list){
        List<String> provinceName = new ArrayList<>();
        for (ProvinceBean provinceBean : list) {
            provinceName.add(provinceBean.getName());
        }
        //条件选择器d
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                if (list.size() != 0) {
                    tv_province.setText(list.get(options1).getName());
                    province_id_user = list.get(options1).getId();
                }
            }
        })
                .setDividerColor(Color.parseColor("#A0A0A0"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setContentTextSize(18)//设置滚轮文字大小
                .setOutSideCancelable(true)//点击外部dismiss default true
                .build();
        pvOptions.setPicker(provinceName);
        pvOptions.show();
    }

    //城市选择
    public void city() {
        city.clear();
        if (city_map.size() == 0) {
            String area_str = FileUtil.readAssets(this, "area.json");
            city_map = parser.getJSONParserResultArray(area_str, "area1");
        }
        final List<Cityinfo> cityinfoList = city_map.get(province_id_user);
        for (Cityinfo cityinfo : cityinfoList) {
            city.add(cityinfo.getCity_name());
        }
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                tv_city.setText(city.get(options1));
                city_id_user = cityinfoList.get(options1).getId();
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
        if (couny_map.size() == 0) {
            String area_str = FileUtil.readAssets(this, "area.json");
            couny_map = parser.getJSONParserResultArray(area_str, "area2");
        }
        final List<Cityinfo> cityinfoList = couny_map.get(city_id_user);
        for (Cityinfo cityinfo : cityinfoList) {
            local.add(cityinfo.getCity_name());
        }
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                tv_couny.setText(local.get(options1));
                county_id_user = cityinfoList.get(options1).getId();
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
                .params("id", county_id_user)
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
}
