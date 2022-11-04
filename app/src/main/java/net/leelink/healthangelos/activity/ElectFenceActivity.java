package net.leelink.healthangelos.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.google.android.material.tabs.TabLayout;
import com.pattonsoft.pattonutil1_0.util.MapUtil;
import com.pattonsoft.pattonutil1_0.util.Mytoast;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.SettingService;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.Pager2Adapter;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.fragment.MonitorLimitsFragment;
import net.leelink.healthangelos.fragment.MonitorPlanFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

public class ElectFenceActivity extends BaseActivity {

    private TabLayout tabLayout;
    private ViewPager2 view_pager;
    private List<Fragment> fragments;
    private RelativeLayout rl_back;
    private RelativeLayout img_add;
    private Context mContext;
    public static final int ADD_PLAN = 0;
    public static final int EDIT_PLAN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elect_fence);
        mContext = this;
        init();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tabLayout = findViewById(R.id.tabLayout);
        view_pager = findViewById(R.id.view_pager);
        tabLayout.addTab(tabLayout.newTab().setText("监控计划"));
        tabLayout.addTab(tabLayout.newTab().setText("监控范围"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                view_pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        fragments = new ArrayList<>();
        fragments.add(new MonitorPlanFragment());
        fragments.add(new MonitorLimitsFragment());
        view_pager.setAdapter(new Pager2Adapter(this,fragments));
        view_pager.setCurrentItem(0);
        view_pager.setUserInputEnabled(false);
        img_add = findViewById(R.id.img_add);
        img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View popView = getLayoutInflater().inflate(R.layout.view_rail_top, null);
                LinearLayout ll_create_plan = (LinearLayout) popView.findViewById(R.id.ll_create_plan);
                LinearLayout ll_create_scope = (LinearLayout) popView.findViewById(R.id.ll_create_scope);

                final PopupWindow pop = new PopupWindow(popView,
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

                pop.setContentView(popView);
                pop.setOutsideTouchable(true);
                pop.setBackgroundDrawable(new BitmapDrawable());

                ll_create_plan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //  新增监控计划
                        startActivity(new Intent(mContext, RailCreatePlanActivity.class));
                        pop.dismiss();
                    }
                });
                ll_create_scope.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //新增监控范围
                        doGetLBSPermission(null);
                        pop.dismiss();

                    }
                });

                pop.showAsDropDown(img_add);
            }
        });
    }


    //获取权限 并扫描
    void doGetLBSPermission(final Map<String, Object> map) {
        AndPermission.with(mContext)
                .permission(
                        Permission.ACCESS_COARSE_LOCATION, Permission.ACCESS_FINE_LOCATION
                )
                .rationale(new Rationale() {
                    @Override
                    public void showRationale(Context context, Object data, RequestExecutor executor) {
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                        builder.setMessage("地图定位需要用户开启定位,是否同意开启定位权限");
                        builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 如果用户同意去设置：
                                executor.execute();
                            }
                        });
                        //设置点击对话框外部区域不关闭对话框
                        builder.setCancelable(false);
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 如果用户不同意去设置：
                                executor.cancel();
                                Mytoast.show(mContext, "无法获取定位");

                            }
                        });
                        builder.show();
                    }
                })
                .onGranted(new Action() {
                    @Override
                    public void onAction(Object data) {
                        Intent intent = new Intent(mContext, ActivitySetMapPoint.class);

                        if (map != null && map.size() > 0) {

                            intent.putExtra("scopeId", MapUtil.getInt(map, "Id"));
                            intent.putExtra("Alias", MapUtil.getString(map, "Alias"));
                            intent.putExtra("La1", MapUtil.getDouble(map, "La1"));
                            intent.putExtra("Lo1", MapUtil.getDouble(map, "Lo1"));
                            intent.putExtra("La2", MapUtil.getDouble(map, "La2"));
                            intent.putExtra("Lo2", MapUtil.getDouble(map, "Lo2"));
                        }
                        startActivity(intent);
                    }
                })
                .onDenied(new Action() {
                    @Override
                    public void onAction(Object data) {
                        if (AndPermission.hasAlwaysDeniedPermission(mContext, (List<String>) data)) {
                            // 这里使用一个Dialog展示没有这些权限应用程序无法继续运行，询问用户是否去设置中授权。

                            final SettingService settingService = AndPermission.permissionSetting(mContext);
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                            builder.setMessage("定位权限被禁止,用户将无法获取定位,请到\"设置\"开启");
                            builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 如果用户同意去设置：
                                    settingService.execute();

                                }
                            });
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 如果用户不同意去设置：
                                    settingService.cancel();
                                }
                            });
                            //设置点击对话框外部区域不关闭对话框
                            builder.setCancelable(false);
                            builder.show();
                        }
                    }

                })
                .start();


    }


}
