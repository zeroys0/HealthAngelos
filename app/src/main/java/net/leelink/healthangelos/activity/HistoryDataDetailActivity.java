package net.leelink.healthangelos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

public class HistoryDataDetailActivity extends BaseActivity {
    private RelativeLayout rl_back;
    private TextView tv_time,tv_img_count,tv_heart_rate,tv_blood_pressure,tv_blood_oxygen,tv_foot,tv_calorie,tv_blood_sugar,tv_temperature,tv_weight,tv_fat,tv_water,tv_rate,tv_bone,tv_metabolism;
    private ImageView img_1,img_2,img_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_data_detail);
        init();
        initData();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_time = findViewById(R.id.tv_time);
        tv_img_count = findViewById(R.id.tv_img_count);
        tv_heart_rate = findViewById(R.id.tv_heart_rate);
        tv_blood_pressure  =findViewById(R.id.tv_blood_pressure);
        tv_blood_oxygen = findViewById(R.id.tv_blood_oxygen);
        tv_foot = findViewById(R.id.tv_foot);
        tv_calorie = findViewById(R.id.tv_calorie);
        tv_blood_sugar = findViewById(R.id.tv_blood_sugar);
        tv_temperature = findViewById(R.id.tv_temperature);
        tv_weight = findViewById(R.id.tv_weight);
        tv_fat = findViewById(R.id.tv_fat);
        tv_water = findViewById(R.id.tv_water);
        tv_rate = findViewById(R.id.tv_rate);
        tv_bone = findViewById(R.id.tv_bone);
        tv_metabolism = findViewById(R.id.tv_metabolism);
        img_1 = findViewById(R.id.img_1);
        img_2 = findViewById(R.id.img_2);
        img_3 = findViewById(R.id.img_3);

    }

    public void initData(){
        try {
            JSONObject json = new JSONObject(getIntent().getStringExtra("data"));
            tv_time.setText(json.getString("measureTime"));
            int count = 0;
            if(!json.getString("imgOne").equals("null")){
                count ++;
                img_1.setVisibility(View.VISIBLE);
                Glide.with(this).load(Urls.IMG_URL+json.getString("imgOne")).into(img_1);
            }
            if(!json.getString("imgTwo").equals("null")){
                count ++;
                img_2.setVisibility(View.VISIBLE);
                Glide.with(this).load(Urls.IMG_URL+json.getString("imgTwo")).into(img_2);
            }
            if(!json.getString("imgThree").equals("null")){
                count ++;
                img_3.setVisibility(View.VISIBLE);
                Glide.with(this).load(Urls.IMG_URL+json.getString("imgThree")).into(img_3);
            }
            tv_img_count.setText("用户输入 "+count+"张图片");
            tv_heart_rate.setText(json.getString("healthRate"));
            tv_blood_pressure.setText(json.getString("systolic")+"/"+json.getString("diastolic"));
            tv_blood_oxygen.setText(json.getString("oxygen"));
            tv_foot.setText(json.getString("foot"));
            tv_calorie.setText(json.getString("calorie"));
            tv_blood_sugar.setText(json.getString("sugar"));
            tv_temperature.setText(json.getString("temperature"));
            tv_weight.setText(json.getString("weight"));
            tv_fat.setText(json.getString("fat"));
            tv_water.setText(json.getString("water"));
            tv_rate.setText(json.getString("rate"));
            tv_bone.setText(json.getString("bone"));
            tv_metabolism.setText(json.getString("metabolism"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
