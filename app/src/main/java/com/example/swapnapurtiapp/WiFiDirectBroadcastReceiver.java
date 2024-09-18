package com.example.swapnapurtiapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private MainActivity activity;
    private ViewAndSendDataFragment fragmentViewAndSendData;
    private ReceiveDataFragment receiveDataFragment;
    private String formName = "";

    public static String WiFiDirectDeviceName = "";     //saves current device name (my device wifi direct name)

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity activity) {
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
        formName = "MainActivity";
    }

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, ViewAndSendDataFragment fragment) {
        this.manager = manager;
        this.channel = channel;
        this.fragmentViewAndSendData = fragment;
        formName = "ViewAndSendData";
    }

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, ReceiveDataFragment fragment) {
        this.manager = manager;
        this.channel = channel;
        this.receiveDataFragment = fragment;
        formName = "ReceiveData";
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            if (manager != null) {
                //manager.requestPeers(channel, activity.peerListListener);
                if (formName.equals("ViewAndSendData")) {
                    //added this below if statement because android studio was giving error and this is the solution for it. the return statement is on purpose commented
                    if (ActivityCompat.checkSelfPermission(fragmentViewAndSendData.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(fragmentViewAndSendData.getContext(), android.Manifest.permission.NEARBY_WIFI_DEVICES) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        //return;
                    }
                    manager.requestPeers(channel, fragmentViewAndSendData.peerListListener);
                }
                else if(formName.equals("ReceiveData"))
                {
                    //added this below if statement because android studio was giving error and this is the solution for it. the return statement is on purpose commented
                    if (ActivityCompat.checkSelfPermission(receiveDataFragment.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(receiveDataFragment.getContext(), android.Manifest.permission.NEARBY_WIFI_DEVICES) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        //return;
                    }
                    manager.requestPeers(channel, receiveDataFragment.peerListListener);
                }

            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
            if(manager!=null)
            {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                if(networkInfo.isConnected())
                {
                    //manager.requestConnectionInfo(channel, activity.connectionInfoListener);
                    if(formName.equals("ViewAndSendData"))
                        manager.requestConnectionInfo(channel, fragmentViewAndSendData.connectionInfoListener);
                    else if(formName.equals("ReceiveData"))
                        manager.requestConnectionInfo(channel, receiveDataFragment.connectionInfoListener);
                }
                else {
                    //activity.connectionStatus.setText("Not connected");
                    if(formName.equals("ViewAndSendData"))
                        fragmentViewAndSendData.connectionStatus.setText("Not connected");
                    if(formName.equals("ReceiveData"))
                        receiveDataFragment.connectionStatus.setText("Not connected");
                }
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
            WifiP2pDevice myDevice = (WifiP2pDevice)intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            WiFiDirectDeviceName = myDevice.deviceName;
            CommonVariables.DeviceName = myDevice.deviceName;
            Toast.makeText(context, "Device name = "+myDevice.deviceName, Toast.LENGTH_SHORT).show();
        }
    }
}

