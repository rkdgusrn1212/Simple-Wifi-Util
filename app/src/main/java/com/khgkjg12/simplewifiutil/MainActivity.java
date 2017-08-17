package com.khgkjg12.simplewifiutil;

import android.app.Activity;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.util.Log;
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
    private ArrayList<String> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ssidText = (EditText)findViewById(R.id.ssidText);
        passwordText = (EditText)findViewById(R.id.pwdText);
        scanBtn = (Button)findViewById(R.id.strtscnbtn);
        connectBtn = (Button)findViewById(R.id.cntbtn);
        listView = (ListView)findViewById(R.id.listview);

        arrayList = new ArrayList<String>();
        listView.setAdapter(new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,arrayList));
        listView.setBackgroundColor(Color.DKGRAY);

        simpleWifiUtil = new SimpleWifiUtil(getApplicationContext());
        simpleWifiUtil.setOnWifiScanedListener(new SimpleWifiUtil.OnWifiScanedListener() {
            @Override
            public void onWifiScaned(List<ScanResult> scanResults) {
                arrayList.clear();
                Log.d("test",scanResults.toString());
                for(ScanResult scanResult:scanResults) {
                    arrayList.add(scanResult.SSID+" capa:"+scanResult.capabilities);
                }
                ((ArrayAdapter)listView.getAdapter()).notifyDataSetChanged();
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
