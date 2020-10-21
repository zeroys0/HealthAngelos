package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.RecordBean;

import org.xml.sax.DTDHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {
    OnOrderListener onOrderListener;
    Context context;
    List<RecordBean> list;

    public RecordAdapter(  List<RecordBean> list, Context context,    OnOrderListener onOrderListener) {
        this.list  = list;
        this.context = context;
        this.onOrderListener = onOrderListener;
    }
    @NonNull
    @Override
    public RecordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecordAdapter.ViewHolder holder, final int position) {
        holder.tv_time.setText(list.get(position).getCreateTime());
        holder.tv_content.setText(list.get(position).getText());
        holder.ll_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderListener.onButtonClick(v,position);
            }
        });
        SimpleDateFormat dateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date(System.currentTimeMillis());
        Date update = new Date();
        try {
            update =  dateFormat.parse(list.get(position).getUpdateTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(now.getTime()<update.getTime()){
            holder.tv_state.setText("未发送");
        } else {

        }
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_time,tv_content,tv_state;
        LinearLayout ll_delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_content = itemView.findViewById(R.id.tv_content);
            ll_delete = itemView.findViewById(R.id.ll_delete);
            tv_state = itemView.findViewById(R.id.tv_state);

        }
    }
}
