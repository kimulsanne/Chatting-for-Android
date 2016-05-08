package com.kim.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kim.common.bean.User;

import java.util.ArrayList;
import java.util.List;

/**
 * 在手机中储存当前所有在线用户
 */
public class UserDB {
	private DBHelper helper;

	public UserDB(Context context) {
		helper = new DBHelper(context);
	}

	//查找信息
	public User selectInfo(int account) {
		User u = new User();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = db.rawQuery("select * from user where account=?",
				new String[] { account + "" });
		if (c.moveToFirst()) {

			u.setName(c.getString(c.getColumnIndex("name")));
		}
		return u;
	}

	//添加用户
	public void addUser(List<User> list) {
		SQLiteDatabase db = helper.getWritableDatabase();
		for (User u : list) {
			db.execSQL(
					"insert into user (id,account,name,tag,online) values(?,?,?,?,?)",
					new Object[] { u.getId(), u.getAccount(), u.getName(), u.getTag(), u.getOnline()});
		}

		db.close();
	}

	//更新用户
	public void updateUser(List<User> list) {
		if (list.size() > 0) {
			delete();
			addUser(list);
		}
	}

	//查找用户
	public List<User> getUser() {
		SQLiteDatabase db = helper.getWritableDatabase();
		List<User> list = new ArrayList<User>();
		Cursor c = db.rawQuery("select * from user", null);
		while (c.moveToNext()) {
			User u = new User();
			u.setId(c.getInt(c.getColumnIndex("id")));
			u.setName(c.getString(c.getColumnIndex("name")));
			u.setOnline(c.getInt(c.getColumnIndex("online")));
			u.setAccount(c.getString(c.getColumnIndex("account")));
			u.setTag(c.getString(c.getColumnIndex("tag")));
			if ((u.getOnline() == 0) && (!u.getAccount().equals("--")) )
				list.add(u);
		}
		c.close();
		db.close();
		return list;
	}

	//查找好友
	public List<User> getFriendUser() {
		SQLiteDatabase db = helper.getWritableDatabase();
		List<User> list = new ArrayList<User>();
		Cursor c = db.rawQuery("select * from user", null);
		while (c.moveToNext()) {
			User u = new User();
			u.setId(c.getInt(c.getColumnIndex("id")));
			u.setName(c.getString(c.getColumnIndex("name")));
			u.setOnline(c.getInt(c.getColumnIndex("online")));
			u.setAccount(c.getString(c.getColumnIndex("account")));
			u.setTag(c.getString(c.getColumnIndex("tag")));
			if (u.getAccount().equals("--"))
				list.add(u);
		}
		c.close();
		db.close();
		return list;
	}
	//删除表
	public void delete() {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("delete from user");
		db.close();
	}
}
