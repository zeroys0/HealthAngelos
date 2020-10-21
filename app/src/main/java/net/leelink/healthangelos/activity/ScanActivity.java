package net.leelink.healthangelos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.app.BaseActivity;

public class ScanActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
    }
}
