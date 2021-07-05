package net.leelink.healthangelos.reform;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.reform.adapter.ProjectListAdapter;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ProjectConfirmActivity extends BaseActivity implements View.OnClickListener {
    private RecyclerView reform_list;
    private RelativeLayout rl_back;
    private Context context;
    private ProjectListAdapter projectListAdapter;
    private TextView tv_state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_confirm);
        init();
        context = this;
        initList();

    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        reform_list = findViewById(R.id.reform_list);
        tv_state = findViewById(R.id.tv_state);
        tv_state.setOnClickListener(this);
    }

    public void initList(){
        projectListAdapter = new ProjectListAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
        reform_list.setAdapter(projectListAdapter);
        reform_list.setLayoutManager(layoutManager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_state:
                break;
        }
    }
}
