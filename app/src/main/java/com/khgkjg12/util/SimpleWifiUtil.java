package com.khgkjg12.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.util.List;

/**
 * Created by xrisp-khgkjg12 on 2017-08-16.
 */

public class SimpleWifiUtil {

    private OnWifiScanedListener onWifiScanedListener;
    private WifiManager wifiManager;

    public interface OnWifiScanedListener{
        public void onWifiScaned(List<ScanResult> scanResults);
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
    }

    public void startScan(){
        wifiManager.startScan();
    }

    public boolean enableConfiguredNetwork(String ssid){

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                return wifiManager.getConnectionInfo().getSSID().equals("\"" + ssid + "\"");
            }
        }
        return false;
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
}
