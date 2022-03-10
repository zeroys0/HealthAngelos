package net.leelink.healthangelos.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.loader.ImageLoader;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.CommentAdapter;
import net.leelink.healthangelos.adapter.OnContactListener;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.WorkCommentBean;
import net.leelink.healthangelos.util.Urls;
import net.leelink.healthangelos.view.CircleImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WorkDetailActivity extends BaseActivity implements View.OnClickListener, OnContactListener {
    private RelativeLayout rl_back;
    private Context context;
    private CommentAdapter commentAdapter;
    private RecyclerView comment_list;
    private Banner banner;
    private TextView tv_delete, tv_name, tv_time, tv_thumb_count, tv_content, tv_work_name, tv_comment, tv_ok;
    private int id;
    private CircleImageView img_head;
    private ImageView img_zan;
    private List<String> banner_list = new ArrayList<>();
    private LinearLayout ll_edit;
    EditText ed_comment;
    private List<WorkCommentBean> commentBeans;
    private boolean thumb = false;
    public static final int DELETE_COMMENT = 1;
    public static final int DELETE_WORK = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_work_detail);
        context = this;
        createProgressBar(context);
        init();
        initData();
        initList();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        comment_list = findViewById(R.id.comment_list);
        banner = findViewById(R.id.banner);

        tv_delete = findViewById(R.id.tv_delete);
        tv_delete.setOnClickListener(this);
        id = getIntent().getIntExtra("work_id", 0);
        img_head = findViewById(R.id.img_head);
        tv_name = findViewById(R.id.tv_name);
        tv_time = findViewById(R.id.tv_time);
        tv_thumb_count = findViewById(R.id.tv_thumb_count);
        img_zan = findViewById(R.id.img_zan);
        img_zan.setOnClickListener(this);
        tv_content = findViewById(R.id.tv_content);
        tv_work_name = findViewById(R.id.tv_work_name);
        tv_comment = findViewById(R.id.tv_comment);
        tv_comment.setOnClickListener(this);
        ll_edit = findViewById(R.id.ll_edit);
        tv_ok = findViewById(R.id.tv_ok);


    }

    public void initData() {
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().PRODUCTION_LIST + "/" + id)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("根据id查询作品详情", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                if (!json.isNull("elderlyImgName")) {
                                    Glide.with(context).load(Urls.getInstance().IMG_URL + json.getString("elderlyImgName")).into(img_head);
                                }
                                tv_name.setText(json.getString("elderlyName"));
                                tv_time.setText(json.getString("uploadTime"));
                                tv_thumb_count.setText(json.getString("thumbUpNumber"));
                                if (json.getInt("thumbUp") == 0) {
                                    img_zan.setImageResource(R.drawable.img_weidianzan);
                                } else {
                                    img_zan.setImageResource(R.drawable.img_dianzan);
                                }
                                if (!json.isNull("picture")) {
                                    String[] arr = json.getString("picture").split(",");
                                    List<String> img_list = Arrays.asList(arr);
                                    for (String imgPath : img_list) {
                                        banner_list.add(Urls.getInstance().IMG_URL + "/img/" + imgPath);
                                    }
                                }
                                tv_work_name.setText(json.getString("actName"));
                                //banner.setBannerTitles(bannertitles);
                                banner.setIndicatorGravity(BannerConfig.CENTER);
                                banner.setImageLoader(new ImageLoader() {
                                    @Override
                                    public void displayImage(Context context, Object path, ImageView imageView) {
                                        //Glide 加载图片简单用法
                                        Glide.with(context).load(path).into(imageView);
                                    }
                                });
                                banner.setImages(banner_list);
                                banner.isAutoPlay(true);
                                banner.setDelayTime(5000);
                                banner.start();
                                tv_content.setText(json.getString("introduce"));
                                if (json.getInt("thumbUp") == 0) {
                                    thumb = false;
                                } else {
                                    thumb = true;
                                }
                                if (!json.getString("elderlyId").equals(MyApplication.userInfo.getOlderlyId())) {
                                    tv_delete.setVisibility(View.INVISIBLE);
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

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        stopProgressBar();
                        Toast.makeText(context, "系统繁忙,请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void initList() {

        showProgressBar();
        OkGo.<String>get(Urls.getInstance().REVIEW)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("pageNum", 1)
                .params("pageSize", 10)
                .params("productionId", id)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("评论列表", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                JSONArray jsonArray = json.getJSONArray("list");
                                Gson gson = new Gson();
                                commentBeans = gson.fromJson(jsonArray.toString(), new TypeToken<List<WorkCommentBean>>() {
                                }.getType());
                                commentAdapter = new CommentAdapter(context, commentBeans, WorkDetailActivity.this);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                                comment_list.setAdapter(commentAdapter);
                                comment_list.setLayoutManager(layoutManager);
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
                        Toast.makeText(context, "系统繁忙,请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_delete:
                showpup(DELETE_WORK, id);
                break;
            case R.id.tv_comment:
                showComment("", 0);
                ed_comment.requestFocus();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(ed_comment, InputMethodManager.SHOW_IMPLICIT);
                break;
            case R.id.img_zan:
                thumbUp();
                break;

        }
    }

    public void review(String comment,int reply_id) {
        HttpParams httpParams = new HttpParams();
        httpParams.put("productionId", id);
        httpParams.put("commentContent", comment);
        if(reply_id !=0) {
            httpParams.put("replyId", reply_id);
        }
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().REVIEW)
                .tag(this)
                .headers("token", MyApplication.token)
                .params(httpParams)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("提交评论", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "评论完成", Toast.LENGTH_SHORT).show();
                                initList();
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
                        Toast.makeText(context, "系统繁忙,请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void review(String comment) {
        review(comment,0);
    }

    public void thumbUp() {
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().THUMPUP + "/" + id)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("点赞", json.toString());
                            if (json.getInt("status") == 200) {
                                if (thumb) {
                                    Toast.makeText(context, "已取消点赞", Toast.LENGTH_SHORT).show();
                                    img_zan.setImageResource(R.drawable.img_weidianzan);
                                    int i = Integer.valueOf(tv_thumb_count.getText().toString());
                                    tv_thumb_count.setText(String.valueOf(i - 1));
                                    thumb = false;
                                } else {
                                    Toast.makeText(context, "点赞成功", Toast.LENGTH_SHORT).show();
                                    img_zan.setImageResource(R.drawable.img_dianzan);
                                    int i = Integer.valueOf(tv_thumb_count.getText().toString());
                                    tv_thumb_count.setText(String.valueOf(i + 1));
                                    thumb = true;
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

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        stopProgressBar();
                        Toast.makeText(context, "系统繁忙,请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void delete() {

        showProgressBar();
        OkGo.<String>delete(Urls.getInstance().PRODUCTION_LIST + "/" + id)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("删除作品", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                                finish();

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
                        Toast.makeText(context, "系统繁忙,请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void deleteComment(int id) {

        showProgressBar();
        OkGo.<String>delete(Urls.getInstance().REVIEW + "/" + id)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("删除评论", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                                initList();
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
                        Toast.makeText(context, "系统繁忙,请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @SuppressLint("WrongConstant")
    public void showpup(int type, int id) {
        View popview = LayoutInflater.from(WorkDetailActivity.this).inflate(R.layout.popu_save, null);
        PopupWindow popuPhoneW = new PopupWindow(popview,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView tv_title = popview.findViewById(R.id.tv_title);
        tv_title.setText("删除的信息无法找回是否进行删除");
        TextView tv_cancel = popview.findViewById(R.id.tv_cancel);
        tv_cancel.setText("我再想想");
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popuPhoneW.dismiss();
            }
        });
        TextView tv_confirm = popview.findViewById(R.id.tv_confirm);
        tv_confirm.setText("确认删除");
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popuPhoneW.dismiss();
                if (type == DELETE_WORK) {
                    delete();
                } else if (type == DELETE_COMMENT) {
                    deleteComment(id);
                }
            }
        });
        popuPhoneW.setFocusable(false);
        popuPhoneW.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popuPhoneW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popuPhoneW.setOutsideTouchable(false);
        popuPhoneW.setBackgroundDrawable(new BitmapDrawable());
        popuPhoneW.setOnDismissListener(new WorkDetailActivity.poponDismissListener());
        popuPhoneW.showAtLocation(rl_back, Gravity.CENTER, 0, 0);
        backgroundAlpha(0.5f);
    }


    @SuppressLint("WrongConstant")
    public void showComment(String name, int reply_id) {
        View popview = LayoutInflater.from(WorkDetailActivity.this).inflate(R.layout.dialog_input, null);
        ed_comment = popview.findViewById(R.id.ed_comment);

        TextView tv_ok = popview.findViewById(R.id.tv_ok);
        tv_ok.setText("发送");
        TextView tv_reply = popview.findViewById(R.id.tv_reply);
        if (!name.equals("")) {
            tv_reply.setText("回复 " + name + ":");
        }
        final PopupWindow popuPhoneW = new PopupWindow(popview,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popuPhoneW.setFocusable(true);
        popuPhoneW.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popuPhoneW.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popuPhoneW.setOutsideTouchable(true);
        popuPhoneW.setBackgroundDrawable(new BitmapDrawable());
        popuPhoneW.setOnDismissListener(new WorkDetailActivity.poponDismissListener());
        popuPhoneW.showAtLocation(rl_back, Gravity.BOTTOM, 0, 0);
        backgroundAlpha(0.5f);
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popuPhoneW.dismiss();
                if (!name.equals("")) {
                    review(ed_comment.getText().toString().trim(),reply_id);
                } else {
                    review(ed_comment.getText().toString().trim());
                }
            }
        });
    }

    @Override
    public void OnItemClick(View v) {

    }

    @Override
    public void OnEditClick(View v, int position) {
        showComment(commentBeans.get(position).getElderlyName(), commentBeans.get(position).getId());
    }

    @Override
    public void OnDeleteClick(View v, int position) {
        showpup(DELETE_COMMENT, commentBeans.get(position).getId());
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
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        if (bgAlpha == 1) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        getWindow().setAttributes(lp);
    }
}
