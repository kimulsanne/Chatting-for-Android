package com.kim.common.bean;

import java.io.Serializable;

/**
 * 用户类
 */
public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;// QQ号码
	private String name;// 昵称
	private String account;// 账号
	private String email;// 邮箱
	private String password;// 密码
	private String tag;// 标签
	private int online;// 是否在线
	private String ip;// ip
	private int port;// 端口

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getOnline() {
		return online;
	}

	public void setOnline(int online) {
		this.online = online;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof User) {
			User user = (User) o;
			if (user.getId() == id && user.getIp().equals(ip)
					&& user.getPort() == port) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", account=" + account + ", online=" + online +", email=" + email
				+ ", password=" + password + ", tag=" + tag + ", ip=" + ip
				+ ", port=" + port + "]";
	}

}
