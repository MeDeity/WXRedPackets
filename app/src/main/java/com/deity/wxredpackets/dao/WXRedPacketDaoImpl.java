package com.deity.wxredpackets.dao;

import android.hardware.camera2.CameraMetadata;

import java.text.SimpleDateFormat;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * 微信红包数据库保存类
 * Created by Deity on 2016/9/2.
 */
public class WXRedPacketDaoImpl implements IWXRedPacketDao {
    private static final SimpleDateFormat TIME_FORMAT= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    /**
     * 添加红包信息到数据库
     *
     * @param entity
     */
    @Override
    public WXRedPacketEntity addWXRedPacket(WXRedPacketEntity entity) {
        Realm.getDefaultInstance().beginTransaction();
        Realm.getDefaultInstance().copyToRealm(entity);
        Realm.getDefaultInstance().commitTransaction();
        return entity;
    }

    /**
     * 更新红包信息,当前已被打开，该过程不可逆
     * @param redPacketSenderName 发送人
     * @param redPacketReceiveTime 发送时间
     */
    @Override
    public void updateWXRedPacket(String redPacketSenderName, String redPacketReceiveTime) {
        WXRedPacketEntity data2update = queryWXRedPacket(redPacketSenderName,redPacketReceiveTime);
        if (null!=data2update) {
            Realm.getDefaultInstance().beginTransaction();
            data2update.setPicked(true);
            Realm.getDefaultInstance().commitTransaction();
        }
    }

    /**
     * 删除单个红包信息
     *
     * @param entity
     */
    @Override
    public void deleteWXRedPacket(WXRedPacketEntity entity) {
        Realm.getDefaultInstance().beginTransaction();
        entity.deleteFromRealm();
        Realm.getDefaultInstance().commitTransaction();
    }

    /**
     * 根据时间批量删除红包信息
     *
     * @param timestamp
     */
    @Override
    public void deleteWXRedPacketTx(long timestamp) {
//        Realm.getDefaultInstance().where(WXRedPacketEntity.class).lessThan("redPacketReceiveTime",)
    }

    /**
     * 清空所有的红包信息
     */
    @Override
    public void deleteWXRedPacketTx() {
        Realm.getDefaultInstance().beginTransaction();
        RealmResults<WXRedPacketEntity> entities = Realm.getDefaultInstance().where(WXRedPacketEntity.class).findAll();
        for (WXRedPacketEntity entity:entities){
            deleteWXRedPacket(entity);
        }
        Realm.getDefaultInstance().commitTransaction();
    }

    /**
     * 查找特定的红包信息
     *
     * @param redPacketSenderName 发送人
     * @param redPacketReceiveTime 发送时间
     * 可能返回空
     */
    @Override
    public WXRedPacketEntity queryWXRedPacket(String redPacketSenderName, String redPacketReceiveTime) {
        WXRedPacketEntity entity = Realm.getDefaultInstance().where(WXRedPacketEntity.class).equalTo("redPacketSenderName",redPacketSenderName).equalTo("redPacketReceiveTime",redPacketReceiveTime).findFirst();
        return entity;
    }
}
