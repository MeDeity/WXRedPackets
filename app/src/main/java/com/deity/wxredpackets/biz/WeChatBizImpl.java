package com.deity.wxredpackets.biz;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.deity.wxredpackets.data.AppParameters;

import java.util.List;

/**
 * 微信业务逻辑实现
 * Created by Deity on 2016/8/28.
 */
@SuppressWarnings("unused")
public class WeChatBizImpl implements IWeChatBiz {
    private static WeChatBizImpl mWeChatBizImpl;

    private WeChatBizImpl(){};

    public static WeChatBizImpl getInstance(){
        if (null==mWeChatBizImpl){
            synchronized (WeChatBizImpl.class){
                if (null==mWeChatBizImpl){
                    mWeChatBizImpl = new WeChatBizImpl();
                }
            }
        }
        return mWeChatBizImpl;
    }

    private String currentActivityName = AppParameters.WECHAT_LUCKMONEY_GENERAL_ACTIVITY;
    private final static String TAG =  WeChatBizImpl.class.getSimpleName();

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
            //是否需要保存到数据库中
            Log.d(TAG,"检测到红包信息,但是不知道打开了没");
            return true;
        }
        Log.d(TAG,"未检测到红包信息");
        return false;
    }

    @Override
    public AccessibilityNodeInfo getTheLastNode(String... texts) {
        return null;
    }

    @Override
    public void checkNodeInfo(AccessibilityEvent event) {

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

    /**
     * 获取当前的页面信息
     *
     * @param event 事件
     */
    @Override
    public void setCurrentActivityName(Context context,AccessibilityEvent event) {
        if(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED!=event.getEventType()){//打开一个PopupWindow、Menu、Dialog ,etc
            return;
        }
        try{
            ComponentName componentName = new ComponentName(event.getPackageName().toString(),event.getClassName().toString());
            //TODO
            context.getPackageManager().getActivityInfo(componentName, 0);
            currentActivityName = componentName.flattenToShortString();
        }catch (PackageManager.NameNotFoundException e){
            currentActivityName = "";
        }
        Log.d(TAG,"currentActivityName>>>"+currentActivityName);
    }

    /**
     * 检测聊天界面中的红包
     *
     * @param event
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean watchWeChatList(AccessibilityEvent event) {
        if (AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED!=event.getEventType()||event.getSource() == null){//聊天界面未发生变化 || event.getSource() == nul
            return false;
        }
        List<AccessibilityNodeInfo> accessNodes = event.getSource().findAccessibilityNodeInfosByText(AppParameters.WECHAT_NOTIFICATION_TIP);
        if (!accessNodes.isEmpty()){
            AccessibilityNodeInfo accessNode = accessNodes.get(0);

            Log.d(TAG,"聊天列表中检测到红包信息,并尝试打开");
            //TODO 检测当前的节点是否点击过
//            CharSequence contentDescription = nodeToClick.getContentDescription();
//            if (contentDescription != null && !lastContentDescription.equals(contentDescription)) {
//                nodeToClick.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                lastContentDescription = contentDescription.toString();
//                return true;
//            }
            accessNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return true;
        }
        return false;
    }

    /**
     * 检测聊天信息中的红包
     *
     * @param event
     */
    @Override
    public boolean watchWeChat(AccessibilityEvent event) {
//        AccessibilityService
        return false;
    }
}
