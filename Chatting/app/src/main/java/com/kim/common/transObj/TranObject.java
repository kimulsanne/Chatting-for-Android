package com.kim.common.transObj;

import java.io.Serializable;

/**
 * 传输的对象,直接通过Socket传输的对象
 */
public class TranObject<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private TranObjectType type;// 发送的消息类型

	private int fromUser;// 来自哪个用户
	
	private int toUser;// 发往哪个用户

	private T object;// 传输的对象，这个对象我们可以自定义任何
	
	public TranObject(TranObjectType type) {
		this.type = type;
	}

	public int getFromUser() {
		return fromUser;
	}

	public void setFromUser(int fromUser) {
		this.fromUser = fromUser;
	}

	public int getToUser() {
		return toUser;
	}

	public void setToUser(int toUser) {
		this.toUser = toUser;
	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

	public TranObjectType getType() {
		return type;
	}
}
