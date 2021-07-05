package net.leelink.healthangelos.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pattonsoft.pattonutil1_0.util.SPUtils;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.DataArrayAdapter;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.bean.HealthDataSort;
import net.leelink.healthangelos.view.SimpleItemTouchCallBack;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EditLeapActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back,img_add;
    private Context context;
    private RecyclerView data_array;
    private DataArrayAdapter dataArrayAdapter;
    List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_leap);
        init();
        context = this;
        initList();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        img_add = findViewById(R.id.img_add);
        img_add.setOnClickListener(this);
        data_array = findViewById(R.id.data_array);
    }

    public void initList(){
        list.add("步 数");
        list.add("心 率");
        list.add("血 压");
        list.add("血 氧");
        list.add("血 糖");
        dataArrayAdapter = new DataArrayAdapter(context,list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
        data_array.setAdapter(dataArrayAdapter);
        data_array.setLayoutManager(layoutManager);
        // 拖拽移动和左滑删除
        SimpleItemTouchCallBack simpleItemTouchCallBack = new SimpleItemTouchCallBack(dataArrayAdapter);
        // 要实现侧滑删除条目，把 false 改成 true 就可以了
        simpleItemTouchCallBack.setmSwipeEnable(false);
        ItemTouchHelper helper = new ItemTouchHelper(simpleItemTouchCallBack);
        helper.attachToRecyclerView(data_array);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.img_add:
                Toast.makeText(context, "编辑完成", Toast.LENGTH_SHORT).show();
                StringBuilder stringBuilder = new StringBuilder();
                for(String string:list){
                    stringBuilder.append(string+",");
                }
                String s = stringBuilder.toString();
                s = s.substring(0,s.length()-1);
                SPUtils.put(context,"sort",s);
                EventBus.getDefault().post(new HealthDataSort(s));
                Log.e( "onClick: ",s );
                finish();
                break;
        }
    }
}


