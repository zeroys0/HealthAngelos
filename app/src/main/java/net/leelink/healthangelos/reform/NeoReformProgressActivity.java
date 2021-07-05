package net.leelink.healthangelos.reform;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.reform.adapter.NeoProgressAdapter;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NeoReformProgressActivity extends BaseActivity implements OnOrderListener {
    Context context;
    private RecyclerView progress_list;
    private RelativeLayout rl_back;
    private NeoProgressAdapter neoProgressAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neo_reform_progress);
        init();
        context = this;
        initList();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        progress_list = findViewById(R.id.progress_list);
    }

    public void initList(){
        neoProgressAdapter = new NeoProgressAdapter(context,NeoReformProgressActivity.this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
        progress_list.setAdapter(neoProgressAdapter);
        progress_list.setLayoutManager(layoutManager);
    }

    @Override
    public void onItemClick(View view) {
        int position =  progress_list.getChildLayoutPosition(view);

    }

    @Override
    public void onButtonClick(View view, int position) {

    }
}
