package com.deity.wxredpackets.dao;

/**
 * 微信红包数据库保存类
 * Created by Deity on 2016/9/2.
 */
public class WXRedPacketDaoImpl implements IWXRedPacketDao {

    /**
     * 添加红包信息到数据库
     *
     * @param entity
     */
    @Override
    public WXRedPacketEntity addWXRedPacket(WXRedPacketEntity entity) {
        return null;
    }

    /**
     * 更新红包信息
     *
     * @param entity
     */
    @Override
    public void updateWXRedPacket(WXRedPacketEntity entity) {

    }

    /**
     * 删除单个红包信息
     *
     * @param entity
     */
    @Override
    public WXRedPacketEntity deleteWXRedPacket(WXRedPacketEntity entity) {
        return null;
    }

    /**
     * 根据时间批量删除红包信息
     *
     * @param timestamp
     */
    @Override
    public void deleteWXRedPacketTx(long timestamp) {

    }

    /**
     * 清空所有的红包信息
     */
    @Override
    public void deleteWXRedPacketTx() {

    }
}
