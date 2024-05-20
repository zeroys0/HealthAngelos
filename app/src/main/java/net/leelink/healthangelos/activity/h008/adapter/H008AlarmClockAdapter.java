package net.leelink.healthangelos.activity.h008.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.home.RemindTimesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

public class H008AlarmClockAdapter extends RecyclerView.Adapter<H008AlarmClockAdapter.ViewHolder> {
    JSONArray jsonArray;
    Context context;
    OnAlarmClockListener onAlarmClockListener;

    public H008AlarmClockAdapter(JSONArray jsonArray, Context context, OnAlarmClockListener onAlarmClockListener) {
        this.jsonArray = jsonArray;
        this.context = context;
        this.onAlarmClockListener = onAlarmClockListener;
    }

    @NonNull
    @Override
    public H008AlarmClockAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_h008_alarm,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull H008AlarmClockAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(position);
            String text = "";
            int Type = jsonObject.getInt("repeat");
            if (Type == 0) {
                text ="重复一次";
            } else if (Type == 127) {
                text ="每天重复";
            } else {
                text = "(每周 ";
                if (RemindTimesManager.ifSundayHave(Type)) text += "日 ";
                if (RemindTimesManager.ifMondayHave(Type)) text += "一 ";
                if (RemindTimesManager.ifTuesdayHave(Type)) text += "二 ";
                if (RemindTimesManager.ifWednesdayHave(Type)) text += "三 ";
                if (RemindTimesManager.ifThursdayHave(Type)) text += "四 ";
                if (RemindTimesManager.ifFridayHave(Type)) text += "五 ";
                if (RemindTimesManager.ifSaturdayHave(Type)) text += "六 ";
                text += ")重复";
            }
            holder.cb_alarm.setChecked(jsonObject.getInt("state") != 0);
            holder.cb_alarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    onAlarmClockListener.onCheckChange(buttonView,position,isChecked);
                }
            });
            String time = jsonObject.getString("time");
            StringBuilder builder =new StringBuilder();
            builder.append(time);
            builder.insert(2,":");
            holder.tv_time.setText(builder.toString());
            String content = "";
            switch (jsonObject.getInt("type")){
                case 0:
                    content = "吃药提醒";
                    break;
                case 1:
                    content = "喝水提醒";
                    break;
                case 2:
                    content = "运动提醒";
                    break;
                case 3:
                    content = "闹铃提醒";
                    break;
            }
            holder.tv_content.setText(content+","+text);
            holder.tv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAlarmClockListener.onDelete(v,position);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return jsonArray==null?0:jsonArray.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_time,tv_content,tv_delete;
        SwitchCompat cb_alarm;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_delete = itemView.findViewById(R.id.tv_delete);
            cb_alarm = itemView.findViewById(R.id.cb_alarm);
        }
    }
}
