package com.khgkjg12.simplewifimanager;

import android.app.Activity;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.khgkjg12.util.SimpleWifiUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private Button scanBtn, connectBtn;
    private EditText ssidText, passwordText;
    private SimpleWifiUtil simpleWifiUtil;
    private ListView listView;
    private ArrayList<ScanResult> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ssidText = (EditText)findViewById(R.id.ssidText);
        passwordText = (EditText)findViewById(R.id.pwdText);
        scanBtn = (Button)findViewById(R.id.strtscnbtn);
        connectBtn = (Button)findViewById(R.id.cntbtn);
        listView = (ListView)findViewById(R.id.listview);

        arrayList = new ArrayList<>();
        listView.setAdapter(new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,arrayList));

        simpleWifiUtil = new SimpleWifiUtil(getApplicationContext());
        simpleWifiUtil.setOnWifiScanedListener(new SimpleWifiUtil.OnWifiScanedListener() {
            @Override
            public void onWifiScaned(List<ScanResult> scanResults) {
                arrayList.clear();
                arrayList = (ArrayList<ScanResult>) scanResults;
                listView.notify();
            }
        });

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpleWifiUtil.startScan();
            }
        });

        connectBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                simpleWifiUtil.connnect(ssidText.getText().toString(),passwordText.getText().toString());
            }
        });

    }
}
