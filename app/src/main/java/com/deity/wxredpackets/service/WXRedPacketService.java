package com.deity.wxredpackets.service;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.deity.wxredpackets.biz.IWeChatBiz;
import com.deity.wxredpackets.biz.WeChatBizImpl;

/**
 * 抢红包服务线程
 * Created by Deity on 2016/8/26.
 */
public class WXRedPacketService extends AccessibilityService {
    private final static String TAG=WXRedPacketService.class.getSimpleName();
    private IWeChatBiz mWeChatBiz = WeChatBizImpl.getInstance();
    /**检测到事件*/
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        /**
         * 1.当窗口有变化或者通知有变化时，检测红包信息，并将红包信息加入到数据库中
         * 2.检测节点动态，找出所有未打开的红包
         * 3.拆开红包，成功后，更新数据库信息并关闭当前页面
         */
        Log.d(TAG,"accessibilityEvent>>>>");
        mWeChatBiz.setCurrentActivityName(this,accessibilityEvent);
        mWeChatBiz.watchNotifications(accessibilityEvent);
        mWeChatBiz.watchWeChatList(accessibilityEvent);
    }

    @Override
    public void onInterrupt() {

    }
}
