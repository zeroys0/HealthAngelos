package com.pattonsoft.pattonutil1_0.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.pattonsoft.pattonutil1_0.R;
import com.pattonsoft.pattonutil1_0.util.DensityUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by loading182 on 2017/11/18.
 * 时间支持最大时间 999:59:59
 */
@SuppressLint("HandlerLeak")
public class DownTimerView extends LinearLayout {

    TextView tv_hour_hundred;
    TextView tv_hour_decade;
    TextView tv_hour_unit;
    LinearLayout ll_hour;
    TextView colon_hour;
    TextView tv_min_decade;
    TextView tv_min_unit;
    LinearLayout ll_min;
    TextView colon_minute;
    TextView tv_sec_decade;
    TextView tv_sec_unit;
    TextView colon_sec;
    LinearLayout ll_sec;

    private Context context;

    private int hour_hundred;
    private int hour_decade;
    private int hour_unit;
    private int min_decade;
    private int min_unit;
    private int sec_decade;
    private int sec_unit;
    CallBack callBack;

    private Timer timer;

    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            countDown();
        }
    };

    public DownTimerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_time_dwon, this);

        tv_hour_hundred = (TextView) view.findViewById(R.id.tv_hour_hundred);
        tv_hour_decade = (TextView) view.findViewById(R.id.tv_hour_decade);
        tv_hour_unit = (TextView) view.findViewById(R.id.tv_hour_unit);
        ll_hour = (LinearLayout) view.findViewById(R.id.ll_hour);
        colon_hour = (TextView) view.findViewById(R.id.colon_hour);
        tv_min_decade = (TextView) view.findViewById(R.id.tv_min_decade);
        tv_min_unit = (TextView) view.findViewById(R.id.tv_min_unit);
        ll_min = (LinearLayout) view.findViewById(R.id.ll_min);
        colon_minute = (TextView) view.findViewById(R.id.colon_minute);
        tv_sec_decade = (TextView) view.findViewById(R.id.tv_sec_decade);
        tv_sec_unit = (TextView) view.findViewById(R.id.tv_sec_unit);
        colon_sec = (TextView) view.findViewById(R.id.colon_sec);
        ll_sec = (LinearLayout) view.findViewById(R.id.ll_sec);
        @SuppressLint("Recycle") TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DownTimerView);

        //字体大小
        //   int size = array.getInteger(R.styleable.DownTimerView_viewSize, 12);
        int size = array.getDimensionPixelOffset(R.styleable.DownTimerView_timeTextSize, DensityUtils.sp2px(context, 14));
        int hourColor = array.getColor(R.styleable.DownTimerView_hourColor, 0);
        int minColor = array.getColor(R.styleable.DownTimerView_minColor, 0);
        int secColor = array.getColor(R.styleable.DownTimerView_secColor, 0);
        int colon_color = array.getColor(R.styleable.DownTimerView_colon_color, 0);
        int hourBackground = array.getResourceId(R.styleable.DownTimerView_hourBackground, -1);
        int minBackground = array.getResourceId(R.styleable.DownTimerView_minBackground, -1);
        int secBackground = array.getResourceId(R.styleable.DownTimerView_secBackground, -1);
        int type = array.getInt(R.styleable.DownTimerView_style, 1);

        if (type == 1) {
            colon_hour.setText(":");
            colon_minute.setText(":");
            colon_sec.setVisibility(GONE);
        } else if (type == 2) {
            colon_hour.setText("时");
            colon_minute.setText("分");
            colon_sec.setText("秒");
            colon_sec.setVisibility(VISIBLE);
        }


        if (hourColor != 0) {
            tv_hour_hundred.setTextColor(hourColor);
            tv_hour_decade.setTextColor(hourColor);
            tv_hour_unit.setTextColor(hourColor);

        }
        if (minColor != 0) {
            tv_min_decade.setTextColor(minColor);
            tv_min_unit.setTextColor(minColor);

        }
        if (secColor != 0) {
            tv_sec_decade.setTextColor(secColor);
            tv_sec_unit.setTextColor(secColor);

        }
        if (colon_color != 0) {
            colon_minute.setTextColor(colon_color);
            colon_hour.setTextColor(colon_color);
            colon_sec.setTextColor(colon_color);
        }

        size = (int) DensityUtils.px2sp(context, size);
        tv_hour_hundred.setTextSize(size);
        tv_hour_decade.setTextSize(size);
        tv_hour_unit.setTextSize(size);
        tv_min_decade.setTextSize(size);
        tv_min_unit.setTextSize(size);
        tv_sec_decade.setTextSize(size);
        tv_sec_unit.setTextSize(size);
        colon_minute.setTextSize(size);
        colon_hour.setTextSize(size);
        colon_sec.setTextSize(size);

        if (hourBackground != -1) {
            ll_hour.setBackgroundResource(hourBackground);
        }
        if (minBackground != -1) {
            ll_min.setBackgroundResource(minBackground);
        }
        if (secBackground != -1) {
            ll_sec.setBackgroundResource(secBackground);
        }


    }

    /**
     * 开始倒计时
     *
     * @param callBack 回调
     */
    public void start(CallBack callBack) {
        this.callBack = callBack;
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(0);
                }
            }, 0, 1000);
        }
    }

    /**
     * 停止倒计时
     */
    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * 初始化时间
     *
     * @param hour 小时 支持到 999
     * @param min  分钟 支持到 59
     * @param sec  秒  支持到 59
     * @throws RuntimeException
     */
    public void setTime(int hour, int min, int sec) throws RuntimeException {

        if (hour > 999 || min >= 60 || sec >= 60 || hour < 0 || min < 0
                || sec < 0) {
            throw new RuntimeException("时间格式错误,请检查你的代码");
        }

        hour_hundred = hour / 100;
        hour_decade = hour / 10 - hour_hundred * 10;
        hour_unit = hour - hour_decade * 10 - hour_hundred * 100;
        min_decade = min / 10;
        min_unit = min - min_decade * 10;
        sec_decade = sec / 10;
        sec_unit = sec - sec_decade * 10;
        if (hour_hundred == 0) {
            tv_hour_hundred.setVisibility(GONE);
        }
        tv_hour_hundred.setText(hour_hundred + "");
        tv_hour_decade.setText(hour_decade + "");
        tv_hour_unit.setText(hour_unit + "");
        tv_min_decade.setText(min_decade + "");
        tv_min_unit.setText(min_unit + "");
        tv_sec_decade.setText(sec_decade + "");
        tv_sec_unit.setText(sec_unit + "");
    }

    /**
     * 倒计时操作与回调
     */
    private void countDown() {
        if (isCarry4Unit(tv_sec_unit)) {
            if (isCarry4Decade(tv_sec_decade)) {
                if (isCarry4Unit(tv_min_unit)) {
                    if (isCarry4Decade(tv_min_decade)) {
                        if (isCarry4Unit(tv_hour_unit)) {
                            if (isCarry4Decade2(tv_hour_decade)) {
                                if (isCarry4Hundred(tv_hour_hundred)) {
                                    Toast.makeText(context, "计数完成",
                                            Toast.LENGTH_SHORT).show();
                                    stop();
                                    callBack.finish();
                                    setTime(0, 0, 0);//重置为0
                                } else {
                                    callBack.countDowning(getHour(), getMin(), getSec());
                                }
                            } else {
                                callBack.countDowning(getHour(), getMin(), getSec());
                            }
                        } else {
                            callBack.countDowning(getHour(), getMin(), getSec());
                        }
                    } else {
                        callBack.countDowning(getHour(), getMin(), getSec());
                    }
                } else {
                    callBack.countDowning(getHour(), getMin(), getSec());
                }
            } else {
                callBack.countDowning(getHour(), getMin(), getSec());
            }
        } else {
            callBack.countDowning(getHour(), getMin(), getSec());
        }

    }


    private boolean isCarry4Decade2(TextView tv) {

        int time = Integer.valueOf(tv.getText().toString());
        time = time - 1;
        if (time < 0) {
            time = 9;
            tv.setText(time + "");
            return true;
        } else {
            tv.setText(time + "");
            return false;
        }
    }

    private boolean isCarry4Hundred(TextView tv) {

        int time = Integer.valueOf(tv.getText().toString());
        time = time - 1;
        if (time < 0) {
            time = 9;
            tv.setText(time + "");
            return true;
        } else {
            tv.setText(time + "");
            return false;
        }
    }

    private boolean isCarry4Decade(TextView tv) {

        int time = Integer.valueOf(tv.getText().toString());
        time = time - 1;
        if (time < 0) {
            time = 5;
            tv.setText(time + "");
            return true;
        } else {
            tv.setText(time + "");
            return false;
        }
    }

    private boolean isCarry4Unit(TextView tv) {

        int time = Integer.valueOf(tv.getText().toString());
        time = time - 1;
        if (time < 0) {
            time = 9;
            tv.setText(time + "");
            return true;
        } else {
            tv.setText(time + "");
            return false;
        }
    }

    public interface CallBack {
        void countDowning(int hour, int min, int sec);

        void finish();
    }

    /**
     * 获取当前倒计时:时
     *
     * @return 时
     */
    int getHour() {
        hour_hundred = Integer.valueOf(tv_hour_hundred.getText().toString());
        hour_decade = Integer.valueOf(tv_hour_decade.getText().toString());
        hour_unit = Integer.valueOf(tv_hour_unit.getText().toString());
        return hour_hundred * 100 + hour_decade * 10 * +hour_unit;
    }

    /**
     * 获取当前倒计时:分
     *
     * @return 分
     */
    int getMin() {
        min_decade = Integer.valueOf(tv_min_decade.getText().toString());
        min_unit = Integer.valueOf(tv_min_unit.getText().toString());
        return min_decade * 10 * +min_unit;

    }

    /**
     * 获取当前倒计时:分
     *
     * @return 秒
     */
    int getSec() {
        sec_decade = Integer.valueOf(tv_min_decade.getText().toString());
        sec_unit = Integer.valueOf(tv_min_unit.getText().toString());
        return sec_decade * 10 * +sec_unit;
    }
}