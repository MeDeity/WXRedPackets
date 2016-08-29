package com.deity.wxredpackets.biz;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.deity.wxredpackets.data.AppParameters;

/**
 * 微信业务逻辑实现
 * Created by Deity on 2016/8/28.
 */
@SuppressWarnings("unused")
public class WeChatBizImpl implements IWeChatBiz {
    @Override
    public void sendComment() {

    }

    @Override
    public boolean watchNotifications(AccessibilityEvent event) {
        if (event.getEventType()!=AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED){//通知栏变动
            return false;
        }
        //如果通知栏中没有指定的字符串，也不是微信红包
        if (event.getText().toString().equals(AppParameters.MSG_WECHAT_REDPACKET)){
            return true;
        }
        return false;
    }

    @Override
    public AccessibilityNodeInfo getTheLastNode(String... texts) {
        return null;
    }

    @Override
    public void checkNodeInfo(int eventType) {

    }

    /**
     * 找到可点击的按钮，拆红包用
     * @param node 节点遍历，拆红包按钮
     * @return 返回按钮
     */
    @Override
    public AccessibilityNodeInfo mFindOpenButton(AccessibilityNodeInfo node) {
        if (null==node) return null;
        int size = node.getChildCount();
        for (int i = 0;i<size;i++){
            AccessibilityNodeInfo nodeTemp = node.getChild(i);
            if(nodeTemp.getClassName().equals(AppParameters.WIDGET_BUTTON)){//只要是按钮Button,立即返回
                return nodeTemp;
            }
        }
        return null;
    }
}
