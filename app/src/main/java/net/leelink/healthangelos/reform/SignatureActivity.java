package net.leelink.healthangelos.reform;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;
import net.leelink.healthangelos.view.SignatureView;

import java.io.IOException;

public class SignatureActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_back,rl_save;
    SignatureView view_signature;
    private Context context;
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        init();
        context = this;
    }

    public void init(){
        rl_back  = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        rl_save = findViewById(R.id.rl_save);
        rl_save.setOnClickListener(this);
        view_signature = findViewById(R.id.view_signature);
        view_signature.init(context);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_save:
                    try {
                        view_signature.setMakeImage();
                        path = view_signature.save(context, true, 10);
                        Intent intent = new Intent();
                        intent.putExtra("img",path);
                        setResult(101,intent);
                        finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                break;
        }
    }
}
