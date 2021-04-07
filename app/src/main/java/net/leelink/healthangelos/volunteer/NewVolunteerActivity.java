package net.leelink.healthangelos.volunteer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;

public class NewVolunteerActivity extends BaseActivity {
    private RelativeLayout rl_back;
    private TextView tv_text;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_volunteer);
        context = this;
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
        tv_text = findViewById(R.id.tv_text);
        SpannableString spannableString = new SpannableString("您还不是志愿者，快点击申请加入我们吧!");
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(context, VolunteerApplyActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.blue)); //设置颜色
            }
        }, 9, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_text.setText(spannableString);
        tv_text.setMovementMethod(LinkMovementMethod.getInstance());  //很重要，点击无效就是由于没有设置这个引起
    }

}
