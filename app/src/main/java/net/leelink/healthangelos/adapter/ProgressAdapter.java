package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.ViewHolder> {
    Context context;
    JSONArray jsonArray;
    public ProgressAdapter(JSONArray jsonArray,Context context){
        this.jsonArray = jsonArray;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            JSONObject json = jsonArray.getJSONObject(position);
            String date = json.getString("applyTime");
            holder.tv_day.setText(date.substring(5,10));
            holder.tv_time.setText(date.substring(11,16));
            switch (position){
                case 0:
                    holder.img_up.setVisibility(View.INVISIBLE);
                    if(json.getInt("state")==0) {
                        holder.tv_content.setText("未申请");
                        holder.tv_content.setTextColor(context.getResources().getColor(R.color.text_grey));
                    } else  {
                        holder.tv_content.setText("已申请");
                    }
                    break;
                case 1:
                    if(json.getInt("state")==0) {
                        holder.tv_content.setText("未派工");
                        holder.tv_content.setTextColor(context.getResources().getColor(R.color.text_grey));
                    } else {
                        holder.tv_content.setText("已派工,评估人员将于"+json.getString("appointTime")+"前往评估");
                    }
                    break;
                case 2:
                    if(json.getInt("state")==0) {
                        holder.tv_content.setText("未评估");
                        holder.tv_content.setTextColor(context.getResources().getColor(R.color.text_grey));
                    } else {
                        holder.tv_content.setText("评估完成,评估结果为:"+json.getString("findGrade"));
                    }
                    break;
                case 3:
                    if(json.getInt("state")==0) {
                        holder.tv_content.setText("社区居委会(未审核)");
                        holder.tv_content.setTextColor(context.getResources().getColor(R.color.text_grey));
                    } else if(json.getInt("state")==1){
                        holder.tv_content.setText("社区居委会(已通过)");
                    } else {
                        holder.tv_content.setText("社区居委会(未通过)");
                    }
                    break;
                case 4:
                    if(json.getInt("state")==0) {
                        holder.tv_content.setText("街道审核(未审核)");
                        holder.tv_content.setTextColor(context.getResources().getColor(R.color.text_grey));
                    } else if(json.getInt("state")==1){
                        holder.tv_content.setText("街道审核(已通过)");
                    } else {
                        holder.tv_content.setText("街道审核(未通过)");
                    }
                    break;
                case 5:
                    holder.img_down.setVisibility(View.INVISIBLE);
                    if(json.getInt("state")==0) {
                        holder.tv_content.setText("区民政局社会事务科审批(未审核)");
                        holder.tv_content.setTextColor(context.getResources().getColor(R.color.text_grey));
                    } else if(json.getInt("state")==1){
                        holder.tv_content.setText("区民政局社会事务科审批(已通过)");
                    } else {
                        holder.tv_content.setText("区民政局社会事务科审批(未通过)");
                    }

                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return 6;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View img_up,img_down;
        TextView tv_day,tv_time,tv_content;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_up = itemView.findViewById(R.id.img_up);
            img_down = itemView.findViewById(R.id.img_down);
            tv_day = itemView.findViewById(R.id.tv_day);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_content = itemView.findViewById(R.id.tv_content);
        }
    }
}
