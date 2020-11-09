package net.leelink.healthangelos.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.leelink.healthangelos.activity.LoginActivity;

import androidx.fragment.app.Fragment;


public abstract class BaseFragment extends Fragment {
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


}
