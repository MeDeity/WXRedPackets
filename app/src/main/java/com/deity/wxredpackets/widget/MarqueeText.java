/**
 * <table><tr><td><b>Project Name</b></td><td>KT_TITS_2.7.2</td></tr>
 * <tr><td><b>File Name</b></td><td>MarqueeText.java</td></tr>
 * <tr><td><b>Package Name</b></td><td>com.keytop.TITS.widget</td></tr>
 * <tr><td><b>Date</b></td><td>2015年8月21日下午3:05:14</td></tr>
 * Copyright (c) 2015, <b>KEYTOP</b>  All Rights Reserved.
 *
*/

package com.deity.wxredpackets.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * <table><tr><td><b>ClassName</b></td><td>MarqueeText</td></tr>
 * <tr><td><b>Function</b></td><td>主要用在需要使用跑马灯效果场景</td></tr>
 * <tr><td><b>Reason</b></td><td>
 * 使用过程中需要在xml中设置如下属性:<br>
 * android:ellipsize="marquee"
 * android:marqueeRepeatLimit="marquee_forever"
 * android:singleLine="true"
 * <tr><td><b>Date</b></td><td>2015年8月21日 下午3:05:14 ;</td></tr>
 * @author   fengwh@rd.keytop.com.cn
 * @version  
 * @see 	 
 */
public class MarqueeText extends TextView {

	public MarqueeText(Context context) {
		super(context);
	}

	public MarqueeText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MarqueeText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean isFocused() {
		return true;
	}
	
}

