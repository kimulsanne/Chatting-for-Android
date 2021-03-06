package com.kim.daoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList; 

import com.kim.common.bean.User;
import com.kim.common.utils.Constants;
import com.kim.common.utils.DButil;
import com.kim.dao.UserDao;

public class UserDaoImpl implements UserDao {

	@Override
	public int register(User u) {
		int id;
		Connection con = DButil.connect();
		String sql1 = "insert into user(name, password, mail,time, account, tag, online) values(?,?,?,?,?,?,?)";
		String sql2 = "select id from user";

		try {
			PreparedStatement ps = con.prepareStatement(sql1);
			ps.setString(1, u.getName());
			ps.setString(2, u.getPassword());
			ps.setString(3, u.getEmail());
			ps.setString(4, "shijian");
			ps.setString(5, u.getAccount());
			ps.setString(6, u.getTag());
			ps.setInt(7, 1);
			int res = ps.executeUpdate();
			if (res > 0) {
				PreparedStatement ps2 = con.prepareStatement(sql2);
				ResultSet rs = ps2.executeQuery();
				if (rs.last()) {
					id = rs.getInt("id");
					return id;
				}
			}
		} catch (SQLException e) {
			 e.printStackTrace();
		} finally {
			DButil.close(con);
		}
		return Constants.REGISTER_FAIL;
	}

	@Override
	public ArrayList<User> login(User u) {
		Connection con = DButil.connect();
		String sql = "select * from user where account=? and password=?";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, u.getAccount());
			ps.setString(2, u.getPassword());
			ResultSet rs = ps.executeQuery();
			if (rs.first()) {
				int id = rs.getInt("id");
				setOnline(id);
				ArrayList<User> refreshList = getFriend(id);
				return refreshList;
			}
		} catch (SQLException e) {
			// e.printStackTrace();
		} finally {
			DButil.close(con);
		}
		return null;
	}


	public User findMe(int id) {
		User me = new User();
		Connection con = DButil.connect();
		String sql = "select * from user where id=?";
		PreparedStatement ps;
		try {
			ps = con.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.first()) {
				me.setId(rs.getInt("id"));			
				me.setEmail(rs.getString("mail"));			
				me.setName(rs.getString("name"));				
				me.setOnline(rs.getInt("online"));
				me.setAccount("--");
				me.setTag(rs.getString("tag"));
			}

			return me;
		} catch (SQLException e) {
			// e.printStackTrace();
		} finally {
			DButil.close(con);
		}
		return null;
	}

	/*
	public ArrayList<User> refresh(int id) {
		ArrayList<User> list = new ArrayList<User>();
		User me = findMe(id);
		list.add(me);
		Connection con = DButil.connect();
		String sql = "select * from user where online=0";
		PreparedStatement ps;
		try {
			ps = con.prepareStatement(sql);
			//ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.first()) {
				do {
					User friend = new User();
					friend.setId(rs.getInt("id"));
					friend.setName(rs.getString("name"));
					friend.setOnline(rs.getInt("online"));		
					friend.setAccount(rs.getString("account"));
					friend.setTag(rs.getString("tag"));
					if (friend.getId() != id)
						list.add(friend);
					
				} while (rs.next());
			}
			 
			return list;
		} catch (SQLException e) {
			// e.printStackTrace();
		} finally {
			DButil.close(con);
		}
		return null;
	}
*/
	@Override
	public void logout(int id) {
		Connection con = DButil.connect();
		try {
			String sql = "update user set online=1 where id=?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, id);
			ps.executeUpdate();
		} catch (SQLException e) {
			// e.printStackTrace();
		} finally {
			DButil.close(con);
		}
	}


	public void setOnline(int id) {
		Connection con = DButil.connect();
		try {
			String sql = "update user set online=0 where id=?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, id);
			ps.executeUpdate();

		} catch (SQLException e) {
			// e.printStackTrace();
		} finally {
			DButil.close(con);
		}
	}

	/*
	//添加所有的好友
	public ArrayList<User> addFriend(ArrayList<User> list) {
		int id = list.get(0).getId();
		Connection con = DButil.connect();
		String sql = "select * from friendlist where uid=?";
		PreparedStatement ps;
		try {
			ps = con.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery(); 
			if (rs.first()) {
				do {
					User friend = new User();
					friend.setId(rs.getInt("id"));
					friend.setName(rs.getString("name"));
					friend.setOnline(rs.getInt("online"));		
					friend.setAccount("--");	//表明他是好友
					friend.setTag(rs.getString("tag"));
					if (friend.getId() != id)
						list.add(friend);
					
				} while (rs.next());
			}
			return list;
		} catch (SQLException e) {
			
		} finally {
			DButil.close(con);
		}
		return null;
	}*/
	
	@Override
	public ArrayList<User> getFriend(int id) {
		ArrayList<User> list = new ArrayList<User>();
		User me = findMe(id);
		list.add(me);
		Connection con = DButil.connect();
		String sql = "select * from user where online=0";
		String sql2 = "select * from friendlist where uid=?";
		String sql3 = "select * from user where id=?";
		PreparedStatement ps, ps2;
		try {
			ps = con.prepareStatement(sql);			
			ResultSet rs = ps.executeQuery();
			if (rs.first()) {
				do {
					User friend = new User();
					friend.setId(rs.getInt("id"));
					friend.setName(rs.getString("name"));
					friend.setOnline(rs.getInt("online"));		
					friend.setAccount(rs.getString("account"));
					friend.setTag(rs.getString("tag"));
					if (friend.getId() != id)
						list.add(friend);
					
				} while (rs.next());
			}
			ps = con.prepareStatement(sql2);
			ps.setInt(1, id);
			rs = ps.executeQuery(); 
			if (rs.first()) {
				do {
					ps2 = con.prepareStatement(sql3);
					ps2.setInt(1, rs.getInt("fid"));
					ResultSet rs2 = ps2.executeQuery();
					if (rs2.first()) {
						do {
							User friend = new User();				
							rs2.first();
							friend.setId(rs2.getInt("id"));
							friend.setName(rs2.getString("name"));
							friend.setOnline(rs2.getInt("online"));		
							friend.setAccount("--");	//表明他是好友
							friend.setTag(rs2.getString("tag"));
							if (friend.getId() != id)
								list.add(friend);
						
						} while (rs2.next());							
					}	
				} while (rs.next());
			}

			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DButil.close(con);
		}
		return null;
	}

