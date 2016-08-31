package com.deity.wxredpackets.biz;

import android.content.Context;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * 微信操作事件
 * 1.一般来说，需要监听TYPE_WINDOW_STATE_CHANGED状态事件
 * Created by Deity on 2016/8/28.
 */
@SuppressWarnings("unused")
public interface IWeChatBiz {
    /**发送评论*/
    void sendComment();
    /**
     * 监听通知栏
     * 如果用户没有在微信界面，可以通过通知栏拦截微信红包消息
     * 控制是否往下执行的标志
     * */
    boolean watchNotifications(AccessibilityEvent event);
    /**获取最后一个节点*/
    AccessibilityNodeInfo getTheLastNode(String... texts);
    /**检测节点*/
    void checkNodeInfo(AccessibilityEvent event);
    /**找到红包打开的按钮*/
    AccessibilityNodeInfo mFindOpenButton(AccessibilityNodeInfo node);
    /**获取当前的页面信息*/
    void setCurrentActivityName(Context context,AccessibilityEvent event);
    /**检测聊天界面中的红包，控制是否执行下去的标志*/
    boolean watchWeChatList(AccessibilityEvent event);

}
