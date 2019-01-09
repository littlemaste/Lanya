package com.example.zhaoyongcheng.wifiassistant.connect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import java.io.IOException;
import java.util.UUID;


/**
 * Created by zhaoyongcheng on 2017/10/28.
 */

public class ConnectThread extends Thread {
    private static final UUID MY_UUID = UUID.fromString(Constant.CONNECTTION_UUID);
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private final Handler mHandler;
    private ConnectedThread mConnectedThread;

    public ConnectThread(BluetoothDevice device, BluetoothAdapter adapter, Handler handler) {
        BluetoothSocket tmp = null;
        mmDevice = device;
        mBluetoothAdapter = adapter;
        mHandler = handler;
        //根据给定的设备获取一个BluetoothSocket对象
        try {
            // 和服务器端使用相同的UUID
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) { }
        mmSocket = tmp;
    }

    public void run() {
        // 取消搜索设备因为会关闭连接
        mBluetoothAdapter.cancelDiscovery();
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        } catch (Exception connectException) {
            mHandler.sendMessage(mHandler.obtainMessage(Constant.MSG_ERROR, connectException));
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }

        // Do work to manage the connection (in a separate thread)
        manageConnectedSocket(mmSocket);
    }

    private void manageConnectedSocket(BluetoothSocket mmSocket) {
        mHandler.sendEmptyMessage(Constant.MSG_CONNECTED_TO_SERVER);
        mConnectedThread = new ConnectedThread(mmSocket, mHandler);
        mConnectedThread.start();
    }

    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }

    public void sendData(byte[] data) {
        if( mConnectedThread!=null){
            mConnectedThread.write(data);
        }
    }
}

