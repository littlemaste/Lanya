package com.example.zhaoyongcheng.wifiassistant.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by zhaoyongcheng on 2017/11/2.
 */

public class Udpsend extends Thread  {
    private    String s;
    private    DatagramSocket  Usocket;
    private   InetAddress  serverAddress;
    private   int  port;
    public Udpsend(String se, DatagramSocket socket, InetAddress inetAddress, int poo){
        s=se;
        Usocket=socket;
        serverAddress =inetAddress;
        port=poo;
    }
    public void run() {
        byte data[]=s.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, port);
        try {
            Usocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void cancel() {

        Usocket.close();

    }
}

