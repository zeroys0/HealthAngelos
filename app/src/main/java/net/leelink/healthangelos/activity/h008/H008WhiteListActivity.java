package net.leelink.healthangelos.activity.h008;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.h008.adapter.H008WhiteListAdapter;
import net.leelink.healthangelos.activity.h008.adapter.OnAlarmClockListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class H008WhiteListActivity extends BaseActivity implements OnAlarmClockListener {
    Context context;
    RelativeLayout rl_back,img_add;
    RecyclerView white_list;
    JSONArray pro_jsonArray,jsonArray;
    H008WhiteListAdapter h008WhiteListAdapter;
    private Button btn_save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h008_white_list);
        context = this;
        createProgressBar(context);
        init();
        initList();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        img_add = findViewById(R.id.img_add);
        img_add.setOnClickListener(this);
        white_list = findViewById(R.id.white_list);
        btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);
    }

    public void initList(){
        try {
            jsonArray = new JSONArray(getIntent().getStringExtra("data"));
            pro_jsonArray = new JSONArray(getIntent().getStringExtra("data"));
            h008WhiteListAdapter = new H008WhiteListAdapter(jsonArray,context,this);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
            white_list.setAdapter(h008WhiteListAdapter);
            white_list.setLayoutManager(layoutManager);
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.rl_back:
                if(jsonArray.toString().equals(pro_jsonArray.toString())){
                    finish();
                } else {
                    showTips();
                }
                break;
            case R.id.img_add:
                Intent intent = new Intent(context,H008NewContactActivity.class);
                startActivityForResult(intent,2);
                break;
            case R.id.btn_save:
                save();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2){
            if(data!=null){
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("name",data.getStringExtra("name"));
                    jsonObject.put("phone",data.getStringExtra("phone"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArray.put(jsonObject);
                h008WhiteListAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onItemClick(View view) {

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onDelete(View view, int position) {
        jsonArray.remove(position);
        h008WhiteListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCheckChange(View view, int position, boolean checked) {

    }

    public void save(){
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().H006_PHB + "/" + getIntent().getStringExtra("imei"))
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(jsonArray.toString())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("修改白名单", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "同步成功", Toast.LENGTH_LONG).show();
                                pro_jsonArray = jsonArray;
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        stopProgressBar();
                    }
                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(jsonArray.toString().equals(pro_jsonArray.toString())){
                finish();
            } else {
                showTips();
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @SuppressLint("WrongConstant")
    public void showTips() {

        // TODO Auto-generated method stub
        View popview = LayoutInflater.from(H008WhiteListActivity.this).inflate(R.layout.popu_shutdown, null);
        PopupWindow popuPhoneW = new PopupWindow(popview,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView tv_text = popview.findViewById(R.id.tv_text);
        tv_text.setText("您编辑的内容还未同步,是否继续退出?");
        TextView tv_cancel = popview.findViewById(R.id.tv_cancel);
        TextView tv_confirm = popview.findViewById(R.id.tv_confirm);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popuPhoneW.dismiss();
            }
        });
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                popuPhoneW.dismiss();
            }
        });
        popuPhoneW.setFocusable(false);
        popuPhoneW.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popuPhoneW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popuPhoneW.setOutsideTouchable(true);
        popuPhoneW.setBackgroundDrawable(new BitmapDrawable());
        popuPhoneW.setOnDismissListener(new H008WhiteListActivity.poponDismissListener());
        popuPhoneW.showAtLocation(rl_back, Gravity.CENTER, 0, 0);
        backgroundAlpha(0.5f);

    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        if (bgAlpha == 1) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        getWindow().setAttributes(lp);
    }

    class poponDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            // Log.v("List_noteTypeActivity:", "我是关闭事件");
            backgroundAlpha(1f);

        }
    }

}