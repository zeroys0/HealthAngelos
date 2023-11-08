package net.leelink.healthangelos.reform;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bumptech.glide.Glide;
import com.lcw.library.imagepicker.ImagePicker;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.ChooseAddressActivity;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.util.GlideLoader;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class ReformApplyActivity extends BaseActivity {
    private Context context;
    private RelativeLayout rl_back, rl_identity, rl_home_address, rl_house_type;
    private ImageView img_sign1, img_master, img_personal, img_house_certificate, img_five, img_low;
    private TextView tv_state, tv_civil, tv_name, tv_sex, tv_id_number, tv_home_address, tv_identity, tv_house_type;
    private EditText ed_family_size, ed_contact_person, ed_telephone, ed_content;
    private static final int REQUEST_MASTER = 0x01;
    private static final int REQUEST_PERSONAL = 0x02;
    private static final int REQUEST_HOUSE_CERTIFICATE = 0x03;
    private static final int REQUEST_FIVE = 0x04;
    private static final int REQUEST_LOW = 0x05;
    private static final int REQUEST_FEATURE = 0x13;
    private static final int REQUEST_SIGN = 101;
    SharedPreferences sp;
    int idType = -1;
    private JSONObject householdAddress;
    private List<String> list_type = new ArrayList<>();
    private int houseSituation = -1;
    private JSONObject json = new JSONObject();
    private Button btn_submit;
    String homePeopleImg, myPageImg, houseImg, fiveCertificate, lowCertificate, applySignImg;
    private String applyId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reform_apply);
        init();
        context = this;
        createProgressBar(context);
        initData();

    }

    public void init() {
        sp = getSharedPreferences("sp", 0);

        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        img_sign1 = findViewById(R.id.img_sign1);
        img_sign1.setOnClickListener(this);
        rl_identity = findViewById(R.id.rl_identity);
        rl_identity.setOnClickListener(this);
        tv_state = findViewById(R.id.tv_state);
        tv_state.setOnClickListener(this);
        tv_civil = findViewById(R.id.tv_civil);
        tv_civil.setText(sp.getString("committeeName", "社区名称"));
        int sex = sp.getInt("sex", 0);
        ed_family_size = findViewById(R.id.ed_family_size);
        ed_contact_person = findViewById(R.id.ed_contact_person);
        ed_telephone = findViewById(R.id.ed_telephone);
        ed_content = findViewById(R.id.ed_content);
        tv_name = findViewById(R.id.tv_name);
        tv_name.setText(sp.getString("elderlyName", ""));
        tv_sex = findViewById(R.id.tv_sex);
        if (sex == 0) {
            tv_sex.setText("男");
        } else {
            tv_sex.setText("女");
        }
        tv_home_address = findViewById(R.id.tv_home_address);
        tv_house_type = findViewById(R.id.tv_house_type);
        tv_id_number = findViewById(R.id.tv_id_number);
        String id = sp.getString("idNumber", "");
        if (id != null && id.length() == 18) {
            StringBuilder sb = new StringBuilder();
            sb.append(id);
            sb.replace(2, 16, "**************");
            tv_id_number.setText(sb.toString());
        }
        img_master = findViewById(R.id.img_master);
        img_master.setOnClickListener(this);
        img_personal = findViewById(R.id.img_personal);
        img_personal.setOnClickListener(this);
        img_house_certificate = findViewById(R.id.img_house_certificate);
        img_house_certificate.setOnClickListener(this);
        rl_home_address = findViewById(R.id.rl_home_address);
        rl_home_address.setOnClickListener(this);
        rl_house_type = findViewById(R.id.rl_house_type);
        rl_house_type.setOnClickListener(this);
        img_five = findViewById(R.id.img_five);
        img_five.setOnClickListener(this);
        img_low = findViewById(R.id.img_low);
        img_low.setOnClickListener(this);
        tv_identity = findViewById(R.id.tv_identity);
        btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
    }

    public void initData() {
        //查询最新一条适老化改造申请
        OkGo.<String>get(Urls.getInstance().CIVILL_LATEST)
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("查询最新的改造申请", json.toString());
                            if (json.getInt("status") == 200) {
                                if (!json.has("data")) {
                                    return;
                                }
                                json = json.getJSONObject("data");
                                applyId = json.getString("applyId");
                                int state = json.getInt("state");
                                if(state!=5 && state !=6 &&state !=7){
                                    img_master.setClickable(false);
                                    img_personal.setClickable(false);
                                    img_house_certificate.setClickable(false);
                                    img_five.setClickable(false);
                                    img_low.setClickable(false);
                                    img_sign1.setClickable(false);
                                    rl_home_address.setClickable(false);
                                    rl_house_type.setClickable(false);
                                    rl_identity.setClickable(false);
                                    ed_content.setClickable(false);
                                    ed_telephone.setClickable(false);
                                    ed_contact_person.setClickable(false);
                                    ed_family_size.setClickable(false);
                                    btn_submit.setClickable(false);
                                    btn_submit.setBackground(getResources().getDrawable(R.drawable.bg_gray_radius));
                                }

                                householdAddress = new JSONObject(json.getString("currentAddress"));
                                tv_home_address.setText(householdAddress.getString("fullAddress"));
                                if (json.has("homePeopleImg")) {
                                    homePeopleImg = json.getString("homePeopleImg");
                                    Glide.with(context).load(Urls.getInstance().IMG_URL + homePeopleImg).into(img_master);
                                }
                                if (json.has("myPageImg")) {
                                    myPageImg = json.getString("myPageImg");
                                    Glide.with(context).load(Urls.getInstance().IMG_URL + myPageImg).into(img_personal);
                                }
                                if (json.has("houseImg")) {
                                    houseImg = json.getString("houseImg");
                                    Glide.with(context).load(Urls.getInstance().IMG_URL + houseImg).into(img_house_certificate);
                                }
                                if (json.has("fiveCertificate")) {
                                    fiveCertificate = json.getString("fiveCertificate");
                                    Glide.with(context).load(Urls.getInstance().IMG_URL + fiveCertificate).into(img_five);
                                }
                                if (json.has("lowCertificate")) {
                                    lowCertificate = json.getString("lowCertificate");
                                    Glide.with(context).load(Urls.getInstance().IMG_URL + lowCertificate).into(img_low);
                                }
                                houseSituation = json.getInt("houseSituation");
                                if(houseSituation==0){
                                    tv_house_type.setText("非自住");
                                } else if(houseSituation==1){
                                    tv_house_type.setText("自住");
                                }
                                ed_family_size.setText(json.getString("familyNum"));
                                ed_contact_person.setText(json.getString("familyContact"));
                                ed_telephone.setText(json.getString("familyPhone"));
                                idType = json.getInt("idType");
                                if(idType==1){
                                    tv_identity.setText("分散供养");
                                } else if(idType==2){
                                    tv_identity.setText("建档立卡");
                                }
                                ed_content.setText(json.getString("applyFamilyOpinion"));
                                applySignImg = json.getString("applySignImg");
                                Glide.with(context).load(Urls.getInstance().IMG_URL + applySignImg).into(img_sign1);
                            } else if (json.getInt("status") == 505) {

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.img_sign1:
                Intent intent = new Intent(context, SignatureActivity.class);
                startActivityForResult(intent, REQUEST_SIGN);
                break;
            case R.id.rl_identity:
                Intent intent1 = new Intent(context, ChooseFeatureActivity.class);
                startActivityForResult(intent1, REQUEST_FEATURE);
                break;
            case R.id.tv_state:
                //审批进度
                Intent intent2 = new Intent(context, NeoReformProgressActivity.class);
                intent2.putExtra("applyId",applyId);
                startActivity(intent2);
                break;
            case R.id.img_master:
                //户主页面
                getImage(REQUEST_MASTER, imageMasters);
                break;
            case R.id.img_personal:
                //个人页面
                getImage(REQUEST_PERSONAL, imagePersonals);
                break;
            case R.id.rl_home_address:
                //家庭改造住址
                Intent intent3 = new Intent(context, ChooseAddressActivity.class);
                startActivityForResult(intent3, 11);
                break;
            case R.id.img_house_certificate:
                //房产证页
                getImage(REQUEST_HOUSE_CERTIFICATE, imagePaths);
                break;
            case R.id.rl_house_type:
                //住宅情况
                showType();
                break;
            case R.id.img_five:
                //五保证
                getImage(REQUEST_FIVE, imgFives);
                break;
            case R.id.img_low:
                getImage(REQUEST_LOW, imageLows);
                //低保证
                break;
            case R.id.btn_submit:
                apply();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == REQUEST_MASTER && resultCode == RESULT_OK) {
                imageMasters = data.getStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES);
                Glide.with(this).load(imageMasters.get(0)).into(img_master);
                upload(new File(imageMasters.get(0)), REQUEST_MASTER);
            } else if (requestCode == REQUEST_PERSONAL && resultCode == RESULT_OK) {
                imagePersonals = data.getStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES);
                Glide.with(this).load(imagePersonals.get(0)).into(img_personal);
                upload(new File(imagePersonals.get(0)), REQUEST_PERSONAL);
            } else if (requestCode == REQUEST_HOUSE_CERTIFICATE && resultCode == RESULT_OK) {
                imagePaths = data.getStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES);
                Glide.with(this).load(imagePaths.get(0)).into(img_house_certificate);
                upload(new File(imagePaths.get(0)), REQUEST_HOUSE_CERTIFICATE);
            } else if (requestCode == REQUEST_FIVE && resultCode == RESULT_OK) {
                imgFives = data.getStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES);
                Glide.with(this).load(imgFives.get(0)).into(img_five);
                upload(new File(imgFives.get(0)), REQUEST_FIVE);
            } else if (requestCode == REQUEST_LOW && resultCode == RESULT_OK) {
                imageLows = data.getStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES);
                Glide.with(this).load(imageLows.get(0)).into(img_low);
                upload(new File(imageLows.get(0)), REQUEST_LOW);
            } else if (requestCode == 11) {
                try {
                    householdAddress = new JSONObject(data.getStringExtra("json"));
                    tv_home_address.setText(householdAddress.getString("fullAddress"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_FEATURE) {
                tv_identity.setText(data.getStringExtra("typeName"));
                idType = data.getIntExtra("idType", -1);
            } else if (requestCode == REQUEST_SIGN) {
                String path = data.getStringExtra("img");
                Glide.with(context).load(path).into(img_sign1);
                upload(new File(path), REQUEST_SIGN);
            }
        }

    }

    public void apply() {
        //提交审核
        showProgressBar();
        try {
            json.put("committeeId", sp.getString("committeeId", ""));
            json.put("name", tv_name.getText().toString());
            json.put("elderlyId", MyApplication.userInfo.getOlderlyId());
            json.put("sex", sp.getInt("sex", 0));
            json.put("idNumber", sp.getString("idNumber", ""));
            if (homePeopleImg != null) {
                json.put("homePeopleImg", homePeopleImg);
            }
            if (myPageImg != null) {
                json.put("myPageImg", myPageImg);
            }
            if (houseImg != null) {
                json.put("houseImg", houseImg);
            }
            if (fiveCertificate != null) {
                json.put("fiveCertificate", fiveCertificate);
            }
            if (lowCertificate != null) {
                json.put("lowCertificate", lowCertificate);
            }
            if(householdAddress==null){
                Toast.makeText(context, "请填写改造住址", Toast.LENGTH_SHORT).show();
            }
            json.put("currentAddress", householdAddress.toString());
            if (houseSituation == -1) {
                Toast.makeText(context, "请选择住宅情况", Toast.LENGTH_SHORT).show();
                return;
            } else {
                json.put("houseSituation", houseSituation);
            }
            if (ed_family_size.getText().toString().equals("")) {
                Toast.makeText(context, "请填写家庭人数", Toast.LENGTH_SHORT).show();
                return;
            } else {
                json.put("familyNum", ed_family_size.getText().toString());
            }
            if (ed_contact_person.getText().toString().equals("")) {
                Toast.makeText(context, "请填写联系人", Toast.LENGTH_SHORT).show();
                return;
            } else {
                json.put("familyContact", ed_contact_person.getText().toString());
            }
            if (ed_telephone.getText().toString().equals("")) {
                Toast.makeText(context, "请填写联系电话", Toast.LENGTH_SHORT).show();
                return;
            } else {
                json.put("telephone", sp.getString("telephone", ""));
            }
            if (ed_telephone.getText().toString().equals("")) {
                Toast.makeText(context, "请填写联系电话", Toast.LENGTH_SHORT).show();
                return;
            } else {
                json.put("familyPhone", ed_telephone.getText().toString());
            }
            if (idType == -1) {
                Toast.makeText(context, "请选择身份特征", Toast.LENGTH_SHORT).show();
                return;
            } else {
                json.put("idType", idType);
            }
            if (ed_content.getText().toString().equals("")) {
                Toast.makeText(context, "请填写意见", Toast.LENGTH_SHORT).show();
                return;
            } else {
                json.put("applyFamilyOpinion", ed_content.getText().toString());
            }
            if (applySignImg == null) {
                Toast.makeText(context, "请签名!", Toast.LENGTH_SHORT).show();
                return;
            } else {
                json.put("applySignImg", applySignImg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkGo.<String>post(Urls.getInstance().CIVILL_APPLY)
                .tag(this)
                .headers("token", MyApplication.token)
                .upJson(json)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("申请适老化改造", json.toString());
                            if (json.getInt("status") == 200) {
                                Toast.makeText(context, "提交申请成功", Toast.LENGTH_SHORT).show();
                                finish();
                            } else if (json.getInt("status") == 505) {

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

    public void upload(File file, int type) {
        showProgressBar();
        OkGo.<String>post(Urls.getInstance().PHOTO)
                .tag(this)
                .headers("token", MyApplication.token)
                .params("multipartFile", file)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        stopProgressBar();
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("获取图片地址", json.toString());
                            if (json.getInt("status") == 200) {
                                switch (type) {
                                    case REQUEST_MASTER:
                                        homePeopleImg = json.getString("data");
                                        break;
                                    case REQUEST_PERSONAL:
                                        myPageImg = json.getString("data");
                                        break;
                                    case REQUEST_HOUSE_CERTIFICATE:
                                        houseImg = json.getString("data");
                                        break;
                                    case REQUEST_FIVE:
                                        fiveCertificate = json.getString("data");
                                        break;
                                    case REQUEST_LOW:
                                        lowCertificate = json.getString("data");
                                        break;
                                    case REQUEST_SIGN:
                                        applySignImg = json.getString("data");
                                        break;
                                }
                            } else if (json.getInt("status") == 505) {

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

    ArrayList<String> imageMasters = new ArrayList<>();
    ArrayList<String> imagePersonals = new ArrayList<>();
    ArrayList<String> imagePaths = new ArrayList<>();
    ArrayList<String> imgFives = new ArrayList<>();
    ArrayList<String> imageLows = new ArrayList<>();

    public void getImage(int code, ArrayList<String> paths) {
        if (requestPermissions()) {
            ImagePicker.getInstance()
                    .setTitle("标题")//设置标题
                    .showCamera(true)//设置是否显示拍照按钮
                    .showImage(true)//设置是否展示图片
                    .showVideo(true)//设置是否展示视频
                    .setSingleType(true)//设置图片视频不能同时选择
                    .setMaxCount(1)//设置最大选择图片数目(默认为1，单选)
                    .setImagePaths(paths)//保存上一次选择图片的状态，如果不需要可以忽略
                    .setImageLoader(new GlideLoader())//设置自定义图片加载器
                    .start(ReformApplyActivity.this, code);
        }
    }

    //性别选择器
    public void showType() {
        list_type.clear();
        list_type.add("自住");
        list_type.add("非自住");
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                tv_house_type.setText(list_type.get(options1));
                if (list_type.get(options1).equals("非自住")) {
                    houseSituation = 0;
                } else if (list_type.get(options1).equals("自住")) {
                    houseSituation = 1;
                }
            }
        })
                .setDividerColor(Color.parseColor("#A0A0A0"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setContentTextSize(18)//设置滚轮文字大小
                .setOutSideCancelable(true)//点击外部dismiss default true
                .build();
        pvOptions.setPicker(list_type);
        pvOptions.show();
    }

}
