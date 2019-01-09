package com.example.zhaoyongcheng.wifiassistant;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

/**
 *蓝牙适配器
 * Created by zhaoyongcheng on 2017/10/27.
 */

public class bluetoothcontroller {
    private BluetoothAdapter mAdapter;

    public bluetoothcontroller() {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public BluetoothAdapter getAdapter() {
        return mAdapter;
    }


    /**
     * 是否支持蓝牙
     */
    public boolean isSupportBlueTooth() {
        if (mAdapter != null) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * 判断当前蓝牙状态
     */
    public boolean getBlueToothStatus(){
        assert (mAdapter !=null);
        return  mAdapter.isEnabled();
    }

    /**
     * 打开蓝牙设备
     * @param activity
     * @param requestCode
     */
    public void turnOnBlueTooth(Activity activity , int requestCode){
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent,requestCode);
    }

    /**
     * 退出蓝牙
     */
    public void turnOffBlueTooth() {

    }

    /**
     * 打开蓝牙可见性
     */
    public void enableVisibly(Context context){
        Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION , 300);
        context.startActivity(discoverableIntent);
    }

    /**
     * 查找设备
     */
    public void findDevice(){
        assert (mAdapter != null);
        mAdapter.startDiscovery();
    }

    /**
     * 获取绑定设备
     */
    public List<BluetoothDevice> getBoothDeviceList(){
        return new ArrayList<>(mAdapter.getBondedDevices());
    }


}