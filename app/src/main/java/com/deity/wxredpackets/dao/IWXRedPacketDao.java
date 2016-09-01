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
    WXRedPacketEntity deleteWXRedPacket(WXRedPacketEntity entity);
    /**根据时间批量删除红包信息*/
    void deleteWXRedPacketTx(long timestamp);
    /**清空所有的红包信息*/
    void deleteWXRedPacketTx();

}
