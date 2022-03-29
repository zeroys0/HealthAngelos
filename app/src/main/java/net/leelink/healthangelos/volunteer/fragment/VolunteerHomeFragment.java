package net.leelink.healthangelos.volunteer.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.LoginActivity;
import net.leelink.healthangelos.adapter.OnItemClickListener;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.adapter.VolunteerAdapter;
import net.leelink.healthangelos.adapter.VolunteerNoticeAdapter;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.BannerBean;
import net.leelink.healthangelos.bean.NoticeBean;
import net.leelink.healthangelos.bean.VolunteerEventBean;
import net.leelink.healthangelos.fragment.BaseFragment;
import net.leelink.healthangelos.util.Acache;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.volunteer.CreatePartyActivity;
import net.leelink.healthangelos.volunteer.ExchangeActivity;
import net.leelink.healthangelos.volunteer.MissionDetailActivity;
import net.leelink.healthangelos.volunteer.MyTeamActivity;
import net.leelink.healthangelos.volunteer.NoticeActivity;
import net.leelink.healthangelos.volunteer.SingleVolunteerActivity;
import net.leelink.healthangelos.volunteer.TeamListActivity;
import net.leelink.healthangelos.volunteer.TeamMissionDetailActivity;
import net.leelink.healthangelos.volunteer.TeamMissionListActivity;
import net.leelink.healthangelos.volunteer.VolNoticeActivity;
import net.leelink.healthangelos.volunteer.VolunteerActivity;
import net.leelink.healthangelos.volunteer.VolunteerApplyActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VolunteerHomeFragment extends BaseFragment implements View.OnClickListener, OnItemClickListener, OnOrderListener,OnBannerListener {
    Context context;
    RelativeLayout rl_back, rl_personal, rl_exchange, rl_party, rl_left, rl_right, rl_left_1, rl_right_1;
    RecyclerView notice_list, action_list;
    VolunteerNoticeAdapter volunteerNoticeAdapter;
    List<NoticeBean> noticeBeans;
    List<VolunteerEventBean> list = new ArrayList<>();
    VolunteerAdapter volunteerAdapter;
    private TextView tv_organ, tv_team;
    private String id;
    private Banner banner;
    List<String> banner_list = new ArrayList<>();
    private ViewFlipper viewFlipper;

    @Override
    public void handleCallBack(Message msg) {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_volunteer_home, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        context = getContext();
        init(view);
        initNotice();
        initBanner(view);
        setBotViewConfig();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        list.clear();
        initList();
        check();
    }

    public void init(View view) {
        rl_back = view.findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        rl_left = view.findViewById(R.id.rl_left);
        rl_left.setOnClickListener(this);
        rl_left_1 = view.findViewById(R.id.rl_left_1);
        tv_team = view.findViewById(R.id.tv_team);
        banner = view.findViewById(R.id.banner);
        rl_right = view.findViewById(R.id.rl_right);
        rl_right.setOnClickListener(this);
        rl_right_1 = view.findViewById(R.id.rl_right_1);
        rl_right_1.setOnClickListener(this);
        action_list = view.findViewById(R.id.action_list);
        rl_personal = view.findViewById(R.id.rl_personal);
        rl_personal.setOnClickListener(this);
        rl_exchange = view.findViewById(R.id.rl_exchange);
        rl_exchange.setOnClickListener(this);
        rl_party = view.findViewById(R.id.rl_party);
        rl_party.setOnClickListener(this);
        viewFlipper = view.findViewById(R.id.text_scroll);

        tv_organ = view.findViewById(R.id.tv_organ);

    }

    /**
     * 设置上下切换控件配置
     */
    private void setBotViewConfig() {

        viewFlipper.setInAnimation(getContext(),R.anim.anim_in);
        viewFlipper.setOutAnimation(getContext(),R.anim.anim_out);
        viewFlipper.setFlipInterval(5000);
        viewFlipper.startFlipping();

    }

    public void initNotice() {

        OkGo.<String>get(Urls.getInstance().VOL_NOTICE)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("志愿者公告", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                Gson gson = new Gson();
                                noticeBeans = gson.fromJson(jsonArray.toString(), new TypeToken<List<NoticeBean>>() {
                                }.getType());
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < noticeBeans.size(); i++) {
                                    stringBuilder.append(noticeBeans.get(i).getTitle() + "    ");

                                    View view = getLayoutInflater().inflate(R.layout.item_view_flipper,null);
                                    TextView textView = view.findViewById(R.id.item_text);
                                    textView.setText(noticeBeans.get(i).getTitle());
                                    viewFlipper.addView(view);
                                    int finalI = i;
                                    view.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(getContext(), VolNoticeActivity.class);
                                            intent.putExtra("title",noticeBeans.get(finalI).getTitle());
                                            intent.putExtra("content",noticeBeans.get(finalI).getContent());
                                            startActivity(intent);
                                        }
                                    });
                                }

                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                                volunteerNoticeAdapter = new VolunteerNoticeAdapter(noticeBeans, context, VolunteerHomeFragment.this);
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void initBanner(View view) {
        banner = view.findViewById(R.id.banner);
        Log.e( "initBanner: ", Urls.getInstance().HOMEBANNER);
        OkGo.<String>get(Urls.getInstance().VOL_BANNER)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("志愿者banner", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                Gson gson = new Gson();
                                final List<BannerBean> list = gson.fromJson(jsonArray.toString(), new TypeToken<List<BannerBean>>() {
                                }.getType());
                                for (BannerBean bannerBean : list) {
                                    banner_list.add(Urls.getInstance().IMG_URL + bannerBean.getImgPath());
                                }

                                //banner.setBannerTitles(bannertitles);
                                banner.setBannerStyle(BannerConfig.NOT_INDICATOR);

                                banner.setIndicatorGravity(BannerConfig.RIGHT);
                                banner.setImageLoader(new ImageLoader() {
                                    @Override
                                    public void displayImage(Context context, Object path, ImageView imageView) {
                                        //Glide 加载图片简单用法
                                        Glide.with(context).load(path).into(imageView);

                                    }
                                });
                                banner.setOnBannerListener(VolunteerHomeFragment.this);
                                banner.setOnBannerListener(new OnBannerListener() {
                                    @Override
                                    public void OnBannerClick(int position) {
                                        //banner点击事件
//                                        Intent intent = new Intent(context, WebActivity.class);
//                                        try {
//                                            intent.putExtra("url", jsonArray.getJSONObject(position).getString("imgRemark"));
//                                            intent.putExtra("title", jsonArray.getJSONObject(position).getString("title"));
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                        startActivity(intent);
                                    }
                                });

                                banner.setImages(banner_list);
                                banner.isAutoPlay(true);
                                banner.setDelayTime(5000);
                                banner.start();
                            } else if (json.getInt("status") == 505) {
                                SharedPreferences sp = Objects.requireNonNull(getContext()).getSharedPreferences("sp", 0);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.remove("secretKey");
                                editor.remove("telephone");
                                editor.apply();
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                Objects.requireNonNull(getActivity()).finish();
                            } else {
                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    public void initList() {
        OkGo.<String>get(Urls.getInstance().TEAMS_MINE_QB)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("state", 1)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("活动列表", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                JSONArray jsonArray = json.getJSONArray("list");
                                Gson gson = new Gson();
                                list = gson.fromJson(jsonArray.toString(), new TypeToken<List<VolunteerEventBean>>() {
                                }.getType());
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                                volunteerAdapter = new VolunteerAdapter(list, context, VolunteerHomeFragment.this);
                                action_list.setLayoutManager(layoutManager);
                                action_list.setAdapter(volunteerAdapter);
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                getActivity().finish();
                break;
            case R.id.rl_personal:
                Intent intent = new Intent(getContext(), SingleVolunteerActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_exchange:
                if (state != 1) {
                    Intent intent1 = new Intent(context, VolunteerApplyActivity.class);
                    startActivity(intent1);
                    return;
                }
                Intent intent4 = new Intent(getContext(), ExchangeActivity.class);
                if (id != null) {
                    intent4.putExtra("id", id);
                }
                startActivity(intent4);
                break;
            case R.id.rl_party:
                Intent intent2 = new Intent(getContext(), TeamMissionListActivity.class);
                startActivity(intent2);
                break;
            case R.id.rl_left:
                Intent intent1 = new Intent(getContext(), VolunteerApplyActivity.class);
                startActivity(intent1);
                break;
            case R.id.rl_right:
                if (state != 1) {
                    Log.e( "onClick: ","为成为志愿者" );
                    Intent intent5 = new Intent(context, VolunteerApplyActivity.class);
                    startActivity(intent5);
                    return;
                }
                Intent intent3 = new Intent(getContext(), TeamListActivity.class);
                startActivity(intent3);
                break;
            case R.id.rl_right_1:       //查看所属团队
                if (roleState == 1 && teamState == 2) {     //志愿者
                    Intent intent5 = new Intent(getContext(), MyTeamActivity.class);
                    intent5.putExtra("type", 0);
                    startActivity(intent5);
                } else if (roleState == 2) {     //志愿者队长
                    checkTeam();
                } else if(teamState ==1) {      //团队申请中
                    Toast.makeText(context, "您的申请正在处理中", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent5 = new Intent(getContext(), TeamListActivity.class);
                    //intent5.putExtra("organ_id", organ_id + "");
                    startActivity(intent5);
                }
                break;
        }
    }

    @Override
    public void OnItemClick(View view) {        //点击公告
        int position = notice_list.getChildLayoutPosition(view);
        Intent intent = new Intent(getContext(), NoticeActivity.class);
        intent.putExtra("title", noticeBeans.get(position).getTitle());
        intent.putExtra("content", noticeBeans.get(position).getContent());
        intent.putExtra("time", noticeBeans.get(position).getCreateTime());
        startActivity(intent);
    }

    @Override
    public void onItemClick(View view) {        //点击任务
        int position = action_list.getChildLayoutPosition(view);
        if (list.get(position).getType() == 1) {
            //个人任务详情
            Intent intent = new Intent(getContext(), MissionDetailActivity.class);
            intent.putExtra("mission", list.get(position));
            intent.putExtra("id", list.get(position).getId());
            startActivity(intent);
        } else if (list.get(position).getType() == 2) {
            //团队任务详情
            Intent intent = new Intent(getContext(), TeamMissionDetailActivity.class);
            intent.putExtra("mission", list.get(position));
            intent.putExtra("id", list.get(position).getId());
            startActivity(intent);
        }
    }

    @Override
    public void onButtonClick(View view, int position) {

    }

    private static int roleState = -1, teamState = -1;
    private int state = -1;
    public static int LEADER = 0;
    public static int ORGAN_ID = 0;

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
                            Acache.get(getContext()).put("is_vol", "true");
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                roleState = json.getInt("roleState");
                                LEADER = json.getInt("roleState");
                                teamState = json.getInt("teamState");
                                state = json.getInt("state");
                                ORGAN_ID = json.getInt("organId");
                                if (!json.isNull("id")) {
                                    id = json.getString("id");
                                    VolunteerActivity.VOL_ID = json.getInt("id");
                                }
                                Acache.get(getContext()).put("volunteer", json);
                                if (json.getInt("state") == 1) {
                                    rl_left_1.setVisibility(View.VISIBLE);
                                    rl_left.setVisibility(View.INVISIBLE);
                                    tv_organ.setText(json.getString("organName"));
                                }
                                if (json.getInt("teamState") == 2) {
                                    rl_right_1.setVisibility(View.VISIBLE);
                                    rl_right.setVisibility(View.INVISIBLE);
                                    tv_team.setText(json.getString("volTeam"));
                                } else {
                                    rl_right_1.setVisibility(View.INVISIBLE);
                                    rl_right.setVisibility(View.VISIBLE);
                                }
                            } else if (json.getInt("status") == 201) {
                                Acache.get(getContext()).put("is_vol", "false");
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

    public void checkTeam() {
        OkGo.<String>get(Urls.getInstance().TEAM_TITLE)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("我的团队", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                if (json.getInt("state") == 1) {
                                    Intent intent3 = new Intent(getContext(), MyTeamActivity.class);
                                    intent3.putExtra("type", 1);
                                    startActivity(intent3);

                                } else {
                                    Intent intent = new Intent(getContext(), CreatePartyActivity.class);
                                    startActivity(intent);
                                }
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    @Override
    public void OnBannerClick(int position) {

    }
}
