package net.leelink.healthangelos.reform;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ocr.ui.camera.CameraActivity;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.pattonsoft.pattonutil1_0.util.SPUtils;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.ChooseAddressActivity;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.bean.BindBean;
import net.leelink.healthangelos.city.FileUtil;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import androidx.annotation.Nullable;

public class TransFormApplyAvtivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private RelativeLayout rl_back, rl_his, rl_people, rl_reform_history,rl_home_address,rl_address;
    private TextView tv_civil, tv_name, tv_number, tv_telephone,tv_home_address,tv_address,tv_state;
    private Button btn_confirm;
    private JSONObject householdAddress,currentAddress;
    private ImageView img_card_front,img_card_back;
    private static final int REQUEST_CODE_CAMERA = 102;
    private static final int FRONT = 201;
    private static final int BACK = 202;
    private String idNumberPositive,idNumberReverse;
    private int committeeId;
    private int state = 0;
    private int recordId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String fontSize = (String) SPUtils.get(this, "font", "");
        if (fontSize.equals("1.3")) {
            setTheme(R.style.theme_large);
        } else {
            setTheme(R.style.theme_standard);
        }
        setContentView(R.layout.activity_trans_form_apply_avtivity);
        init();
        context = this;
        createProgressBar(context);
        initData();
    }

    public void init() {
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        rl_his = findViewById(R.id.rl_his);
        rl_his.setOnClickListener(this);
        rl_reform_history = findViewById(R.id.rl_reform_history);
        rl_reform_history.setOnClickListener(this);
        rl_people = findViewById(R.id.rl_people);
        rl_people.setOnClickListener(this);
        tv_civil = findViewById(R.id.tv_civil);
        tv_name = findViewById(R.id.tv_name);
        tv_number = findViewById(R.id.tv_number);
        tv_telephone = findViewById(R.id.tv_telephone);
        rl_home_address = findViewById(R.id.rl_home_address);
        rl_home_address.setOnClickListener(this);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
        tv_home_address = findViewById(R.id.tv_home_address);
        tv_address = findViewById(R.id.tv_address);
        rl_address = findViewById(R.id.rl_address);
        rl_address.setOnClickListener(this);
        img_card_front = findViewById(R.id.img_card_front);
        img_card_front.setOnClickListener(this);
        img_card_back = findViewById(R.id.img_card_back);
        img_card_back.setOnClickListener(this);
        tv_state = findViewById(R.id.tv_state);
        state  = getIntent().getIntExtra("state",0);
        if(state ==1) {
            tv_state.setText("审核中");
            btn_confirm.setText("撤销申请");
            initView();
        }
    }

    public void initData() {
        showProgressBar();
        OkGo.<String>get(Urls.getInstance().INFO)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("个人中心", json.toString());
                            if (json.getInt("status") == 200) {
                                json = json.getJSONObject("data");
                                tv_name.setText(json.getString("name"));
                                JSONObject jsonObject = json.getJSONObject("elderlyUserInfo");
                                tv_number.setText(jsonObject.getString("idCard"));
                                tv_telephone.setText(jsonObject.getString("telephone"));

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
                    }
                });
