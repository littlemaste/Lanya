package com.example.zhaoyongcheng.wifiassistant.UDP;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by zhaoyongcheng on 2017/10/30.
 */

public class Udprec extends Thread {
    private  DatagramSocket  sockets;
    byte data[] = new byte[1024];
    DatagramPacket dataPacket = new DatagramPacket(data, data.length);
    Handler mhandler;

    public Udprec(DatagramSocket datagramSocket,Handler handler)
    {
        sockets=datagramSocket;
        mhandler=handler;
    }

    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes;
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // 从输入流读取数据
                sockets.receive(dataPacket);
                // Send the obtained bytes to the UI activity

                Message message = mhandler.obtainMessage(11,new String(dataPacket.getData() , dataPacket.getOffset() , dataPacket.getLength(),"utf-8"));
                mhandler.sendMessage(message);

            }
            catch (IOException e) {
                mhandler.sendMessage(mhandler.obtainMessage(10, e));
                break;
            }
        }
    }
    public void cancel() {

        sockets.close();

    }
}

