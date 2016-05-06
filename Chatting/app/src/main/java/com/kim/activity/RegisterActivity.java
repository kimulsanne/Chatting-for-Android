package com.kim.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.kim.client.Client;
import com.kim.client.ClientOutputThread;
import com.kim.common.bean.User;
import com.kim.common.transObj.TranObject;
import com.kim.common.transObj.TranObjectType;
import com.kim.util.DialogFactory;


/**
 * 用户注册的Activity
 */
public class RegisterActivity extends MyActivity implements OnClickListener {

    private Button mBtnRegister;    //注册按钮
    private Button mRegBack;        //返回按钮
    private EditText mEmailEt, mNameEt, mPasswdEt, mPasswdEt2, mAccountEt;
    private Dialog mDialog = null;
    private MyApplication application;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.register);
        application = (MyApplication) this.getApplicationContext();
        initView();

    }

    // 初始化页面
    public void initView() {
        mBtnRegister = (Button) findViewById(R.id.register_btn);
        mRegBack = (Button) findViewById(R.id.reg_back_btn);
        mBtnRegister.setOnClickListener(this);
        mRegBack.setOnClickListener(this);

        mEmailEt = (EditText) findViewById(R.id.reg_email);
        mNameEt = (EditText) findViewById(R.id.reg_name);
        mAccountEt = (EditText) findViewById(R.id.reg_account);
        mPasswdEt = (EditText) findViewById(R.id.reg_password);
        mPasswdEt2 = (EditText) findViewById(R.id.reg_password2);

    }

    //显示提示框
    private void showRequestDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        mDialog = DialogFactory.creatRequestDialog(this, "正在注册中...");
        mDialog.show();
    }

    @Override
    // 捕获返回键
    public void onBackPressed() {
        toast(RegisterActivity.this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_btn:
                showRequestDialog();
                estimate();
                break;
            case R.id.reg_back_btn:
                toast(RegisterActivity.this);
                break;
            default:
                break;
        }
    }

    private void toast(Context context) {
        new AlertDialog.Builder(context).setTitle("账号注册")
                .setMessage("您真的不注册了吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("取消", null).create().show();
    }

    private void estimate() {
        String email = mEmailEt.getText().toString();
        String name = mNameEt.getText().toString();
        String account = mAccountEt.getText().toString();
        String passwd = mPasswdEt.getText().toString();
        String passwd2 = mPasswdEt2.getText().toString();
        if (account.equals("") || name.equals("") || passwd.equals("")
                || passwd2.equals("")) {
            DialogFactory.ToastDialog(RegisterActivity.this, "账号注册",
                    "带*项不能为空!");
            if (mDialog != null && mDialog.isShowing())
                mDialog.dismiss();
        } else {
            if (passwd.equals(passwd2)) {
                showRequestDialog();
                //提交注册信息
                if (application.isClientStart()) {// 如果已连接上服务器
                    Client client = application.getClient();
                    ClientOutputThread out = client.getClientOutputThread();
                    TranObject<User> o = new TranObject<User>(
                            TranObjectType.REGISTER);
                    User u = new User();
                    u.setEmail(email);
                    u.setName(name);
                    u.setAccount(account);
                    u.setPassword(passwd);
                    o.setObject(u);
                    out.setMsg(o);
                } else {
                    if (mDialog != null && mDialog.isShowing())
                        mDialog.dismiss();
                    DialogFactory.ToastDialog(this, "账号注册", "服务器暂未开放!");
                }

            } else {
                if (mDialog != null && mDialog.isShowing())
                    mDialog.dismiss();
                DialogFactory.ToastDialog(RegisterActivity.this, "账号注册",
                        "两次输入的密码不一致!");
            }
        }
    }

    @Override
    //从服务器收到返回消息后处理函数
    public void getMessage(TranObject msg) {
        switch (msg.getType()) {
            case REGISTER:
                User u = (User) msg.getObject();
                int id = u.getId();
                if (id > 0) {
                    if (mDialog != null) {
                        mDialog.dismiss();
                        mDialog = null;
                    }
                    DialogFactory.ToastDialog(RegisterActivity.this, "账号注册",
                            "注册成功!");
                } else {
                    if (mDialog != null) {
                        mDialog.dismiss();
                        mDialog = null;
                    }
                    DialogFactory.ToastDialog(RegisterActivity.this, "账号注册",
                            "注册失败!");
                }
                break;
            default:
                break;
        }
    }
}
