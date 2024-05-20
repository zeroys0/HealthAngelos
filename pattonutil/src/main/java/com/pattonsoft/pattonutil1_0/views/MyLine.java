package com.pattonsoft.pattonutil1_0.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pattonsoft.pattonutil1_0.util.DensityUtils;

import androidx.annotation.Nullable;
import pattonutil.R;


/**
 * 自定义控件1
 * Created by zhao on 2017/5/23.
 */

public class MyLine extends LinearLayout {


    private LinearLayout ll;//外框
    private ImageView im1;//头部图
    private ImageView im2;//尾部图
    private TextView tv1;//头部文字
    private TextView tv2;//尾部文字
    private int line_padding;//内边距
    private int image1_width;//图一宽度
    private int image1_height;//图一高度
    private int image1_rsc;//图一内容
    private int image2_width;//图二宽度
    private int image2_height;//图二高度
    private int image2_rsc;//图二内容
    private int text1_color;//文字一颜色
    private int text2_color;//文字二颜色
    private int text1_size;//文字一大小
    private int text2_size;//文字二大小
    private String text1_text;//文字一内容
    private String text2_text;//文字二内容

    public MyLine(Context context) {
        super(context);
    }

    public MyLine(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.myline, this, true);
        ll = (LinearLayout) view.findViewById(R.id.ll);
        im1 = (ImageView) view.findViewById(R.id.im_1);
        im2 = (ImageView) view.findViewById(R.id.im_2);
        tv1 = (TextView) view.findViewById(R.id.tv_1);
        tv2 = (TextView) view.findViewById(R.id.tv_2);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.MyLine);
        if (attributes != null) {
            line_padding = attributes.getDimensionPixelOffset(R.styleable.MyLine_line_padding, DensityUtils.dp2px(context, 10));
            image1_width = attributes.getDimensionPixelOffset(R.styleable.MyLine_image1_width, DensityUtils.dp2px(context, 25));
            image1_height = attributes.getDimensionPixelOffset(R.styleable.MyLine_image1_height, DensityUtils.dp2px(context, 25));
            image1_rsc = attributes.getResourceId(R.styleable.MyLine_image1_rsc, -1);
            if (image1_rsc == -1) {
                im1.setVisibility(View.GONE);
            } else {
                ViewGroup.LayoutParams p1 = im1.getLayoutParams();
                p1.width = image1_width;
                p1.height = image1_height;
                im1.setLayoutParams(p1);
                im1.setImageResource(image1_rsc);
            }
            image2_width = attributes.getDimensionPixelOffset(R.styleable.MyLine_image2_width, DensityUtils.dp2px(context, 20));
            image2_height = attributes.getDimensionPixelOffset(R.styleable.MyLine_image2_height, DensityUtils.dp2px(context, 20));
            image2_rsc = attributes.getResourceId(R.styleable.MyLine_image2_rsc, -1);
            if (image2_rsc == -1) {
                im2.setVisibility(View.GONE);
            } else {
                ViewGroup.LayoutParams p2 = im2.getLayoutParams();
                p2.width = image2_width;
                p2.height = image2_height;
                im2.setLayoutParams(p2);
                im2.setImageResource(image2_rsc);
            }

            text1_color = attributes.getColor(R.styleable.MyLine_text1_color, Color.BLACK);
            text2_color = attributes.getColor(R.styleable.MyLine_text2_color, Color.BLACK);
            text1_text = attributes.getString(R.styleable.MyLine_text1_text);
            text2_text = attributes.getString(R.styleable.MyLine_text2_text);

            text1_size = attributes.getDimensionPixelOffset(R.styleable.MyLine_text1_size, DensityUtils.sp2px(context, 14));
            text2_size = attributes.getDimensionPixelOffset(R.styleable.MyLine_text2_size, DensityUtils.sp2px(context, 14));

            ll.setPadding(line_padding, line_padding, line_padding, line_padding);
            tv1.setTextColor(text1_color);
            tv2.setTextColor(text2_color);

            if (text1_text != null) tv1.setText(text1_text);
            else tv1.setText("");
            if (text2_text != null) tv2.setText(text2_text);
            else tv2.setText("");

            int size1 = (int) DensityUtils.px2sp(context, text1_size);
            int size2 = (int) DensityUtils.px2sp(context, text2_size);

            tv1.setTextSize(size1);
            tv2.setTextSize(size2);
        }

    }

    public MyLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        new MyLine(context, attrs);

    }

    public ImageView getIm1() {
        return im1;
    }

    public ImageView getIm2() {
        return im2;
    }

    public TextView getTv1() {
        return tv1;
    }

    public TextView getTv2() {
        return tv2;
    }

    public int getImage1_width() {
        return image1_width;
    }

    public int getImage1_height() {
        return image1_height;
    }

    public int getImage1_rsc() {
        return image1_rsc;
    }

    public int getImage2_width() {
        return image2_width;
    }

    public int getImage2_height() {
        return image2_height;
    }

    public int getImage2_rsc() {
        return image2_rsc;
    }

    public int getText1_color() {
        return text1_color;
    }

    public int getText2_color() {
        return text2_color;
    }

    public int getText1_size() {
        return text1_size;
    }

    public int getText2_size() {
        return text2_size;
    }

    public String getText1_text() {
        return text1_text;
    }

    public String getText2_text() {
        return text2_text;
    }


    public void setText1_color(int text1_color) {
        this.text1_color = text1_color;
        tv1.setTextColor(text1_color);
    }

    public void setText2_color(int text2_color) {
        this.text2_color = text2_color;
        tv2.setTextColor(text2_color);
    }

    public void setText1_size(int text1_size) {
        this.text1_size = text1_size;
        tv1.setTextSize(text1_size);
    }

    /**
     * @param text1_text sp
     */
    public void setText1_text(String text1_text) {
        this.text1_text = text1_text;
        tv1.setText(text1_text);
    }

    /**
     * @param text2_size sp
     */
    public void setText2_size(int text2_size) {
        this.text2_size = text2_size;
        tv2.setTextSize(text2_size);
    }


    public void setText2_text(String text2_text) {
        this.text2_text = text2_text;
        tv2.setText(text2_text);
    }


    /**
     * 图1尺寸
     *
     * @param image1_width  px
     * @param image1_height px
     */
    public void setImage1_size(int image1_width, int image1_height) {
        this.image1_width = image1_width;
        this.image1_height = image1_height;
        ViewGroup.LayoutParams p = im1.getLayoutParams();
        p.width = image1_width;
        p.height = image1_height;
        im1.setLayoutParams(p);

    }

    /**
     * 图2尺寸
     *
     * @param image2_width  px
     * @param image2_height px
     */
    public void setImage2_size(int image2_width, int image2_height) {
        this.image2_width = image2_width;
        this.image2_height = image2_height;
        ViewGroup.LayoutParams p = im2.getLayoutParams();
        p.width = image2_width;
        p.height = image2_height;
        im2.setLayoutParams(p);

    }


    public void setImage1_rsc(int image1_rsc) {
        this.image1_rsc = image1_rsc;
        im1.setImageResource(image1_rsc);
    }


    public void setImage2_rsc(int image2_rsc) {
        this.image2_rsc = image2_rsc;
        im2.setImageResource(image2_rsc);
    }


}
