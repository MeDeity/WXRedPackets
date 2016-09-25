package com.deity.wxredpackets.dao;

import android.hardware.camera2.CameraMetadata;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * 微信红包数据库保存类
 * Created by Deity on 2016/9/2.
 */
public class WXRedPacketDaoImpl implements IWXRedPacketDao {
    private static final String TAG = WXRedPacketDaoImpl.class.getSimpleName();
    private static final SimpleDateFormat TIME_FORMAT= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private static WXRedPacketDaoImpl mWXRedPacketDaoImpl;

    private WXRedPacketDaoImpl(){};

    public static WXRedPacketDaoImpl getInstance(){
        if (null==mWXRedPacketDaoImpl){
            synchronized (WXRedPacketDaoImpl.class){
                if (null==mWXRedPacketDaoImpl){
                    mWXRedPacketDaoImpl=new WXRedPacketDaoImpl();
                }
            }
        }
        return mWXRedPacketDaoImpl;
    }

    /**
     * 添加红包信息到数据库
     *
     * @param entity
     */
    @Override
    public WXRedPacketEntity addWXRedPacket(WXRedPacketEntity entity) {
        Realm.getDefaultInstance().beginTransaction();
        Realm.getDefaultInstance().copyToRealmOrUpdate(entity);
        Realm.getDefaultInstance().commitTransaction();
        return entity;
    }

    /**
     * 更新红包信息,当前已被打开，该过程不可逆
     */
    @Override
    public void updateWXRedPacket(WXRedPacketEntity entity) {
        WXRedPacketEntity data2update = queryWXRedPacket(entity.getRedPacketId());
        if (null!=data2update) {
            Realm.getDefaultInstance().beginTransaction();
            data2update.setRedPacketMoney(entity.getRedPacketMoney());
            data2update.setPicked(true);
            Realm.getDefaultInstance().copyToRealmOrUpdate(entity);
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
//        for (WXRedPacketEntity entity:entities){
////            deleteWXRedPacket(entity);
//            entities.deleteAllFromRealm()
//        }
        entities.deleteAllFromRealm();
        Realm.getDefaultInstance().commitTransaction();
    }

    /**
     * 查找特定的红包信息
     *
     * 可能返回空
     */
    @Override
    public WXRedPacketEntity queryWXRedPacket(long redPacketId) {
        WXRedPacketEntity entity = Realm.getDefaultInstance().where(WXRedPacketEntity.class).equalTo("redPacketId",redPacketId).findFirst();
        return entity;
    }

    /**
     * 统计抢到的总金额数
     */
    @Override
    public double queryTotalMoney() {
        double totalMoney=0.0;
        RealmResults<WXRedPacketEntity> entities = Realm.getDefaultInstance().where(WXRedPacketEntity.class).findAll();
        if (!entities.isEmpty()){
            for (WXRedPacketEntity entity:entities){
                totalMoney+=entity.getRedPacketMoney();
            }
        }
        return totalMoney;
    }

    public RealmResults<WXRedPacketEntity> queryWXRedPacket() {
        RealmResults<WXRedPacketEntity> entities = Realm.getDefaultInstance().where(WXRedPacketEntity.class).findAllSorted("redPacketId", Sort.DESCENDING);
        return entities;
}

}