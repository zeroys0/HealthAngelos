package net.leelink.healthangelos.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class ChangeView extends View {


    public ChangeView(Context context) {
        super(context);
    }

    public ChangeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ChangeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ChangeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint p = new Paint();
        Shader lg = new LinearGradient(0, 0, getWidth(), getHeight(), Color.parseColor("#007EFF"), Color.parseColor("#34A2FF"), Shader.TileMode.CLAMP);
        p.setShader(lg);
        canvas.drawRect(0,0,getWidth(),getHeight(),p);
    }
}
