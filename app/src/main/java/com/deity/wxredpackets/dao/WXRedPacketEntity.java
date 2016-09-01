package com.deity.wxredpackets.dao;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * 微信红包数据库记录
 * Created by Deity on 2016/9/2.
 */
@SuppressWarnings("unused")
public class WXRedPacketEntity  extends RealmObject {

    @PrimaryKey
    private long redPacketId;//唯一标识符
    private String redPacketSenderName;//微信红包发送者
    private String redPacketReceiveTime;//微信红包接收时间
    private boolean isPicked;//是否已经打开过了

    public long getRedPacketId() {
        return redPacketId;
    }

    public void setRedPacketId(long redPacketId) {
        this.redPacketId = redPacketId;
    }

    public String getRedPacketSenderName() {
        return redPacketSenderName;
    }

    public void setRedPacketSenderName(String redPacketSenderName) {
        this.redPacketSenderName = redPacketSenderName;
    }

    public String getRedPacketReceiveTime() {
        return redPacketReceiveTime;
    }

    public void setRedPacketReceiveTime(String redPacketReceiveTime) {
        this.redPacketReceiveTime = redPacketReceiveTime;
    }

    public boolean isPicked() {
        return isPicked;
    }

    public void setPicked(boolean picked) {
        isPicked = picked;
    }
}
