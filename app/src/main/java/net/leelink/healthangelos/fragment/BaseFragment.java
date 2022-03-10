package net.leelink.healthangelos.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pattonsoft.pattonutil1_0.util.SPUtils;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.LoginActivity;

import androidx.fragment.app.Fragment;


public abstract class BaseFragment extends Fragment {
	ProgressBar mProgressBar;
	protected Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			handleCallBack(msg);
		}

	};

	public abstract void handleCallBack(Message msg);
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

		return super.onCreateView(inflater, container, savedInstanceState);

	}
	
	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	public void reLogin(Context context){

		SharedPreferences sp = context.getSharedPreferences("sp",0);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove("secretKey");
		editor.remove("telephone");
		editor.apply();
		Intent intent = new Intent(context, LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		getActivity().finish();
		Toast.makeText(context, "登录过期,请重新登录", Toast.LENGTH_SHORT).show();

	}
//	public void notch(View view) {
//		if (hasNotchInScreen(getContext()) ) {
//			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
//			params.setMargins(0, NotchUtils.getNotchSize(getContext())[1]+5, 0, 30);//左上右下
//			view.setLayoutParams(params);
//		}
//	}

	public void changeFontSize(String spKey) {
		float scale = 1.0f;
		Configuration c = getResources().getConfiguration();
		if (!TextUtils.isEmpty(spKey)) {
			scale = Float.valueOf(spKey);
		}

		c.fontScale = scale;
//        DisplayMetrics metrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        metrics.scaledDensity = c.fontScale * metrics.density;
		getResources().updateConfiguration(c, getResources().getDisplayMetrics());
	}

	public void checkFontSize(){
		String fontSize = (String) SPUtils.get(getContext(),"font","");
		if(fontSize.equals("1.3")) {
			getContext().setTheme(R.style.theme_large);
		} else {
			getContext().setTheme(R.style.theme_standard);
		}
	}



}
