package net.leelink.healthangelos.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.AddFamilyActivity;
import net.leelink.healthangelos.activity.ContactPersonActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FamilyFragment extends BaseFragment implements View.OnClickListener {
    RelativeLayout rl_1, rl_2, rl_3, rl_4, rl_empty1, rl_empty2, rl_empty3, rl_empty4;
    TextView tv_name1, tv_name2, tv_name3, tv_name4, tv_phone1, tv_phone2, tv_phone3, tv_phone4;
    JSONArray jsonArray;
    ProgressBar mProgressBar;
    private String imei;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_family, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        createProgressBar(getContext());
        init(view);
        initView();
        return view;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ContactPersonActivity) {
            ContactPersonActivity activity = (ContactPersonActivity) context;
            imei = activity.getImei(); // 替换为从 Activity 获取数据的适当方法
        }
    }

    public void init(View v) {
        rl_1 = v.findViewById(R.id.rl_1);
        rl_1.setOnClickListener(this);
        rl_2 = v.findViewById(R.id.rl_2);
        rl_2.setOnClickListener(this);
        rl_3 = v.findViewById(R.id.rl_3);
        rl_3.setOnClickListener(this);
        rl_4 = v.findViewById(R.id.rl_4);
        rl_4.setOnClickListener(this);
        rl_empty1 = v.findViewById(R.id.rl_empty1);
        rl_empty1.setOnClickListener(this);
        rl_empty1.setVisibility(View.VISIBLE);
        rl_empty2 = v.findViewById(R.id.rl_empty2);
        rl_empty2.setOnClickListener(this);
        rl_empty3 = v.findViewById(R.id.rl_empty3);
        rl_empty3.setOnClickListener(this);
        rl_empty4 = v.findViewById(R.id.rl_empty4);
        rl_empty4.setOnClickListener(this);
        tv_name1 = v.findViewById(R.id.tv_name1);
        tv_name2 = v.findViewById(R.id.tv_name2);
        tv_name3 = v.findViewById(R.id.tv_name3);
        tv_name4 = v.findViewById(R.id.tv_name4);
        tv_phone1 = v.findViewById(R.id.tv_phone1);
        tv_phone2 = v.findViewById(R.id.tv_phone2);
        tv_phone3 = v.findViewById(R.id.tv_phone3);
        tv_phone4 = v.findViewById(R.id.tv_phone4);

    }

    public void initView() {
        mProgressBar.setVisibility(View.VISIBLE);
        HttpParams httpParams = new HttpParams();
        if(imei!=null) {
            httpParams.put("imei", imei);
        } else {
            httpParams.put("imei", MyApplication.userInfo.getJwotchImei());
        }
        OkGo.<String>get(Urls.getInstance().RELATIVECONTACTLIST)
                .tag(this)
                .headers("token", MyApplication.token)
                .params(httpParams)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        mProgressBar.setVisibility(View.GONE);
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取亲人号码", json.toString());
                            if (json.getInt("status") == 200) {
                                jsonArray = json.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    if (!jsonObject.isNull("ContactString")) {
                                        switch (jsonObject.getInt("Position")) {
                                            case 1:
                                                rl_empty1.setVisibility(View.GONE);
                                                tv_name1.setText(jsonObject.getString("ContactString"));
                                                tv_phone1.setText(jsonObject.getString("PhoneNumber"));
                                                break;
                                            case 2:
                                                rl_empty2.setVisibility(View.GONE);
                                                tv_name2.setText(jsonObject.getString("ContactString"));
                                                tv_phone2.setText(jsonObject.getString("PhoneNumber"));
                                                break;
                                            case 3:
                                                rl_empty3.setVisibility(View.GONE);
                                                tv_name3.setText(jsonObject.getString("ContactString"));
                                                tv_phone3.setText(jsonObject.getString("PhoneNumber"));
                                                break;
                                            case 4:
                                                rl_empty4.setVisibility(View.GONE);
                                                tv_name4.setText(jsonObject.getString("ContactString"));
                                                tv_phone4.setText(jsonObject.getString("PhoneNumber"));
                                                break;
                                        }
                                    }
                                }
                            } else if (json.getInt("status") == 505) {
                                reLogin(getContext());
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
            case R.id.rl_1:
                backgroundAlpha(0.5f);
                showPopup(0);
                break;
            case R.id.rl_2:
                backgroundAlpha(0.5f);
                showPopup(1);
                break;
            case R.id.rl_3:
                backgroundAlpha(0.5f);
                showPopup(2);
                break;
            case R.id.rl_4:
                backgroundAlpha(0.5f);
                showPopup(3);
                break;
            case R.id.rl_empty1:
                Intent intent = new Intent(getContext(), AddFamilyActivity.class);
                intent.putExtra("position", 1);
                startActivityForResult(intent, 5);
                break;
            case R.id.rl_empty2:
                Intent intent1 = new Intent(getContext(), AddFamilyActivity.class);
                intent1.putExtra("position", 2);
                startActivityForResult(intent1, 5);
                break;
            case R.id.rl_empty3:
                Intent intent2 = new Intent(getContext(), AddFamilyActivity.class);
                intent2.putExtra("position", 3);
                startActivityForResult(intent2, 5);
                break;
            case R.id.rl_empty4:
                Intent intent3 = new Intent(getContext(), AddFamilyActivity.class);
                intent3.putExtra("position", 4);
                startActivityForResult(intent3, 5);
                break;
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5 && resultCode == 0) {
            initView();
        }
    }


    public void showPopup(final int position) {
        final View popView = getLayoutInflater().inflate(R.layout.popu_botton_option, null);
        Button btn_edit = popView.findViewById(R.id.btn_edit);
        Button btn_delete = popView.findViewById(R.id.btn_delete);
        Button btn_cancel = popView.findViewById(R.id.btn_cancel);


        final PopupWindow pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOnDismissListener(new FamilyFragment.poponDismissListener());
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    editPopup(position);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pop.dismiss();
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(position);
                pop.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
            }
        });
        pop.showAtLocation(rl_1, Gravity.BOTTOM, 0, 0);
    }

    public void editPopup(final int position) throws JSONException {
        View popView = getLayoutInflater().inflate(R.layout.popu_edit_phone, null);
        final EditText ed_name = popView.findViewById(R.id.ed_name);
        final EditText ed_phone = popView.findViewById(R.id.ed_phone);
        Button btn_confirm = popView.findViewById(R.id.btn_confirm);
        final JSONObject jsonObject = jsonArray.getJSONObject(position);
        ed_name.setText(jsonObject.getString("ContactString"));
        ed_phone.setText(jsonObject.getString("PhoneNumber"));

        final PopupWindow pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOnDismissListener(new FamilyFragment.poponDismissListener());

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (jsonObject.getString("ContactString").equals(ed_name.getText().toString()) && jsonObject.getString("PhoneNumber").equals(ed_phone.getText().toString())) {
                        Toast.makeText(getContext(), "请不要输入相同数据", Toast.LENGTH_SHORT).show();
                    } else {
                        edit(position, ed_name.getText().toString().trim(), ed_phone.getText().toString().trim());
                        pop.dismiss();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        pop.showAtLocation(rl_1, Gravity.CENTER, 0, 0);
    }

    public void edit(int position, String name, String phone) {
        JSONObject jsonObject = new JSONObject();
        position = position + 1;
        try {
            if(imei!=null) {
                jsonObject.put("imei", imei);
            } else {
                jsonObject.put("imei", MyApplication.userInfo.getJwotchImei());
            }
            jsonObject.put("contact", name);
            jsonObject.put("position", position);
            jsonObject.put("phone", phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mProgressBar.setVisibility(View.VISIBLE);
        OkGo.<String>post(Urls.getInstance().RELATIVE)
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        mProgressBar.setVisibility(View.GONE);
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("编辑号码", json.toString());
                            if (json.getInt("status") == 200) {
                                initView();

                            } else {
                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void delete(final int position) {
        int p = position + 1;
        HttpParams httpParams = new HttpParams();
        if(imei!=null) {
            httpParams.put("imei", imei);
        } else {
            httpParams.put("imei", MyApplication.userInfo.getJwotchImei());
        }
        httpParams.put("position", p);
        mProgressBar.setVisibility(View.VISIBLE);
        OkGo.<String>delete(Urls.getInstance().RELATIVE)
                .tag(this)
                .headers("token", MyApplication.token)
                .params(httpParams)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        mProgressBar.setVisibility(View.GONE);
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("删除号码", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                                switch (p) {
                                    case 1:
                                        rl_empty1.setVisibility(View.VISIBLE);
                                        break;
                                    case 2:
                                        rl_empty2.setVisibility(View.VISIBLE);
                                        break;
                                    case 3:
                                        rl_empty3.setVisibility(View.VISIBLE);
                                        break;
                                    case 4:
                                        rl_empty4.setVisibility(View.VISIBLE);
                                        break;
                                }
                                initView();
                            } else {
                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        if (bgAlpha == 1) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        } else {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        getActivity().getWindow().setAttributes(lp);
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


    @Override
    public void handleCallBack(Message msg) {

    }

    public void createProgressBar(Context context) {

        //整个Activity布局的最终父布局,参见参考资料
        FrameLayout rootFrameLayout = (FrameLayout) getActivity().findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        mProgressBar = new ProgressBar(context);
        mProgressBar.setLayoutParams(layoutParams);
        mProgressBar.setVisibility(View.GONE);
        rootFrameLayout.addView(mProgressBar);
    }
}
