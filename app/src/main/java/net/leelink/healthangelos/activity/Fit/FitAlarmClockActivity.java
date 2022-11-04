package net.leelink.healthangelos.activity.Fit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.htsmart.wristband2.WristbandApplication;
import com.htsmart.wristband2.WristbandManager;
import com.htsmart.wristband2.bean.WristbandAlarm;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.Fit.adapter.FitAlarmAdapter;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.BaseActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class FitAlarmClockActivity extends BaseActivity implements View.OnClickListener , OnOrderListener {
    private Context context;
    private RelativeLayout rl_back,rl_default;
    private RecyclerView alarm_clock_list;
    private FitAlarmAdapter alarmAdapter;
    private ImageView img_add;
    private WristbandManager mWristManager = WristbandApplication.getWristbandManager();
    private Disposable mRequestAlarmListDisposable;
    private ArrayList<WristbandAlarm> mAlarmList = new ArrayList<>(8);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fit_alarm_clock);
        context = this;
        init();
        createProgressBar(context);
        initAlarm();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        alarm_clock_list = findViewById(R.id.alarm_clock_list);
        img_add = findViewById(R.id.img_add);
        img_add.setOnClickListener(this);
        rl_default = findViewById(R.id.rl_default);
        alarmAdapter = new FitAlarmAdapter(mAlarmList,context,this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
        alarm_clock_list.setAdapter(alarmAdapter);
        alarm_clock_list.setLayoutManager(layoutManager);
    }

    public void initAlarm(){
        refreshAlarmList();

    }

    private void refreshAlarmList() {
        if (mRequestAlarmListDisposable != null && !mRequestAlarmListDisposable.isDisposed()) {
            return;
        }
        mRequestAlarmListDisposable = mWristManager
                .requestAlarmList()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
//                        mAlarmUpdated = false;
                        invalidateOptionsMenu();
//                        mDataLceView.lceShowLoading();
                        showProgressBar();
                    }
                })
                .subscribe(new Consumer<List<WristbandAlarm>>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void accept(List<WristbandAlarm> alarms) throws Exception {
//                        mAlarmUpdated = true;
                        stopProgressBar();
                        if (alarms.size() <= 0) {
                            rl_default.setVisibility(View.VISIBLE);
                        } else {
                            rl_default.setVisibility(View.GONE);
                        }
                        mAlarmList.clear();
                        mAlarmList.addAll(alarms);
                        Collections.sort(mAlarmList, mAlarmComparator);
                        invalidateOptionsMenu();
                        Log.d( "accept: ",mAlarmList.size()+"");
                        alarmAdapter.notifyDataSetChanged();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
//                        toast(throwable.getMessage());
//                        mDataLceView.lceShowError(R.string.tip_load_error);
                        Toast.makeText(context, "读取失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.img_add:  //添加闹钟
                Intent intent = new Intent(context,AlarmDetailActivity.class);
                startActivity(intent);
                break;
        }
    }

    private Comparator<WristbandAlarm> mAlarmComparator = new Comparator<WristbandAlarm>() {
        @Override
        public int compare(WristbandAlarm o1, WristbandAlarm o2) {
            int v1 = o1.getHour() * 60 + o1.getMinute();
            int v2 = o2.getHour() * 60 + o2.getMinute();
            if (v1 > v2) {
                return 1;
            } else if (v1 < v2) {
                return -1;
            } else {
                return o1.getAlarmId() - o2.getAlarmId();
            }
        }
    };

    @Override
    public void onItemClick(View view) {

    }

    @Override
    public void onButtonClick(View view, int position) {

    }
}