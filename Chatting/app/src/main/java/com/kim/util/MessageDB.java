package com.kim.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kim.activity.ChatMsgEntity;
import com.kim.common.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * 用来在手机中储存已经发送的消息
 */
public class MessageDB {
	private SQLiteDatabase db;

	public MessageDB(Context context) {
		db = context.openOrCreateDatabase(Constants.DBNAME,
				Context.MODE_PRIVATE, null);
	}

	//保存信息
	public void saveMsg(int id, ChatMsgEntity entity) {
		db.execSQL("CREATE table IF NOT EXISTS _"
				+ id
				+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT, date TEXT,isCome TEXT,message TEXT)");
		int isCome = 0;
		if (entity.getMsgType()) {//如果是收到的消息，保存在数据库的值为1
			isCome = 1;
		}
		db.execSQL(
				"insert into _" + id
						+ " (name,date,isCome,message) values(?,?,?,?)",
				new Object[] { entity.getName(), entity.getDate(), isCome, entity.getMessage() });
	}

	//获得信息
	public List<ChatMsgEntity> getMsg(int id) {
		List<ChatMsgEntity> list = new ArrayList<ChatMsgEntity>();
		db.execSQL("CREATE table IF NOT EXISTS _"
				+ id
				+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT, date TEXT,isCome TEXT,message TEXT)");
		Cursor c = db.rawQuery("SELECT * from _" + id + " ORDER BY _id DESC LIMIT 5", null);
		while (c.moveToNext()) {
			String name = c.getString(c.getColumnIndex("name"));
			String date = c.getString(c.getColumnIndex("date"));
			int isCome = c.getInt(c.getColumnIndex("isCome"));
			String message = c.getString(c.getColumnIndex("message"));
			boolean isComMsg = false;
			if (isCome == 1) {
				isComMsg = true;
			}
			ChatMsgEntity entity = new ChatMsgEntity(name, date, message, isComMsg);
			list.add(entity);
		}
		c.close();
		return list;
	}

	public void close() {
		if (db != null)
			db.close();
	}
}
