package net.leelink.healthangelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinnerBaseAdapter;

import net.leelink.healthangelos.R;

import java.util.List;

public class SpinnerAdapter extends MaterialSpinnerBaseAdapter {
private List<String> list;
private Context context;

    public SpinnerAdapter(Context context) {
        super(context);
    }

    public SpinnerAdapter(Context context,List<String> list) {
        super(context);
        this.list = list;
    }


    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object get(int position) {
        return null;
    }

    @Override
    public List getItems() {
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView=LayoutInflater.from(context).inflate(R.layout.item_custom, null);
        TextView tvdropdowview=(TextView) convertView.findViewById(R.id.tv_type);
        tvdropdowview.setText(getItem(position).toString());
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        convertView=LayoutInflater.from(context).inflate(R.layout.item_custom, null);
        TextView tvdropdowview=(TextView) convertView.findViewById(R.id.tv_type);
        tvdropdowview.setText(getItem(position).toString());
        return convertView;
    }
}
