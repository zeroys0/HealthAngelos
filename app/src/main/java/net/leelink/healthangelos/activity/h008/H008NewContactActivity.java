package net.leelink.healthangelos.activity.h008;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;

public class H008NewContactActivity extends BaseActivity {
    Context context;
    RelativeLayout rl_back;
    EditText ed_name,ed_phone;
    Button btn_confirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h008_new_contact);
        context = this;
        init();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        ed_name = findViewById(R.id.ed_name);
        ed_phone = findViewById(R.id.ed_phone);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_confirm:
                if(ed_name.getText().toString().equals("")){
                    Toast.makeText(context, "请填写联系人姓名", Toast.LENGTH_SHORT).show();
                }
                if(ed_phone.getText().toString().equals("")){
                    Toast.makeText(context, "请填写联系电话", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent();
                intent.putExtra("name",ed_name.getText().toString());
                intent.putExtra("phone",ed_phone.getText().toString());
                setResult(7,intent);
                finish();
                break;
        }
    }
}