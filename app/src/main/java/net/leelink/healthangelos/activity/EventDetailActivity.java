package net.leelink.healthangelos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;

public class EventDetailActivity extends BaseActivity {
    TextView tv_num;
    RelativeLayout rl_back;
    EditText ed_detail;
    Button btn_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        init();
    }

    public void init(){
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_num = findViewById(R.id.tv_num);
        ed_detail = findViewById(R.id.ed_detail);
        ed_detail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int a = ed_detail.getText().toString().trim().length();
                tv_num.setText((300 - a) + "/300");
            }
        });
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                putDetail();

            }
        });
    }

    public void putDetail(){
        if(ed_detail.getText().toString().equals("")){
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("detail",ed_detail.getText().toString().trim());
        setResult(3,intent);
        finish();
    }
}
