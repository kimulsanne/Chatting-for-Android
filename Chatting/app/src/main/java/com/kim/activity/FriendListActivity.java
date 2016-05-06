package com.kim.activity;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.kim.common.bean.TextMessage;
import com.kim.common.bean.User;
import com.kim.common.transObj.TranObject;
import com.kim.common.utils.Constants;
import com.kim.util.GroupFriend;
import com.kim.util.MessageDB;
import com.kim.util.MyDate;
import com.kim.util.SharePreferenceUtil;
import com.kim.util.UserDB;

import java.util.ArrayList;
import java.util.List;

/**
 * 好友列表的Activity
 */
public class FriendListActivity extends MyActivity implements OnClickListener {

    private static final int PAGE1 = 0;// 页面1
    private static final int PAGE2 = 1;// 页面2
    private List<GroupFriend> group;// 需要传递给适配器的数据
    private String[] groupName = { "在线", "离线" };// 大组成员名
    private SharePreferenceUtil util;
    private UserDB userDB;// 保存好友列表数据库对象
    private MessageDB messageDB;// 消息数据库对象

    private ExpandableListView myListView;// 好友列表自定义listView
    private FriendAdapter friendAdapter;// 好友列表的适配器

    private ListView mRecentListView;// 最近会话的listView
    private int newNum = 0;

    private ExpandableListView mGroupListView;// 群组listView

    private ViewPager mPager;
    private List<View> mListViews;// Tab页面
    private LinearLayout layout_body_activity;
    private ImageView img_recent_chat;// 最近会话
    private ImageView img_friend_list;// 好友列表
    private ImageView img_group_friend;// 群组

    private ImageView myHeadImage;// 头像
    private TextView myName;// 名字

    private ImageView cursor;// 标题背景图片

    private int currentIndex = PAGE1; // 默认选中第1个，可以动态的改变此参数值
    private int offset = 0;// 动画图片偏移量
    private int bmpW;// 动画图片宽度

    private TranObject msg;
    private List<User> list;
    private MenuInflater mi;// 菜单

