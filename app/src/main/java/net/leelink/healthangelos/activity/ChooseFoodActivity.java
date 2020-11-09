package net.leelink.healthangelos.activity;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.zjun.widget.RuleView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.ChooseFoodAdapter;
import net.leelink.healthangelos.adapter.ChooseListAdapter;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.adapter.OnchooseLisenter;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.FoodBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChooseFoodActivity extends BaseActivity implements View.OnClickListener, OnOrderListener, OnchooseLisenter {
    RelativeLayout rl_back;
    RecyclerView food_list,choose_list;
    Context context;
    ChooseFoodAdapter chooseFoodAdapter;
    List<FoodBean> list=  new ArrayList<>();
    RuleView ruler_view;
    TextView text_title,tv_save;
    JSONArray jsonArray = new JSONArray();
    int recordTypeState;
    ChooseListAdapter chooseListAdapter;
    List<String > name_list = new ArrayList<>();
    ImageView img_near,img_often;
    TextView tv_near,tv_often;
    EditText ed_search;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_food);
        context = this;
        createProgressBar(this);
        init();
        initList();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        text_title = findViewById(R.id.text_title);
        food_list = findViewById(R.id.food_list);
        tv_save = findViewById(R.id.tv_save);
        tv_save.setOnClickListener(this);
        img_near = findViewById(R.id.img_near);
        img_near.setOnClickListener(this);
        img_often = findViewById(R.id.img_often);
        img_often.setOnClickListener(this);
        tv_near = findViewById(R.id.tv_near);
        tv_near.setOnClickListener(this);
        tv_often = findViewById(R.id.tv_often);
        tv_often.setOnClickListener(this);
        choose_list = findViewById(R.id.choose_list);
        recordTypeState = getIntent().getIntExtra("type",1);
        switch (recordTypeState){
            case 1:
                text_title.setText("记早餐");
                break;
            case 2:
                text_title.setText("记午餐");
                break;
            case 3:
                text_title.setText("记晚餐");
                break;
            case 4:
                text_title.setText("记加餐");
                break;
        }
        ed_search = findViewById(R.id.ed_search);
        ed_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    searchList();
                }
                return false;
            }
        });
    }

    public void initList(){
        showProgressBar();
        HttpParams httpParams = new HttpParams();
        httpParams.put("pageNum",1);
        httpParams.put("pageSize",10);
        OkGo.<String>get(Urls.FOODRECORD)
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
                            Log.d("查询食物列表", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                JSONArray jsonArray = json.getJSONArray("list");
                                Gson gson = new Gson();
                                list = gson.fromJson(jsonArray.toString(),new TypeToken<List<FoodBean>>(){}.getType());
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                                chooseFoodAdapter = new ChooseFoodAdapter(list,context,ChooseFoodActivity.this);
                                food_list.setLayoutManager(layoutManager);
                                food_list.setAdapter(chooseFoodAdapter);

                            } else if (json.getInt("status") == 505) {
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

    public void searchList(){
        showProgressBar();
        HttpParams httpParams = new HttpParams();
        httpParams.put("pageNum",1);
        httpParams.put("pageSize",10);
        httpParams.put("content",ed_search.getText().toString());
        OkGo.<String>get(Urls.FOODRECORD)
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
                            Log.d("查询食物列表", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                JSONArray jsonArray = json.getJSONArray("list");
                                Gson gson = new Gson();
                                list = gson.fromJson(jsonArray.toString(),new TypeToken<List<FoodBean>>(){}.getType());
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                                chooseFoodAdapter = new ChooseFoodAdapter(list,context,ChooseFoodActivity.this);
                                food_list.setLayoutManager(layoutManager);
                                food_list.setAdapter(chooseFoodAdapter);

                            } else if (json.getInt("status") == 505) {
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
    public void initData(){
        showProgressBar();
        HttpParams httpParams = new HttpParams();
        httpParams.put("pageNum",1);
        httpParams.put("pageSize",10);
        OkGo.<String>get(Urls.RECENTRECORD)
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
                            Log.d("查询最近食物列表", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                JSONArray jsonArray = json.getJSONArray("list");
                                Gson gson = new Gson();
                                list = gson.fromJson(jsonArray.toString(),new TypeToken<List<FoodBean>>(){}.getType());
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                                chooseFoodAdapter = new ChooseFoodAdapter(list,context,ChooseFoodActivity.this);
                                food_list.setLayoutManager(layoutManager);
                                food_list.setAdapter(chooseFoodAdapter);

                            }  else if (json.getInt("status") == 505) {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_save:
                if(name_list.size()>0) {
                    save();
                } else {
                    Toast.makeText(context, "请添加数据", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_often:
            case R.id.img_often:
                initList();
                break;
            case R.id.tv_near:
            case R.id.img_near:
                initData();
                break;
        }
    }

    public void save(){
        showProgressBar();
        OkGo.<String>post(Urls.RECORD)
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(jsonArray)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("上传食物", json.toString());
                            if (json.getInt("status") == 200) {
                                jsonArray = new JSONArray();

                                chooseListAdapter.notifyDataSetChanged();
                                Toast.makeText(context, "上传成功", Toast.LENGTH_LONG).show();
                            } else if (json.getInt("status") == 505) {
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

    @Override
    public void onItemClick(View view) {

    }

    @Override
    public void onChooseClick(View view, int position) {
        jsonArray.remove(position);
        name_list.remove(position);
        chooseListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onButtonClick(View view, int position) {
        backgroundAlpha(0.5f);
        final Integer[] totalCarbo = new Integer[1];
        final Integer[] totalFat = new Integer[1];
        final Integer[] totalHeav = new Integer[1];
        final Integer[] totalKcal = new Integer[1];
        final Integer[] totalProtein = new Integer[1];
        final int[] type = {1};
        final FoodBean foodBean = list.get(position);
        View popView = getLayoutInflater().inflate(R.layout.popu_bottom_ruler_choose, null);
        TextView tv_name = popView.findViewById(R.id.tv_name);
        TextView tv_cancel = popView.findViewById(R.id.tv_cancel);
        final TextView tv_unit = popView.findViewById(R.id.tv_unit);
        final TextView tv_type = popView.findViewById(R.id.tv_type);
        Button btn_enter = popView.findViewById(R.id.btn_enter);
        tv_name.setText(foodBean.getName());
        ruler_view = popView.findViewById(R.id.ruler_view);
        ruler_view.setValue(0,10000,0,5,10);
        TextView tv_g =popView.findViewById(R.id.tv_g);
        tv_g.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ruler_view.setValue(0,10000,0,5,10);
                tv_unit.setText("千卡");
                type[0] = 1;
            }
        });
        TextView tv_single = popView.findViewById(R.id.tv_single);
        tv_single.setText(foodBean.getUnit());
        tv_single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ruler_view.setValue(0,100,0,0.1f,10);
                tv_unit.setText(foodBean.getUnit());
                type[0] = 2;
            }
        });
        final float unit_kcal = foodBean.getKcal()/100;
        final float unit_fat = foodBean.getFat()/100;
        final float unit_protein = foodBean.getProtein()/100;
        final float unit_carbon = foodBean.getCarbo()/100;
        final DecimalFormat decimalFormat = new DecimalFormat("0.0");


        final TextView tv_size = popView.findViewById(R.id.tv_size);
        ruler_view.setOnValueChangedListener(new RuleView.OnValueChangedListener() {
            @Override
            public void onValueChanged(float value) {
                if(type[0] ==1) {
                    float f = unit_kcal * value;
                    tv_size.setText( decimalFormat.format(f));
                    totalKcal[0] = (int) (unit_kcal*value);
                    totalCarbo[0] = (int)(unit_carbon*value);
                    totalFat[0] = (int)(unit_fat*value);
                    totalProtein[0] = (int)(unit_protein*value);
                    totalHeav[0] =(int)(value);

                    tv_type.setText(value+"克");
                } else if(type[0]==2) {
                    float f = unit_kcal * value *foodBean.getUnitGram()*10;
                    float weight = value *foodBean.getUnitGram()*10;
                    tv_size.setText(value+"");
                    totalKcal[0] = (int) (unit_kcal*weight);
                    totalCarbo[0] = (int)(unit_carbon*weight);
                    totalFat[0] = (int)(unit_fat*weight);
                    totalProtein[0] = (int)(unit_protein*weight);
                    totalHeav[0] =(int)(weight);
                    tv_type.setText("约"+ weight+"g "+ decimalFormat.format(f)+"千卡");
                }
            }
        });
        final PopupWindow pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOnDismissListener(new ChooseFoodActivity.poponDismissListener());

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
            }
        });
        pop.showAtLocation(rl_back, Gravity.BOTTOM,0,0);
        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("jkFootId",foodBean.getId());
                    jsonObject.put("recordTypeState",recordTypeState);
                    jsonObject.put("totalCarbo",totalCarbo[0]);
                    jsonObject.put("totalFat",totalFat[0]);
                    jsonObject.put("totalHeav",totalHeav[0]);
                    jsonObject.put("totalKcal",totalKcal[0]);
                    jsonObject.put("totalProtein",totalProtein[0]);
                    for(int i=0;i<name_list.size();i++) {
                        if(name_list.get(i).equals(foodBean.getName())) {
                            jsonArray.remove(i);
                            name_list.remove(i);
                        }
                    }
                    name_list.add(foodBean.getName());
                    jsonArray.put(jsonObject);

                    update();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pop.dismiss();

            }
        });
    }

    public void update(){
        if(chooseListAdapter == null) {
            chooseListAdapter = new ChooseListAdapter(jsonArray, context, this,name_list);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
            choose_list.setAdapter(chooseListAdapter);
            choose_list.setLayoutManager(layoutManager);
        } else {
            chooseListAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        if (bgAlpha == 1) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        getWindow().setAttributes(lp);
    }

    /**
     * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     *
     * @author cg
     */
    class poponDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            // Log.v("List_noteTypeActivity:", "我是关闭事件");
            backgroundAlpha(1f);
        }
    }
}
