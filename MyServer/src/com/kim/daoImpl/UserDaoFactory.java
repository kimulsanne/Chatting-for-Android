package com.kim.daoImpl;

import com.kim.dao.UserDao;

/**
 * 产生dao的工厂类
 */
public class UserDaoFactory {
	private static UserDao dao;

	public static UserDao getInstance() {
		if (dao == null) {
			dao = new UserDaoImpl();
		}
		return dao;
	}
}
