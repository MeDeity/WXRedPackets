package com.deity.wxredpackets.biz;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * 微信操作事件
 * 1.一般来说，需要监听TYPE_WINDOW_STATE_CHANGED状态事件
 * Created by Deity on 2016/8/28.
 */
public interface IWeChatBiz {
    /**发送评论*/
    void sendComment();
    /**监听通知栏*/
    boolean watchNotifications(AccessibilityEvent event);
    /**获取最后一个节点*/
    AccessibilityNodeInfo getTheLastNode(String... texts);
    /**检测节点*/
    void checkNodeInfo(int eventType);
    /**找到红包打开的按钮*/
    AccessibilityNodeInfo mFindOpenButton(AccessibilityNodeInfo node);

}
