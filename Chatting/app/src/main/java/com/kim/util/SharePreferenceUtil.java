package com.kim.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.kim.common.utils.Constants;

public class SharePreferenceUtil {
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	public SharePreferenceUtil(Context context, String file) {
		sp = context.getSharedPreferences(file, context.MODE_PRIVATE);
		editor = sp.edit();
	}

	// 用户的密码
	public void setPassword(String password) {
		editor.putString("password", password);
		editor.commit();
	}

	public String getPassword() {
		return sp.getString("password", "");
	}

	// 用户的id
	public void setId(int id) {
		editor.putInt("id", id);
		editor.commit();
	}

	public int getId() {
		return sp.getInt("id", 0);
	}

	// 用户的账号
	public void setAccount(String account) {
		editor.putString("account", account);
		editor.commit();
	}

	public String getAccount() {
		return sp.getString("account", "");
	}

	// 用户的昵称
	public String getName() {
		return sp.getString("name", "");
	}

	public void setName(String name) {
		editor.putString("name", name);
		editor.commit();
	}

	// 用户的邮箱
	public String getEmail() {
		return sp.getString("email", "");
	}

	public void setEmail(String email) {
		editor.putString("email", email);
		editor.commit();
	}

	// 用户自己的头像
	public Integer getImg() {
		return sp.getInt("img", 0);
	}

	public void setImg(int i) {
		editor.putInt("img", i);
		editor.commit();
	}

	// ip
	public void setIp(String ip) {
		editor.putString("ip", ip);
		editor.commit();
	}

	public String getIp() {
		return sp.getString("ip", Constants.SERVER_IP);
	}

	// 端口
	public void setPort(int port) {
		editor.putInt("port", port);
		editor.commit();
	}

	public int getPort() {
		return sp.getInt("port", Constants.SERVER_PORT);
	}

	// 是否在后台运行标记
	public void setIsStart(boolean isStart) {
		editor.putBoolean("isStart", isStart);
		editor.commit();
	}

	public boolean getIsStart() {
		return sp.getBoolean("isStart", false);
	}

	// 是否第一次运行本应用
	public void setIsFirst(boolean isFirst) {
		editor.putBoolean("isFirst", isFirst);
		editor.commit();
	}

	public boolean getisFirst() {
		return sp.getBoolean("isFirst", true);
	}
}
