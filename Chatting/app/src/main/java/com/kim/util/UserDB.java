package com.kim.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kim.common.bean.User;

import java.util.ArrayList;
import java.util.List;

//用来在手机中储存当前登录的用户
public class UserDB {
	private DBHelper helper;

	public UserDB(Context context) {
		helper = new DBHelper(context);
	}

	public User selectInfo(int id) {
		User u = new User();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = db.rawQuery("select * from user where id=?",
				new String[] { id + "" });
		if (c.moveToFirst()) {

			u.setName(c.getString(c.getColumnIndex("name")));
		}
		return u;
	}

	public void addUser(List<User> list) {
		SQLiteDatabase db = helper.getWritableDatabase();
		User su = list.get(0);
		System.out.println("kimm userdb:  " + su);
		for (User u : list) {
			db.execSQL(
					"insert into user (id,account,name,online) values(?,?,?,?)",
					new Object[] { u.getId(), u.getAccount(),u.getName(),u.getOnline()});
		}
		db.close();
	}

	public void updateUser(List<User> list) {
		if (list.size() > 0) {
			delete();
			addUser(list);
		}
	}

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
			list.add(u);
		}
		c.close();
		db.close();
		return list;
	}

	public void delete() {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("delete from user");
		db.close();
	}
}
