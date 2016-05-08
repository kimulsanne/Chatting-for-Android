package com.kim.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kim.client.Client;
import com.kim.client.ClientOutputThread;
import com.kim.common.bean.User;
import com.kim.common.transObj.TranObject;
import com.kim.common.transObj.TranObjectType;
import com.kim.common.utils.Constants;
import com.kim.util.DialogFactory;
import com.kim.util.SharePreferenceUtil;
import com.kim.util.UserDB;

import java.util.List;


public class LoginActivity extends MyActivity implements OnClickListener {
    private SharePreferenceUtil util;
    private MyApplication application;

    private Button mBtnRegister;
    private Button mBtnLogin;
    private EditText mAccounts, mPassword;
    private MenuInflater mi;// 菜单
    private Dialog mDialog = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        util = new SharePreferenceUtil(this, Constants.SAVE_USER);
        if (util.getisFirst()) {
            util.setIsFirst(false);
        }
        application = (MyApplication) this.getApplication();
        initView();
        mi = new MenuInflater(this);
    }

    @Override
    //先判断网络是否可用，再启动服务
    protected void onResume() {
        super.onResume();
        final Intent service = new Intent(this, GetMsgService.class);
        new Thread() {
            @Override
            public void run() {
                if (isNetworkAvailable()) {
                    startService(service);
                } else {
                    toast(LoginActivity.this);
                }
            }
        }.start();

    }

    //初始化界面
    public void initView() {
        mBtnRegister = (Button) findViewById(R.id.regist_btn);
        mBtnRegister.setOnClickListener(this);

        mBtnLogin = (Button) findViewById(R.id.login_btn);
        mBtnLogin.setOnClickListener(this);

        mAccounts = (EditText) findViewById(R.id.login_accounts);
        mPassword = (EditText) findViewById(R.id.login_password);
    }

    //点击按钮的函数
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.regist_btn:
                goRegisterActivity();
                break;
            case R.id.login_btn:
                submit();
                break;
            default:
                break;
        }
    }

    //进入注册界面
    public void goRegisterActivity() {
        Intent intent = new Intent();
        intent.setClass(this, RegisterActivity.class);
        startActivity(intent);
    }

    //点击登录后提交请求给服务器
    private void submit() {
        new Thread() {
            @Override
            public void run() {

                String accounts = mAccounts.getText().toString();
                String password = mPassword.getText().toString();
                if (accounts.length() == 0 || password.length() == 0) {
                    LoginActivity.this.getMainLooper().prepare();
                    DialogFactory.ToastDialog(LoginActivity.this, "账号登录", "帐号或密码不能为空!");
                    LoginActivity.this.getMainLooper().loop();
                } else {
                    // 通过Socket验证信息
                    if (application.isClientStart()) {
                        Client client = application.getClient();
                        ClientOutputThread out = client.getClientOutputThread();
                        TranObject<User> o = new TranObject<User>(TranObjectType.LOGIN);
                        User u = new User();
                        u.setAccount(accounts);
                        u.setPassword(password);
                        o.setObject(u);
                        out.setMsg(o);
                    } else {
                        if ((mDialog != null) && (mDialog.isShowing()))
                            mDialog.dismiss();
                        LoginActivity.this.getMainLooper().prepare();
                        DialogFactory.ToastDialog(LoginActivity.this, "提示",
                                 "服务器暂未开放!");
                        LoginActivity.this.getMainLooper().loop();
                    }
                }
            }
        }.start();
    }

    @Override
    //登录成功后转为显示好友列表的Activity
    public void getMessage(TranObject msg) {
        if (msg != null) {

            switch (msg.getType()) {
                case LOGIN:// LoginActivity只处理登录的消息
                    List<User> list = (List<User>) msg.getObject();
                    if (list != null && list.size() > 0) {
                        // 保存自己的信息
                        SharePreferenceUtil util = new SharePreferenceUtil(
                                LoginActivity.this, Constants.SAVE_USER);
                        util.setAccount(mAccounts.getText().toString());
                        util.setPassword(mPassword.getText().toString());
                        util.setId(list.get(0).getId());
                        util.setEmail(list.get(0).getEmail());
                        util.setName(list.get(0).getName());
                        System.out.println("kiim login size: " + list.size());
                        System.out.println("kiim login user: " + list.get(0));
                        //保存所有在线用户的信息
                        UserDB db = new UserDB(LoginActivity.this);
                        db.addUser(list);
                        System.out.println("kiim login addok " );
                        Intent i = new Intent(LoginActivity.this,
                                FriendListActivity.class);
                        i.putExtra(Constants.MSGKEY, msg);
                        startActivity(i);
                        finish();
                        Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                    } else {
                        DialogFactory.ToastDialog(LoginActivity.this, "登录失败",
                                "您的帐号或密码错误!");
                        if (mDialog != null && mDialog.isShowing())
                            mDialog.dismiss();
                    }
                    break;
                default:
                    break;
            }
        }

    }
    /*
    @Override
    // 添加菜单
    public boolean onCreateOptionsMenu(Menu menu) {
        mi.inflate(R.menu.login_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    // 菜单选项添加事件处理
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.login_menu_setting:
                setDialog();
                break;
            case R.id.login_menu_exit:
                exitDialog(LoginActivity.this, "提示", "亲！您真的要退出吗？");
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onBackPressed() {// 捕获返回按键
        exitDialog(LoginActivity.this, "提示", "您真的要退出吗？");
    }


    private void exitDialog(Context context, String title, String msg) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(msg)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (application.isClientStart()) {// 如果连接还在，说明服务还在运行
                            // 关闭服务
                            Intent service = new Intent(LoginActivity.this,
                                    GetMsgService.class);
                            stopService(service);
                        }
                        close();// 调用父类自定义的循环关闭方法
                    }
                }).setNegativeButton("取消", null).create().show();
    }


    private void setDialog() {
      /*  final View view = LayoutInflater.from(LoginActivity.this).inflate(
                R.layout.setting_view, null);
        new AlertDialog.Builder(LoginActivity.this).setTitle("设置服务器ip、port")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 把ip和port保存到文件中
                        EditText ipEditText = (EditText) view
                                .findViewById(R.id.setting_ip);
                        EditText portEditText = (EditText) view
                                .findViewById(R.id.setting_port);
                        String ip = ipEditText.getText().toString();
                        String port = portEditText.getText().toString();
                        SharePreferenceUtil util = new SharePreferenceUtil(
                                LoginActivity.this, Constants.IP_PORT);
                        if (ip.length() > 0 && port.length() > 0) {
                            util.setIp(ip);
                            util.setPort(Integer.valueOf(port));
                            Toast.makeText(getApplicationContext(),
                                    "亲！保存成功，重启生效哦", 0).show();
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(),
                                    "亲！ip和port都不能为空哦", 0).show();
                        }
                    }
                }).setNegativeButton("取消", null).create().show();*/
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager mgr = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = mgr.getAllNetworkInfo();
        if (info != null) {
            for (int i = 0; i < info.length; i++) {
                if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    private void toast(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage("您的网络连接未打开!")
                .setPositiveButton("前往打开",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Intent intent = new Intent(
                                        android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                                startActivity(intent);
                            }
                        }).setNegativeButton("取消", null).create().show();
    }
}
