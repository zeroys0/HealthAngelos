package net.leelink.healthangelos.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.adapter.UserNameAdapter;
import net.leelink.healthangelos.app.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SaveTemplateActivity extends BaseActivity implements View.OnClickListener, OnOrderListener {
    private TextView tv_title;
    private RelativeLayout rl_back;
    private Button btn_save;
    List<Map<String, Object>> list2;
    PopupWindow pop;
    private Context context;
    private RecyclerView user_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_template);
        init();
        context = this;
    }

    public void init(){
        tv_title = findViewById(R.id.tv_title);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_save:
                backgroundAlpha(0.5f);
                showPopup1();
                break;
        }
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

    public void showPopup1() {
        View popView = getLayoutInflater().inflate(R.layout.popu_choose_user, null);

        user_list = popView.findViewById(R.id.user_list);

        SharedPreferences sp = getSharedPreferences("sp", 0);
        String userList = sp.getString("user_name", "");
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(userList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        UserNameAdapter userNameAdapter = new UserNameAdapter(jsonArray, SaveTemplateActivity.this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        user_list.setAdapter(userNameAdapter);
        user_list.setLayoutManager(layoutManager);
        pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOnDismissListener(new SaveTemplateActivity.poponDismissListener());

        pop.showAtLocation(rl_back, Gravity.BOTTOM, 0, 100);
    }

    @Override
    public void onItemClick(View view) {

    }

    @Override
    public void onButtonClick(View view, int position) {

    }

    /**
     * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     *
     * @author cg
     */
    class poponDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            // Log.v("List_noteTypeActivity:", "我是关闭事件");
            backgroundAlpha(1f);
        }
    }
}
