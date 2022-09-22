package net.leelink.healthangelos.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.bean.BalanceBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BalanceAdapter  extends RecyclerView.Adapter<BalanceAdapter.ViewHolder>  {
    Context context;
    List<BalanceBean> list;

    public BalanceAdapter(Context context,List<BalanceBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public BalanceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.balance_record_item,parent,false);
        BalanceAdapter.ViewHolder v= new BalanceAdapter.ViewHolder(view);

        return v;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull BalanceAdapter.ViewHolder holder, int position) {
        holder.tv_time.setText(list.get(position).getCreateTime());

        switch (list.get(position).getType()){
            case 1:
                holder.tv_name.setText("充值");
                holder.tv_cost.setText("+"+list.get(position).getAmount());
                break;
            case 2:
                holder.tv_name.setText("退款");
                holder.tv_cost.setText("+"+list.get(position).getAmount());
                break;
            case 3:
                holder.tv_name.setText("消费");
                holder.tv_cost.setTextColor(context.getResources().getColor(R.color.text_green));
                holder.tv_cost.setText("-"+list.get(position).getAmount());
                break;
            case 4:
                holder.tv_name.setText("财务退住结算");
                holder.tv_cost.setTextColor(context.getResources().getColor(R.color.text_grey));
                holder.tv_cost.setText(list.get(position).getAmount());
                break;
            case 5:
                holder.tv_name.setText("财务抄表缴费");
                holder.tv_cost.setTextColor(context.getResources().getColor(R.color.text_green));
                holder.tv_cost.setText("-"+list.get(position).getAmount());
                break;
            case 6:
                holder.tv_name.setText("机构月费用");
                holder.tv_cost.setTextColor(context.getResources().getColor(R.color.text_green));
                holder.tv_cost.setText("-"+list.get(position).getAmount());
                break;
            case 7:
                holder.tv_name.setText("机构入住费用");
                holder.tv_cost.setTextColor(context.getResources().getColor(R.color.text_green));
                holder.tv_cost.setText("-"+list.get(position).getAmount());
                break;
            case 8:
                holder.tv_name.setText("机构阶段性费用");
                holder.tv_cost.setTextColor(context.getResources().getColor(R.color.text_green));
                holder.tv_cost.setText("-"+list.get(position).getAmount());
                break;
            case 9:
                holder.tv_name.setText("机构床位费");
                holder.tv_cost.setTextColor(context.getResources().getColor(R.color.text_green));
                holder.tv_cost.setText("-"+list.get(position).getAmount());
                break;
            case 10:
                holder.tv_name.setText("退款到余额");
                holder.tv_cost.setText("+"+list.get(position).getAmount());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name,tv_time,tv_cost;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_cost = itemView.findViewById(R.id.tv_cost);
        }
    }
}
