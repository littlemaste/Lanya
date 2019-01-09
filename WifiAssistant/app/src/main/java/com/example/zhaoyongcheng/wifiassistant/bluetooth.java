package com.example.zhaoyongcheng.wifiassistant;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhaoyongcheng.wifiassistant.UDP.Udpsend;
import com.example.zhaoyongcheng.wifiassistant.connect.AcceptThread;
import com.example.zhaoyongcheng.wifiassistant.connect.ConnectThread;
import com.example.zhaoyongcheng.wifiassistant.connect.Constant;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
public class bluetooth extends AppCompatActivity {

    public static final int REQUEST_CODE = 0;
    private List<BluetoothDevice> mDeviceList = new ArrayList<>();
    private List<BluetoothDevice> mBondedDeviceList = new ArrayList<>();
//设备列表
    private OutputStream outStream=null;
    private InputStream inStream=null;
    private BluetoothSocket btSocket=null;
//
    private bluetoothcontroller mController = new bluetoothcontroller();
    private ListView mListView;
    private DeviceAdapter mAdapter;
    private Toast mToast;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;

    Udpsend uDpS = null;
    private DatagramSocket Udpsocket=null;

    EditText ed_ip;
    Button btn_send;
    private InetAddress serAddress;
    private  boolean  stasend=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bluetooth);
        initUI();

        registerBluetoothReceiver();//注册
        mController.turnOnBlueTooth(this, REQUEST_CODE);

        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // String s=ed_ip.getText().toString();
                say("#K!#K!#K!#K!#K!");
            }
        });

    }

    private void registerBluetoothReceiver() {
        IntentFilter filter = new IntentFilter();
        //开始查找
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        //结束查找
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //查找设备
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        //设备扫描模式改变
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        //绑定状态
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        registerReceiver(mReceiver, filter);
    }

    private Handler mUIHandler = new MyHandler();

    private BroadcastReceiver mReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {

                //初始化列表
                mDeviceList.clear();
                mAdapter.notifyDataSetChanged();
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {

            }
            else if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //找到一个，添加一个
                mDeviceList.add(device);
                mAdapter.notifyDataSetChanged();
            }
            else if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action))
            {
                int scanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, 0);
                if (scanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
                {

                }
                else {
                }
            }
            else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action))
            {
                BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (remoteDevice == null) {
                    showToast("no device");
                    return;
                }
                int status = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, 0);
                if (status == BluetoothDevice.BOND_BONDED) {
                    showToast("Bonding" + remoteDevice.getName());
                } else if (status == BluetoothDevice.BOND_NONE) {
                    showToast("Not bond " + remoteDevice.getName());
                }
            }
        }
    };

    private void initUI()
    {
        mListView = (ListView) findViewById(R.id.device_List);
        mAdapter = new DeviceAdapter(mDeviceList, this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(binDeviceClick);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
        }
        if (mConnectThread != null) {
            mConnectThread.cancel();
        }
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void showToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
        }
        mToast.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.enable_visiblity) {
            mController.enableVisibly(this);
        } else if (id == R.id.find_device) {
            //查找设备
            mAdapter.refresh(mDeviceList);
            mController.findDevice();
            mListView.setOnItemClickListener(binDeviceClick);
        } else if (id == R.id.bonded_device) {
            //查看绑定设备
            mBondedDeviceList = mController.getBoothDeviceList();
            mAdapter.refresh(mBondedDeviceList);
            mListView.setOnItemClickListener(bindedDeviceClick);
        } else if (id ==R.id.listening) {
            if (mAcceptThread != null) {
                mAcceptThread.cancel();
            }
        } else if (id == R.id.stop_listening) {
            if (mAcceptThread != null) {
                mAcceptThread.cancel();
            }
            mAcceptThread = new AcceptThread(mController.getAdapter() , mUIHandler);
            mAcceptThread.start();
        } else if (id == R.id.disconnect) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
            }
        } else if (id == R.id.say_hello) {
            say("hello");
        } else if (id == R.id.say_hi) {
            say("hi");
        }

        return super.onOptionsItemSelected(item);
    }

    private void say(String word) {
        if (mAcceptThread != null) {
            try {
                mAcceptThread.sendData(word.getBytes("utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
             else if( mConnectThread != null) {
                try {
                    mConnectThread.sendData(word.getBytes("utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }


    private AdapterView.OnItemClickListener binDeviceClick = new AdapterView.OnItemClickListener() {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            BluetoothDevice device = mDeviceList.get(i);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                device.createBond();
            }
        }
    };

    private AdapterView.OnItemClickListener bindedDeviceClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            BluetoothDevice device = mBondedDeviceList.get(i);
            if( mConnectThread != null) {
                mConnectThread.cancel();
            }
            mConnectThread = new ConnectThread(device,mController.getAdapter(), mUIHandler);
            mConnectThread.start();
        }
    };


    public void clear(View v){
        TextView txv2;
        txv2=(TextView)findViewById(R.id.txv1);
        txv2.clearComposingText();
    }

    String [] com=null;
    private class MyHandler extends Handler{
        String xxx="";
        String xxxx="";
        @Override
        public void handleMessage(Message msg) {
             super.handleMessage(msg);
              switch (msg.what) {
                case Constant.MSG_START_LISTENING:
                break;
                case Constant.MSG_FINISH_LISTENING:
                break;
                case Constant.MSG_GOT_DATA:

                    TextView txv;
                    txv=(TextView)findViewById(R.id.txv1);
                   // txv.setText(String.valueOf(msg.obj));

                    String s=String.valueOf(msg.obj);
                    xxx+=s;
                    xxxx+=s;
                   // txv.setText("开锁成功");
                   // txv.setText(xxxx);
                    if(xxxx.length()>2)
                    {

                       // xxxx.replaceAll(" ","");
                        com = xxxx.split("#");
                        for(int i=0;i<com.length-1;i++)
                            if(com[i].contains("C!")) {
                                txv.setText("开锁成功");

                               txv.setTextColor(Color.RED);
                                /*
                                String  sss="$iot,"+String.valueOf(com[i + 2])+","+String.valueOf(com[i + 3])
                                        +","+String.valueOf(com[i + 1])+","+String.valueOf(com[i + 5])+"," +
                                        String.valueOf(com[i + 4]);
                                uDpS=new Udpsend(sss,Udpsocket,serAddress,Integer.parseInt(ed_port.getText().toString()));
                                uDpS.start();*/
                            }
                        xxxx="";

                    }

                  //  txv.setText(xxx);

                break;
                /*
                  case sendOver:
                      Toast.makeText(MainActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                      break; */
              case Constant.MSG_ERROR:
                showToast("error: "+String.valueOf(msg.obj));
                break;
              case Constant.MSG_CONNECTED_TO_SERVER:
                showToast("Connected to Server");
                break;
              case Constant.MSG_GOT_A_CLINET:
                showToast("Got a Client");
                break;
              }
         }
     }
}







