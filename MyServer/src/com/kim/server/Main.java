package com.kim.server;

import java.io.*;  

import java.net.ServerSocket;  
import java.net.Socket;
  
@SuppressWarnings("resource")  
public class Main implements Runnable {  
    public static final String SERVERIP = "59.78.22.200";  
    public static final int SERVERPORT = 9999;  
  
    public void run() {  
        try {  
            System.out.println("S: Connecting...");  
  
			ServerSocket serverSocket = new ServerSocket(SERVERPORT);  
            while (true) {  
                // 等待接受客户端请求   
                Socket client = serverSocket.accept();  
                String ip = client.getInetAddress().toString();		
                System.out.println("S: Receiving from ip: " + ip);  
                  
                try {  
                    // 接受客户端信息  
                    BufferedReader in = new BufferedReader(  
                            new InputStreamReader(client.getInputStream()));  
                      
                    // 发送给客户端的消息   
                    PrintWriter out = new PrintWriter(new BufferedWriter(  
                            new OutputStreamWriter(client.getOutputStream())),true);  
                      
                    System.out.println("S: 111111");  
                    String str = in.readLine(); // 读取客户端的信息  
                    System.out.println("S: 222222");  
                    if (str != null ) {  
                        // 设置返回信息，把从客户端接收的信息再返回给客户端  
                        out.println("You sent to server message is:" + str);  
                        out.flush();                           
                       
                    } else {  
                        System.out.println("Not receiver anything from client!");  
                    }  
                } catch (Exception e) {  
                    System.out.println("S: Error 1");  
                    e.printStackTrace();  
                } finally {  
                    client.close();  
                    System.out.println("S: Done.");  
                }  
            }  
        } catch (Exception e) {  
            System.out.println("S: Error 2");  
            e.printStackTrace();  
        }  
    }  
      
    public static void main(String [] args ) {  
        Thread desktopServerThread = new Thread(new Main());  
        desktopServerThread.start();  
  
    }  
}

  /*
public class Main {  
    private static final int PORT = 9999;  
    public static void main(String[] args) {  
        	String s = "abc";
 
        try {  
            ServerSocket server = new ServerSocket(PORT);  
            while (true) {  
                Socket socket = server.accept();  
 
              /*  OutputStream os=socket.getOutputStream();
                os.write("您好，您收到了服务器的新年祝福！n".getBytes("utf-8"));
                os.close();
                socket.close();*/

                

                /*PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter(socket.getOutputStream())),true); 
                out.write("haaaaa");
                out.flush();  
                out.close(); 
                BufferedWriter writer = new BufferedWriter(  
                        new OutputStreamWriter(socket.getOutputStream())); 
                //writer.write("haaaaa".getBytes("utf-8"));                 
                writer.write(s);
                writer.flush();  
                writer.close();             
                socket.close();
                 
            }  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
  
    }  
  
}  */