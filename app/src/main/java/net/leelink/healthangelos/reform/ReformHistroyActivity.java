package net.leelink.healthangelos.reform;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.reform.adapter.ReformHistoryAdapter;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ReformHistroyActivity extends BaseActivity {
    private Context context;
    private RelativeLayout rl_back;
    private RecyclerView reform_history;
    private ReformHistoryAdapter reformHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reform_histroy);
        context = this;
        init();
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
        reform_history = findViewById(R.id.reform_history);
    }

    public void initList(){
        reformHistoryAdapter = new ReformHistoryAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
        reform_history.setLayoutManager(layoutManager);
        reform_history.setAdapter(reformHistoryAdapter);
    }
}
