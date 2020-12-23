package net.leelink.healthangelos.im.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.leelink.healthangelos.R;
import net.leelink.healthangelos.im.modle.ChatMessage;
import net.leelink.healthangelos.util.Urls;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class Adapter_ChatMessage extends BaseAdapter {
    List<ChatMessage> mChatMessageList;
    LayoutInflater inflater;
    Context context;
    //item 最小最大值
    private int mMinItemWidth;
    private int mMaxIItemWidth;
    String send_head;
    String recerve_head;

    public Adapter_ChatMessage(Context context, List<ChatMessage> list,String send_head,String recerve_head) {
        this.mChatMessageList = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.send_head = send_head;
        this.recerve_head = recerve_head;
        //获取屏幕宽度
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);

        //item 设定最小最大值
        mMaxIItemWidth = (int) (outMetrics.widthPixels * 0.7f);
        mMinItemWidth = (int) (outMetrics.widthPixels * 0.15f);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (mChatMessageList.get(position).getIsMeSend() == 1)
            return 0;// 返回的数据位角标
        else
            return 1;
    }

    @Override
    public int getCount() {
        return mChatMessageList.size();
    }


    @Override
    public Object getItem(int i) {
        return mChatMessageList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ChatMessage mChatMessage = mChatMessageList.get(i);
        String content = mChatMessage.getContent();
        String time = formatTime(mChatMessage.getTime());
        int isMeSend = mChatMessage.getIsMeSend();
        int isRead = mChatMessage.getIsRead();////是否已读（0未读 1已读）
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            if (isMeSend == 0) {//对方发送
                view = inflater.inflate(R.layout.item_chat_receive_text, viewGroup, false);
                holder.tv_content = view.findViewById(R.id.tv_content);
                holder.tv_sendtime = view.findViewById(R.id.tv_sendtime);
//                holder.tv_display_name = view.findViewById(R.id.tv_display_name);
                holder.img_picture = view.findViewById(R.id.img_picture);
                holder.id_recorder_length = view.findViewById(R.id.id_recorder_length);
                holder.img_head_receiver = view.findViewById(R.id.img_head_receiver);
                Glide.with(context).load(Urls.getInstance().IMG_URL + recerve_head).into(holder.img_head_receiver);
            } else {
                view = inflater.inflate(R.layout.item_chat_send_text, viewGroup, false);
                holder.tv_content = view.findViewById(R.id.tv_content);
                holder.tv_sendtime = view.findViewById(R.id.tv_sendtime);
                holder.tv_isRead = view.findViewById(R.id.tv_isRead);
                holder.img_picture = view.findViewById(R.id.img_picture);
                holder.id_recorder_length = view.findViewById(R.id.id_recorder_length);
//                holder.id_recorder_time = view.findViewById(R.id.id_recorder_time);
                holder.img_head_send = view.findViewById(R.id.img_head_send);
                Glide.with(context).load(Urls.getInstance().IMG_URL + send_head).into(holder.img_head_send);
            }

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.tv_sendtime.setText(time);
        if(mChatMessage.getType()==1) {
            holder.tv_content.setVisibility(View.VISIBLE);
            holder.tv_content.setText(content);
        }else if(mChatMessage.getType() ==2) {
            holder.tv_content.setVisibility(View.GONE);
            holder.img_picture.setVisibility(View.VISIBLE);
            Glide.with(context).load(Urls.getInstance().IMG_URL +content).into(holder.img_picture);
        } else if(mChatMessage.getType() ==3) {
            holder.tv_content.setVisibility(View.GONE);
            holder.id_recorder_length.setVisibility(View.VISIBLE);
            if(isMeSend ==1) {
                //设置背景的宽度
                ViewGroup.LayoutParams lp = holder.id_recorder_length.getLayoutParams();
                //getItem(position).time
                lp.width = (int) (mMinItemWidth + (mMaxIItemWidth / 60f*mChatMessage.getRecorderTime()));
            } else {
                //设置背景的宽度
                ViewGroup.LayoutParams lp = holder.id_recorder_length.getLayoutParams();
                Log.e( "getView: ", getDurationInMilliseconds(Urls.getInstance().IMG_URL+content)+"");
                lp.width = (int) (mMinItemWidth + (mMaxIItemWidth / 60f*getDurationInMilliseconds(Urls.getInstance().IMG_URL+content)));
            }
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
//            holder.tv_display_name.setVisibility(View.GONE);
        //    holder.tv_display_name.setText("服务器");
        }

        return view;
    }

    class ViewHolder {
        private TextView tv_content, tv_sendtime, tv_isRead,id_recorder_time;
        private ImageView img_picture,img_head_send,img_head_receiver;
        private LinearLayout id_recorder_length;
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

    public void inputstreamtofile(String string,File file){
        ByteArrayInputStream stream = new ByteArrayInputStream(string.getBytes());
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        while ((bytesRead = stream.read(buffer, 0, 8192)) != -1) {
            try {
                os.write(buffer, 0, bytesRead);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getDurationInMilliseconds(String url) {
        FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
        mmr.setDataSource(url);
        int duration = Integer.parseInt(mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION));
        mmr.release();//释放资源
        return duration/1000;
    }
}
