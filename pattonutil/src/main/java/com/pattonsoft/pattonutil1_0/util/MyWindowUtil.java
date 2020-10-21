package com.pattonsoft.pattonutil1_0.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

import java.lang.reflect.Field;

/**
 * 界面管理类 计算界面宽高度等
 * 
 * @author zhao
 * @time 2016.02.14
 */
public class MyWindowUtil {

	/**
	 * 获取状态栏高度（别人的方法）
	 * 
	 * @param context
	 * @return 状态栏高度 int top
	 */
	public static int getTop(Context context) {

		Class<?> c = null;

		Object obj = null;

		Field field = null;

		int x = 0, statusBarHeight = 0;

		try {

			c = Class.forName("com.android.internal.R$dimen");

			obj = c.newInstance();

			field = c.getField("status_bar_height");

			x = Integer.parseInt(field.get(obj).toString());

			statusBarHeight = context.getResources().getDimensionPixelSize(x);

		} catch (Exception e1) {

			e1.printStackTrace();

		}
		// 返回前先判断 Api是否大于等于19
		// 小于19则不需要去除顶部高度 值=0；
		// api19以下不支持沉浸式状态栏 故在此区分
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			statusBarHeight = 0;
		}
		return statusBarHeight;

	}

	/**
	 * 获取屏幕宽度
	 * 
	 * @param context
	 * @return 状态栏宽度 int weidth
	 */
	public static int getWidth(Context context) {

		Resources resources = context.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		float density1 = dm.density;
		int width = dm.widthPixels;
		int height = dm.heightPixels;

		return width;

	}

	/**
	 * 获取屏幕宽度
	 * 
	 * @param context
	 * @return 状态栏高度 int height
	 */
	public static int getHeight(Context context) {

		Resources resources = context.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		float density1 = dm.density;
		int width = dm.widthPixels;
		int height = dm.heightPixels;

		return height;

	}

	/**
	 * 控制scroll滚动顶部
	 * 
	 * @param scroll
	 * 
	 */
	public static void scrollToTop(final View scroll, final View inner) {

		Handler mHandler = new Handler();

		mHandler.post(new Runnable() {
			public void run() {
				if (scroll == null || inner == null) {
					return;
				}
				int offset = inner.getMeasuredHeight() - scroll.getHeight();
				if (offset < 0) {
					offset = 0;
				}

				scroll.scrollTo(0, offset);
			}
		});
	}

	/**
	 * 控制scroll滚动底部
	 * 
	 * @param scroll
	 * 
	 */
	public static void scrollToBottom(final ScrollView scroll) {

		Handler mHandler = new Handler();

		mHandler.post(new Runnable() {
			public void run() {
				scroll.fullScroll(ScrollView.FOCUS_DOWN);

			}
		});
	}

	/**
	 * 获取当前屏幕截图，包含状态栏
	 * 
	 * @param activity
	 * @return
	 */
	public static Bitmap snapShotWithStatusBar(Activity activity) {
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		int width = getWidth(activity);
		int height = getHeight(activity);
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
		view.destroyDrawingCache();
		return bp;

	}

	/**
	 * 获取当前屏幕截图，不包含状态栏
	 * 
	 * @param activity
	 * @return
	 */
	public static Bitmap snapShotWithoutStatusBar(Activity activity) {
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;

		int width = getWidth(activity);
		int height = getHeight(activity);
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
				- statusBarHeight);
		view.destroyDrawingCache();
		return bp;

	}
	/**
	 * 用于嵌套如ScrollView 动态设置ListView的高度
	 * 
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		if (listView == null)
			return;
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}
}
