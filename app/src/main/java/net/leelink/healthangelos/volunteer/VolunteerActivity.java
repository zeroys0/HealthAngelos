package net.leelink.healthangelos.volunteer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Acache;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.util.Utils;
import net.leelink.healthangelos.volunteer.fragment.VolunteerClockInFragment;
import net.leelink.healthangelos.volunteer.fragment.VolunteerHomeFragment;
import net.leelink.healthangelos.volunteer.fragment.VolunteerMineFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class VolunteerActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener{

    BottomNavigationBar nv_bottom;
    FragmentManager fm;
    VolunteerHomeFragment volunteerHomeFragment;
    VolunteerMineFragment volunteerMineFragment;
    VolunteerClockInFragment volunteerClockInFragment;
    private Context context;
    public static int VOL_ID = -1;      //志愿者id
    private int state = -1;     //志愿者审核状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeFontSize("1.0");
        setContentView(R.layout.activity_volunteer);
        context = this;
        init();
        check();

    }

    public void init() {
        nv_bottom = findViewById(R.id.nv_bottom);
        setBottomNavigationItem(nv_bottom, 15, 26, 10);
        nv_bottom.setTabSelectedListener(VolunteerActivity.this);
        nv_bottom.setMode(BottomNavigationBar.MODE_FIXED);
        nv_bottom.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        nv_bottom.setBarBackgroundColor(R.color.white);
        nv_bottom
                .addItem(new BottomNavigationItem(R.drawable.volunteer_home_selected, "主页").setInactiveIcon(getResources().getDrawable(R.drawable.volunteer_home)).setActiveColorResource(R.color.v_yellow))
                .addItem(new BottomNavigationItem(R.drawable.volunteer_colockin_selected, "打卡").setInactiveIcon(getResources().getDrawable(R.drawable.volunteer_clockin)).setActiveColorResource(R.color.v_yellow))
                .addItem(new BottomNavigationItem(R.drawable.volunteer_mine_selected, "我的").setInactiveIcon(getResources().getDrawable(R.drawable.volunteer_mine)).setActiveColorResource(R.color.v_yellow))
                .setFirstSelectedPosition(0)
                .initialise();
        fm = getSupportFragmentManager();
//        setBottomNavigationItem(8, 18);
        FragmentTransaction ft = fm.beginTransaction();
        volunteerHomeFragment = (VolunteerHomeFragment) fm.findFragmentByTag("volunteer_home");
        if (volunteerHomeFragment == null) {
            volunteerHomeFragment = new VolunteerHomeFragment();
        }
        ft.add(R.id.fragment_view, volunteerHomeFragment, "volunteer_home");
        ft.commit();
    }


    private void setBottomNavigationItem(BottomNavigationBar bottomNavigationBar, int space, int imgLen, int textSize) {
        Class barClass = bottomNavigationBar.getClass();
        Field[] fields = barClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            if (field.getName().equals("mTabContainer")) {
                try {
                    //反射得到 mTabContainer
                    LinearLayout mTabContainer = (LinearLayout) field.get(bottomNavigationBar);
                    for (int j = 0; j < mTabContainer.getChildCount(); j++) {
                        //获取到容器内的各个Tab
                        View view = mTabContainer.getChildAt(j);
                        //获取到Tab内的各个显示控件
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(56));
                        FrameLayout container = (FrameLayout) view.findViewById(R.id.fixed_bottom_navigation_container);
                        container.setLayoutParams(params);
                        container.setPadding(dip2px(12), dip2px(0), dip2px(12), dip2px(0));

                        //获取到Tab内的文字控件
                        TextView labelView = (TextView) view.findViewById(com.ashokvarma.bottomnavigation.R.id.fixed_bottom_navigation_title);
                        //计算文字的高度DP值并设置，setTextSize为设置文字正方形的对角线长度，所以：文字高度（总内容高度减去间距和图片高度）*根号2即为对角线长度，此处用DP值，设置该值即可。
                        labelView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
                        labelView.setIncludeFontPadding(false);
                        labelView.setPadding(0, 0, 0, dip2px(20 - textSize - space / 2));

                        //获取到Tab内的图像控件
                        ImageView iconView = (ImageView) view.findViewById(com.ashokvarma.bottomnavigation.R.id.fixed_bottom_navigation_icon);
                        //设置图片参数，其中，MethodUtils.dip2px()：换算dp值
                        params = new FrameLayout.LayoutParams(dip2px(imgLen), dip2px(imgLen));
                        params.setMargins(0, 0, 0, space / 2);
                        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                        iconView.setLayoutParams(params);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

        public int dip2px(float dpValue) {
        final float scale = getApplication().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    @Override
    public void onTabSelected(int position) {
        FragmentTransaction ft = getFragmentTransaction();
        if(Acache.get(context).getAsString("is_vol").equals("true")){
            try {
                state = Acache.get(context).getAsJSONObject("volunteer").getInt("state");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        switch (position) {
            case 0:
                if (volunteerHomeFragment == null) {
                    ft.add(R.id.fragment_view, new VolunteerHomeFragment(), "volunteer_home");
                } else {
                    ft.show(volunteerHomeFragment);
                }
                Utils.setStatusTextColor(true, VolunteerActivity.this);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
                break;
            case 1:
                if (state ==0 ) {
                    Intent intent3 = new Intent(context, ExamineVolunteerActivity.class);
                    startActivity(intent3);
                    return;
                }
                if(state !=1) {
                    Intent intent = new Intent(context,VolunteerApplyActivity.class);
                    startActivity(intent);
                    return;
                }
                if (volunteerClockInFragment == null) {
                    ft.add(R.id.fragment_view, new VolunteerClockInFragment(), "volunteer_clockin");
                } else {
                    ft.show(volunteerClockInFragment);
                }
                Utils.setStatusTextColor(true, VolunteerActivity.this);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
                break;
            case 2:
                if (state ==0 ) {
                    Intent intent3 = new Intent(context, ExamineVolunteerActivity.class);
                    startActivity(intent3);
                    return;
                }
                if(state !=1) {
                    Intent intent = new Intent(context,VolunteerApplyActivity.class);
                    startActivity(intent);
                    return;
                }
                if (volunteerMineFragment == null) {
                    ft.add(R.id.fragment_view, new VolunteerMineFragment(), "volunteer_mine");
                } else {
                    ft.show(volunteerMineFragment);
                }
                Utils.setStatusTextColor(true, VolunteerActivity.this);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
                break;
            default:
                break;
        }
    }

    public void check() {
        OkGo.<String>get(Urls.getInstance().MINE_INFO)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("志愿者个人信息", json.toString());
                            Acache.get(context).put("is_vol", "true");
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                if(!json.isNull("id")) {
                                    VOL_ID = json.getInt("id");
                                }
                                state = json.getInt("state");
                            } else if (json.getInt("status") == 201) {
                                Acache.get(context).remove("volunteer");

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
                        Toast.makeText(context, "连接失败,请检查网络", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

    protected FragmentTransaction getFragmentTransaction() {
        // TODO Auto-generated method stub
        FragmentManager fm = getSupportFragmentManager();
        volunteerHomeFragment = (VolunteerHomeFragment) fm.findFragmentByTag("volunteer_home");
        volunteerClockInFragment = (VolunteerClockInFragment) fm.findFragmentByTag("volunteer_clockin");
        volunteerMineFragment = (VolunteerMineFragment) fm.findFragmentByTag("volunteer_mine");
        FragmentTransaction ft = fm.beginTransaction();
        /** 如果存在hide掉 */
        if (volunteerHomeFragment != null)
            ft.hide(volunteerHomeFragment);

        if (volunteerClockInFragment != null)
            ft.hide(volunteerClockInFragment);
        if (volunteerMineFragment != null)
            ft.hide(volunteerMineFragment);
        return ft;
    }

}
