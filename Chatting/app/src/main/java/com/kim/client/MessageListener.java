package com.kim.client;

import com.kim.common.transObj.TranObject;

/**
 * 消息监听接口
 */
public interface MessageListener {
	public void Message(TranObject msg);
}
