package com.example.kim.chattest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 测试Android客户端与PC服务器通过socket进行交互
 * 客户端：把用户输入的信息发送给服务器
 * @author Ameyume
 *
 */
public class MainActivity extends Activity {
    private static final String TAG = "Socket_Android";

    private EditText mEditText = null;
    private TextView tx1 = null;
    private Button mButton = null;
    private String msg;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = (Button)findViewById(R.id.btn_receive);
        mEditText = (EditText)findViewById(R.id.tx);
        tx1 = (TextView)findViewById(R.id.txt);

        mButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        setTitle("测试Socket连接");
                        Socket socket = null;

                        try {
                    /* 指定Server的IP地址，此地址为局域网地址，如果是使用WIFI上网，则为PC机的WIFI IP地址
                     * 在ipconfig查看到的IP地址如下：
                     * Ethernet adapter 无线网络连接:
                     * Connection-specific DNS Suffix  . : IP Address. . . . . . . . . . . . : 192.168.1.100
                     */
                            InetAddress serverAddr = InetAddress.getByName("59.78.22.200");// TCPServer.SERVERIP
                            Log.d("TCP", "C: Connecting...");

                            // 应用Server的IP和端口建立Socket对象
                            socket = new Socket(serverAddr, 9999);
                            String message = "---Test_Socket_Android---";

                            Log.d("TCP", "C: Sending: '" + message + "'");

                            // 将信息通过这个对象来发送给Server
                            PrintWriter out = new PrintWriter(new BufferedWriter(
                                    new OutputStreamWriter(socket.getOutputStream())),
                                    true);

                            // 把用户输入的内容发送给server
                            String toServer = mEditText.getText().toString();
                            Log.d(TAG, "To server:'" + toServer + "'");
                            out.println(toServer);
                            out.flush();


                            // 接收服务器信息
                            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(socket.getInputStream()));
                            // 得到服务器信息
                            msg = in.readLine();
                            Log.d(TAG, "From server:'" + msg + "'");
                            // 在页面上进行显示
                            //tx1.setText(msg);
                            handler.sendEmptyMessage(0);
                        } catch (UnknownHostException e) {
                            Log.e(TAG, "this is unkown server!");
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                socket.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();
            }
        });
    }


    private Handler handler = new Handler() {
        @Override
        // 当有消息发送出来的时候就执行Handler的这个方法
        public void handleMessage(Message msg1) {
            super.handleMessage(msg1);
            // 处理UI
            tx1.setText(msg);

        }
    };
}
/*
@SuppressLint("HandlerLeak")
public class MainActivity extends ActionBarActivity {

    private Button btn_receive;
    private TextView txt;
    private String line;

    private static final String HOST = "59.78.22.200";
    private static final int PORT = 9999;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initControl();
    }

    private void initControl() {
        btn_receive = (Button) findViewById(R.id.btn_receive);
        txt = (TextView) findViewById(R.id.txt);
        btn_receive.setOnClickListener(new ReceiverListener());
    }


    @SuppressLint("HandlerLeak")
    class ReceiverListen       er implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            new Thread() {
                @Override
                public void run() {
                    // 执行完毕后给handler发送一个空消息
                    try {
                        // 实例化Socket
                        Socket socket = new Socket(HOST, PORT);
                        // 获得输入流
                        BufferedReader br = new BufferedReader(
                                new InputStreamReader(socket.getInputStream()));
                        line = br.readLine();
                        System.out.println("receive message: " + line);
                        br.close();
                        socket.close();
                    } catch (UnknownHostException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    //handler.sendEmptyMessage(0);
                }
             }.start();
        }

    }

    // 定义Handler对象
    private Handler handler = new Handler() {
        @Override
        // 当有消息发送出来的时候就执行Handler的这个方法
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 处理UI
            txt.setText(line);
            Log.i("PDA", "----->" + line);
        }
    };
}
*/