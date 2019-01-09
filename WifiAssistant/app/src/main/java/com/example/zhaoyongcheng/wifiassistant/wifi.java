package com.example.zhaoyongcheng.wifiassistant;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhaoyongcheng.wifiassistant.UDP.Udprec;
import com.example.zhaoyongcheng.wifiassistant.UDP.Udpsend;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import static android.R.attr.x;

public class wifi extends AppCompatActivity {
    private Button btn_close;
    private Button btn_con;
    private Button  btn_send;
    private EditText e_sendip;
    private EditText e_recport;
    private EditText e_sendport;
    private TextView txt_shidu;
    private  TextView txt_guang;
    private TextView txt_shuizhi;
    private TextView txt_zhuodu;
    private TextView txt_wendu;
    private TextView txt_command;
    private Handler mUIHandler = new MyHandler();
    private Udprec udprec;
    private Udpsend uDpS;
    private DatagramSocket Udpsocket = null;
    boolean clickedrec=false;
    boolean  staSend=false;
    private  Toast toast;
    private InetAddress  serAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        btn_close = (Button) findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Udpsocket!=null)
                    Udpsocket.close();
                finish();
            }
        });

        btn_con = (Button) findViewById(R.id.btn_open);
        toast = Toast.makeText(getApplicationContext(), "ios", Toast.LENGTH_SHORT);
        toast.show();

        btn_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedrec=true;
                btn_con.setEnabled(false);
                String s_port=e_recport.getText().toString();
                int port = Integer.parseInt(s_port);

                try {
                    Udpsocket = new DatagramSocket(port);
                } catch (SocketException e) {
                    e.printStackTrace();
                }
                udprec = new Udprec(Udpsocket,mUIHandler);
                udprec.start();

                toast.setText("开始接收");
                toast.show();
            }
        });
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                staSend=true;
                String  s_sendip=e_sendip.getText().toString();

                try {
                    serAddress=InetAddress.getByName(s_sendip);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                btn_send.setEnabled(false);
            }
        });
        e_recport=(EditText)findViewById(R.id.ed_recport);
        e_recport.setText("8000");
        e_sendip=(EditText)findViewById(R.id.ed_sendip);
        e_sendip.setText("192.168.31.18");
        e_sendport=(EditText)findViewById(R.id.ed_sendport);
        e_sendport.setText("8080");
        txt_wendu = (TextView) findViewById(R.id.txt_wendu);
        txt_shidu = (TextView) findViewById(R.id.txt_shidu);
        txt_guang = (TextView) findViewById(R.id.txt_guangqiang);
        txt_shuizhi = (TextView) findViewById(R.id.txt_shuizhi);
        txt_zhuodu= (TextView) findViewById(R.id.txt_zhuodu);
        txt_zhuodu= (TextView) findViewById(R.id.txt_zhuodu);
        txt_command=(TextView)findViewById(R.id.txt_command);
        txt_command.setMovementMethod(ScrollingMovementMethod.getInstance());


    }
    String  [] com=null;
    String  [] com1=null;
    String  xxx="";
    String  xxxx="";
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 10:
                    toast.setText("Eroor");
                    toast.show();
                    break;
                case 11:
                    TextView txv1;
                   txv1=(TextView)findViewById(R.id.txt_command);
                    String s=String.valueOf(msg.obj);
                    xxx+=s;
                    txv1.setText(xxx);
                    xxxx+=s;

                    if(xxxx.length()>140) {
                        xxxx.replaceAll(" ","");
                        com1 = xxxx.split("#");
                        com=com1[1].split(",");
                        for (int i = 0; i < com.length-6; i++)
                            if (com[i].contains("IOS")) {

                                toast.setText("Update");
                                toast.show();
                                txt_guang.setText(String.valueOf(com[i + 1]));
                                if (Double.valueOf(String.valueOf(com[i + 1])) < 50) {
                                    txt_guang.setTextColor(Color.RED);
                                } else {
                                    txt_guang.setTextColor(Color.DKGRAY);
                                }
                                txt_wendu.setText(String.valueOf(com[i + 2]));
                                txt_wendu.setTextColor(Color.DKGRAY);
                                txt_shidu.setText(String.valueOf(com[i + 3]));
                                txt_shidu.setTextColor(Color.DKGRAY);
                                txt_shuizhi.setText(String.valueOf(com[i + 4]));
                                txt_shuizhi.setTextColor(Color.DKGRAY);
                                txt_zhuodu.setText(String.valueOf(com[i + 5]));
                                txt_zhuodu.setTextColor(Color.DKGRAY);
                                String  sss="$iot,"+String.valueOf(com[i + 2])+","+String.valueOf(com[i + 3])
                                        +","+String.valueOf(com[i + 1])+","+String.valueOf(com[i + 5])+"," +
                                        String.valueOf(com[i + 4]);
                                uDpS=new Udpsend(sss,Udpsocket,serAddress,Integer.parseInt(e_sendport.getText().toString()));
                                uDpS.start();
                            }
                        xxxx="";
                    }
                    break;

            }
        }
    }

}
