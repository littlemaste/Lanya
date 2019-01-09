package com.example.zhaoyongcheng.wifiassistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button lanya;
    Button TcpClient;
    Button tuichu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main);
        TcpClient=(Button)findViewById(R.id.TcpClient);
        lanya=(Button)findViewById(R.id.lanya);
        tuichu=(Button)findViewById(R.id.button3);
        TcpClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,wifi.class);
                startActivity(intent);
            }
        });

        lanya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,bluetooth.class);
                startActivity(intent);
            }
        });

        tuichu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent intent=new Intent(MainActivity.this,bluetooth.class);
              //  startActivity(intent);
                System.exit(0);
            }
        });
    }
}
