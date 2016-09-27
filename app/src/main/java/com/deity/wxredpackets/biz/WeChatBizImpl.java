package com.deity.wxredpackets.biz;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;

import com.deity.wxredpackets.SmallUtils;
import com.deity.wxredpackets.dao.IWXRedPacketDao;
import com.deity.wxredpackets.dao.WXRedPacketDaoImpl;
import com.deity.wxredpackets.dao.WXRedPacketEntity;
import com.deity.wxredpackets.data.AppParameters;
import com.deity.wxredpackets.data.WXRedPacketApplication;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Handler;

/**
 * 微信业务逻辑实现
 * Created by Deity on 2016/8/28.
 */
@SuppressWarnings("unused")
public class WeChatBizImpl implements IWeChatBiz {
    private static WeChatBizImpl mWeChatBizImpl;
    private IWXRedPacketDao mWXRedPacketDao;
    private WXRedPacketEntity currentPacketEntity;

    private WeChatBizImpl(){}

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void editComment(AccessibilityNodeInfo root,boolean needEmpty) {
        if (!WXRedPacketApplication.instance.getSharePreference().getBoolean("pref_auto_send",true)) return;
        if (null==root) return;
        List<AccessibilityNodeInfo> childNodes = root.findAccessibilityNodeInfosByViewId(AppParameters.WIDGET_EDITTEXT_ID);
        if (null==childNodes||childNodes.isEmpty()) return;
        String comment="";
        if (!needEmpty){
            comment = randomComment();
            if (null!=currentPacketEntity){
                comment = comment+"@"+currentPacketEntity.getRedPacketSenderName();
            }
            Log.e(TAG,"当前感谢语为>>>"+comment);
        }
        AccessibilityNodeInfo editNode = childNodes.get(0);
        Bundle arguments = new Bundle();
        arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,comment);
        editNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
    }

    public String randomComment(){
        String commentStr = WXRedPacketApplication.instance.getSharePreference().getString("pref_send_data",AppParameters.DEFAULT_THX);
        String[] comments = commentStr.split(" ");
        if (comments.length<=0) return "";
        Random random = new Random();
        int selectInt = random.nextInt(comments.length);
        return comments[selectInt];
    }

    /**
     * 发送评论
     *
     * @param root
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void sendComment(AccessibilityNodeInfo root) {
        if (!WXRedPacketApplication.instance.getSharePreference().getBoolean("pref_auto_send",true)) return;
        if (null==root) return;
        if (null==currentPacketEntity||currentPacketEntity.isPicked()) {
//            editComment(root,true);//清空EditText
            return;
        }
        if (currentActivityName.contains(AppParameters.WECHAT_LUCKMONEY_GENERAL_ACTIVITY)) {
            List<AccessibilityNodeInfo> childNodes = root.findAccessibilityNodeInfosByViewId(AppParameters.WIDGET_EDITTEXT_ID);
            if (null==childNodes||childNodes.isEmpty()) return;
            List<AccessibilityNodeInfo> sendNodes = root.findAccessibilityNodeInfosByViewId(AppParameters.WIDGET_SEND_ID);
            if (null == sendNodes || sendNodes.isEmpty()) return;
            AccessibilityNodeInfo sendNode = sendNodes.get(0);
            sendNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    /***
     * 微信在防止外挂上，将Notifications的Type:TYPE_NOTIFICATION_STATE_CHANGED强制改变成了TYPE_WINDOW_CONTENT_CHANGED
     * 目前状态栏监听主流上的抢红包软件功能全军覆没
     * @param event
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean watchNotifications(AccessibilityEvent event) {
        if (!WXRedPacketApplication.instance.getSharePreference().getBoolean("pref_watch_notification",true)) return false;
        Log.e(TAG,"检测通知栏成功..."+event.getText().toString());
        //如果通知栏中没有指定的字符串，也不是微信红包
        if (event.getText().toString().contains(AppParameters.MSG_WECHAT_REDPACKET)){
            //是否需要保存到数据库中
            Log.e(TAG,"检测到红包信息,尝试打开通知栏!");
            Parcelable parcelable = event.getParcelableData();
            if (parcelable instanceof Notification){
                Notification notification = (Notification) parcelable;
                try {
                    notification.contentIntent.send();
                } catch (PendingIntent.CanceledException e) {e.printStackTrace();}
            }
            return true;
        }
        Log.e(TAG,"检测到通知栏,但不是红包信息");
        return false;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public AccessibilityNodeInfo getTheLastNode(AccessibilityNodeInfo root,String... texts) {
        int boundBottom=0;
        Log.e(TAG,"getTheLastNode currentActivityName[LauncherUI]>>>"+currentActivityName);
        if (null==root||!currentActivityName.contains(AppParameters.WECHAT_LUCKMONEY_GENERAL_ACTIVITY)){
            Log.e(TAG,"根目录为空,找不到红包节点||不是聊天界面");
            return null;
        }
        AccessibilityNodeInfo lastNode = null,tmpNode;
        for (String text:texts){
            if (TextUtils.isEmpty(text)) continue;
            Log.e(TAG,"当前查找的字符串为:"+text);
            List<AccessibilityNodeInfo> nodeList = root.findAccessibilityNodeInfosByText(text);
            if (null!=nodeList&&!nodeList.isEmpty()){
                int size = nodeList.size();
                Rect nodeRect = new Rect();
                tmpNode = nodeList.get(size-1);
                if (null==tmpNode) return null;
                tmpNode.getBoundsInScreen(nodeRect);
                if (nodeRect.bottom>boundBottom){
                    boundBottom = nodeRect.bottom;
                    lastNode = tmpNode;
                }
            }
        }
        return lastNode;
    }

    /***
     * 检测红包并打开到红包界面
     * @param root
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public boolean checkIsExistRedPacketNode(AccessibilityNodeInfo root) {
        if (null==root) return false;
        AccessibilityNodeInfo nodeInfo = getTheLastNode(root,AppParameters.WECHAT_VIEW_OTHERS_CH,AppParameters.WECHAT_VIEW_SELF_CH,AppParameters.WECHAT_NOTIFICATION_TIP);//str>>领取红包
        if (null!=nodeInfo) {
            //TODO 进行保存并记录
            if (isNewRedPacketData(nodeInfo)){
//                editComment(root,false);//感谢语
                nodeInfo.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
            return true;
        }
        return false;
    }
    /**保存红包信息到数据库*/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean isNewRedPacketData(AccessibilityNodeInfo nodeInfo){//领取红包，查看红包 节点
        if (null==nodeInfo) return false;
        AccessibilityNodeInfo parentNode = nodeInfo.getParent();
        if (null==parentNode) return false;
        /**节点必须是LINEARLAYOUT*/
        String clzName = TextUtils.isEmpty(parentNode.getClassName())?"":parentNode.getClassName().toString();
        if (!AppParameters.WIDGET_LINEARLAYOUT.equals(clzName)) return false;
        /**子节点必须包含特定字符串*/
        String redPacketContent = parentNode.getChild(0).getText().toString();
        if (TextUtils.isEmpty(redPacketContent)||redPacketContent.equals(AppParameters.WECHAT_VIEW_SELF_CH)) return false;
        AccessibilityNodeInfo granpaNode = parentNode.getParent();
        /**在屏幕中必须是可见的*/
        Rect rect = new Rect();
        granpaNode.getBoundsInScreen(rect);
        if (rect.top<0) return false;
        WXRedPacketEntity entity =  getRedPacketMessage(redPacketContent,granpaNode);
        //防止多次打开同一个红包
        if (null!=currentPacketEntity&&currentPacketEntity.getRedPacketSenderName().equals(entity.getRedPacketSenderName())&&currentPacketEntity.getRedPacketReceiveTime().equals(entity.getRedPacketReceiveTime())&&currentPacketEntity.getRedPacketMessage().equals(entity.getRedPacketMessage())) return false;
        currentPacketEntity = entity;
        return true;
    }

    public WXRedPacketEntity getRedPacketMessage(String redPacketContent,AccessibilityNodeInfo node){
        WXRedPacketEntity entity = new WXRedPacketEntity();
        entity.setRedPacketId(new Date().getTime());
        int size = node.getChildCount();
        for (int i=0;i<size;i++){
            AccessibilityNodeInfo childNode = node.getChild(i);
            if (AppParameters.WIDGET_IMAGEVIEW.equals(childNode.getClassName())){
                CharSequence redPacketSenderName = childNode.getContentDescription();
                entity.setRedPacketSenderName(TextUtils.isEmpty(redPacketSenderName)?"神秘土豪":redPacketSenderName.toString().replaceAll("头像$",""));
            }
            if (AppParameters.WIDGET_TEXTVIEW.equals(childNode.getClassName())){
                CharSequence redPacketTime = childNode.getText();
                entity.setRedPacketReceiveTime(TextUtils.isEmpty(redPacketTime)?SmallUtils.TIME_FORMAT.format(new Date()):redPacketTime.toString());
            }
            entity.setRedPacketOpenTime(SmallUtils.TIME_FORMAT.format(new Date()));
        }
        entity.setRedPacketMessage(redPacketContent);
        return entity;
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
                Log.e(TAG,"找到了按钮");
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
        Log.e(TAG,"GetClass>>>"+event.getClassName().toString()+" currentActivityName>>>"+currentActivityName);
    }

    /**
     * 点击按钮
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void clickButton(AccessibilityNodeInfo node) {
        if (null!=node){
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    /**
     * 打开红包
     */
    @Override
    public void openRedPacket(AccessibilityNodeInfo root) {
        if (!WXRedPacketApplication.instance.getSharePreference().getBoolean("pref_auto_open",true)) return;
        if (null!=root&&currentActivityName.contains(AppParameters.WECHAT_LUCKMONEY_RECEIVE_ACTIVITY)) {
            final AccessibilityNodeInfo clickButton = mFindOpenButton(root);
            if (null!=clickButton) {
                if (null==currentPacketEntity) return;
                WXRedPacketDaoImpl.getInstance().addWXRedPacket(currentPacketEntity);
                int delay = WXRedPacketApplication.instance.getSharePreference().getInt("pref_open_delay",0);
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        clickButton(clickButton);
                    }
                },delay*1000);

            }
        }
    }

    /**
     * 检测是否含有特定的字符串
     * @param texts
     */
    @Override
    public boolean hasAmouseNodes(AccessibilityNodeInfo root,String... texts) {
        if (null!=root&& null!=texts&&texts.length>0){
            for (String query:texts){
                List<AccessibilityNodeInfo> accessibilityNodeInfoList = root.findAccessibilityNodeInfosByText(query);
                if (!SmallUtils.isListEmpty(accessibilityNodeInfoList)) return true;
            }
        }
        return false;
    }

    /**
     * 返回上一页面
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void comeBack(AccessibilityService mAccessibilityService) {
        /* 戳开红包，红包已被抢完，遍历节点匹配“红包详情”和“手慢了” */
        boolean hasNodes = hasAmouseNodes(mAccessibilityService.getRootInActiveWindow(),
                AppParameters.WECHAT_BETTER_LUCK_CH, AppParameters.WECHAT_DETAILS_CH,
                AppParameters.WECHAT_BETTER_LUCK_EN, AppParameters.WECHAT_DETAILS_EN, AppParameters.WECHAT_EXPIRES_CH);
        Log.e(TAG,"检测回退条件...");
        if (hasNodes&&(currentActivityName.contains(AppParameters.WECHAT_LUCKMONEY_DETAIL_ACTIVITY)
                || currentActivityName.contains(AppParameters.WECHAT_LUCKMONEY_RECEIVE_ACTIVITY))){
            Log.e(TAG,"回退操作");
            mAccessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            //更新红包信息，当前已拆开
//            if (null!=currentPacketEntity) currentPacketEntity.setPicked(true);//已拆开
        }
        //TODO 回到软件界面

    }

    /**
     * 是否开启无障碍服务
     */
    @Override
    public boolean isServiceEnable(AccessibilityManager manager) {
        List<AccessibilityServiceInfo> accessibilityServiceList = manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info:accessibilityServiceList){
            if (info.getId().equals(WXRedPacketApplication.instance.getPackageName()+"/.service.WXRedPacketService")){
                Log.e(TAG,"插件已开启");
                return true;
            }
        }
        Log.e(TAG,"插件未开启");
        return false;

    }

    /**
     * 检测聊天列表中的红包信息
     *
     * @param event
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean watchWeChatList(AccessibilityEvent event) {
        if (!WXRedPacketApplication.instance.getSharePreference().getBoolean("pref_watch_wechatlist",true)) return false;
        if (AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED!=event.getEventType()||event.getSource() == null){//聊天界面未发生变化 || event.getSource() == nul
            return false;
        }
        List<AccessibilityNodeInfo> accessNodes = event.getSource().findAccessibilityNodeInfosByText(AppParameters.WECHAT_NOTIFICATION_TIP);
        if (!accessNodes.isEmpty()){
            AccessibilityNodeInfo nodeToClick = accessNodes.get(0);

            Log.e(TAG,"聊天列表中检测到红包信息,并尝试打开");
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
        if (!WXRedPacketApplication.instance.getSharePreference().getBoolean("pref_watch_wechat",true)) return false;
        //1.检测窗口是否可以获取
        AccessibilityNodeInfo rootInActiveWindow = mAccessibilityService.getRootInActiveWindow();
        if (null == rootInActiveWindow) return false;
        //2.检测节点并打开
        boolean isExistRedPacketNode = checkIsExistRedPacketNode(rootInActiveWindow);
        if (!isExistRedPacketNode) {
            Log.e(TAG,"isExistRedPacketNode come back");
            comeBack(mAccessibilityService);
            return false;
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void updateRedPacketMoney(AccessibilityService mAccessibilityService){
        if (null!=currentPacketEntity&&!currentPacketEntity.isPicked()&&currentActivityName.contains(AppParameters.WECHAT_LUCKMONEY_DETAIL_ACTIVITY)){
            //1.检测窗口是否可以获取
            AccessibilityNodeInfo rootInActiveWindow = mAccessibilityService.getRootInActiveWindow();
            if (null == rootInActiveWindow) return;
            List<AccessibilityNodeInfo> childNodes = rootInActiveWindow.findAccessibilityNodeInfosByViewId(AppParameters.WIDGET_MONEY_ID);
            if (null!=childNodes&&!childNodes.isEmpty()){
                double moneyValue=0.0;
                AccessibilityNodeInfo moneyNode = childNodes.get(0);
                try {
                    moneyValue = Double.parseDouble(moneyNode.getText().toString());
                }catch (Exception e){//转化失败
                    Log.e(TAG,moneyNode.getText().toString()+"转Double失败");
                }
                currentPacketEntity.setRedPacketMoney(moneyValue);
                WXRedPacketDaoImpl.getInstance().updateWXRedPacket(currentPacketEntity);
                Log.e(TAG,"over come back");
                comeBack(mAccessibilityService);
            }
        }
    }
}
