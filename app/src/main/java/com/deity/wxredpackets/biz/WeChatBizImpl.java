package com.deity.wxredpackets.biz;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Parcelable;
import android.text.TextUtils;
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

    /***
     * 微信在防止外挂上，将Notifications的Type:TYPE_NOTIFICATION_STATE_CHANGED强制改变成了TYPE_WINDOW_CONTENT_CHANGED
     * 目前状态栏监听主流上的抢红包软件功能全军覆没
     * @param event
     * @return
     */
    @Deprecated
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean watchNotifications(AccessibilityEvent event) {
        if (event.getEventType()!=AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED){//通知栏变动
            return false;
        }
        Log.d(TAG,"检测通知栏成功...");
        //如果通知栏中没有指定的字符串，也不是微信红包
        if (event.getText().toString().contains(AppParameters.MSG_WECHAT_REDPACKET)){
            //是否需要保存到数据库中
            Log.d(TAG,"检测到红包信息,但是不知道打开了没");
            Parcelable parcelable = event.getParcelableData();
            if (parcelable instanceof Notification){
                Notification notification = (Notification) parcelable;
                try {
                    notification.contentIntent.send();
                } catch (PendingIntent.CanceledException e) {e.printStackTrace();}
            }
            return true;
        }
        Log.d(TAG,"未检测到红包信息");
        return false;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public AccessibilityNodeInfo getTheLastNode(AccessibilityNodeInfo root,String... texts) {
        if (null==root||!currentActivityName.contains(AppParameters.WECHAT_LUCKMONEY_GENERAL_ACTIVITY)){
            Log.d(TAG,"根目录为空,找不到红包节点||不是聊天界面");
            return null;
        }
        AccessibilityNodeInfo lastNode = null;
        for (String text:texts){
            if (TextUtils.isEmpty(text)) continue;
            Log.d(TAG,"当前查找的字符串为:"+text);
            List<AccessibilityNodeInfo> nodeList = root.findAccessibilityNodeInfosByText(text);
            if (null!=nodeList&&!nodeList.isEmpty()){
                int size = nodeList.size();
                Rect nodeRect = new Rect();
                lastNode = nodeList.get(size-1);
                if (null!=lastNode){
                    Log.d(TAG,text+"找到红包节点");
                }else {
                    Log.d(TAG,text+"找不到红包节点");
                }
                //TODO 可见的情况下才可以被点开，防封号判断
//                node.getBoundsInScreen(nodeRect);
//                if (nodeRect.bottom>0&&)
            }
        }
        if (null==lastNode){
            Log.d(TAG,"找不到红包节点");
        }else {
            lastNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
        return lastNode;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void checkNodeInfo(AccessibilityNodeInfo root) {
        if (null==root) return;
        AccessibilityNodeInfo nodeInfo = getTheLastNode(root,AppParameters.WECHAT_VIEW_OTHERS_CH);
        if (null!=nodeInfo) {
            nodeInfo.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
        AccessibilityNodeInfo clickButton = mFindOpenButton(root);
        clickButton(clickButton);
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
            if (TextUtils.isEmpty(nodeTemp.getClassName())) continue;
            if(nodeTemp.getClassName().equals(AppParameters.WIDGET_BUTTON)){//只要是按钮Button,立即返回
                Log.d(TAG,"找到了按钮");
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
     * 点击按钮
     */
    @Override
    public void clickButton(AccessibilityNodeInfo node) {
        if (null!=node){
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    /**
     * 返回上一页面
     */
    @Override
    public void comeBack() {
//        performGlobalAction(GLOBAL_ACTION_BACK);
//        Intent startMain = new Intent()
    }

    /**
     * 检测聊天列表中的红包信息
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
            AccessibilityNodeInfo nodeToClick = accessNodes.get(0);

            Log.d(TAG,"聊天列表中检测到红包信息,并尝试打开");
            //TODO 检测当前的节点是否点击过
            clickButton(nodeToClick);
            return true;
        }
        return false;
    }

    /**
     * 检测聊天信息中的红包
     * 需要检索retrieve window content的权限
     * @param event
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean watchWeChat(AccessibilityService mAccessibilityService, AccessibilityEvent event) {
        //1.检测窗口是否可以获取
        AccessibilityNodeInfo rootInActiveWindow = mAccessibilityService.getRootInActiveWindow();
        if (null == rootInActiveWindow) return false;
        //2.检测节点并打开
        checkNodeInfo(rootInActiveWindow);
        return false;
    }
}
