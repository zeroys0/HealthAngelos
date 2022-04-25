package net.leelink.healthangelos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pattonsoft.pattonutil1_0.util.SPUtils;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.ClassAdapter;
import net.leelink.healthangelos.adapter.OnClassListener;
import net.leelink.healthangelos.app.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChooseClassActivity extends BaseActivity implements OnClassListener {
    private RelativeLayout rl_back;
    private RecyclerView custom_list, inner_list, out_list, orther_list;
    private int[] img_list, img_list1, img_list2, img_list3;
    private List<String> list1 = new ArrayList<>();
    private List<String> list2 = new ArrayList<>();
    private List<String> list3 = new ArrayList<>();
    private List<String> list4 = new ArrayList<>();
    private EditText ed_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String fontSize = (String) SPUtils.get(this, "font", "");
        if (fontSize.equals("1.3")) {
            setTheme(R.style.theme_large);
        } else {
            setTheme(R.style.theme_standard);
        }
        setContentView(R.layout.activity_choose_class);
        init();
        initView();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        custom_list = findViewById(R.id.custom_list);
        inner_list = findViewById(R.id.inner_list);
        out_list = findViewById(R.id.out_list);
        orther_list = findViewById(R.id.orther_list);
        ed_key = findViewById(R.id.ed_key);
        ed_key.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    Intent intent = new Intent(ChooseClassActivity.this, DoctorListActivity.class);
                    intent.putExtra("type", ed_key.getText().toString().trim());
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    public void initView() {
        img_list = new int[]{R.drawable.class31, R.drawable.class1, R.drawable.class2, R.drawable.class3, R.drawable.class4, R.drawable.class5, R.drawable.class6};
        list1.add("全科");
        list1.add("消化内科");
        list1.add("口腔科");
        list1.add("骨科");
        list1.add("泌尿外科");
        list1.add("皮肤科");
        list1.add("耳鼻咽喉科");
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false);
        ClassAdapter classAdapter = new ClassAdapter(this, img_list, list1, this, 0);
        custom_list.setLayoutManager(layoutManager);
        custom_list.setAdapter(classAdapter);

        img_list1 = new int[]{R.drawable.class7, R.drawable.class8, R.drawable.class9, R.drawable.class10, R.drawable.class11, R.drawable.class12, R.drawable.class13, R.drawable.class14, R.drawable.class15};
        list2.add("呼吸内科");
        list2.add("普内科");
        list2.add("感染科/传染科");
        list2.add("心血管科");
        list2.add("神经内科");
        list2.add("肾脏内科");
        list2.add("风湿免疫科");
        list2.add("内分泌科");
        list2.add("血液科");
        RecyclerView.LayoutManager layoutManager1 = new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false);
        ClassAdapter classAdapter1 = new ClassAdapter(this, img_list1, list2, this, 1);
        inner_list.setLayoutManager(layoutManager1);
        inner_list.setAdapter(classAdapter1);

        img_list2 = new int[]{R.drawable.class16, R.drawable.class17, R.drawable.class18, R.drawable.class19, R.drawable.class20, R.drawable.class21};
        list3.add("普外科");
        list3.add("心胸外科");
        list3.add("神经外科");
        list3.add("肝胆胰乳腺");
        list3.add("甲状腺乳腺");
        list3.add("美容整形科");
        RecyclerView.LayoutManager layoutManager2 = new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false);
        ClassAdapter classAdapter2 = new ClassAdapter(this, img_list2, list3, this, 2);
        out_list.setLayoutManager(layoutManager2);
        out_list.setAdapter(classAdapter2);

        img_list3 = new int[]{R.drawable.class22, R.drawable.class23, R.drawable.class24, R.drawable.class25, R.drawable.class26, R.drawable.class27, R.drawable.class28, R.drawable.class30, R.drawable.class29};
        list4.add("眼科");
        list4.add("肿瘤科");
        list4.add("药剂科");
        list4.add("疫苗科");
        list4.add("影像经验科");
        list4.add("精神心理科");
        list4.add("儿科");
        list4.add("妇科");
        list4.add("性病科");
        RecyclerView.LayoutManager layoutManager3 = new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false);
        ClassAdapter classAdapter3 = new ClassAdapter(this, img_list3, list4, this, 3);
        orther_list.setLayoutManager(layoutManager3);
        orther_list.setAdapter(classAdapter3);
    }

    @Override
    public void onItemClick(int position, int type) {
        String department = "";
        switch (type) {
            case 0:
//                if (position == 0) {
//                    department = list1.get(position);
//                } else {
//                    Toast.makeText(this, position + "", Toast.LENGTH_SHORT).show();
//                }
                department = list1.get(position);
                break;
            case 1:
                department = list2.get(position);
                break;
            case 2:
                department = list3.get(position);
                break;
            case 3:
                department = list4.get(position);
                break;
            default:
                break;
        }
        Intent intent = new Intent(this, DoctorListActivity.class);
        //  Toast.makeText(this, department, Toast.LENGTH_SHORT).show();
        intent.putExtra("type", department);
        startActivity(intent);
    }
}
