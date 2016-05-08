package com.kim.activity;


import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.kim.client.Client;
import com.kim.client.ClientOutputThread;
import com.kim.common.bean.TextMessage;
import com.kim.common.bean.User;
import com.kim.common.transObj.TranObject;
import com.kim.common.transObj.TranObjectType;
import com.kim.common.utils.Constants;
import com.kim.util.MessageDB;
import com.kim.util.MyDate;
import com.kim.util.SharePreferenceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ChatActivity extends MyActivity implements OnClickListener {
    private Button mBtnSend;// 发送btn
    private Button mBtnBack;// 返回btn
    private EditText mEditTextContent;
    private TextView mFriendName;
    private ListView mListView;
    private ChatMsgViewAdapter mAdapter;// 消息视图的Adapter
    private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();// 消息对象数组
    private SharePreferenceUtil util;
    private User user;
    private MessageDB messageDB;
    private MyApplication application;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        setContentView(R.layout.chat);
        application = (MyApplication) getApplicationContext();
        messageDB = new MessageDB(this);
        user = (User) getIntent().getSerializableExtra("user");     //当前聊天的对象
        util = new SharePreferenceUtil(this, Constants.SAVE_USER);
        initView();// 初始化view
        initData();// 初始化数据
    }

    /**
     * 初始化view
     */
    public void initView() {
        mListView = (ListView) findViewById(R.id.listview);
        mBtnSend = (Button) findViewById(R.id.chat_send);
        mBtnSend.setOnClickListener(this);
        mBtnBack = (Button) findViewById(R.id.chat_back);
        mBtnBack.setOnClickListener(this);
        mFriendName = (TextView) findViewById(R.id.chat_name);
        mFriendName.setText(user.getName());
        mEditTextContent = (EditText) findViewById(R.id.chat_editmessage);
    }

    /**
     * 加载消息历史，从数据库中读出
     */
    public void initData() {
        List<ChatMsgEntity> list = messageDB.getMsg(user.getId());
        if (list.size() > 0) {
            for (ChatMsgEntity entity : list) {
                if (entity.getName().equals("")) {
                    entity.setName(user.getName());
                }

                mDataArrays.add(entity);
            }
            Collections.reverse(mDataArrays);
        }
        mAdapter = new ChatMsgViewAdapter(this, mDataArrays);
        mListView.setAdapter(mAdapter);
        mListView.setSelection(mAdapter.getCount() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        messageDB.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat_send:// 发送按钮点击事件
                send();
                break;
            case R.id.chat_back:// 返回按钮点击事件
                finish();// 结束,实际开发中，可以返回主界面
                break;
        }
    }

    /**
     * 发送消息
     */
    private void send() {
        String contString = mEditTextContent.getText().toString();
        System.out.println("kiim  收到发送消息: " + contString );
        if (contString.length() > 0) {
            ChatMsgEntity entity = new ChatMsgEntity();
            entity.setName(util.getName());
            entity.setDate(MyDate.getDateEN());
            entity.setMessage(contString);
            entity.setMsgType(false);
            System.out.println("kiim 保存到数据库:  " + entity.getName());
            System.out.println("kiim 保存到数据库:  " + entity.getMessage());
            messageDB.saveMsg(user.getId(), entity);
            System.out.println("kiim 消息总数:"   + messageDB.getMsg(user.getId()).size());
            mDataArrays.add(entity);
            mAdapter.notifyDataSetChanged();// 通知ListView，数据已发生改变
            mEditTextContent.setText("");// 清空编辑框数据
            mListView.setSelection(mListView.getCount() - 1);// 发送一条消息时，ListView显示选择最后一项
            MyApplication application = (MyApplication) this
                    .getApplicationContext();
            Client client = application.getClient();
            ClientOutputThread out = client.getClientOutputThread();
            if (out != null) {
                TranObject<TextMessage> o = new TranObject<TextMessage>(
                        TranObjectType.MESSAGE);
                TextMessage message = new TextMessage();
                message.setMessage(contString);
                o.setObject(message);
                o.setFromUser((util.getId()));
                o.setToUser(user.getId());
                System.out.println("kiim 开始发送消息");
                System.out.println("kiim 接收人: " + user.getId());
                System.out.println("kiim 发送人: " + util.getId() );
                out.setMsg(o);
            }

        }
    }

    @Override
    public void getMessage(TranObject msg) {
        switch (msg.getType()) {
            case MESSAGE:
                TextMessage tm = (TextMessage) msg.getObject();
                String message = tm.getMessage();
                ChatMsgEntity entity = new ChatMsgEntity(user.getName(),
                        MyDate.getDateEN(), message, true);// 收到的消息
                if (msg.getFromUser() == user.getId() || msg.getFromUser() == 0) {// 如果是正在聊天的好友的消息，或者是服务器的消息

                    messageDB.saveMsg(user.getId(), entity);

                    mDataArrays.add(entity);
                    mAdapter.notifyDataSetChanged();
                    mListView.setSelection(mListView.getCount() - 1);

                } else {
                    messageDB.saveMsg(msg.getFromUser(), entity);// 保存到数据库
                    /*Toast.makeText(ChatActivity.this,
                            "您有新的消息来自：" + msg.getFromUser() + ":" + message, 0)
                            .show();// 其他好友的消息，就先提示，并保存到数据库*/

                }
                break;
            case LOGIN:
                User loginUser = (User) msg.getObject();
                /*Toast.makeText(ChatActivity.this, loginUser.getId() + "上线了", 0)
                        .show();*/

                break;
            case LOGOUT:
                User logoutUser = (User) msg.getObject();
                /*Toast.makeText(ChatActivity.this, logoutUser.getId() + "下线了", 0)
                        .show();*/

                break;
            default:
                break;
        }
    }
}