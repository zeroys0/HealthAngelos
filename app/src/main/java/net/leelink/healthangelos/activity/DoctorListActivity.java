package net.leelink.healthangelos.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.DoctorListAdapter;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.adapter.SpinnerAdapter;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.DoctorBean;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static net.leelink.healthangelos.app.MyApplication.getContext;

public class DoctorListActivity extends BaseActivity implements OnOrderListener {
private RelativeLayout rl_back;
private MaterialSpinner spinner1,spinner2,spinner3;
private MaterialSpinner spinner;
private RecyclerView doctor_list;
private DoctorListAdapter doctorListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);
        init();
        initData();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        spinner = findViewById(R.id.spinner);
        spinner.setTextColor(getResources().getColor(R.color.text_grey));
//        spinner.setItems("按评分", "价格从低到高", "价格从高到低");
        List<String> list = new ArrayList<>();
        list.add("按评分");
        list.add("价格从低到高");
        list.add("价格从高到低");
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this,list);

        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
            }
        });
        spinner.setAdapter(spinnerAdapter);
        spinner1 = findViewById(R.id.spinner1);
   //     spinner1.setAdapter(spinnerAdapter);
        spinner2 = findViewById(R.id.spinner2);
    //    spinner2.setAdapter(spinnerAdapter);
        spinner3 = findViewById(R.id.spinner3);
    //    spinner3.setAdapter(spinnerAdapter);
    }

    public void initData(){
        doctor_list = findViewById(R.id.doctor_list);
        OkGo.<String>get(Urls.DOCTOR)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("pageNum",1)
                .params("pageSize",5)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("医生列表", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                JSONArray jsonArray = json.getJSONArray("list");
                                Gson gson = new Gson();
                                final List<DoctorBean> list = gson.fromJson(jsonArray.toString(), new TypeToken<List<DoctorBean>>() {}.getType());
                                doctorListAdapter = new DoctorListAdapter(list,getContext(),DoctorListActivity.this);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
                                doctor_list.setLayoutManager(layoutManager);
                                doctor_list.setAdapter(doctorListAdapter);


                            } else if (json.getInt("status") == 505) {
                                SharedPreferences sp = Objects.requireNonNull(getContext()).getSharedPreferences("sp",0);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.remove("secretKey");
                                editor.remove("telephone");
                                editor.apply();
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }  else {
                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onItemClick(View view) {

    }

    @Override
    public void onButtonClick(View view, int position) {

    }
}
