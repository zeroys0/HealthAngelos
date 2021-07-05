package net.leelink.healthangelos.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.SafeDeviceAdapter;
import net.leelink.healthangelos.app.BaseActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SafeDeviceActivity extends BaseActivity implements View.OnClickListener
{
private RelativeLayout rl_back;
private RecyclerView option_list;
private Context context;
private SafeDeviceAdapter safeDeviceAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_device);
        init();
        context = this;
        initList();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        option_list = findViewById(R.id.option_list);
    }

    public void initList(){
        option_list.setNestedScrollingEnabled(false);
        safeDeviceAdapter = new SafeDeviceAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
        option_list.setAdapter(safeDeviceAdapter);
        option_list.setLayoutManager(layoutManager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
        }
    }
}
