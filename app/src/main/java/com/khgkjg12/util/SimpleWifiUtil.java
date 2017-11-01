package com.khgkjg12.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import java.util.List;

/**
 * Created by xrisp-khgkjg12 on 2017-08-16.
 */

public class SimpleWifiUtil {

    private OnWifiScanedListener onWifiScanedListener;
    private WifiManager wifiManager;

    public interface OnWifiScanedListener{
        void onWifiScaned(List<ScanResult> scanResults);
    }

    public void setOnWifiScanedListener(OnWifiScanedListener onWifiScanedListener){
        this.onWifiScanedListener = onWifiScanedListener;
    }

    public SimpleWifiUtil(Context context){
        wifiManager = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onWifiScanedListener.onWifiScaned(wifiManager.getScanResults());
            }
        };
        context.getApplicationContext().registerReceiver(broadcastReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        context.getApplicationContext().registerReceiver(new WifiBroadcastListener(),new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
    }

    public void startScan(){
        wifiManager.startScan();
    }

    public void connectNetwork(String ssid, String password){
        Log.d("debug809","join success"+ssid);

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();

        WifiConfiguration wifiConfig = null;

        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
                if(Build.VERSION.SDK_INT<23) {
                    wifiConfig = i;
                }else{
                    try {
                        wifiManager.removeNetwork(i.networkId);
                        Log.d("xrispdebug","remove "+i.networkId);
                    }catch(RuntimeException e){
                        e.printStackTrace();
                        Log.d("xrispdebug","runtime excep");
                    }
                }

            }
        }

        if(wifiConfig == null){
            Log.d("xrispdebug","wifi has not configured");
            wifiConfig = new WifiConfiguration();
            wifiConfig.SSID = "\""+ssid+"\"";
            wifiConfig.preSharedKey = "\"" + password + "\"";
            try{
                wifiConfig.networkId = wifiManager.addNetwork(wifiConfig);
            }catch(RuntimeException e){
                e.printStackTrace();
            }
        }else {
            Log.d("xrispdebug","wifi has configured");
            wifiConfig.preSharedKey = "\"" + password + "\"";
            try{
                wifiConfig.networkId = wifiManager.updateNetwork(wifiConfig);
            }catch(RuntimeException e){
                e.printStackTrace();
                wifiConfig.networkId = -1;
            }
        }

        if(wifiConfig.networkId!=-1){
            Log.d("xrispdebug","successfully configured network");
            boolean networkConnected = false;
            try {
                wifiManager.disconnect();

                Log.d("xrispdebug","successfully disconnected");
                Log.d("xrispdebug", ""+wifiConfig.networkId);
                wifiManager.enableNetwork(wifiConfig.networkId, true);

                Log.d("xrispdebug","successfully enabled");
                wifiManager.reconnect();
                Log.d("xrispdebug", ""+wifiConfig.networkId);
                if(wifiManager.getConnectionInfo().getNetworkId()==wifiConfig.networkId){
                    networkConnected = true;
                }

            }catch(RuntimeException e){
                e.printStackTrace();
                networkConnected = false;
                Log.d("xrispdebug","fail to reconnect");
            }
            if(!networkConnected){
                Log.d("xrispdebug","device's wifi connection failed");
            }
        }else{
            Log.d("xrispdebug","device's wifi connection failed");
        }
    }

    public boolean enableConfiguredNetwork(int networkId){
        wifiManager.disconnect();
        wifiManager.enableNetwork(networkId, true);
        wifiManager.reconnect();
        return wifiManager.getConnectionInfo().getNetworkId() == networkId;
    }

    public boolean connnect(String ssid, String password){

        List<ScanResult> scanResults = wifiManager.getScanResults();
        for(ScanResult scanResult:scanResults ){
            if(scanResult.SSID.equals(ssid)){
                int networkId = configureNetwork(ssid,password,scanResult.capabilities);
                if(networkId == -1)
                    return false;
                return enableConfiguredNetwork(networkId);
            }
        }

        return false;
    }


    public int configureNetwork(String ssid, String password, String capatabilities){
        WifiConfiguration wifiConfig = new WifiConfiguration();
        if(capatabilities.contains("WPA")){
            wifiConfig.preSharedKey = "\""+ password +"\"";
        }else if(capatabilities.contains("WEP")){
            wifiConfig.wepKeys[0] = "\""+password+"\"";
            wifiConfig.wepTxKeyIndex = 0;
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        }else{
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        return wifiManager.addNetwork(wifiConfig);
    }

    private class WifiBroadcastListener extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            Log.d("xrispDebug","network Info : "+networkInfo.getState()+" / detailed : "+networkInfo.getDetailedState());
        }
    }
}
