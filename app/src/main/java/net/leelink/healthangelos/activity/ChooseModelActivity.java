package net.leelink.healthangelos.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.pattonsoft.pattonutil1_0.util.MapUtil;
import com.pattonsoft.pattonutil1_0.views.NoScrollListView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.MonitorLimitsAdapter;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.LimitBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChooseModelActivity extends BaseActivity implements View.OnClickListener {
    ListView listView ;
    List<Map<String, Object>> list;//公共
    List<Map<String, Object>> list2;
    int type = 0;
    LinearLayout ll_private,ll_public;
    Context context;
    TextView tv_1,tv_2;
    View view1,view2;
   RelativeLayout img_add,rl_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_model);
        context = this;
        init();
    }

    public void init(){
        listView = findViewById(R.id.list);
        ll_private = findViewById(R.id.ll_private);
        ll_private.setOnClickListener(this);
        ll_public = findViewById(R.id.ll_public);
        ll_public.setOnClickListener(this);
        tv_1 = findViewById(R.id.tv_1);
        tv_1.setOnClickListener(this);
        tv_2 = findViewById(R.id.tv_2);
        tv_2.setOnClickListener(this);
        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        img_add = findViewById(R.id.img_add);
        img_add.setOnClickListener(this);
        rl_back=  findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
    }

    /**
     * 设置公共模板
     */
    void setPublicModels() {
        list = new ArrayList<>();//模板分类集合
        getPublicModelList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (type == 0) {
            setPublicModels();
        } else {
            setPersonalModels();
        }
    }

    /**
     * 设置个人模板
     */
    void setPersonalModels() {
        list2 = new ArrayList<>();//模板分类集合
        getPrivateModelList();
    }

    /**
     *获取公共模板
     */
    public void getPublicModelList(){
        OkGo.<String>get(Urls.VOICETEMPLATE)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("titleId",1)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取公共模板", json.toString());
                            if (json.getInt("status") == 200) {
                                Map<String, Object> data = new Gson().fromJson(body, new TypeToken<Map<String, Object>>() {}.getType());
                                list = new ArrayList<>();
                                list = (List<Map<String, Object>>)data.get("data");
                                if (list != null) {
                                    //加入打开状态

                                    for (int i = 0; i < list.size(); i++) {
                                        Map<String, Object> map = list.get(i);
                                        map.put("open", 1);
                                        list.set(i, map);
                                    }
                                    listView.setAdapter(new PublicModelAdapter());

                                }

                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     *
     * 获取个人模板
     */
    public void getPrivateModelList(){
        OkGo.<String>get(Urls.VOICETEMPLATE)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("titleId",2)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取个人模板", json.toString());
                            if (json.getInt("status") == 200) {
                                Map<String, Object> data = new Gson().fromJson(body, new TypeToken<Map<String, Object>>() {}.getType());
                                list2 = new ArrayList<>();
                                list2 = (List<Map<String, Object>>) data.get("data");
                                if (list2 != null) {
                                    //加入打开状态

                                    for (int i = 0; i < list2.size(); i++) {
                                        Map<String, Object> map = list2.get(i);
                                        map.put("open", 1);
                                        list2.set(i, map);
                                    }
                                    listView.setAdapter(new PersonalModelAdapter());
                                }

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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_1:
                type = 0;
                tv_1.setTextColor(getResources().getColor(R.color.blue));
                view1.setBackgroundColor(getResources().getColor(R.color.blue));
                tv_2.setTextColor(getResources().getColor(R.color.black));
                view2.setBackgroundColor(getResources().getColor(R.color.white));
                setPublicModels();
                img_add.setVisibility(View.GONE);
                break;
            case R.id.tv_2:
                type = 1;
                tv_2.setTextColor(getResources().getColor(R.color.blue));
                view2.setBackgroundColor(getResources().getColor(R.color.blue));
                tv_1.setTextColor(getResources().getColor(R.color.black));
                view1.setBackgroundColor(getResources().getColor(R.color.white));
                setPersonalModels();
                img_add.setVisibility(View.VISIBLE);
                break;
            case R.id.rl_back:
                finish();
                break;
            case R.id.img_add:  //添加新的模板
                Intent intent = new Intent(this,ModelAddActivity.class);
                startActivity(intent);
                break;
        }
    }

    //公共模板类型列表
    class PublicModelAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_model, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Map<String, Object> map = list.get(position);
            //1模板分类名称
            String name = MapUtil.getString(map, "typeName");
            holder.tvTitle.setText(name);
            //2是否展开
            final int open = MapUtil.getInt(map, "open");
            if (open == 1) {
                holder.listFestival.setVisibility(View.VISIBLE);
                holder.upp.setImageResource(R.drawable.shrink);
            } else {
                holder.listFestival.setVisibility(View.GONE);
                holder.upp.setImageResource(R.drawable.expand);
            }
            //3模板列表
            List<Map<String, Object>> cList = (List<Map<String, Object>>) map.get("contentList");
            holder.listFestival.setAdapter(new PublicModelInfoAdapter(cList));

            holder.upp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int open2 = open == 0 ? 1 : 0;
                    Map<String, Object> map2 = list.get(position);
                    map2.put("open", open2);
                    list.set(position, map2);
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }


        class ViewHolder {

            TextView tvTitle;

            ImageView upp;

            NoScrollListView listFestival;

            ViewHolder(View view) {
                tvTitle = view.findViewById(R.id.tv_title);
                upp = view.findViewById(R.id.upp);
                listFestival = view.findViewById(R.id.list_festival);
            }
        }
    }
    //公共模板列表
    class PublicModelInfoAdapter extends BaseAdapter {
        List<Map<String, Object>> list;

        public PublicModelInfoAdapter(List<Map<String, Object>> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Holder holder;
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.item_model_festival, viewGroup, false);
                holder = new Holder(view);
                view.setTag(holder);

            } else {
                holder = (Holder) view.getTag();
            }
            final Map<String, Object> map = list.get(i);
            //1模板分类名称
            final String content = MapUtil.getString(map, "content");
            final int TemplateId = MapUtil.getInt(map, "templateId");
            if (TemplateId == getIntent().getIntExtra("templateId", 0)) {
                holder.imCheck.setImageResource(R.drawable.check_yes);
            } else {
                holder.imCheck.setImageResource(R.drawable.check_no);
            }
            holder.festival.setText(content);
            holder.llContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //返回 选择的模版
                    Intent intent = new Intent();
                    intent.putExtra("content", content);
                    intent.putExtra("templateId", TemplateId);
                    String title = MapUtil.getString(map, "content");
                    intent.putExtra("title", title);
                    setResult(-1, intent);
                    finish();
                }
            });

            return view;
        }


        class Holder {
            ImageView imCheck;

            TextView festival;

            LinearLayout llContent;

            Holder(View view) {
                imCheck = view.findViewById(R.id.im_check);
                festival = view.findViewById(R.id.festival);
                llContent = view.findViewById(R.id.ll_content);

            }
        }
    }

    //个人模板类型列表
    class PersonalModelAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list2.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_model2, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Map<String, Object> map = list2.get(position);
            //1模板分类名称
            String name = MapUtil.getString(map, "typeName");
            holder.tvTitle.setText(name);
            //2是否展开
            final int open = MapUtil.getInt(map, "open");
            if (open == 1) {
                holder.listFestival.setVisibility(View.VISIBLE);
            } else {
                holder.listFestival.setVisibility(View.GONE);
            }

            //3模板列表
            List<Map<String, Object>> cList = (List<Map<String, Object>>) map.get("contentList");
            holder.listFestival.setAdapter(new PersonalModelInfoAdapter(cList,name));

            holder.upp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View popView = getLayoutInflater().inflate(R.layout.popwindow_model, null);
                    final PopupWindow mPop;
                    mPop = new PopupWindow(view);//新建PW
                    mPop.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                    mPop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                    mPop.setFocusable(true);
                    mPop.setOutsideTouchable(true);
                    mPop.setContentView(popView);
                    mPop.setBackgroundDrawable(new BitmapDrawable());

                    LinearLayout ll1 = (LinearLayout) popView.findViewById(R.id.ll_1);
                    LinearLayout ll2 = (LinearLayout) popView.findViewById(R.id.ll_2);
                    LinearLayout ll3 = (LinearLayout) popView.findViewById(R.id.ll_3);
                    LinearLayout ll4 = (LinearLayout) popView.findViewById(R.id.ll_4);
                    TextView tv1 = (TextView) popView.findViewById(R.id.tv_1);
                    ImageView im1 = (ImageView) popView.findViewById(R.id.im_1);

                    tv1.setText(open == 1 ? "收起" : "展开");
                    im1.setImageResource(open == 1 ? R.drawable.shrink : R.drawable.expand);

                    ll1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int open2 = open == 0 ? 1 : 0;
                            Map<String, Object> map2 = list2.get(position);
                            map2.put("open", open2);
                            list2.set(position, map2);
                            notifyDataSetChanged();
                            mPop.dismiss();
                        }
                    });
                    ll2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int classId = MapUtil.getInt(map, "id");
                            String title = MapUtil.getString(map, "content");
                            int templateId = MapUtil.getInt(map,"templateId");
                            String typeName = MapUtil.getString(map,"typeName");
                            startActivity(new Intent(ChooseModelActivity.this, ModelContentAddActivity.class).putExtra("id", classId).putExtra("content", title).putExtra("templateId",templateId).putExtra("typeName",typeName));
                            mPop.dismiss();
                        }
                    });
                    ll3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int classId = MapUtil.getInt(map, "templateId");
                            String title = MapUtil.getString(map, "typeName");
                            startActivity(new Intent(ChooseModelActivity.this, ModelAddActivity.class).putExtra("type", 1).putExtra("templateId", classId).putExtra("typeName", title));
                            mPop.dismiss();
                        }
                    });
                    ll4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mPop.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("是否删除模版分类以及下面的模版?")
                                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            int classId = MapUtil.getInt(map, "templateId");
                                            deleteModel(classId);
                                        }
                                    })
                                    .setNegativeButton("否", null)
                                    .show();

                        }
                    });

                    mPop.showAsDropDown(holder.upp);


                }
            });

            return convertView;
        }


        class ViewHolder {

            TextView tvTitle;
            ImageView upp;
            NoScrollListView listFestival;

            ViewHolder(View view) {
                tvTitle = view.findViewById(R.id.tv_title);
                upp = view.findViewById(R.id.upp);
                listFestival = view.findViewById(R.id.list_festival);

            }
        }
    }

    //个人公共模板列表
    class PersonalModelInfoAdapter extends BaseAdapter {
        List<Map<String, Object>> list;
        String name;
        public PersonalModelInfoAdapter(List<Map<String, Object>> list,String name) {
            this.list = list;
            this.name = name;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Holder holder;
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.item_model_festival2, viewGroup, false);
                holder = new Holder(view);
                view.setTag(holder);

            } else {
                holder = (Holder) view.getTag();
            }
            final Map<String, Object> map = list.get(i);
            //1模板分类名称
            final String content = MapUtil.getString(map, "content");
            final int TemplateId = MapUtil.getInt(map, "TemplateId");
            holder.festival.setText(content);
            holder.tvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int classId = MapUtil.getInt(map, "id");
                    String title = MapUtil.getString(map, "Title");
                    Intent intent = new Intent(ChooseModelActivity.this, ModelContentAddActivity.class);
                    intent.putExtra("type", 1);
                    intent.putExtra("id", classId);
                    intent.putExtra("title", title);
                    intent.putExtra("content", content);
                    intent.putExtra("templateId", MapUtil.getInt(map, "templateId"));
                    intent.putExtra("typeName",name);
                    startActivity(intent);
                }
            });
            holder.tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("是否删除此模版?")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int templateId = MapUtil.getInt(map, "id");
                                    delete(templateId);
                                }
                            })
                            .setNegativeButton("否", null)
                            .show();
                }
            });
            holder.llContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //返回 选择的模版
                    Intent intent = new Intent();
                    intent.putExtra("content", content);
                    String title = MapUtil.getString(map, "content");
                    intent.putExtra("title", title);
                    intent.putExtra("templateId", TemplateId);
                    setResult(-1, intent);
                    finish();
                }
            });
            return view;
        }


        class Holder {
            ImageView imCheck;
            TextView festival;
            TextView tvEdit;
            TextView tvDelete;
            LinearLayout llContent;

            Holder(View view) {
                imCheck = view.findViewById(R.id.im_check);
                festival = view.findViewById(R.id.festival);
                tvEdit = view.findViewById(R.id.tv_edit);
                tvDelete = view.findViewById(R.id.tv_delete);
                llContent = view.findViewById(R.id.ll_content);
            }
        }
    }

    public void deleteModel(int id){
        OkGo.<String>delete(Urls.VOICETEMPLATE+"/"+id)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("删除模板", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                                setPersonalModels();
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void delete(int id) {
        OkGo.<String>delete(Urls.VOICECONTENT+"/"+id)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("删除模板内容", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                                setPersonalModels();
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
