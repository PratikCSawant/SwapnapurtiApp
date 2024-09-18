package com.example.swapnapurtiapp;

import static android.os.Looper.getMainLooper;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ReceiveDataFragment extends Fragment {

    TextView connectionStatus, messageTextView, connectionType;
    Button btnSwitch, btnDiscover;
    ListView listView;
    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    BroadcastReceiver receiver;
    IntentFilter intentFilter;

    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    String[] devicesNameArray;
    WifiP2pDevice[] deviceArray;

    Socket socket;
    ServerClass serverClass;
    CLientClass cLientClass;
    //Boolean isHost;

    EditText etMultiLine;
    CheckBox cbOverwrite;
    String connectionTypeHostOrClient = "";

    String receiverDeviceName="";
    DBHandler db;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_receive_data, container, false);

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //for closing the display timeout



        //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Swapnapurti - Receive Data");

        db = new DBHandler(getContext());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        connectionStatus = (TextView) rootView.findViewById(R.id.textViewReceiveDataConnectionStatus);
        messageTextView = (TextView) rootView.findViewById(R.id.textViewReceiveDataMessage);
        btnSwitch = (Button) rootView.findViewById(R.id.buttonReceiveDataSwitch);
        btnDiscover = (Button) rootView.findViewById(R.id.buttonReceiveDataDiscover);
        listView = (ListView) rootView.findViewById(R.id.listViewReceiveDataDevicesList);
        connectionType = (TextView) rootView.findViewById(R.id.textViewReceiveDataConnectionType);

        manager = (WifiP2pManager) getActivity().getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(getContext(), getMainLooper(), null);
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivityForResult(intent, 1);
            }
        });

        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.NEARBY_WIFI_DEVICES) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    //return;
                }
                manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        connectionStatus.setText("Discovery started");
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        connectionStatus.setText("Discovery NOT started");
                    }
                });
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final WifiP2pDevice device = deviceArray[i];

                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                try {
                    if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.NEARBY_WIFI_DEVICES) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        //return;
                    }
                    manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            connectionStatus.setText("Connected : " + device.deviceName);
                            receiverDeviceName = device.deviceName;
                        }

                        @Override
                        public void onFailure(int i) {
                            connectionStatus.setText("Not connected");
                            receiverDeviceName = "";
                        }
                    });
                }
                catch (Exception e1)
                {
                    Log.d("listView.setOnItem- ",e1.getMessage());
                }

            }
        });


        etMultiLine = (EditText) rootView.findViewById(R.id.editTextReceiveDataMultiLineLog);

        cbOverwrite = (CheckBox) rootView.findViewById(R.id.checkboxReceiveDataOverwrite);

        return rootView;
    }

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
            try {
                if (!wifiP2pDeviceList.equals(peers)) {
                    peers.clear();
                    peers.addAll(wifiP2pDeviceList.getDeviceList());
                    devicesNameArray = new String[wifiP2pDeviceList.getDeviceList().size()];
                    deviceArray = new WifiP2pDevice[wifiP2pDeviceList.getDeviceList().size()];
                    int index = 0;
                    for (WifiP2pDevice device : wifiP2pDeviceList.getDeviceList()) {
                        devicesNameArray[index] = device.deviceName;
                        deviceArray[index] = device;
                        index++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, devicesNameArray);
                    listView.setAdapter(adapter);

                    if (peers.size() == 0) {
                        connectionStatus.setText("No device found");
                        return;
                    }
                }
            }
            catch(Exception e1)
            {
                //Toast.makeText(getContext(),                        "Exception occurred at onPeersAvailable : " + e1.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("WifiP2pMan.PeerLi-",e1.getMessage());
            }
        }
    };

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            try {
                final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;
                if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                    connectionType.setText("Connection type - Host");
                    //isHost = true;
                    connectionTypeHostOrClient = "Host";
                    serverClass = new ServerClass();
                    serverClass.start();
                } else if (wifiP2pInfo.groupFormed) {
                    connectionType.setText("Connection type - Client");
                    //isHost = false;
                    connectionTypeHostOrClient = "Client";
                    cLientClass = new CLientClass(groupOwnerAddress);
                    cLientClass.start();
                }
            }
            catch(Exception e1)
            {
                //Toast.makeText(getContext(),                        "Exception occurred at onConnectionInfoAvailable : " + e1.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("WifiP2pMan.Connection-",e1.getMessage());
            }
        }
    };

    @Override
    public void onResume() {
        try {
            super.onResume();
            requireActivity().registerReceiver(receiver, intentFilter);
        }
        catch (Exception e) {
            Log.d("onResume-",e.getMessage());
            //Toast.makeText(getContext(),                    "Error occurred at onResume method. " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {
        try {
            super.onPause();
            requireActivity().unregisterReceiver(receiver);
        }
        catch (Exception e) {
            Log.d("onPause-",e.getMessage());
            //Toast.makeText(getContext(),                    "Error occurred at onPause method. " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    //this class will not be used as receiver will be the client and not the server
    public class ServerClass extends Thread{
        ServerSocket serverSocket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public void write(byte[] bytes)
        {
            try {
                outputStream.write(bytes);
            } catch (Exception e) {
                Log.d("server write-",e.getMessage());
                //Toast.makeText(getContext(),                        "Error occurred at Server write method. " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void run() {
            try {


                try {
                    //serverSocket = new ServerSocket(8888);
                    serverSocket = new ServerSocket();
                    serverSocket.setReuseAddress(true);
                    serverSocket.bind(new InetSocketAddress(8888));

                    socket = serverSocket.accept();
                    inputStream = socket.getInputStream();
                    outputStream = socket.getOutputStream();

                } catch (Exception e) {
                    //Toast.makeText(getContext(),                            "Error occurred at Server Run method internal catch. " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d("server run inside-",e.getMessage());
                }

                ExecutorService executorService = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());

                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        byte[] buffer = new byte[1024];
                        int bytes;
                        while (socket != null) {
                            try {
                                bytes = inputStream.read(buffer);
                                if (bytes > 0) {
                                    int finalbytes = bytes;
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            String tempMsg = new String(buffer, 0, finalbytes);
                                            messageTextView.setText(tempMsg);

                                            HandlePackets(tempMsg);

                                        }
                                    });
                                }
                            } catch (Exception e) {
                                Log.d("server executorService-",e.getMessage());
                            }
                        }
                    }
                });
            }
            catch (Exception e) {
                //Toast.makeText(getContext(),                        "Error occurred at Server Run method external catch " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("server executorService-",e.getMessage());
            }
        }
    }

    public class CLientClass extends Thread{
        String hostAdd;
        private InputStream inputStream;
        private OutputStream outputStream;

        public CLientClass(InetAddress hostAddress) {
            hostAdd = hostAddress.getHostAddress();
            socket = new Socket();
        }

        public void write(byte[] bytes)
        {
            try {
                outputStream.write(bytes);
            } catch (Exception e) {
                Log.d("client write-",e.getMessage());
                //Toast.makeText(getContext(),                        "Error occurred at Client write method. " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void run()
        {
            try {


                try {
                    socket.connect(new InetSocketAddress(hostAdd, 8888), 500);
                    inputStream = socket.getInputStream();
                    outputStream = socket.getOutputStream();
                } catch (Exception e) {
                    //Toast.makeText(getContext(),                            "Error occurred at Client Run method internal catch. " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d("client run inside-",e.getMessage());
                }

                ExecutorService executorService = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        byte[] buffer = new byte[1024];
                        int bytes;
                        while (socket != null) {
                            try {
                                bytes = inputStream.read(buffer);
                                if (bytes > 0) {
                                    int finalbytes = bytes;
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            String tempMsg = new String(buffer, 0, finalbytes);
                                            messageTextView.setText(tempMsg);

                                            HandlePackets(tempMsg);
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                //Toast.makeText(getContext(),                                        "Error occurred at client handler method. " + e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.d("client executorService-",e.getMessage());
                            }
                        }
                    }
                });
            }
            catch (Exception e) {
                //Toast.makeText(getContext(),                        "Error occurred at Client Run method external catch. " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("client run-",e.getMessage());
            }
        }
    }

    public void HandlePackets(String tempMsg)
    {
        try {
            String messagformultiline = "From sender device - " + tempMsg + "\n\n" + etMultiLine.getText();
            etMultiLine.setText(messagformultiline);

            String[] delimitedMsg = tempMsg.split(",");

            if (delimitedMsg[2].equals("INSERT_PERSONAL_INFO"))
            {
                String statement = delimitedMsg[4].replace("$", ",");
                statement = statement.replace("'","");
                String[] statementValues = statement.split(",");

                //check uid already exist or not
                Cursor cur = db.GetAllPersonalInfoDetailsAgainstUID(statementValues[0].replace("'", ""));
                if (cur.getCount() > 0) {
                    //uid already exist
                    if (cbOverwrite.isChecked()) {
                        //user has selected checkbox OVERWRITE
                        //hence we can delete the old data and insert the new data
                        //we are passing overwrite signal as yes ("1") in the below insert method
                        InsertPersonalInfoData(statementValues, statement, delimitedMsg[0], "1");
                    } else {
                        //user has NOT selected checkbox OVERWRITE
                        //hence we cannot insert the received data
                        //we will send tha ACK as CANCEL
                        String packet =
                                WiFiDirectBroadcastReceiver.WiFiDirectDeviceName + //sender device name (your device name)
                                        "," + delimitedMsg[0] +                          //receiver device name (the sender's device name)
                                        "," + "ACK" +                                    //function/operation name
                                        "," + CommonVariables.UserId +                       //logged in user id
                                        "," + "CANCEL" +                                   //result-CANCEL-HID exist so saving ignored
                                        "," + statementValues[0] +                                    //HID
                                        "," + "INSERT_PERSONAL_INFO";
                        if (connectionTypeHostOrClient.equals("Host"))
                            serverClass.write(packet.getBytes());
                        else if (connectionTypeHostOrClient.equals("Client"))
                            cLientClass.write(packet.getBytes());
                    }
                } else {
                    //Uid does not exist
                    InsertPersonalInfoData(statementValues, statement, delimitedMsg[0], "0");
                }
            }
            else if (delimitedMsg[2].equals("SUMMARY"))
            {
                AlertDialog ad = new AlertDialog.Builder(getActivity())
                        .create();
                ad.setCancelable(false);
                ad.setTitle("Data receive summary");
                ad.setMessage(delimitedMsg[4]);
                ad.setButton(getActivity().getString(R.string.ok_text), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                ad.show();
            }
        }
        catch (Exception e) {
            //Toast.makeText(getContext(),                    "Error occurred at Handle packets method " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("client HandlePackets-",e.getMessage());
        }
    }

    public void InsertPersonalInfoData(String[] statementValuesArray, String insertStatement, String receiverDeviceName, String overwrite)
    {
        //String insertStatement = statement.replace("'","");
        //String[] PersonalInfoData = insertStatement.split(",");

        Toast.makeText(getContext(),
                "insert statement values : "+insertStatement+ " total cols : "+Integer.toString(statementValuesArray.length), Toast.LENGTH_LONG).show();

        try
        {
            String res = "";
            if(overwrite.equals("1"))
            {
                res = db.DeleteAndInsertPersonalInfoForm(statementValuesArray);
            }
            else {
                res = db.InsertPersonalInfoForm(statementValuesArray);
            }
            if (res.equals("1")) {
                String packet =
                        WiFiDirectBroadcastReceiver.WiFiDirectDeviceName + //sender device name
                                "," + receiverDeviceName +                          //receiver device name
                                "," + "ACK" +                                    //function/operation name
                                "," + CommonVariables.UserId +                       //logged in user id
                                "," + "SUCCESS" +                                   //result-SUCCESS-inserted successfully
                                "," + statementValuesArray[0] +                                    //HID
                                "," + "INSERT_PERSONAL_INFO";
                if(connectionTypeHostOrClient.equals("Host"))
                    serverClass.write(packet.getBytes());
                else if(connectionTypeHostOrClient.equals("Client"))
                    cLientClass.write(packet.getBytes());

                String messagformultiline = "By receiver (you) - SUCCESS " + packet + "\n\n" + etMultiLine.getText();
                etMultiLine.setText(messagformultiline);
            } else {
                String packet =
                        WiFiDirectBroadcastReceiver.WiFiDirectDeviceName + //sender device name
                                "," + receiverDeviceName +                          //receiver device name
                                "," + "ACK" +                                    //function/operation name
                                "," + CommonVariables.UserId +                       //logged in user id
                                "," + "FAILED" +                                   //result-FAILED-not inserted
                                "," + statementValuesArray[0] +                                    //HID
                                "," + "INSERT_PERSONAL_INFO";
                if(connectionTypeHostOrClient.equals("Host"))
                    serverClass.write(packet.getBytes());
                else if(connectionTypeHostOrClient.equals("Client"))
                    cLientClass.write(packet.getBytes());

                String messagformultiline = "By receiver (you) - FAILED " + packet + "\n\n" + etMultiLine.getText();
                etMultiLine.setText(messagformultiline);
            }

        }
        catch (Exception e1)
        {
            //Toast.makeText(getContext(),                    "Exception occurred at InsertPersonalInfoData : " + e1.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("InsertPersonalInfoData-",e1.getMessage());
            String packet =
                    WiFiDirectBroadcastReceiver.WiFiDirectDeviceName+ //sender device name
                            "," + receiverDeviceName +                          //receiver device name
                            "," + "ACK" +                                    //function/operation name
                            "," +CommonVariables.UserId +                       //logged in user id
                            "," + "FAILED" +                                   //result-FAILED-not inserted
                            "," + statementValuesArray[0] +                                    //HID
                            "," + "INSERT_PERSONAL_INFO";
            if(connectionTypeHostOrClient.equals("Host"))
                serverClass.write(packet.getBytes());
            else if(connectionTypeHostOrClient.equals("Client"))
                cLientClass.write(packet.getBytes());

            String messagformultiline = "By receiver (you) - FAILED " + packet + "\n\n" + etMultiLine.getText();
            etMultiLine.setText(messagformultiline);
        }
    }
}