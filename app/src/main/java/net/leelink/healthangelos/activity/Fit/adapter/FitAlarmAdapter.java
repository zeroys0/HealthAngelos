package net.leelink.healthangelos.activity.Fit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.htsmart.wristband2.bean.WristbandAlarm;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.OnOrderListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

public class FitAlarmAdapter extends RecyclerView.Adapter<FitAlarmAdapter.ViewHolder> {
    ArrayList<WristbandAlarm> mAlarmList;
    Context context;
    OnOrderListener onOrderListener;
    private CharSequence[] mDayValuesSimple;

    public FitAlarmAdapter(ArrayList<WristbandAlarm> mAlarmList, Context context, OnOrderListener onOrderListener) {
        this.mAlarmList = mAlarmList;
        this.context = context;
        this.onOrderListener = onOrderListener;
        mDayValuesSimple = new CharSequence[]{
                context.getString(R.string.ds_alarm_repeat_00_simple),
                context.getString(R.string.ds_alarm_repeat_01_simple),
                context.getString(R.string.ds_alarm_repeat_02_simple),
                context.getString(R.string.ds_alarm_repeat_03_simple),
                context.getString(R.string.ds_alarm_repeat_04_simple),
                context.getString(R.string.ds_alarm_repeat_05_simple),
                context.getString(R.string.ds_alarm_repeat_06_simple),
        };
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fit_alarm,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final WristbandAlarm alarm = mAlarmList.get(position);
        holder.tv_repeat.setText(repeatToSimpleStr(alarm.getRepeat()));
      //  holder.tv_time.setText(mAlarmList.get(position).get);
        holder.cb_open.setChecked(alarm.isEnable());
        holder.cb_open.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mAlarmList==null?0:mAlarmList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_time,tv_repeat;
        SwitchCompat cb_open;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_repeat = itemView.findViewById(R.id.tv_repeat);
            cb_open = itemView.findViewById(R.id.cb_open);
        }
    }

    private String repeatToSimpleStr(int repeat) {
        String text = null;
        int sumDays = 0;
        String resultString = "";
        for (int i = 0; i < 7; i++) {
            if (WristbandAlarm.isRepeatEnableIndex(repeat, i)) {
                sumDays++;
                resultString += (mDayValuesSimple[i] + " ");
            }
        }
        if (sumDays == 7) {
            text = context.getString(R.string.ds_alarm_repeat_every_day);
        } else if (sumDays == 0) {
            text = context.getString(R.string.ds_alarm_repeat_never);
        } else if (sumDays == 5) {
            boolean sat = !WristbandAlarm.isRepeatEnableIndex(repeat, 5);
            boolean sun = !WristbandAlarm.isRepeatEnableIndex(repeat, 6);
            if (sat && sun) {
                text = context.getString(R.string.ds_alarm_repeat_workdays);
            }
        } else if (sumDays == 2) {
            boolean sat = WristbandAlarm.isRepeatEnableIndex(repeat, 5);
            boolean sun = WristbandAlarm.isRepeatEnableIndex(repeat, 6);
            if (sat && sun) {
                text = context.getString(R.string.ds_alarm_repeat_weekends);
            }
        }
        if (text == null) {
            text = resultString;
        }
        return text;
    }
}
