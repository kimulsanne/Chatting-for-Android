package com.kim.activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.kim.common.bean.User;
import com.kim.util.GroupFriend;

import java.util.List;

/**
 * 用户显示好友列表的适配器
 */
public class FriendAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<GroupFriend> group;// 传递过来的经过处理的总数据

    public FriendAdapter(Context context, List<GroupFriend> group) {
        super();
        this.context = context;
        this.group = group;
    }

    // 得到大组成员的view
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.parent_group, null);
        }

        TextView title = (TextView) convertView.findViewById(R.id.content_001);
        title.setText(getGroup(groupPosition).toString());// 设置大组成员名称

        return convertView;
    }

    // 得到大组成员的id
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    // 得到大组成员名称
    public Object getGroup(int groupPosition) {
        return group.get(groupPosition).getGroupName();
    }

    // 得到大组成员总数
    public int getGroupCount() {
        return group.size();
    }

    // 得到小组成员的view
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item, null);
        }
        final TextView title = (TextView) convertView
                .findViewById(R.id.name_item);// 显示用户名
        final String name = group.get(groupPosition).getChild(childPosition)
                .getName();
        final String account = group.get(groupPosition).getChild(childPosition)
                .getAccount();
        final int id = group.get(groupPosition).getChild(childPosition)
                .getId();
        title.setText(name);// 大标题
        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 下面是切换到聊天界面处理
                User u = new User();
                u.setName(name);
                u.setAccount(account);
                u.setId(id);
                System.out.println("kimm:  friend chose account:  " + name);
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("user", u);
                context.startActivity(intent);

            }
        });
        return convertView;
    }

    // 得到小组成员id
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    // 得到小组成员的名称
    public Object getChild(int groupPosition, int childPosition) {
        return group.get(groupPosition).getChild(childPosition);
    }

    // 得到小组成员的数量
    public int getChildrenCount(int groupPosition) {
        return group.get(groupPosition).getChildSize();
    }

    //表明大組和小组id是否稳定的更改底层数据
    public boolean hasStableIds() {
        return true;
    }

    // 得到小组成员是否被选择
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}