    private MyApplication application;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        setContentView(R.layout.friend_list);
        application = (MyApplication) this.getApplicationContext();
        initData();// 初始化数据
        initImageView();// 初始化动画
        initUI();// 初始化界面
    }

    @Override
    protected void onResume() {// 如果从后台恢复，服务被系统干掉，就重启一下服务
        // TODO Auto-generated method stub
        newNum = application.getRecentNum();// 从新获取一下全局变量
        if (!application.isClientStart()) {
            Intent service = new Intent(this, GetMsgService.class);
            startService(service);
        }
        new SharePreferenceUtil(this, Constants.SAVE_USER).setIsStart(false);
        NotificationManager manager = application.getmNotificationManager();
        if (manager != null) {
            manager.cancel(Constants.NOTIFY_ID);
            application.setNewMsgNum(0);// 把消息数目置0
            application.getmRecentAdapter().notifyDataSetChanged();
        }
        super.onResume();
    }

    /**
     * 初始化系统数据
     */
    private void initData() {
        userDB = new UserDB(FriendListActivity.this);// 本地用户数据库
        messageDB = new MessageDB(this);// 本地消息数据库
        util = new SharePreferenceUtil(this, Constants.SAVE_USER);

        msg = (TranObject) getIntent().getSerializableExtra(Constants.MSGKEY);// 从intent中取出消息对象
        if (msg == null) {// 如果为空，说明是从后台切换过来的，需要从数据库中读取好友列表信息
            list = userDB.getUser();
        } else {// 如果是登录界面切换过来的，就把好友列表信息保存到数据库
            list = (List<User>) msg.getObject();
            userDB.updateUser(list);
        }
        initListViewData(list);
    }

    /**
     * 处理服务器传递过来的用户数组数据，
     */
    private void initListViewData(List<User> list) {
        group = new ArrayList<GroupFriend>();// 实例化
        for (int i = 0; i < groupName.length; ++i) {// 根据大组的数量，循环给各大组分配成员
            List<User> child = new ArrayList<User>();// 装小组成员的list
            GroupFriend groupInfo = new GroupFriend(groupName[i], child);// 我们自定义的大组成员对象
            for (User u : list) {
                if (u.getOnline() == i)// 判断一下是属于哪个大组
                    child.add(u);
            }
            group.add(groupInfo);// 把自定义大组成员对象放入一个list中，传递给适配器
        }
    }

    /**
     * 初始化动画
     */
    private void initImageView() {
        cursor = (ImageView) findViewById(R.id.tab2_bg);
        bmpW = BitmapFactory.decodeResource(getResources(),
                R.drawable.topbar_select).getWidth();// 获取图片宽度
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;// 获取分辨率宽度
        // System.out.println("屏幕宽度:" + screenW);
        offset = screenW / 2;// 计算偏移量:屏幕宽度/3，平分为3分，如果是3个view的话，再减去图片宽度，因为图片居中，所以要得到两变剩下的空隙需要再除以2
        Matrix matrix = new Matrix();
        matrix.postTranslate(screenW / 4 - bmpW / 2, 0);// 初始化位置
        cursor.setImageMatrix(matrix);// 设置动画初始位置
    }

    private void initUI() {
        mi = new MenuInflater(this);
        layout_body_activity = (LinearLayout) findViewById(R.id.bodylayout);


        img_friend_list = (ImageView) findViewById(R.id.tab1);
        img_friend_list.setOnClickListener(this);
        img_recent_chat = (ImageView) findViewById(R.id.tab2);
        img_recent_chat.setOnClickListener(this);

        myName = (TextView) findViewById(R.id.friend_list_myName);
        cursor = (ImageView) findViewById(R.id.tab2_bg);

        myName.setText(list.get(0).getName());
        layout_body_activity.setFocusable(true);

        mPager = (ViewPager) findViewById(R.id.viewPager);
        mListViews = new ArrayList<View>();
        LayoutInflater inflater = LayoutInflater.from(this);
        View lay1 = inflater.inflate(R.layout.tab1, null);
        View lay2 = inflater.inflate(R.layout.tab2, null);
        mListViews.add(lay1);
        mListViews.add(lay2);
        mPager.setAdapter(new MyPagerAdapter(mListViews));
        mPager.setCurrentItem(PAGE1);
        mPager.addOnPageChangeListener(new MyOnPageChangeListener());

        // 下面是处理好友列表界面处理
        myListView = (ExpandableListView) lay1.findViewById(R.id.tab1_listView);
        friendAdapter = new FriendAdapter(this, group);
        myListView.setAdapter(friendAdapter);
        //myListView.setGroupIndicator(null);// 不设置大组指示器图标，因为我们自定义设置了
        myListView.setDivider(null);// 设置图片可拉伸的
        myListView.setFocusable(true);// 聚焦才可以下拉刷新
       // myListView.setonRefreshListener(new MyRefreshListener());

        // 下面是最近会话界面处理
        mRecentListView = (ExpandableListView) lay2.findViewById(R.id.tab2_listView);
        //mRecentAdapter = new RecentChatAdapter(FriendListActivity.this,
        //application.getmRecentList());// 从全局变量中获取最近聊天对象数组
        // mRecentListView.setAdapter(application.getmRecentAdapter());// 先设置空对象，要么从数据库中读出


        // 下面是群组界面处理
        /*mGroupListView = (ListView) lay3.findViewById(R.id.tab3_listView);
        List<GroupEntity> groupList = new ArrayList<GroupEntity>();
        GroupEntity entity = new GroupEntity(0, "C175地带", "怀念高中生活...");
        GroupEntity entity2 = new GroupEntity(0, "Android开发",
                "爱生活...爱Android...");
        groupList.add(entity);
        groupList.add(entity2);
        GroupAdapter adapter = new GroupAdapter(this, groupList);
        mGroupListView.setAdapter(adapter);*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab1:
                mPager.setCurrentItem(PAGE1);// 点击页面1
                break;
            case R.id.tab2:
                mPager.setCurrentItem(PAGE2);// 点击页面2
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //mi.inflate(R.menu.friend_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messageDB != null)
            messageDB.close();
    }

    @Override
    // 菜单选项添加事件处理
    public boolean onOptionsItemSelected(MenuItem item) {
        /*switch (item.getItemId()) {
            case R.id.friend_menu_add:
                Toast.makeText(getApplicationContext(), "亲！此功能暂未实现哦", 0).show();
                break;
            case R.id.friend_menu_exit:
                exitDialog(FriendListActivity.this, "QQ提示", "亲！您真的要退出吗？");
                break;
            default:
                break;
        }*/
        return super.onOptionsItemSelected(item);
    }

    // 完全退出提示窗
    private void exitDialog(Context context, String title, String msg) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(msg)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 关闭服务
                        if (application.isClientStart()) {
                            Intent service = new Intent(
                                    FriendListActivity.this,
                                    GetMsgService.class);
                            stopService(service);
                        }
                        close();// 父类关闭方法
                    }
                }).setNegativeButton("取消", null).create().show();
    }

    @Override
    public void getMessage(TranObject msg) {// 重写父类的方法，处理消息
        // TODO Auto-generated method stub
        switch (msg.getType()) {
            case MESSAGE:
                newNum++;
                application.setRecentNum(newNum);// 保存到全局变量
                TextMessage tm = (TextMessage) msg.getObject();
                String message = tm.getMessage();
                ChatMsgEntity entity = new ChatMsgEntity("", MyDate.getDateEN(),
                        message, true);// 收到的消息
                messageDB.saveMsg(msg.getFromUser(), entity);// 保存到数据库
                User user2 = userDB.selectInfo(msg.getFromUser());// 通过id查询对应数据库该好友信息
                RecentChatEntity entity2 = new RecentChatEntity(msg.getFromUser(),
                         newNum, user2.getName(), MyDate.getDate(),
                        message);
                application.getmRecentAdapter().remove(entity2);// 先移除该对象，目的是添加到首部
                application.getmRecentList().addFirst(entity2);// 再添加到首部
                application.getmRecentAdapter().notifyDataSetChanged();
                break;
            case LOGIN:
                User logUser = (User) msg.getObject();

                break;
            /*case LOGOUT:
                User logoutUser = (User) msg.getObject();
                Toast.makeText(FriendListActivity.this,
                        "亲！" + logoutUser.getId() + "下线了哦", 0).show();
                MediaPlayer.create(this, R.raw.msg).start();
                break;*/
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {// 捕获返回按键事件，进入后台运行
        // TODO Auto-generated method stub
        // 发送广播，通知服务，已进入后台运行
        Intent i = new Intent();
        i.setAction(Constants.BACKKEY_ACTION);
        sendBroadcast(i);

        util.setIsStart(true);// 设置后台运行标志，正在运行
        finish();// 再结束自己
    }

    // ViewPager页面切换监听

    public class MyOnPageChangeListener implements OnPageChangeListener {

        int one = offset;// 页卡1 -> 页卡2 偏移量
        public void onPageSelected(int arg0) {
            // TODO Auto-generated method stub
            Animation animation = null;
            switch (arg0) {
                case PAGE1:// 切换到页卡1
                    if (currentIndex == PAGE2) {// 如果之前显示的是页卡2
                        animation = new TranslateAnimation(one, 0, 0, 0);
                    }
                    break;
                case PAGE2:// 切换到页卡2
                    if (currentIndex == PAGE1) {// 如果之前显示的是页卡1
                        animation = new TranslateAnimation(0, one, 0, 0);
                    }
                    break;
                default:
                    break;
            }
            currentIndex = arg0;// 动画结束后，改变当前图片位置
            animation.setFillAfter(true);// True:图片停在动画结束位置
            animation.setDuration(300);
            cursor.startAnimation(animation);
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }
    }

    /**
     * 好友列表下拉刷新监听与实现，异步任务
     *
     * @author way
     *
     */
    /*public class MyRefreshListener implements MyListView.OnRefreshListener {

        @Override
        public void onRefresh() {
            new AsyncTask<Void, Void, Void>() {
                List<User> list;

                protected Void doInBackground(Void... params) {
                    // 从服务器重新获取好友列表
                    if (application.isClientStart()) {
                        ClientOutputThread out = application.getClient()
                                .getClientOutputThread();
                        TranObject o = new TranObject(TranObjectType.REFRESH);
                        o.setFromUser(Integer.parseInt(util.getId()));
                        out.setMsg(o);
                        // 为了及时收到服务器发过来的消息，我这里直接通过监听收消息线程，获取好友列表，就不通过接收广播了
                        ClientInputThread in = application.getClient()
                                .getClientInputThread();
                        in.setMessageListener(new MessageListener() {

                            @Override
                            public void Message(TranObject msg) {
                                // TODO Auto-generated method stub
                                if (msg != null
                                        && msg.getType() == TranObjectType.REFRESH) {
                                    list = (List<User>) msg.getObject();
                                    if (list.size() > 0) {
                                        // System.out.println("Friend:" + list);
                                        initListViewData(list);
                                        myExAdapter.updata(group);
                                        userDB.updateUser(list);// 保存到数据库
                                    }
                                }
                            }
                        });
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    myExAdapter.notifyDataSetChanged();
                    myListView.onRefreshComplete();
                    Toast.makeText(FriendListActivity.this, "刷新成功", 0).show();
                }

            }.execute(null);
        }
    }*/
}