//        JSONObject jsonObject = Acache.get(context).getAsJSONObject("userBean");
//        try {
//            tv_name.setText(jsonObject.getString("name"));
//            JSONObject json = jsonObject.getJSONObject("elderlyUserInfo");
//            tv_number.setText(json.getString("idCard"));
//            tv_telephone.setText(json.getString("telephone"));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    public void initView(){
        HttpParams httpParams = new HttpParams();
        httpParams.put("pageNum",1);
        httpParams.put("pageSize",1);
        OkGo.<String>get(Urls.getInstance().CIVILL_RECORD)
                .tag(this)
                .params(httpParams)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询绑定记录", json.toString());
                            if (json.getInt("status") == 200) {
                                Gson gson= new Gson();
                                json  = json.getJSONObject("data");
                                JSONArray jsonArray = json.getJSONArray("list");
                                List<BindBean> beans = gson.fromJson(jsonArray.toString(),new TypeToken<List<BindBean>>(){}.getType());
                                tv_name.setText(beans.get(0).getElderlyName());
                                tv_number.setText(beans.get(0).getIdNumber());
                                tv_telephone.setText(beans.get(0).getTelephone());
                                tv_civil.setText(beans.get(0).getCommitteeName());
                                JSONObject jsonObject = new JSONObject(beans.get(0).getHouseholdAddress());
                                tv_home_address.setText(jsonObject.getString("fullAddress"));
                                jsonObject = new JSONObject(beans.get(0).getCurrentAddress());
                                tv_address.setText(jsonObject.getString("fullAddress"));
                                recordId =beans.get(0).getRecordId();
                                Glide.with(context).load(Urls.getInstance().IMG_URL+beans.get(0).getIdNumberPositive()).into(img_card_front);
                                Glide.with(context).load(Urls.getInstance().IMG_URL+beans.get(0).getIdNumberReverse()).into(img_card_back);
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
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_his:
                Intent intent = new Intent(context, BindHistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_people:
                Intent intent1 = new Intent(context, CivilListActivity.class);
                startActivityForResult(intent1, 7);
                break;
            case R.id.rl_reform_history:
                Intent intent2 = new Intent(context, ReformHistroyActivity.class);
                startActivity(intent2);
                break;
            case R.id.rl_home_address:
                Intent intent3 = new Intent(context, ChooseAddressActivity.class);
                startActivityForResult(intent3,11);
                break;
            case R.id.btn_confirm:
                if(state ==1) {
                    cancel();
                }else {
                    apply();
                }
                break;
            case R.id.rl_address:
                Intent intent4 = new Intent(context,ChooseAddressActivity.class);
                startActivityForResult(intent4,13);
                break;

            case R.id.img_card_front:   //身份证正面
                // 身份证正面拍照
                Intent intent5 = new Intent(context, CameraActivity.class);
                intent5.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent5.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_FRONT);
                startActivityForResult(intent5, REQUEST_CODE_CAMERA);
                break;
            case R.id.img_card_back:    //身份证背面
                Intent intent6 = new Intent(context, CameraActivity.class);
                intent6.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent6.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_BACK);
                startActivityForResult(intent6, REQUEST_CODE_CAMERA);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                String contentType = data.getStringExtra(CameraActivity.KEY_CONTENT_TYPE);
                String filePath = FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath();
                if (!TextUtils.isEmpty(contentType)) {
                    if (CameraActivity.CONTENT_TYPE_ID_CARD_FRONT.equals(contentType)) {

                        File file = new File(filePath);
                        if(file.exists()){
                            Bitmap bm = BitmapFactory.decodeFile(filePath);
                            img_card_front.setImageBitmap(bm);
                            getPath(file,FRONT);
                        }
                    } else if (CameraActivity.CONTENT_TYPE_ID_CARD_BACK.equals(contentType)) {

                        File file = new File(filePath);
                        if(file.exists()){
                            Bitmap bm = BitmapFactory.decodeFile(filePath);
                            img_card_back.setImageBitmap(bm);
                            getPath(file,BACK);
                        }
                    }
                }
            }
        }
        if (data != null) {
            if (requestCode == 7) {
                tv_civil.setText(data.getStringExtra("name"));
                committeeId = data.getIntExtra("id",1);
            }
            if(requestCode ==11){
                try {
                    householdAddress = new JSONObject(data.getStringExtra("json"));
                    tv_home_address.setText(householdAddress.getString("fullAddress"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(requestCode ==13) {
                try {
                    currentAddress = new JSONObject(data.getStringExtra("json"));
                    tv_address.setText(currentAddress.getString("fullAddress"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public void apply(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("committeeId",committeeId);
            jsonObject.put("currentAddress",currentAddress.toString());
            jsonObject.put("elderlyId",MyApplication.userInfo.getOlderlyId());
            jsonObject.put("elderlyName",tv_name.getText().toString());
            jsonObject.put("householdAddress",householdAddress.toString());
            jsonObject.put("idNumber",tv_number.getText().toString());
            jsonObject.put("idNumberPositive",idNumberPositive);
            jsonObject.put("idNumberReverse",idNumberReverse);
            jsonObject.put("telephone",tv_telephone.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.<String>post(Urls.getInstance().CIVILL)
                .tag(this)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("申请绑定民政单位", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "提交申请成功", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    public void cancel(){
        OkGo.<String>post(Urls.getInstance().CANCEL+"/"+recordId)
                .tag(this)
                .headers("token",MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("撤销申请民政单位", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "撤销申请成功", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    public void getPath(File file,int CARD){
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().PHOTO)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("multipartFile",file)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取地址", json.toString());
                            if (json.getInt("status") == 200) {
                                if(CARD ==FRONT) {
                                    idNumberPositive = json.getString("data");
                                }
                                if(CARD ==BACK) {
                                    idNumberReverse = json.getString("data");
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
                    }
                });
    }
}
