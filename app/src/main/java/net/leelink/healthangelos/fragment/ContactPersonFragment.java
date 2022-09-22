package net.leelink.healthangelos.fragment;

import android.content.Context;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.ContactAdapter;
import net.leelink.healthangelos.adapter.OnContactListener;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.Contact;
import net.leelink.healthangelos.bean.ContactBean;
import net.leelink.healthangelos.util.Urls;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ContactPersonFragment extends  BaseFragment implements OnContactListener {
    RecyclerView contact_list;
    ContactAdapter contactAdapter;
    ProgressBar mProgressBar;
    List<ContactBean> list;

    @Override
    public void handleCallBack(Message msg) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        createProgressBar(getContext());
        init(view);
        initData();
        EventBus.getDefault().register(this);
        return  view;
    }

    public void init(View view){
        contact_list = view.findViewById(R.id.contact_list);

    }

    public void initData(){
        mProgressBar.setVisibility(View.VISIBLE);
        OkGo.<String>get(Urls.getInstance().URGENTPEOPLE)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("imei",MyApplication.userInfo.getJwotchImei())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                      mProgressBar.setVisibility(View.GONE);
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取紧急联系人", json.toString());
                            if (json.getInt("status") == 200) {
                                Gson gson = new Gson();
                                JSONArray jsonArray = json.getJSONArray("data");
                                list = gson.fromJson(jsonArray.toString(),new TypeToken<List<ContactBean>>(){}.getType());
                                contactAdapter = new ContactAdapter(list,getContext(),ContactPersonFragment.this);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
                                contact_list.setLayoutManager(layoutManager);
                                contact_list.setAdapter(contactAdapter);
                            } else if (json.getInt("status") == 505) {
                                reLogin(getContext());
                            }  else {
                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Contact event) {
       initData();
    }

    @Override
    public void OnItemClick(View v) {

    }


    //编辑联系人
    @Override
    public void OnEditClick(View v, final int position) {
        backgroundAlpha(0.5f);
        View popView = getLayoutInflater().inflate(R.layout.popu_edit_phone, null);
        final EditText ed_name = popView.findViewById(R.id.ed_name);
        final EditText ed_phone = popView.findViewById(R.id.ed_phone);
        Button btn_confirm = popView.findViewById(R.id.btn_confirm);
        ed_name.setText(list.get(position).getUname());
        ed_phone.setText(list.get(position).getPhone());

        final PopupWindow pop = new PopupWindow(popView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pop.setContentView(popView);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOnDismissListener(new ContactPersonFragment.poponDismissListener());

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.get(position).getUname().equals(ed_name.getText().toString()) && list.get(position).getPhone().equals(ed_phone.getText().toString())) {
                    Toast.makeText(getContext(), "请不要输入相同数据", Toast.LENGTH_SHORT).show();
                } else {
                    edit(position,ed_name.getText().toString().trim(),ed_phone.getText().toString().trim());
                    pop.dismiss();
                }

            }
        });
        pop.showAtLocation(contact_list, Gravity.CENTER, 0, 0);



    }

   public void edit(int position,String name,String phone){
       JSONObject jsonObject = new JSONObject();
       try {
           jsonObject.put("imei",MyApplication.userInfo.getJwotchImei());
           jsonObject.put("phone",phone);
           jsonObject.put("uid",list.get(position).getUid());
           jsonObject.put("uname",name);
       } catch (JSONException e) {
           e.printStackTrace();
       }

       mProgressBar.setVisibility(View.VISIBLE);
       OkGo.<String>post(Urls.getInstance().UPDATEURGENTPEOPLE)
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
                           Log.d("编辑紧急联系人", json.toString());
                           if (json.getInt("status") == 200) {

                               Toast.makeText(getContext(), "编辑成功", Toast.LENGTH_LONG).show();
                               initData();
                           } else if (json.getInt("status") == 505) {
                               reLogin(getContext());
                           }  else {
                               Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                           }
                       } catch (JSONException e) {
                           e.printStackTrace();
                       }
                   }
               });
   }


    //删除联系人
    @Override
    public void OnDeleteClick(View v, final int position) {
        mProgressBar.setVisibility(View.VISIBLE);
        OkGo.<String>delete(Urls.getInstance().URGENTPEOPLE)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("imei",MyApplication.userInfo.getJwotchImei())
                .params("uid",list.get(position).getUid())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        mProgressBar.setVisibility(View.GONE);
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("删除紧急联系人", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(getContext(), "删除成功", Toast.LENGTH_LONG).show();
                                list.remove(position);
                                contactAdapter.notifyDataSetChanged();
                            } else if (json.getInt("status") == 505) {
                                reLogin(getContext());
                            }  else {
                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
