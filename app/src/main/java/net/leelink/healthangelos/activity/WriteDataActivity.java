package net.leelink.healthangelos.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
import com.lzy.imagepicker.ImageDataSource;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.pattonsoft.pattonutil1_0.util.Mytoast;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.JoinAdapter;
import net.leelink.healthangelos.adapter.OnItemJoinClickListener;
import net.leelink.healthangelos.adapter.TopTenAdapter;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.RankBean;
import net.leelink.healthangelos.util.BitmapCompress;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.view.CustomLinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.app.AlertDialog.THEME_HOLO_LIGHT;


public class WriteDataActivity extends BaseActivity implements View.OnClickListener, OnItemJoinClickListener {
    private TextView tv_history, tv_time, tv_dis, tv_num;
    private CheckBox cb_1, cb_2, cb_3, cb_4, cb_5, cb_6, cb_7, cb_8, cb_9, cb_10, cb_11, cb_12, cb_13;
    private RelativeLayout ll_1, ll_2, ll_3, ll_4, ll_5, ll_6, ll_7, ll_8, ll_9, ll_10, ll_11, ll_12, ll_13, rl_back;
    private EditText ed_1, ed_21, ed_22, ed_3, ed_4, ed_5, ed_6, ed_7, ed_8, ed_9, ed_10, ed_11, ed_12, ed_13;
    Context mContext = this;
    Calendar calendar2;
    String TestDate; //测量时间
    String selectText = "";
    String htmlText = "";
    public static final int IMAGE_ITEM_ADD = -1;
    InvokeParam invokeParam;
    String path1;
    Uri uri;
    int PS;    //int	收缩压	mmHg
    int PD;    //int	舒张压	mmHg
    int PR;    //int	心率	次/分钟
    int BloodOxygen;    //double	血氧	%
    int BloodGlucose;    //	double	血糖	mmol/L
    int Step;    //double	步数	步
    int Calorie;    //double	卡路里	卡
    int Temperature;
    int Weight;    //double	体重	kg
    int FatRate;    //double	体脂肪率	%
    int Moisture;    //	double	水分率	%
    int Muscle;    //double	肌肉率	%
    int Bone;
    int BM;    //double	基础代谢	--
    RecyclerView recyclerview;
    JoinAdapter joinAdapter;
    private List<ImageItem> list = new ArrayList<>();
    Button but_over;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_write_data);
        if (Build.VERSION.SDK_INT >= 24) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        }
        init();
        initData();
        createProgressBar(this);
    }

    public void init() {
        recyclerview = findViewById(R.id.recyclerview);
        tv_history = findViewById(R.id.tv_history);
        tv_history.setOnClickListener(this);
        tv_time = findViewById(R.id.tv_time);
        tv_time.setOnClickListener(this);
        cb_1 = findViewById(R.id.cb_1);
        cb_1.setOnClickListener(this);
        cb_2 = findViewById(R.id.cb_2);
        cb_2.setOnClickListener(this);
        cb_3 = findViewById(R.id.cb_3);
        cb_3.setOnClickListener(this);
        cb_4 = findViewById(R.id.cb_4);
        cb_4.setOnClickListener(this);
        cb_5 = findViewById(R.id.cb_5);
        cb_5.setOnClickListener(this);
        cb_6 = findViewById(R.id.cb_6);
        cb_6.setOnClickListener(this);
        cb_7 = findViewById(R.id.cb_7);
        cb_7.setOnClickListener(this);
        cb_8 = findViewById(R.id.cb_8);
        cb_8.setOnClickListener(this);
        cb_9 = findViewById(R.id.cb_9);
        cb_9.setOnClickListener(this);
        cb_10 = findViewById(R.id.cb_10);
        cb_10.setOnClickListener(this);
        cb_11 = findViewById(R.id.cb_11);
        cb_11.setOnClickListener(this);
        cb_12 = findViewById(R.id.cb_12);
        cb_12.setOnClickListener(this);
        cb_13 = findViewById(R.id.cb_13);
        cb_13.setOnClickListener(this);
        tv_dis = findViewById(R.id.tv_dis);
        tv_dis.setOnClickListener(this);
        ll_1 = findViewById(R.id.ll_1);
        ll_2 = findViewById(R.id.ll_2);
        ll_3 = findViewById(R.id.ll_3);
        ll_4 = findViewById(R.id.ll_4);
        ll_5 = findViewById(R.id.ll_5);
        ll_6 = findViewById(R.id.ll_6);
        ll_7 = findViewById(R.id.ll_7);
        ll_8 = findViewById(R.id.ll_8);
        ll_9 = findViewById(R.id.ll_9);
        ll_10 = findViewById(R.id.ll_10);
        ll_11 = findViewById(R.id.ll_11);
        ll_12 = findViewById(R.id.ll_12);
        ll_13 = findViewById(R.id.ll_13);
        ll_1.setVisibility(View.GONE);
        ll_2.setVisibility(View.GONE);
        ll_3.setVisibility(View.GONE);
        ll_4.setVisibility(View.GONE);
        ll_5.setVisibility(View.GONE);
        ll_6.setVisibility(View.GONE);
        ll_7.setVisibility(View.GONE);
        ll_8.setVisibility(View.GONE);
        ll_9.setVisibility(View.GONE);
        ll_10.setVisibility(View.GONE);
        ll_11.setVisibility(View.GONE);
        ll_12.setVisibility(View.GONE);
        ll_13.setVisibility(View.GONE);
        ed_1 = findViewById(R.id.ed_1);
        ed_21 = findViewById(R.id.ed_21);
        ed_22 = findViewById(R.id.ed_22);
        ed_3 = findViewById(R.id.ed_3);
        ed_4 = findViewById(R.id.ed_4);
        ed_5 = findViewById(R.id.ed_5);
        ed_6 = findViewById(R.id.ed_6);
        ed_7 = findViewById(R.id.ed_7);
        ed_8 = findViewById(R.id.ed_8);
        ed_9 = findViewById(R.id.ed_9);
        ed_10 = findViewById(R.id.ed_10);
        ed_11 = findViewById(R.id.ed_11);
        ed_12 = findViewById(R.id.ed_12);
        ed_13 = findViewById(R.id.ed_13);
        tv_num = findViewById(R.id.tv_num);
        tv_num.setText("0/9");
        rl_back = findViewById(R.id.rl_back);
        but_over = findViewById(R.id.but_over);
        but_over.setOnClickListener(this);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
    }

    public void initData() {
        joinAdapter = new JoinAdapter(list, this, this, 3);
        CustomLinearLayoutManager customLinearLayoutManager = new CustomLinearLayoutManager(this, 3);
        recyclerview.setAdapter(joinAdapter);
        recyclerview.setLayoutManager(customLinearLayoutManager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_history:
                Intent intent = new Intent(this,HistoryDataActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_time:
                Calendar calendar = Calendar.getInstance();

                final DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar2 = Calendar.getInstance();
                        calendar2.set(Calendar.YEAR, year);
                        calendar2.set(Calendar.MONTH, month);
                        calendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                        TimePickerDialog dialog = new TimePickerDialog(mContext, THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                calendar2.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar2.set(Calendar.MINUTE, minute);

                                TestDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:00").format(new java.util.Date(calendar2.getTimeInMillis()));
                                tv_time.setText(TestDate);
                            }
                        }, calendar2.get(Calendar.HOUR_OF_DAY), calendar2.get(Calendar.MINUTE), true);
                        dialog.show();
                    }


                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                datePickerDialog.show();
                break;
            case R.id.cb_1:
                ll_1.setVisibility(cb_1.isChecked() ? View.VISIBLE : View.GONE);
                if (cb_1.isChecked()) {
                    if (!selectText.contains(cb_1.getText())) {
                        selectText += cb_1.getText() + ",";
                    }
                } else {
                    if (selectText.contains(cb_1.getText())) {
                        selectText = selectText.replace(cb_1.getText() + ",", "");
                    }
                }
                htmlText = "您已选择了:<font color=\"red\">" + selectText + "</font>请在下方输入相应的数值";
                tv_dis.setText(Html.fromHtml(htmlText));

                break;
            case R.id.cb_2:
                ll_2.setVisibility(cb_2.isChecked() ? View.VISIBLE : View.GONE);
                if (cb_2.isChecked()) {
                    if (!selectText.contains(cb_2.getText())) {
                        selectText += cb_2.getText() + ",";
                    }
                } else {
                    if (selectText.contains(cb_2.getText())) {
                        selectText = selectText.replace(cb_2.getText() + ",", "");
                    }
                }
                htmlText = "您已选择了:<font color=\"red\">" + selectText + "</font>请在下方输入相应的数值";
                tv_dis.setText(Html.fromHtml(htmlText));
                break;
            case R.id.cb_3:
                ll_3.setVisibility(cb_3.isChecked() ? View.VISIBLE : View.GONE);
                if (cb_3.isChecked()) {
                    if (!selectText.contains(cb_3.getText())) {
                        selectText += cb_3.getText() + ",";
                    }
                } else {
                    if (selectText.contains(cb_3.getText())) {
                        selectText = selectText.replace(cb_3.getText() + ",", "");
                    }
                }
                htmlText = "您已选择了:<font color=\"red\">" + selectText + "</font>请在下方输入相应的数值";
                tv_dis.setText(Html.fromHtml(htmlText));
                break;
            case R.id.cb_4:
                ll_4.setVisibility(cb_4.isChecked() ? View.VISIBLE : View.GONE);
                if (cb_4.isChecked()) {
                    if (!selectText.contains(cb_4.getText())) {
                        selectText += cb_4.getText() + ",";
                    }
                } else {
                    if (selectText.contains(cb_4.getText())) {
                        selectText = selectText.replace(cb_4.getText() + ",", "");
                    }
                }
                htmlText = "您已选择了:<font color=\"red\">" + selectText + "</font>请在下方输入相应的数值";
                tv_dis.setText(Html.fromHtml(htmlText));
                break;
            case R.id.cb_5:
                ll_5.setVisibility(cb_5.isChecked() ? View.VISIBLE : View.GONE);
                if (cb_5.isChecked()) {
                    if (!selectText.contains(cb_5.getText())) {
                        selectText += cb_5.getText() + ",";
                    }
                } else {
                    if (selectText.contains(cb_5.getText())) {
                        selectText = selectText.replace(cb_5.getText() + ",", "");
                    }
                }
                htmlText = "您已选择了:<font color=\"red\">" + selectText + "</font>请在下方输入相应的数值";
                tv_dis.setText(Html.fromHtml(htmlText));
                break;
            case R.id.cb_6:
                ll_6.setVisibility(cb_6.isChecked() ? View.VISIBLE : View.GONE);
                if (cb_6.isChecked()) {
                    if (!selectText.contains(cb_6.getText())) {
                        selectText += cb_6.getText() + ",";
                    }
                } else {
                    if (selectText.contains(cb_6.getText())) {
                        selectText = selectText.replace(cb_6.getText() + ",", "");
                    }
                }
                htmlText = "您已选择了:<font color=\"red\">" + selectText + "</font>请在下方输入相应的数值";
                tv_dis.setText(Html.fromHtml(htmlText));
                break;
            case R.id.cb_7:
                ll_7.setVisibility(cb_7.isChecked() ? View.VISIBLE : View.GONE);
                if (cb_7.isChecked()) {
                    if (!selectText.contains(cb_7.getText())) {
                        selectText += cb_7.getText() + ",";
                    }
                } else {
                    if (selectText.contains(cb_7.getText())) {
                        selectText = selectText.replace(cb_7.getText() + ",", "");
                    }
                }
                htmlText = "您已选择了:<font color=\"red\">" + selectText + "</font>请在下方输入相应的数值";
                tv_dis.setText(Html.fromHtml(htmlText));
                break;
            case R.id.cb_8:
                ll_8.setVisibility(cb_8.isChecked() ? View.VISIBLE : View.GONE);
                if (cb_8.isChecked()) {
                    if (!selectText.contains(cb_8.getText())) {
                        selectText += cb_8.getText() + ",";
                    }
                } else {
                    if (selectText.contains(cb_8.getText())) {
                        selectText = selectText.replace(cb_8.getText() + ",", "");
                    }
                }
                htmlText = "您已选择了:<font color=\"red\">" + selectText + "</font>请在下方输入相应的数值";
                tv_dis.setText(Html.fromHtml(htmlText));
                break;
            case R.id.cb_9:
                ll_9.setVisibility(cb_9.isChecked() ? View.VISIBLE : View.GONE);
                if (cb_9.isChecked()) {
                    if (!selectText.contains(cb_9.getText())) {
                        selectText += cb_9.getText() + ",";
                    }
                } else {
                    if (selectText.contains(cb_9.getText())) {
                        selectText = selectText.replace(cb_9.getText() + ",", "");
                    }
                }
                htmlText = "您已选择了:<font color=\"red\">" + selectText + "</font>请在下方输入相应的数值";
                tv_dis.setText(Html.fromHtml(htmlText));
                break;
            case R.id.cb_10:
                ll_10.setVisibility(cb_10.isChecked() ? View.VISIBLE : View.GONE);
                if (cb_10.isChecked()) {
                    if (!selectText.contains(cb_10.getText())) {
                        selectText += cb_10.getText() + ",";
                    }
                } else {
                    if (selectText.contains(cb_10.getText())) {
                        selectText = selectText.replace(cb_10.getText() + ",", "");
                    }
                }
                htmlText = "您已选择了:<font color=\"red\">" + selectText + "</font>请在下方输入相应的数值";
                tv_dis.setText(Html.fromHtml(htmlText));
                break;
            case R.id.cb_11:
                ll_11.setVisibility(cb_11.isChecked() ? View.VISIBLE : View.GONE);
                if (cb_11.isChecked()) {
                    if (!selectText.contains(cb_11.getText())) {
                        selectText += cb_11.getText() + ",";
                    }
                } else {
                    if (selectText.contains(cb_11.getText())) {
                        selectText = selectText.replace(cb_11.getText() + ",", "");
                    }
                }
                htmlText = "您已选择了:<font color=\"red\">" + selectText + "</font>请在下方输入相应的数值";
                tv_dis.setText(Html.fromHtml(htmlText));
                break;
            case R.id.cb_12:
                ll_12.setVisibility(cb_12.isChecked() ? View.VISIBLE : View.GONE);
                if (cb_12.isChecked()) {
                    if (!selectText.contains(cb_12.getText())) {
                        selectText += cb_12.getText() + ",";
                    }
                } else {
                    if (selectText.contains(cb_12.getText())) {
                        selectText = selectText.replace(cb_12.getText() + ",", "");
                    }
                }
                htmlText = "您已选择了:<font color=\"red\">" + selectText + "</font>请在下方输入相应的数值";
                tv_dis.setText(Html.fromHtml(htmlText));
                break;
            case R.id.cb_13:
                ll_13.setVisibility(cb_13.isChecked() ? View.VISIBLE : View.GONE);
                if (cb_13.isChecked()) {
                    if (!selectText.contains(cb_13.getText())) {
                        selectText += cb_13.getText() + ",";
                    }
                } else {
                    if (selectText.contains(cb_13.getText())) {
                        selectText = selectText.replace(cb_13.getText() + ",", "");
                    }
                }
                htmlText = "您已选择了:<font color=\"red\">" + selectText + "</font>请在下方输入相应的数值";
                tv_dis.setText(Html.fromHtml(htmlText));
                break;
            case R.id.but_over:


                HttpParams httpParams = new HttpParams();


                if (cb_1.isChecked()) {
                    try {
                        String a = ed_1.getText().toString();
                        if (a.length() == 0) {
                            Mytoast.show(mContext, "请输入心率");
                            return;
                        }
                        PR = Integer.parseInt(a);
                        httpParams.put("healthRate", PR);
                    } catch (NumberFormatException e) {

                        e.printStackTrace();
                    }
                }
                if (cb_2.isChecked()) {
                    try {
                        String a = ed_21.getText().toString();
                        if (a.length() == 0) {
                            Mytoast.show(mContext, "请输入收缩压");
                            return;
                        }
                        String b = ed_22.getText().toString();
                        if (b.length() == 0) {
                            Mytoast.show(mContext, "请输入舒张压");
                            return;
                        }


                        PS = Integer.parseInt(a);
                        PD = Integer.parseInt(b);
                        httpParams.put("systolic", PS);
                        httpParams.put("diastolic", PD);
                    } catch (NumberFormatException e) {

                        e.printStackTrace();
                    }
                }
                if (cb_3.isChecked()) {
                    try {
                        String a = ed_3.getText().toString();
                        if (a.length() == 0) {
                            Mytoast.show(mContext, "请输入血氧量");
                            return;
                        }
//                        BloodOxygen = Double.parseDouble(a);
//                        BloodOxygen = Math.round(BloodOxygen * 100) / 100f;
                        BloodOxygen = Integer.valueOf(a);
                        httpParams.put("oxygen", BloodOxygen);
                    } catch (NumberFormatException e) {

                        e.printStackTrace();
                    }
                }
                if (cb_4.isChecked()) {
                    try {
                        String a = ed_4.getText().toString();
                        if (a.length() == 0) {
                            Mytoast.show(mContext, "请输入步数");
                            return;
                        }
                        Step = Integer.parseInt(a);
                        httpParams.put("foot", Step);
                    } catch (NumberFormatException e) {

                        e.printStackTrace();
                    }
                }
                if (cb_5.isChecked()) {
                    try {
                        String a = ed_5.getText().toString();
                        if (a.length() == 0) {
                            Mytoast.show(mContext, "请输入卡路里");
                            return;
                        }
//                        Calorie = Double.parseDouble(a);
//                        Calorie = Math.round(Calorie * 100) / 100f;
                        Calorie = Integer.valueOf(a);
                        httpParams.put("calorie", Calorie);
                    } catch (NumberFormatException e) {

                        e.printStackTrace();
                    }
                }
                if (cb_6.isChecked()) {
                    try {
                        String a = ed_6.getText().toString();
                        if (a.length() == 0) {
                            Mytoast.show(mContext, "请输入血糖");
                            return;
                        }
//                        BloodGlucose = Double.parseDouble(a);
//                        BloodGlucose = Math.round(BloodGlucose * 100) / 100f;
                        BloodGlucose = Integer.valueOf(a);
                        httpParams.put("sugar", BloodGlucose);
                    } catch (NumberFormatException e) {

                        e.printStackTrace();
                    }
                }
                if (cb_7.isChecked()) {
                    try {
                        String a = ed_7.getText().toString();
                        if (a.length() == 0) {
                            Mytoast.show(mContext, "请输入体温");
                            return;
                        }
//                        Temperature = Double.parseDouble(a);
//                        Temperature = Math.round(Temperature * 100) / 100f;
                        Temperature = Integer.valueOf(a);
                        httpParams.put("temperature", Temperature);
                    } catch (NumberFormatException e) {

                        e.printStackTrace();
                    }
                }
                if (cb_8.isChecked()) {
                    try {
                        String a = ed_8.getText().toString();
                        if (a.length() == 0) {
                            Mytoast.show(mContext, "请输体重");
                            return;
                        }
//                        Weight = Double.parseDouble(a);
//                        Weight = Math.round(Weight * 100) / 100f;
                        Weight = Integer.valueOf(a);
                        httpParams.put("weight", Weight);
                    } catch (NumberFormatException e) {

                        e.printStackTrace();
                    }
                }
                if (cb_9.isChecked()) {
                    try {
                        String a = ed_9.getText().toString();
                        if (a.length() == 0) {
                            Mytoast.show(mContext, "请输入脂肪率");
                            return;
                        }
//                        FatRate = Double.parseDouble(a);
//                        FatRate = Math.round(FatRate * 100) / 100f;
                        FatRate = Integer.valueOf(a);
                        httpParams.put("fat", FatRate);
                    } catch (NumberFormatException e) {

                        e.printStackTrace();
                    }
                }
                if (cb_10.isChecked()) {
                    try {
                        String a = ed_10.getText().toString();
                        if (a.length() == 0) {
                            Mytoast.show(mContext, "请输入水分率");
                            return;
                        }
//                        Moisture = Double.parseDouble(a);
//                        Moisture = Math.round(Moisture * 100) / 100f;
                        Moisture = Integer.valueOf(a);
                        httpParams.put("water", Moisture);
                    } catch (NumberFormatException e) {

                        e.printStackTrace();
                    }
                }
                if (cb_11.isChecked()) {
                    try {
                        String a = ed_11.getText().toString();
                        if (a.length() == 0) {
                            Mytoast.show(mContext, "请输入肌肉率");
                            return;
                        }
//                        Muscle = Double.parseDouble(a);
//                        Muscle = Math.round(Muscle * 100) / 100f;
                        Muscle = Integer.valueOf(a);
                        httpParams.put("rate", Muscle);
                    } catch (NumberFormatException e) {

                        e.printStackTrace();
                    }
                }
                if (cb_12.isChecked()) {
                    try {
                        String a = ed_12.getText().toString();
                        if (a.length() == 0) {
                            Mytoast.show(mContext, "请输入骨骼重量");
                            return;
                        }
//                        Bone = Double.parseDouble(a);
//                        Bone = Math.round(Bone * 100) / 100f;
                        Bone = Integer.valueOf(a);
                        httpParams.put("bone", Bone);
                    } catch (NumberFormatException e) {

                        e.printStackTrace();
                    }
                }
                if (cb_13.isChecked()) {
                    try {
                        String a = ed_13.getText().toString();
                        if (a.length() == 0) {
                            Mytoast.show(mContext, "请输入基础代谢");
                            return;
                        }
//                        BM = Double.parseDouble(a);
//                        BM = Math.round(BM * 100) / 100f;
                        BM = Integer.valueOf(a);
                        httpParams.put("metabolism", BM);
                    } catch (NumberFormatException e) {

                        e.printStackTrace();
                    }
                }

                if (TestDate == null || TestDate.length() == 0) {
                    Mytoast.show(mContext, "请选择时间");
                    return;
                } else {
                    httpParams.put("measureTime", TestDate);
                }
                submit(httpParams);

        }
    }

    public void submit(HttpParams httpParams){
//        HttpParams httpParams = new HttpParams();
//        httpParams.put("bone",Bone);
//        httpParams.put("calorie",Calorie);
//        httpParams.put("diastolic",PD);
//        httpParams.put("elderlyId",MyApplication.userInfo.getOlderlyId());
//        httpParams.put("fat",FatRate);
//        httpParams.put("foot",Step);
//        httpParams.put("healthRate",PR);
//        httpParams.put("measureTime",tv_time.getText().toString());
//        httpParams.put("metabolism",BM);
//        httpParams.put("oxygen",BloodOxygen);
//        httpParams.put("rate",Muscle);
//        httpParams.put("sugar",BloodGlucose);
//        httpParams.put("systolic",PS);
//        httpParams.put("temperature",Temperature);
//        httpParams.put("water",Moisture);
//        httpParams.put("weight",Weight);
        if(images.size()>0) {
            httpParams.put("fileOne",new File(images.get(0).path));
        }
        if(images.size() >1) {
            httpParams.put("fileTwo",new File(images.get(1).path));
        }
        if(images.size() >2) {
            httpParams.put("fileThree",new File(images.get(2).path));
        }
        Log.e( "bone: ",Bone+"" );
        Log.e( "calorie: ",Calorie+"" );
        Log.e( "diastolic: ",PD+"" );
        Log.e( "elderlyId: ",MyApplication.userInfo.getOlderlyId()+"" );
        Log.e( "fat: ",FatRate+"" );
        Log.e( "foot: ",Step+"" );
        Log.e( "healthRate: ",PR+"" );
        Log.e( "measureTime: ",tv_time.getText().toString()+"" );
        Log.e( "metabolism: ",BM+"" );
        Log.e( "oxygen: ",BloodOxygen+"" );
        Log.e( "rate: ",Muscle+"" );
        Log.e( "sugar: ",BloodGlucose+"" );
        Log.e( "systolic: ",PS+"" );
        Log.e( "temperature: ",Temperature+"" );
        Log.e( "weight: ",Weight+"" );
        Log.e( "water: ",httpParams.toString()+"" );
        showProgressBar();
        OkGo.<String>post(Urls.INPUT)
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
                            Log.d("数据录入", json.toString());
                            if (json.getInt("status") == 200) {

                                Toast.makeText(mContext, json.getString("message"), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(mContext, json.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    public static final int maxImgCount = 3;
    private List<ImageItem> images = new ArrayList<>();


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == 100) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null) {
                    list.addAll(images);
                    joinAdapter.setImages(list);
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == 101) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (images != null) {
                    list.clear();
                    list.addAll(images);
                    joinAdapter.setImages(list);
                }
            }
        }
    }

    @Override
    public void onItemAdd(View view, int position) {
        switch (position) {
            case IMAGE_ITEM_ADD:
                if (ContextCompat.checkSelfPermission(WriteDataActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(WriteDataActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1);

                } else {
//                    Intent intent = new Intent(this, ImageGridActivity.class);
//                    intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS,true); // 是否是直接打开相机
//                    startActivityForResult(intent, 1);
                    //打开选择,本次允许选择的数量
                    ImagePicker.getInstance().setSelectLimit(maxImgCount - list.size());
                    Intent intent = new Intent(this, ImageGridActivity.class);
                    startActivityForResult(intent, 100);
                }
                break;
            default:
                //打开预览
                Intent intentPreview = new Intent(this, ImagePreviewDelActivity.class);
                intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) joinAdapter.getImages());
                intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
                startActivityForResult(intentPreview, 101);
                break;
        }
    }
}
