package com.kim.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.kim.common.bean.*;
import com.kim.common.transObj.*;
import com.kim.common.utils.Constants;
import com.kim.dao.UserDao;
import com.kim.daoImpl.UserDaoFactory;

/**
 * 读消息线程和处理方法
 * 
 * @author way
 * 
 */
public class InputThread extends Thread {
	private Socket socket;// socket对象
	private OutputThread out;// 传递进来的写消息线程，因为我们要给用户回复消息啊
	private OutputThreadMap map;// 写消息线程缓存器
	private ObjectInputStream ois;// 对象输入流
	private boolean isStart = true;// 是否循环读消息

	public InputThread(Socket socket, OutputThread out, OutputThreadMap map) {
		this.socket = socket;
		this.out = out;
		this.map = map;
		try {
			ois = new ObjectInputStream(socket.getInputStream());// 实例化对象输入流
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void setStart(boolean isStart) {// 提供接口给外部关闭读消息线程
		this.isStart = isStart;
	}

	@Override
	public void run() {
		try {
			while (isStart) {
				// 读取消息
				readMessage();
			}
			if (ois != null)
				ois.close();
			if (socket != null)
				socket.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 读消息以及处理消息，抛出异常
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void readMessage() throws IOException, ClassNotFoundException {
		Object readObject = ois.readObject();// 从流中读取对象
		UserDao dao = UserDaoFactory.getInstance();// 通过dao模式管理后台
		if (readObject != null && readObject instanceof TranObject) {
			TranObject read_tranObject = (TranObject) readObject;// 转换成传输对象
			switch (read_tranObject.getType()) {
			
			case LOGIN:
				User loginUser = (User) read_tranObject.getObject();
				System.out.println("收到登录消息, 密码: " + loginUser.getPassword());
				ArrayList<User> list = dao.login(loginUser);
				System.out.println("0's id: " + list.get(0).getId());
				System.out.println("1's id: " + list.get(0).getId());
				TranObject<ArrayList<User>> login2Object = new TranObject<ArrayList<User>>(
						TranObjectType.LOGIN);
				if (list != null) {// 如果登录成功
					TranObject<User> onObject = new TranObject<User>(
							TranObjectType.LOGIN);
					User login2User = new User();
					login2User.setAccount(loginUser.getAccount());
					onObject.setObject(login2User);
					for (OutputThread onOut : map.getAll()) {
						onOut.setMessage(onObject);// 广播一下用户上线
					}
					map.add(loginUser.getId(), out);// 先广播，再把对应用户id的写线程存入map中
					login2Object.setObject(list);// 把好友列表加入回复的对象中
				} else {
					login2Object.setObject(null);
				}

				out.setMessage(login2Object);// 同时把登录信息回复给用户
					
				System.out.println(" 用户："
						+ loginUser.getAccount() + " 上线了");
				break;

			case MESSAGE:// 如果是转发消息（可添加群发）
				// 获取消息中要转发的对象id，然后获取缓存的该对象的写线程
				int id2 = read_tranObject.getToUser();
				OutputThread toOut = map.getById(id2);
				if (toOut != null) {// 如果用户在线
					toOut.setMessage(read_tranObject);
				} else {// 如果为空，说明用户已经下线,回复用户
					/*TextMessage text = new TextMessage();
					text.setMessage("亲！对方不在线哦，您的消息将暂时保存在服务器");
					TranObject<TextMessage> offText = new TranObject<TextMessage>(
							TranObjectType.MESSAGE);
					offText.setObject(text);
					offText.setFromUser(0);
					out.setMessage(offText);*/
				}
				break;
			default:
				break;
			}
		}
	}
}
