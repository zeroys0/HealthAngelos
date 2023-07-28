package net.leelink.healthangelos.activity.sleepace;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SleepaceAlarmAdapter extends RecyclerView.Adapter<SleepaceAlarmAdapter.ViewHolder> {
    private Context context;
    private List<AlarmBean> list;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public SleepaceAlarmAdapter(List<AlarmBean> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public SleepaceAlarmAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slaap_alarm, parent, false);
        SleepaceAlarmAdapter.ViewHolder viewHolder = new SleepaceAlarmAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SleepaceAlarmAdapter.ViewHolder holder, int position) {
        String s = list.get(position).getTriggerTime();
        long time = Long.parseLong(s+"000");
        Date date = new Date(time);
        holder.tv_time.setText(sdf.format(date));
        holder.tv_state.setText(getState(list.get(position).getType()));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_time, tv_state;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_state = itemView.findViewById(R.id.tv_state);
        }
    }

    public String getState(String type) {
        if (type.equals("alarmSensorFall")) {
            return "传感器脱落报警";
        } else if (type.equals("alarmLeftBed")) {
            return "离床报警";
        } else if (type.equals("alarmHeartRateFast")) {
            return "心率过速报警";
        } else if (type.equals("alarmHeartRateSlow")) {
            return "心率过缓报警";
        } else if (type.equals("alarmBreathRateFast")) {
            return "呼吸过速报警";
        } else if (type.equals("alarmBreathRateSlow")) {
            return "呼吸过缓报警";
        } else if (type.equals("alarmBreathRatePause")) {
            return "呼吸暂停报警";
        } else if (type.equals("alarmBodymove")) {
            return "频繁体动报警";
        } else if (type.equals("alarmNoBodymove")) {
            return "无体动报警";
        } else if (type.equals("alarmSitup")) {
            return "坐起报警";
        } else if (type.equals("alarmOnBed")) {
            return "在床报警";
        } else {
            return "";
        }
    }
}