/*
	public void createFriendtable(int id) {
		Connection con = DButil.connect();
		try {
			String sql = "create table _" + id
					+ " (_id int auto_increment not null primary key,"
					+ "_name varchar(20) not null,"
					+ "_isOnline int(11) not null default 0,"
					+ "_group int(11) not null default 0,"
					+ "_qq int(11) not null default 0,"
					+ "_img int(11) not null default 0)";
			PreparedStatement ps = con.prepareStatement(sql);
			int res = ps.executeUpdate();
			System.out.println(res);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DButil.close(con);
		}
	}


	public void logout(int id) {
		Connection con = DButil.connect();
		try {
			String sql = "update user set _isOnline=0 where _id=?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, id);
			ps.executeUpdate();
			updateAllOff(id);
			// System.out.println(res);
		} catch (SQLException e) {
			// e.printStackTrace();
		} finally {
			DButil.close(con);
		}
	}


	public void updateAllOff(int id) {
		Connection con = DButil.connect();
		try {
			String sql = "update _? set _isOnline=0 where _qq=?";
			PreparedStatement ps = con.prepareStatement(sql);
			for (int offId : getAllId()) {
				ps.setInt(1, offId);
				ps.setInt(2, id);
				ps.executeUpdate();
			}
		} catch (SQLException e) {
			// e.printStackTrace();
		} finally {
			DButil.close(con);
		}
	}


	public void updateAllOn(int id) {
		Connection con = DButil.connect();
		try {
			String sql = "update _? set _isOnline=1 where _qq=?";
			PreparedStatement ps = con.prepareStatement(sql);
			for (int OnId : getAllId()) {
				ps.setInt(1, OnId);
				ps.setInt(2, id);
				ps.executeUpdate();
			}
		} catch (SQLException e) {
			// e.printStackTrace();
		} finally {
			DButil.close(con);
		}
	}

	public List<Integer> getAllId() {
		Connection con = DButil.connect();
		List<Integer> list = new ArrayList<Integer>();
		try {
			String sql = "select _id from user";
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			if (rs.first()) {
				do {
					int id = rs.getInt("_id");
					list.add(id);
				} while (rs.next());
			}
			// System.out.println(list);
			return list;
		} catch (SQLException e) {
			// e.printStackTrace();
		} finally {
			DButil.close(con);
		}
		return null;
	}
*/

}
