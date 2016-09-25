package com.deity.wxredpackets.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.deity.wxredpackets.R;
import com.deity.wxredpackets.dao.WXRedPacketEntity;
import com.deity.wxredpackets.widget.MarqueeText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Deity on 2016/9/21.
 */
public class RedPacketAdapter extends RecyclerView.Adapter<RedPacketAdapter.ViewHolder> {
    private List<WXRedPacketEntity> wxRedPacketEntities = new ArrayList<>();
    private Context context;

    public RedPacketAdapter(Context context){
        this.context = context;
    }

    public void setData(List<WXRedPacketEntity> wxRedPacketEntities){
        this.wxRedPacketEntities = wxRedPacketEntities;
    }

    public void addData(List<WXRedPacketEntity> wxRedPacketEntities){
        this.wxRedPacketEntities.addAll(wxRedPacketEntities);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_redpacket,parent,false);
        ViewHolder holder = new ViewHolder(rootView);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WXRedPacketEntity entity = wxRedPacketEntities.get(position);
        holder.redPacketSenderName.setText(entity.getRedPacketSenderName());
        holder.redPacketTime.setText(entity.getRedPacketOpenTime());
        holder.redPacketContent.setText(entity.getRedPacketMessage());
        holder.redPacketMoney.setText(String.valueOf(entity.getRedPacketMoney()+"元"));
    }

    @Override
    public int getItemCount() {
        return wxRedPacketEntities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private MarqueeText redPacketSenderName;
        private MarqueeText redPacketTime;
        private MarqueeText redPacketContent;
        private MarqueeText redPacketMoney;

        public ViewHolder(View view) {
            super(view);
            redPacketSenderName = (MarqueeText) view.findViewById(R.id.redPacketSenderName);
            redPacketTime = (MarqueeText) view.findViewById(R.id.redPacketTime);
            redPacketContent = (MarqueeText) view.findViewById(R.id.redPacketContent);
            redPacketMoney = (MarqueeText) view.findViewById(R.id.redPacketMoney);
        }
    }
}
