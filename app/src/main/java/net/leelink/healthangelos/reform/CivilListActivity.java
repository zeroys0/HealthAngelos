package net.leelink.healthangelos.reform;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import net.leelink.healthangelos.reform.bean.CommProvinceBean;
import net.leelink.healthangelos.reform.bean.CommunityBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CivilListActivity extends BaseActivity {
    private Context context;
    RelativeLayout rl_province, rl_back, rl_city, rl_local, rl_town, rl_community;
    private TextView tv_province, tv_city, tv_local, tv_town, tv_civil, tv_community;
    private Button btn_confirm;


    String province_id;//省ID
    String city_id;//市ID
    String local_id;//区ID
    String town_id;//街道(乡/镇)id
    String community_id;//社区id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_civil_list);
        init();
        context = this;
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rl_province = findViewById(R.id.rl_province);
        rl_province.setOnClickListener(this);
        rl_city = findViewById(R.id.rl_city);
        rl_city.setOnClickListener(this);
        rl_local = findViewById(R.id.rl_local);
        rl_local.setOnClickListener(this);
        rl_town = findViewById(R.id.rl_town);
        rl_town.setOnClickListener(this);
        tv_province = findViewById(R.id.tv_province);
        tv_city = findViewById(R.id.tv_city);
        tv_local = findViewById(R.id.tv_local);
        rl_community = findViewById(R.id.rl_community);
        rl_community.setOnClickListener(this);
        tv_town = findViewById(R.id.tv_town);
        tv_civil = findViewById(R.id.tv_civil);
        tv_community = findViewById(R.id.tv_community);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            //选择省
            case R.id.rl_province:
                province();
                break;
            //选择市
            case R.id.rl_city:
                if (province_id == null) {
                    Toast.makeText(context, "请先选择省市", Toast.LENGTH_SHORT).show();
                } else {
                    getCity();
                }
                break;
            //选择区
            case R.id.rl_local:
                if (city_id == null) {
                    Toast.makeText(context, "请先选择城市", Toast.LENGTH_SHORT).show();
                } else {
                    getLocal();
                }
                break;
            //选择街道
            case R.id.rl_town:
                if (local_id == null) {
                    Toast.makeText(context, "请先选择区(县)", Toast.LENGTH_SHORT).show();
                } else {
                    getTown();
                }
                break;
                //选择所在社区
            case R.id.rl_community:
                if (town_id == null) {
                    Toast.makeText(context, "请先选择街道(乡/镇)", Toast.LENGTH_SHORT).show();
                } else {
                    getCommunity();
                }
                break;
            case R.id.btn_confirm:
                Intent intent = new Intent();
                intent.putExtra("name", tv_community.getText().toString());
                intent.putExtra("id", community_id);
                setResult(7, intent);
                finish();
                break;
        }
    }


    //选择省份
    public void province() {
        OkGo.<String>get(Urls.getInstance().VILLAGE_PROVINCE)
                .tag(this)
                //      .params("deviceToken", JPushInterface.getRegistrationID(LoginActivity.this))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询民政省份", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                Gson gson = new Gson();
                                List<CommProvinceBean> list = gson.fromJson(jsonArray.toString(), new TypeToken<List<CommProvinceBean>>() {
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
    }

    public void showProvince(List<CommProvinceBean> list) {
        List<String> provinceName = new ArrayList<>();
        for (CommProvinceBean provinceBean : list) {
            provinceName.add(provinceBean.getLabel());
        }
        //条件选择器d
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                if (list.size() != 0) {
                    tv_province.setText(list.get(options1).getLabel());

                    province_id = list.get(options1).getValue().split(",")[0];
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

    public void getCity() {
        Log.d( "getCity: ",province_id);
        OkGo.<String>get(Urls.getInstance().VILLAGE_CITY + "/" + province_id)
                .tag(this)
                //      .params("deviceToken", JPushInterface.getRegistrationID(LoginActivity.this))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询民政市区", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                Gson gson = new Gson();
                                List<CommProvinceBean> list = gson.fromJson(jsonArray.toString(), new TypeToken<List<CommProvinceBean>>() {
                                }.getType());
                                showCity(list);
                            } else {
                                Toast.makeText(context, json.getString("ResultValue"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void showCity(List<CommProvinceBean> list) {
        List<String> provinceName = new ArrayList<>();
        for (CommProvinceBean provinceBean : list) {
            provinceName.add(provinceBean.getLabel());
        }
        //条件选择器d
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                if (list.size() != 0) {
                    tv_city.setText(list.get(options1).getLabel());
                    city_id =  list.get(options1).getValue().split(",")[0];
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


    public void getLocal() {
        OkGo.<String>get(Urls.getInstance().VILLAGE_COUNTY + "/" + city_id)
                .tag(this)
                //      .params("deviceToken", JPushInterface.getRegistrationID(LoginActivity.this))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询区/县", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                Gson gson = new Gson();
                                List<CommProvinceBean> list = gson.fromJson(jsonArray.toString(), new TypeToken<List<CommProvinceBean>>() {
                                }.getType());
                                showLocal(list);
                            } else {
                                Toast.makeText(context, json.getString("ResultValue"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void showLocal(List<CommProvinceBean> list) {
        List<String> provinceName = new ArrayList<>();
        for (CommProvinceBean provinceBean : list) {
            provinceName.add(provinceBean.getLabel());
        }
        //条件选择器d
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                if (list.size() != 0) {
                    tv_local.setText(list.get(options1).getLabel());
                    local_id =  list.get(options1).getValue().split(",")[0];
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

    public void getTown() {
        OkGo.<String>get(Urls.getInstance().VILLAGE_TOWN + "/" + local_id)
                .tag(this)
                //      .params("deviceToken", JPushInterface.getRegistrationID(LoginActivity.this))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询区/县", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                Gson gson = new Gson();
                                List<CommProvinceBean> list = gson.fromJson(jsonArray.toString(), new TypeToken<List<CommProvinceBean>>() {
                                }.getType());
                                showTown(list);
                            } else {
                                Toast.makeText(context, json.getString("ResultValue"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void showTown(List<CommProvinceBean> list) {
        List<String> provinceName = new ArrayList<>();
        for (CommProvinceBean provinceBean : list) {
            provinceName.add(provinceBean.getLabel());
        }
        //条件选择器d
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                if (list.size() != 0) {
                    tv_town.setText(list.get(options1).getLabel());
                    town_id =  list.get(options1).getValue().split(",")[0];
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

    public void getCommunity() {
        OkGo.<String>get(Urls.getInstance().VILLAGE_COMMUNITY + "/" + town_id)
                .tag(this)
                //      .params("deviceToken", JPushInterface.getRegistrationID(LoginActivity.this))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询社区", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                Gson gson = new Gson();
                                List<CommunityBean> list = gson.fromJson(jsonArray.toString(), new TypeToken<List<CommunityBean>>() {
                                }.getType());
                                showCommunity(list);
                            } else {
                                Toast.makeText(context, json.getString("ResultValue"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void showCommunity(List<CommunityBean> list) {
        List<String> provinceName = new ArrayList<>();
        for (CommunityBean communityBean : list) {
            provinceName.add(communityBean.getVillage());
        }
        //条件选择器d
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                if (list.size() != 0) {
                    tv_community.setText(list.get(options1).getVillage());
                    community_id =  list.get(options1).getId();
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






}
