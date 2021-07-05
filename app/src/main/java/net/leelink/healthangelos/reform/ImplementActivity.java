package net.leelink.healthangelos.reform;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.reform.adapter.ProjectAdapter;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ImplementActivity extends BaseActivity implements View.OnClickListener, OnOrderListener {
    private RelativeLayout rl_back;
    private RecyclerView project_list;
    private Context context;
    private ProjectAdapter projectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_implement);
        init();
        context = this;
        initList();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        project_list = findViewById(R.id.project_list);

    }

    public void initList(){
        projectAdapter = new ProjectAdapter(context,ImplementActivity.this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
        project_list.setAdapter(projectAdapter);
        project_list.setLayoutManager(layoutManager);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(View view) {

    }

    @Override
    public void onButtonClick(View view, int position) {
        Intent intent = new Intent(context,ProjectDetailActivity.class);
        startActivity(intent);
    }
}
