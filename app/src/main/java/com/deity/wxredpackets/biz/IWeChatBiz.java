package com.deity.wxredpackets.biz;

import android.accessibilityservice.AccessibilityService;
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
    /**检测聊天列表中的红包*/
    boolean watchWeChatList(AccessibilityEvent event);
    /**检测聊天信息中的红包*/
    boolean watchWeChat(AccessibilityService mAccessibilityService, AccessibilityEvent event);
    /**获取最后一个节点*/
    AccessibilityNodeInfo getTheLastNode(AccessibilityNodeInfo root,String... texts);
    /**检测节点*/
    void checkNodeInfo(AccessibilityNodeInfo root);
    /**找到红包打开的按钮*/
    AccessibilityNodeInfo mFindOpenButton(AccessibilityNodeInfo node);
    /**获取当前的页面信息*/
    void setCurrentActivityName(Context context,AccessibilityEvent event);
    /**点击按钮*/
    void clickButton(AccessibilityNodeInfo node);
    /**
     * 返回上一页面
     * 如果检测到当前在以下页面 手慢了、明细表等
     * */
    void comeBack();


}
