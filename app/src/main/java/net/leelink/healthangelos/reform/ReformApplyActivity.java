package net.leelink.healthangelos.reform;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;

import java.io.File;

import androidx.annotation.Nullable;

public class ReformApplyActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back,rl_identity;
    private ImageView img_sign1,img_master,img_personal;
    private TextView tv_state;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reform_apply);
        init();
        context = this;
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        img_sign1 = findViewById(R.id.img_sign1);
        img_sign1.setOnClickListener(this);
        rl_identity = findViewById(R.id.rl_identity);
        rl_identity.setOnClickListener(this);
        tv_state = findViewById(R.id.tv_state);
        tv_state.setOnClickListener(this);
        img_master = findViewById(R.id.img_master);
        img_personal = findViewById(R.id.img_personal);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.img_sign1:
                Intent intent = new Intent(context,SignatureActivity.class);
                startActivityForResult(intent,101);
                break;
            case R.id.rl_identity:
                Intent intent1 = new Intent(context,ChooseFeatureActivity.class);
                startActivity(intent1);
                break;
            case R.id.tv_state:
                Intent intent2 = new Intent(context, NeoReformProgressActivity.class);
                startActivity(intent2);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            if (requestCode == 101) {
                File file = new File(data.getStringExtra("img"));
                Glide.with(context).load(file).into(img_sign1);
            }
        }
    }
}
