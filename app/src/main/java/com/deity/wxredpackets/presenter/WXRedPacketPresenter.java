package com.deity.wxredpackets.presenter;

import android.view.accessibility.AccessibilityManager;

import com.deity.wxredpackets.biz.IWeChatBiz;
import com.deity.wxredpackets.biz.WeChatBizImpl;
import com.deity.wxredpackets.view.IWXRedPacketView;

/**
 * Created by Deity on 2016/9/1.
 */
public class WXRedPacketPresenter {
    private IWeChatBiz mWeChatBiz;
    private IWXRedPacketView mWXRedPacketView;

    public WXRedPacketPresenter(IWXRedPacketView mWXRedPacketView){
        mWeChatBiz = WeChatBizImpl.getInstance();
        this.mWXRedPacketView = mWXRedPacketView;
    }

    public void updateServiceState(AccessibilityManager manager){
        if (mWeChatBiz.isServiceEnable(manager)){
            mWXRedPacketView.closeService();
        }else {
            mWXRedPacketView.openService();
        }
    }
}
