package com.kim.client;

import com.kim.common.transObj.TranObject;
import com.kim.common.transObj.TranObjectType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 客户端写消息线程
 */
public class ClientOutputThread extends Thread {
	private Socket socket;
	private ObjectOutputStream oos;
	private boolean isStart = true;
	private TranObject msg;

	public ClientOutputThread(Socket socket) {
		this.socket = socket;
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

	// 设置消息并且唤醒发消息的线程
	public void setMsg(TranObject msg) {
		this.msg = msg;
		synchronized (this) {
			notify();
		}
	}

	@Override
	public void run() {
		try {
			while (isStart) {
				if (msg != null) {
					oos.writeObject(msg);
					oos.flush();
					if (msg.getType() == TranObjectType.LOGOUT) {// 如果是发送下线的消息，就直接跳出循环
						break;
					}
					synchronized (this) {
						wait();// 发送完消息后，线程进入等待状态
					}
				}
			}
			oos.close();// 循环结束后，关闭输出流和socket
			if (socket != null)
				socket.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
