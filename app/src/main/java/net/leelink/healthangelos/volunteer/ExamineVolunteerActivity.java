package net.leelink.healthangelos.volunteer;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;

public class ExamineVolunteerActivity extends BaseActivity {
    private RelativeLayout rl_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examine_volunteer);
        rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
