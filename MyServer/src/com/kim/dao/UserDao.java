package com.kim.dao;

import java.util.ArrayList;

import com.kim.common.bean.User;

public interface UserDao {
	
	public int register(User u);
	public ArrayList<User> login(User u);
	public ArrayList<User> refresh(int id);
	public void logout(int id);
}
