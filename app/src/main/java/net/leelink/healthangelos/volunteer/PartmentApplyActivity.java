package net.leelink.healthangelos.volunteer;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.bean.TeamMemberBean;
import net.leelink.healthangelos.volunteer.adapter.TeamPartmentAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class PartmentApplyActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back;
    Context context;
    private RecyclerView event_list;
    TeamPartmentAdapter teamPartmentAdapter;
    private TwinklingRefreshLayout refreshLayout;
    int page = 1;
    boolean hasNextPage;
    List<TeamMemberBean> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partment_apply);
        context = this;
        init();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        event_list = findViewById(R.id.event_list);

    }

    @Override
    public void onClick(View v) {

    }
}
