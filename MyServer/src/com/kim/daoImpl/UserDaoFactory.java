package com.kim.daoImpl;

import com.kim.dao.UserDao;
 
public class UserDaoFactory {
	private static UserDao dao;

	public static UserDao getInstance() {
		if (dao == null) {
			dao = new UserDaoImpl();
		}
		return dao;
	}
}
