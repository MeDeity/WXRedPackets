package com.deity.wxredpackets.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.os.Build;
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
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        /***
         * 窗口状态改变测试
         * ①TYPE_WINDOW_STATE_CHANGED 打开PopupWindow|Menu|Dialog 等
         * ②
         */
        /**
         * 1.当窗口有变化或者通知有变化时，检测红包信息，并将红包信息加入到数据库中
         * 2.检测节点动态，找出所有未打开的红包
         * 3.拆开红包，成功后，更新数据库信息并关闭当前页面
         */
        mWeChatBiz.setCurrentActivityName(this,accessibilityEvent);
        switch (accessibilityEvent.getEventType()){
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                Log.e(TAG,"打开PopupWindow|Menu|Dialog 等");
                mWeChatBiz.openRedPacket(WXRedPacketService.this.getRootInActiveWindow());
                break;
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                Log.e(TAG,"检测到通知栏变化");
                mWeChatBiz.watchNotifications(accessibilityEvent);// TYPE_NOTIFICATION_STATE_CHANGED 目前来看 该方法已经失效
                break;
//            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
//                Log.e(TAG,"编辑框文本发生变化");
//                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                Log.e(TAG,"窗口内容发生变化");
                mWeChatBiz.watchWeChat(WXRedPacketService.this,accessibilityEvent);
                mWeChatBiz.watchWeChatList(accessibilityEvent);
                mWeChatBiz.updateRedPacketMoney(WXRedPacketService.this);
                mWeChatBiz.sendComment(WXRedPacketService.this.getRootInActiveWindow());
                break;
//            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
//                Log.e(TAG,"滚动Scroll");
//                break;
//            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
//                Log.e(TAG,"EditText内容发生改变");
//                break;
//            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
//                Log.e(TAG,"Represents the event change in the windows shown on the screen");
//                break;
//            case AccessibilityEvent.CONTENT_CHANGE_TYPE_UNDEFINED:
//                Log.e(TAG,"Change type for {@link #TYPE_WINDOW_CONTENT_CHANGED} event:The type of change is not defined.");
//                break;
            default:
                break;
        }
    }

    @Override
    public void onInterrupt() {
//        Log.d(TAG,"onInterrupt>>>>");
    }
}
