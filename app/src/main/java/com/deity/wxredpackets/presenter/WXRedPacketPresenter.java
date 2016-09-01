package com.deity.wxredpackets.presenter;

import com.deity.wxredpackets.biz.IWeChatBiz;
import com.deity.wxredpackets.biz.WeChatBizImpl;

/**
 * Created by Deity on 2016/9/1.
 */
public class WXRedPacketPresenter {
    private IWeChatBiz mWeChatBiz;

    public WXRedPacketPresenter(){
        mWeChatBiz = WeChatBizImpl.getInstance();
    }
}
