package net.leelink.healthangelos.im.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.adapter.OnOrderListener;
import net.leelink.healthangelos.im.modle.ChatMessage;
import net.leelink.healthangelos.util.Urls;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import wseemann.media.FFmpegMediaMetadataRetriever;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {
    List<ChatMessage> mChatMessageList;
    LayoutInflater inflater;
    Context context;
    //item 最小最大值
    private int mMinItemWidth;
    private int mMaxIItemWidth;
    String send_head;
    String recerve_head;
    OnOrderListener onOrderListener;
    private String name = "用户名";

    public ChatMessageAdapter(Context context, List<ChatMessage> list, String send_head, String recerve_head, OnOrderListener onOrderListener) {
        this.mChatMessageList = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.send_head = send_head;
        this.recerve_head = recerve_head;
        this.onOrderListener = onOrderListener;
        //获取屏幕宽度
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);

        //item 设定最小最大值
        mMaxIItemWidth = (int) (outMetrics.widthPixels * 0.7f);
        mMinItemWidth = (int) (outMetrics.widthPixels * 0.15f);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_receive_text, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_send_text, parent, false);
        }
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage mChatMessage = mChatMessageList.get(position);
        String content = mChatMessage.getContent();
        String time = formatTime(mChatMessage.getTime());
        int isMeSend = mChatMessage.getIsMeSend();
        int isRead = mChatMessage.getIsRead();////是否已读（0未读 1已读）

        holder.tv_sendtime.setText(time);
        if (isMeSend == 0) {//对方发送
            if (recerve_head!=null && !recerve_head.equals("")) {
                Glide.with(context).load(Urls.getInstance().IMG_URL + recerve_head).into(holder.img_head_receiver);
            }
        } else {
            Glide.with(context).load(Urls.getInstance().IMG_URL + send_head).into(holder.img_head_send);
        }

        if (mChatMessage.getType() == 1) {
            //文本消息
            holder.tv_content.setVisibility(View.VISIBLE);
            holder.img_picture.setVisibility(View.GONE);
            holder.id_recorder_length.setVisibility(View.GONE);
            holder.tv_content.setText(content);
        } else if (mChatMessage.getType() == 2) {
            //图片消息
            holder.tv_content.setVisibility(View.GONE);
            holder.img_picture.setVisibility(View.VISIBLE);
            holder.id_recorder_length.setVisibility(View.GONE);
            Glide.with(context).load(Urls.getInstance().IMG_URL + content).into(holder.img_picture);
        } else if (mChatMessage.getType() == 3) {
            //语音消息
            holder.tv_content.setVisibility(View.GONE);
            holder.img_picture.setVisibility(View.GONE);
            holder.id_recorder_length.setVisibility(View.VISIBLE);
            if (isMeSend == 1) {
                //设置背景的宽度
                ViewGroup.LayoutParams lp = holder.id_recorder_length.getLayoutParams();
                //getItem(position).time
                lp.width = (int) (mMinItemWidth + (mMaxIItemWidth / 60f * mChatMessage.getRecorderTime()));
            } else {
                //设置背景的宽度
                ViewGroup.LayoutParams lp = holder.id_recorder_length.getLayoutParams();
                Log.e("getView: ", getDurationInMilliseconds(Urls.getInstance().IMG_URL + content) + "");
                lp.width = (int) (mMinItemWidth + (mMaxIItemWidth / 60f * getDurationInMilliseconds(Urls.getInstance().IMG_URL + content)));
            }
            holder.id_recorder_length.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOrderListener.onButtonClick(v, position);
                }
            });
        }


        //如果是自己发送才显示未读已读
        if (isMeSend == 1) {
            if (isRead == 0) {
                holder.tv_isRead.setText("未读");
                holder.tv_isRead.setTextColor(context.getResources().getColor(R.color.jmui_jpush_blue));
            } else if (isRead == 1) {
                holder.tv_isRead.setText("已读");
                holder.tv_isRead.setTextColor(Color.GRAY);
            } else {
                holder.tv_isRead.setText("");
            }
        } else {
            holder.tv_display_name.setVisibility(View.VISIBLE);
            holder.tv_display_name.setText(name);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getItemCount() {
        return mChatMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mChatMessageList.get(position).getIsMeSend() == 1)
            return 1;// 返回的数据位角标
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_content, tv_sendtime, tv_display_name, tv_isRead, id_recorder_time;
        private ImageView img_picture, img_head_send, img_head_receiver, jmui_fail_resend_ib;
        private LinearLayout id_recorder_length;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            this.setIsRecyclable(false);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_sendtime = itemView.findViewById(R.id.tv_sendtime);
            tv_display_name = itemView.findViewById(R.id.tv_display_name);
            img_picture = itemView.findViewById(R.id.img_picture);
            id_recorder_length = itemView.findViewById(R.id.id_recorder_length);
            img_head_receiver = itemView.findViewById(R.id.img_head_receiver);
            tv_isRead = itemView.findViewById(R.id.tv_isRead);
            id_recorder_time = itemView.findViewById(R.id.id_recorder_time);
            img_head_send = itemView.findViewById(R.id.img_head_send);
            img_head_receiver = itemView.findViewById(R.id.img_head_receiver);
            jmui_fail_resend_ib = itemView.findViewById(R.id.jmui_fail_resend_ib);
        }
    }

    public void setLastFail() {

    }

    /**
     * 将毫秒数转为日期格式
     *
     * @param timeMillis
     * @return
     */
    private String formatTime(String timeMillis) {
        long timeMillisl = Long.parseLong(timeMillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timeMillisl);
        return simpleDateFormat.format(date);
    }


    public static int getDurationInMilliseconds(String url) {
        try {
            FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
            mmr.setDataSource(url);
            int duration = Integer.parseInt(mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION));
            mmr.release();//释放资源
            return duration / 1000;
        } catch (Exception e) {
            return 0;
        }
    }
}
