package com.kim.activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kim.common.bean.User;

import java.util.List;

/**
 * 用户显示标签列表的适配器
 */
public class TagAdapter extends BaseAdapter {
    private Context context;
    private List<User> group;// 传递过来的经过处理的总数据
    LayoutInflater inflater;

    public TagAdapter(Context context, List<User> group) {
        super();
        this.context = context;
        this.group = group;
        this.inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return group.size();//返回数组的长度
    }

    @Override
    public Object getItem(int position) {
        return group.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.tag_item, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.tag_name_item);
            holder.tag = (TextView) convertView.findViewById(R.id.tag_item);
            convertView.setTag(holder);//绑定ViewHolder对象
        }
        else{
            holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
        }

        holder.name.setText(group.get(position).getName());
        holder.tag.setText(group.get(position).getTag());
        final String name = group.get(position).getName();
        final String account = group.get(position).getAccount();
        final int id = group.get(position).getId();
        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 下面是切换到聊天界面处理
                User u = new User();
                u.setName(name);
                u.setAccount(account);
                u.setId(id);
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("user", u);
                context.startActivity(intent);

            }
        });
        return convertView;
    }


    static class ViewHolder {
        public TextView name;
        public TextView tag;
    }
}
