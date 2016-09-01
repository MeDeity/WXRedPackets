package com.deity.wxredpackets.data;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * 微信红包助手Application
 * Created by Deity on 2016/9/1.
 */
public class WXRedPacketApplication extends Application{
    public static WXRedPacketApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initDB();
    }

    public void initDB(){
        //不关心数据库更新，数据库变动，则删除数据库
        RealmConfiguration config = new RealmConfiguration.Builder(getApplicationContext()).deleteRealmIfMigrationNeeded() .build();
        Realm.setDefaultConfiguration(config);
        /**
         * 如何使用
         * Realm.getDefaultInstance();
         */
    }
}
