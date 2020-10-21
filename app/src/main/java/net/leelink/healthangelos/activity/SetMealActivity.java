package net.leelink.healthangelos.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.adapter.SetMealAdapter;
import net.leelink.healthangelos.app.BaseActivity;

public class SetMealActivity extends BaseActivity  implements OnOrderListener {
    RecyclerView set_meal_list;
    SetMealAdapter setMealAdapter;
    RelativeLayout rl_back,img_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_meal);
        init();
        initData();
    }

    public void init(){
        set_meal_list = findViewById(R.id.set_meal_list);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        img_add = findViewById(R.id.img_add);
        img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetMealActivity.this,SetListActivity.class);
                startActivity(intent);
            }
        });
    }

    public void initData(){
        setMealAdapter = new SetMealAdapter(this,SetMealActivity.this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        set_meal_list.setLayoutManager(layoutManager);
        set_meal_list.setAdapter(setMealAdapter);
    }

    @Override
    public void onItemClick(View view) {

    }

    @Override
    public void onButtonClick(View view, int position) {

    }
}
