package com.deity.wxredpackets.dao;

/**
 * Created by Deity on 2016/9/2.
 */
public interface IWXRedPacketDao {
    /**添加红包信息到数据库*/
    WXRedPacketEntity addWXRedPacket(WXRedPacketEntity entity);
    /**更新红包信息*/
    void updateWXRedPacket(WXRedPacketEntity entity);
    /**删除单个红包信息*/
    void deleteWXRedPacket(WXRedPacketEntity entity);
    /**根据时间批量删除红包信息*/
    void deleteWXRedPacketTx(long timestamp);
    /**清空所有的红包信息*/
    void deleteWXRedPacketTx();
    /**查找特定的红包信息,通过发送人,及发送时间可以精确定位到特定的红包*/
    WXRedPacketEntity queryWXRedPacket(long redPacketId);
    /**统计抢到的总金额数*/
    double queryTotalMoney();

}
