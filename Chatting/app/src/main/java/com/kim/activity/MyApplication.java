package com.kim.activity;

import android.app.Application;
import android.app.NotificationManager;

import com.kim.client.Client;
import com.kim.common.utils.Constants;
import com.kim.util.SharePreferenceUtil;

import java.util.LinkedList;

public class MyApplication extends Application {
	private Client client;// 客户端
	private boolean isClientStart;// 客户端连接是否启动
	private NotificationManager mNotificationManager;
	private int newMsgNum = 0;// 后台运行的消息
	private LinkedList<RecentChatEntity> mRecentList;

	private int recentNum = 0;

	@Override
	public void onCreate() {
		SharePreferenceUtil util = new SharePreferenceUtil(this,
				Constants.SAVE_USER);
		System.out.println("Myapplication:  " + util.getIp() + " " + util.getPort());
		client = new Client(util.getIp(), util.getPort());// 从配置文件中读ip和地址
		System.out.println("Myapplication:  new client ok!");
		mRecentList = new LinkedList<RecentChatEntity>();
		super.onCreate();
	}

	public Client getClient() {
		return client;
	}

	public boolean isClientStart() {
		return isClientStart;
	}

	public void setClientStart(boolean isClientStart) {
		this.isClientStart = isClientStart;
	}

	public NotificationManager getmNotificationManager() {
		return mNotificationManager;
	}

	public void setmNotificationManager(NotificationManager mNotificationManager) {
		this.mNotificationManager = mNotificationManager;
	}

	public int getNewMsgNum() {
		return newMsgNum;
	}

	public void setNewMsgNum(int newMsgNum) {
		this.newMsgNum = newMsgNum;
	}

	public LinkedList<RecentChatEntity> getmRecentList() {
		return mRecentList;
	}

	public void setmRecentList(LinkedList<RecentChatEntity> mRecentList) {
		this.mRecentList = mRecentList;
	}

	public int getRecentNum() {
		return recentNum;
	}

	public void setRecentNum(int recentNum) {
		this.recentNum = recentNum;
	}
}
