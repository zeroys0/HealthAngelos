package net.leelink.healthangelos.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DataHistoryAdapter extends RecyclerView.Adapter<DataHistoryAdapter.ViewHolder> {

    JSONArray jsonArray;
    Context context;
    OnOrderListener onOrderListener;

    public DataHistoryAdapter(JSONArray jsonArray, Context context, OnOrderListener onOrderListener) {
        this.jsonArray = jsonArray;
        this.context = context;
        this.onOrderListener = onOrderListener;
    }

    @NonNull
    @Override
    public DataHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_data_item, parent, false); // 实例化viewholder
        DataHistoryAdapter.ViewHolder viewHolder = new DataHistoryAdapter.ViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderListener.onItemClick(v);
            }
        });
        return viewHolder;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull DataHistoryAdapter.ViewHolder holder, final int position) {

        try {
            JSONObject json = jsonArray.getJSONObject(position);
            List<String[]> list = new ArrayList<>();
            if (!json.getString("healthRate").equals("0")) {
                String[] strings = new String[]{"心率", json.getString("healthRate")+"次/分钟"};
                list.add(strings);

            }
            if (!json.getString("systolic").equals("0")) {
                String[] strings = new String[]{"收缩压", json.getString("systolic")+"mmHg"};
                list.add(strings);
//                map.put("收缩压",json.get("systolic"));
            }
            if (!json.getString("diastolic").equals("0")) {
                String[] strings = new String[]{"舒张压", json.getString("diastolic")+"mmHg"};
                list.add(strings);
                //        map.put("舒张压",json.get("diastolic"));
            }
            if (!json.getString("oxygen").equals("0")) {
                String[] strings = new String[]{"血氧", json.getString("oxygen")+"%"};
                list.add(strings);
                //  map.put("血氧",json.get("oxygen"));
            }
            if (!json.getString("foot").equals("0")) {
                String[] strings = new String[]{"计步", json.getString("foot")+"步"};
                list.add(strings);
                //   map.put("计步",json.get("foot"));
            }
            if (!json.getString("calorie").equals("0")) {
                String[] strings = new String[]{"卡路里", json.getString("calorie")+"kcal"};
                list.add(strings);
                //   map.put("卡路里",json.get("calorie"));
            }
            if (!json.getString("sugar").equals("0")) {
                String[] strings = new String[]{"血糖", json.getString("sugar")+"mmol/L"};
                list.add(strings);
                //    map.put("血糖",json.get("sugar"));
            }
            if (!json.getString("temperature").equals("0")) {
                String[] strings = new String[]{"体温", json.getString("temperature")+"℃"};
                list.add(strings);
                //    map.put("体温",json.get("temperature"));
            }
            if (!json.getString("weight").equals("0")) {
                String[] strings = new String[]{"体重", json.getString("weight")+"kg"};
                list.add(strings);
                //    map.put("体重",json.get("weight"));
            }
            if (!json.getString("fat").equals("0")) {
                String[] strings = new String[]{"体脂", json.getString("fat")+"%"};
                list.add(strings);
                //   map.put("体脂",json.get("fat"));
            }
            if (!json.getString("water").equals("0")) {
                String[] strings = new String[]{"水分率", json.getString("water")+"%"};
                list.add(strings);
                //   map.put("水分率",json.get("water"));
            }
            if (!json.getString("rate").equals("0")) {
                String[] strings = new String[]{"肌肉率", json.getString("rate")+"%"};
                list.add(strings);
                //    map.put("肌肉率",json.get("rate"));
            }
            if (!json.getString("bone").equals("0")) {
                String[] strings = new String[]{"骨骼重量", json.getString("bone")+"kg"};
                list.add(strings);
                //    map.put("骨骼重量",json.get("bone"));
            }
            if (!json.getString("metabolism").equals("0")) {
                String[] strings = new String[]{"基础代谢", json.getString("metabolism")+"kcal"};
                list.add(strings);
                //   map.put("基础代谢",json.get("metabolism"));
            }
            DataAdapter adapter = new DataAdapter(list, context);
            Log.e("onBindViewHolder: l", list.size() + "");

            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 2, RecyclerView.VERTICAL, false);
            holder.data_list.setLayoutManager(layoutManager);
            holder.data_list.setAdapter(adapter);
            holder.data_list.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    float x = 0;
                    float y = 0;
                    float move_x= 0;
                    float move_y = 0;
                    switch (event.getAction()){

                        case MotionEvent.ACTION_DOWN:
                            x = event.getX();
                            y = event.getY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            move_x += Math.abs(event.getX()-x);
                            move_y += Math.abs(event.getY()-y);
                            x = event.getX();
                            y = event.getY();
                            break;
                        case MotionEvent.ACTION_UP:
                            if(move_x<20 || move_y<20) {
                                onOrderListener.onButtonClick(v, position);
                            }
                            break;
                    }
                    return true;
                }
            });

            int count = 0;
            if (!json.getString("imgOne").equals("null")) {
                count++;
            }
            if (!json.getString("imgTwo").equals("null")) {
                count++;
            }
            if (!json.getString("imgThree").equals("null")) {
                count++;
            }
            holder.tv_img_count.setText("用户输入 " + count + "张图片");
            holder.tv_time.setText(json.getString("measureTime"));

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView data_list;
        TextView tv_img_count, tv_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            data_list = itemView.findViewById(R.id.data_list);
            tv_img_count = itemView.findViewById(R.id.tv_img_count);
            tv_time = itemView.findViewById(R.id.tv_time);


        }
    }
}
