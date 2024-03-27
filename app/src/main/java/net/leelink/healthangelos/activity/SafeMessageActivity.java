package net.leelink.healthangelos.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.MessageAdapter;
import net.leelink.healthangelos.app.BaseActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SafeMessageActivity extends BaseActivity {
    private RelativeLayout rl_back;
    Context context;
    private RecyclerView message_list;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_message);
        context = this;
        init();
        initList();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        message_list = findViewById(R.id.message_list);
    }

    public void initList(){
        messageAdapter = new MessageAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);

    }
}