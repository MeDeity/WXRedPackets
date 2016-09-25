package com.deity.wxredpackets.dao;

import java.util.Date;

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
    private String redPacketSenderName="unknowSuperMan";//微信红包发送者
    private String redPacketReceiveTime="unknowTime";//微信红包接收时间 --> 修改为拆包时间
    private String redPacketOpenTime;//拆包时间
    private String redPacketMessage="微信红包助手祝您:生意兴隆";//红包描述
    private double redPacketMoney;//微信红包金额
    private boolean isPicked=false;//是否已经打开过了

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

    public String getRedPacketMessage() {
        return redPacketMessage;
    }

    public void setRedPacketMessage(String redPacketMessage) {
        this.redPacketMessage = redPacketMessage;
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

    public double getRedPacketMoney() {
        return redPacketMoney;
    }

    public void setRedPacketMoney(double redPacketMoney) {
        this.redPacketMoney = redPacketMoney;
    }

    public String getRedPacketOpenTime() {
        return redPacketOpenTime;
    }

    public void setRedPacketOpenTime(String redPacketOpenTime) {
        this.redPacketOpenTime = redPacketOpenTime;
    }
}
