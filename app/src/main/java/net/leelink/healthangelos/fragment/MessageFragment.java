package net.leelink.healthangelos.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.activity.HealthUnusualActivity;
import net.leelink.healthangelos.activity.SafeMessageActivity;
import net.leelink.healthangelos.activity.ServiceMessageActivity;
import net.leelink.healthangelos.activity.VipMessageActivity;
import net.leelink.healthangelos.adapter.ChatListAdapter;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.app.MyApplication;
import net.leelink.healthangelos.im.ChatActivity;
import net.leelink.healthangelos.im.data.MessageListHelper;
import net.leelink.healthangelos.im.modle.ChatListMessage;
import net.leelink.healthangelos.util.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MessageFragment extends BaseFragment implements OnOrderListener, View.OnClickListener {
    private ChatMessageReceiver chatMessageReceiver;
    private RecyclerView chat_list;
    ChatListAdapter chatListAdapter;
    MessageListHelper messageListHelper;
    Context context;
    SharedPreferences sp;
    private List<ChatListMessage> chatMessageList = new ArrayList<>();//消息列表
    private RelativeLayout rl_alarm,rl_service,rl_safe,rl_vip;
    @Override
    public void handleCallBack(Message msg) {

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        init(view);
        context = getContext();
        messageListHelper = new MessageListHelper(context);
        doRegisterReceiver();
        initList();
        return view;
    }

    public void init(View view){
        chat_list = view.findViewById(R.id.chat_list);
        sp = getActivity().getSharedPreferences("sp",0);
        rl_alarm = view.findViewById(R.id.rl_alarm);
        rl_alarm.setOnClickListener(this);
        rl_service = view.findViewById(R.id.rl_service);
        rl_service.setOnClickListener(this);
        rl_safe = view.findViewById(R.id.rl_safe);
        rl_safe.setOnClickListener(this);
        rl_vip = view.findViewById(R.id.rl_vip);
        rl_vip.setOnClickListener(this);
    }

    public void initList(){
        chatMessageList.clear();
        String clientId = sp.getString("clientId","");
        SQLiteDatabase db=messageListHelper.getWritableDatabase();
        String sql="select content,time,isMeSend,isRead,type,RecorderTime,receiveId from MessageListDB where sendId=?";
        Cursor c = db.rawQuery(sql,new String[]{clientId});
        String content;
        String time;
        int isMeSend;
        int isRead;
        int type;
        float RecorderTime;
        String receiveId;

        while(c.moveToNext()){
            content=c.getString(0);
            time=c.getString(1);
            isMeSend=c.getInt(2);
            isRead=c.getInt(3);
            type = c.getInt(4);
            RecorderTime = c.getFloat(5);
            receiveId = c.getString(6);

            ChatListMessage chatMessage = new ChatListMessage();
            chatMessage.setContent(content);
            chatMessage.setTime(time);
            chatMessage.setIsMeSend(isMeSend);
            chatMessage.setIsRead(isRead);
            chatMessage.setType(type);
            chatMessage.setRecorderTime(RecorderTime);
            chatMessage.setReceiveId(receiveId);

            chatMessageList.add(chatMessage);
        }
        db.close();
        c.close();
        if(chatListAdapter==null) {
            chatListAdapter = new ChatListAdapter(chatMessageList, context, MessageFragment.this);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
            chat_list.setAdapter(chatListAdapter);
            chat_list.setLayoutManager(layoutManager);
        }else {
            chatListAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 动态注册广播
     */
    private void doRegisterReceiver() {
        chatMessageReceiver = new ChatMessageReceiver();
        IntentFilter filter = new IntentFilter("net.leelink.healthangelos");
        context.registerReceiver(chatMessageReceiver, filter);
    }

    @Override
    public void onItemClick(View view) {
        int position = chat_list.getChildLayoutPosition(view);
        String clientId = chatMessageList.get(position).getReceiveId();

        Log.e( "onItemClick: ",clientId );
        OkGo.<String>get(Urls.getInstance().CHAT_USERINFO + "/" + clientId+"/2")
                .tag(this)
                .headers("token", MyApplication.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            String body = response.body();
                            JSONObject json = new JSONObject(body);
                            Log.d("消息", json.toString());
                            if (json.getInt("status") == 200) {
                                JSONArray jsonArray = json.getJSONArray("data");
                                String img_head = jsonArray.getJSONObject(0).getString("img_path");
                                Intent intent = new Intent(context, ChatActivity.class);
                                intent.putExtra("clientId",clientId);
                                intent.putExtra("receive_head",img_head);
                                intent.putExtra("name",jsonArray.getJSONObject(0).getString("userName"));   
                                startActivity(intent);
                            } else if (json.getInt("status") == 505) {
                                reLogin(context);
                            } else {
                                Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("clientId",clientId);
                        intent.putExtra("receive_head","");
                        intent.putExtra("name","");
                        startActivity(intent);
                        Toast.makeText(context, "未获取到头像", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    @Override
    public void onButtonClick(View view, int position) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_alarm:
                //健康异常
                Intent intent= new Intent(getContext(), HealthUnusualActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_service:
                //服务消息
                Intent intent1 = new Intent(getContext(), ServiceMessageActivity.class);
                startActivity(intent1);
                break;
            case R.id.rl_safe:
                //安全消息
                Intent intent2 = new Intent(getContext(), SafeMessageActivity.class);
                startActivity(intent2);
                break;
            case R.id.rl_vip:
                //会员消息
                Intent intent3 = new Intent(getContext(), VipMessageActivity.class);
                startActivity(intent3);
                break;
        }

    }

    /**
     * 接收消息广播
     */
    private class ChatMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            try {
                JSONObject jsonObject = new JSONObject(message);
                if (jsonObject.has("messageType")) {
                    if (jsonObject.getInt("messageType") == 4) {
                        initList();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
