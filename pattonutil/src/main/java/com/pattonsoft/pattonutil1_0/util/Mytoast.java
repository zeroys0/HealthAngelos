package com.pattonsoft.pattonutil1_0.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Mytoast {
    private static Toast toast;
    private static final int Bottom = Gravity.BOTTOM;
    private static final int Center = Gravity.CENTER;

    public static final int xOffset = 0;
    public static final int yOffset = 0;
    public static int gravity = Center;

    public static int getGravity() {
        return gravity;
    }

    public static void setGravity(int gravity) {
        Mytoast.gravity = gravity;
    }


    /**
     * 显示文字内容
     *
     * @param context
     * @param text
     */
    public static void show(Context context, String text) {
        if (toast != null) {
            toast.cancel();
        } else {
            toast = new Toast(context);
        }
        int gravity = getGravity();

        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);

        toast.show();
    }


    /**
     * 显示文字内容
     *
     * @param context
     * @param text
     */
    public static void show(Context context, String text, int gravity, int xOffset, int yOffset) {
        if (toast != null) {
            toast.cancel();
        } else {
            toast = new Toast(context);
        }

        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.setGravity(gravity, xOffset, yOffset);
        toast.show();
    }

    /**
     * 显示文字内容
     *
     * @param context
     * @param StringRes
     */
    public static void show(Context context, int StringRes) {
        if (toast != null) {
            toast.cancel();
        } else {
            toast = new Toast(context);
        }


        toast = Toast.makeText(context, StringRes, Toast.LENGTH_SHORT);

        toast.show();
    }

    /**
     * 显示图片内容 图片大小跟随文字
     *
     * @param context
     * @param text
     */
    public static void show(Context context, String text, Drawable drawable) {
        if (toast != null) {
            toast.cancel();
        } else {
            toast = new Toast(context);
        }
        // 文字
        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);


        // 图片
        LinearLayout toastView = (LinearLayout) toast.getView();
        ImageView im = new ImageView(context);
        im.setImageDrawable(drawable);
        toastView.addView(im, 0);

        // 显示
        toast.show();
    }

    /**
     * 显示图片内容 图片大小跟随文字
     *
     * @param context
     * @param text
     */
    public static void show(Context context, String text, Bitmap bitmap) {
        if (toast != null) {
            toast.cancel();
        } else {
            toast = new Toast(context);
        }
        // 文字
        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);


        // 图片
        LinearLayout toastView = (LinearLayout) toast.getView();
        ImageView im = new ImageView(context);
        im.setImageBitmap(bitmap);
        toastView.addView(im, 0);

        // 显示
        toast.show();
    }

    /**
     * 显示图片内容 图片大小跟随文字
     *
     * @param context
     * @param text
     */
    public static void show(Context context, String text, int res) {
        if (toast != null) {
            toast.cancel();
        } else {
            toast = new Toast(context);
        }
        // 文字
        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);


        // 图片
        LinearLayout toastView = (LinearLayout) toast.getView();
        ImageView im = new ImageView(context);
        im.setImageResource(res);
        toastView.addView(im, 0);

        // 显示
        toast.show();
    }

}
