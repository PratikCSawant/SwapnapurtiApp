package com.example.swapnapurtiapp;

import static android.os.Looper.getMainLooper;
import static androidx.core.content.ContextCompat.getSystemService;
import static androidx.core.content.ContextCompat.registerReceiver;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewAndSendDataFragment extends Fragment {

    Button btnPickDate, btnPersonalInfoSearch, btnOralExamFormSearch, btnBreastExamFormSearch, btnSendPersonalInfo;
    TextView tvDate, tvOralExamFormSelectedRecords, tvPersonalInfoFormSelectedRecords, tvBreastExamFormSelectedRecords;
    CheckBox cbPersonalInfoFormSelectAll, cbOralExamFormSelectAll, cbBreastExamFormSelectAll;

    TableLayout tblPersonalInfoForm, tblOralExamForm, tblBreastExamForm;
    DBHandler db;


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
    static String sDate = "";

    String receiverDeviceName = "";
    List<String> selectedRowUID = new ArrayList<String>();
    Boolean sendingDataPackets = false;
    EditText etMultiLine;
    String connectionTypeHostOrClient = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_and_send_data, container, false);

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //for closing the display timeout



        //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Swapnapurti - View and Send Data");

        tvDate = (TextView) rootView.findViewById(R.id.textViewViewAndSendDataDate);

        db = new DBHandler(getContext());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        String sYear = Integer.toString(year);
        String sMonth = ("00" + Integer.toString(month)).substring(Integer.toString(month).length());
        String sDay = ("00" + Integer.toString(day)).substring(Integer.toString(day).length());
        sDate = sYear + "-" + sMonth + "-" + sDay;

        tvDate.setText(sDate);

        btnPickDate = (Button) rootView.findViewById(R.id.buttonViewAndSendDataPickDate);
        btnPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment newFragment = new DatePickerFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                newFragment.show(fragmentManager, "datePicker");
                //Toast.makeText(rootView.getContext(), "date = " + sDate, Toast.LENGTH_SHORT).show();
                setDate();
                //getAllPersonalInfoFormAgainstDate();
            }
        });

        tblPersonalInfoForm = (TableLayout) rootView.findViewById(R.id.tableLayoutViewAndSendFragmentPersonalInfoForm);
        tblOralExamForm = (TableLayout) rootView.findViewById(R.id.tableLayoutViewAndSendFragmentOralExamForm);
        tblBreastExamForm = (TableLayout) rootView.findViewById(R.id.tableLayoutViewAndSendFragmentBreastExamForm);

        btnPersonalInfoSearch = (Button) rootView.findViewById(R.id.buttonViewAndSendDataPersonalInfoFormSearch);
        btnPersonalInfoSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tvDate.getText().toString().equals("")) {
                    //date selected
                    getAllPersonalInfoFormAgainstDate();
                }
            }
        });

        btnOralExamFormSearch = (Button) rootView.findViewById(R.id.buttonViewAndSendDataOralExamFormSearch);
        btnOralExamFormSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tvDate.getText().toString().equals("")) {
                    //date selected
                    getAllOralExamFormAgainstDate();
                }
            }
        });

        btnBreastExamFormSearch = (Button) rootView.findViewById(R.id.buttonViewAndSendDataBreastExamFormSearch);
        btnBreastExamFormSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tvDate.getText().toString().equals("")) {
                    //date selected
                    getAllBreastExamFormAgainstDate();
                }
            }
        });


        connectionStatus = (TextView) rootView.findViewById(R.id.textViewViewAndSendDataConnectionStatus);
        messageTextView = (TextView) rootView.findViewById(R.id.textViewViewAndSendDataMessage);
        btnSwitch = (Button) rootView.findViewById(R.id.buttonViewAndSendDataSwitch);
        btnDiscover = (Button) rootView.findViewById(R.id.buttonViewAndSendDataDiscover);
        listView = (ListView) rootView.findViewById(R.id.listViewViewAndSendDataDevicesList);
        connectionType = (TextView) rootView.findViewById(R.id.textViewViewAndSendDataConnectionType);

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
                catch (Exception e1)
                {
                    Log.d("btnDiscover.setOnClick-",e1.getMessage());
                }
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

        tvPersonalInfoFormSelectedRecords = (TextView) rootView.findViewById(R.id.textViewViewAndSendDataSelectedPersonalInfo);
        tvOralExamFormSelectedRecords = (TextView) rootView.findViewById(R.id.textViewViewAndSendDataSelectedOralExamForm);
        tvBreastExamFormSelectedRecords = (TextView) rootView.findViewById(R.id.textViewViewAndSendDataSelectedBreastExamForm);

        cbPersonalInfoFormSelectAll = (CheckBox) rootView.findViewById(R.id.checkboxViewAndSendDataPersonalInfoSelectAll);
        cbPersonalInfoFormSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if(isChecked)
                    {
                        if(tblPersonalInfoForm.getChildCount() > 4)
                        {
                            int selectCount = 0;
                            int rowCount = tblPersonalInfoForm.getChildCount();
                            for (int i = 4; i < rowCount; i++)
                            {
                                // Get table row.
                                View rowView = tblPersonalInfoForm.getChildAt(i);
                                if (rowView instanceof TableRow)
                                {
                                    TableRow tableRow = (TableRow) rowView;
                                    View columnViewSelect = tableRow.getChildAt(0);
                                    CheckBox chkSelect = (CheckBox) columnViewSelect;
                                    chkSelect.setChecked(true);
                                    selectCount++;
                                }
                            }
                            tvPersonalInfoFormSelectedRecords.setText("Selected : "+Integer.toString(selectCount));
                        }
                        else
                        {
                            cbPersonalInfoFormSelectAll.setChecked(false);
                            Toast.makeText(getContext(),
                                    "No data to select", Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        if(tblPersonalInfoForm.getChildCount() > 4)
                        {
                            int rowCount = tblPersonalInfoForm.getChildCount();
                            for (int i = 4; i < rowCount; i++)
                            {
                                // Get table row.
                                View rowView = tblPersonalInfoForm.getChildAt(i);
                                if (rowView instanceof TableRow)
                                {
                                    TableRow tableRow = (TableRow) rowView;
                                    View columnViewSelect = tableRow.getChildAt(0);
                                    CheckBox chkSelect = (CheckBox) columnViewSelect;
                                    chkSelect.setChecked(false);
                                }
                            }
                            tvPersonalInfoFormSelectedRecords.setText("Selected : 0");
                        }
                    }
                }
                catch (Exception e1)
                {
                    Log.d("cbPersonalSelectOnCheck",e1.getMessage());
                }
            }
        });


        cbOralExamFormSelectAll = (CheckBox) rootView.findViewById(R.id.checkboxViewAndSendDataOralExamFormSelectAll);
        cbOralExamFormSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try{
                    if(isChecked)
                    {
                        if(tblOralExamForm.getChildCount() > 4)
                        {
                            int selectCount = 0;
                            int rowCount = tblOralExamForm.getChildCount();
                            for (int i = 4; i < rowCount; i++)
                            {
                                // Get table row.
                                View rowView = tblOralExamForm.getChildAt(i);
                                if (rowView instanceof TableRow)
                                {
                                    TableRow tableRow = (TableRow) rowView;
                                    View columnViewSelect = tableRow.getChildAt(0);
                                    CheckBox chkSelect = (CheckBox) columnViewSelect;
                                    chkSelect.setChecked(true);
                                    selectCount++;
                                }
                            }
                            tvOralExamFormSelectedRecords.setText("Selected : "+Integer.toString(selectCount));
                        }
                        else
                        {
                            cbOralExamFormSelectAll.setChecked(false);
                            Toast.makeText(getContext(),
                                    "No data to select", Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        if(tblOralExamForm.getChildCount() > 4)
                        {
                            int rowCount = tblOralExamForm.getChildCount();
                            for (int i = 4; i < rowCount; i++)
                            {
                                // Get table row.
                                View rowView = tblOralExamForm.getChildAt(i);
                                if (rowView instanceof TableRow)
                                {
                                    TableRow tableRow = (TableRow) rowView;
                                    View columnViewSelect = tableRow.getChildAt(0);
                                    CheckBox chkSelect = (CheckBox) columnViewSelect;
                                    chkSelect.setChecked(false);
                                }
                            }
                            tvOralExamFormSelectedRecords.setText("Selected : 0");
                        }
                    }
                }
                catch (Exception e1)
                {
                    Log.d("cbOralSelect.OnCheck",e1.getMessage());
                }
            }
        });

        cbBreastExamFormSelectAll = (CheckBox) rootView.findViewById(R.id.checkboxViewAndSendDataBreastExamFormSelectAll);
        cbBreastExamFormSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try{
                    if(isChecked)
                    {
                        if(tblBreastExamForm.getChildCount() > 4)
                        {
                            int selectCount = 0;
                            int rowCount = tblBreastExamForm.getChildCount();
                            for (int i = 4; i < rowCount; i++)
                            {
                                // Get table row.
                                View rowView = tblBreastExamForm.getChildAt(i);
                                if (rowView instanceof TableRow)
                                {
                                    TableRow tableRow = (TableRow) rowView;
                                    View columnViewSelect = tableRow.getChildAt(0);
                                    CheckBox chkSelect = (CheckBox) columnViewSelect;
                                    chkSelect.setChecked(true);
                                    selectCount++;
                                }
                            }
                            tvBreastExamFormSelectedRecords.setText("Selected : "+Integer.toString(selectCount));
                        }
                        else
                        {
                            cbBreastExamFormSelectAll.setChecked(false);
                            Toast.makeText(getContext(),
                                    "No data to select", Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        if(tblBreastExamForm.getChildCount() > 4)
                        {
                            int rowCount = tblBreastExamForm.getChildCount();
                            for (int i = 4; i < rowCount; i++)
                            {
                                // Get table row.
                                View rowView = tblBreastExamForm.getChildAt(i);
                                if (rowView instanceof TableRow)
                                {
                                    TableRow tableRow = (TableRow) rowView;
                                    View columnViewSelect = tableRow.getChildAt(0);
                                    CheckBox chkSelect = (CheckBox) columnViewSelect;
                                    chkSelect.setChecked(false);
                                }
                            }
                            tvBreastExamFormSelectedRecords.setText("Selected : 0");
                        }
                    }
                }
                catch (Exception e1)
                {
                    Log.d("cbBreastSelect.OnCheck",e1.getMessage());
                }
            }
        });

        tblPersonalInfoForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(tblPersonalInfoForm.getChildCount() > 4)
                    {
                        int selectCount = 0;
                        int rowCount = tblPersonalInfoForm.getChildCount();
                        for (int i = 4; i < rowCount; i++)
                        {
                            // Get table row.
                            View rowView = tblPersonalInfoForm.getChildAt(i);
                            if (rowView instanceof TableRow)
                            {
                                TableRow tableRow = (TableRow) rowView;
                                View columnViewSelect = tableRow.getChildAt(0);
                                CheckBox chkSelect = (CheckBox) columnViewSelect;
                                if(chkSelect.isChecked())
                                    selectCount++;
                            }
                        }
                        tvPersonalInfoFormSelectedRecords.setText("Selected : "+Integer.toString(selectCount));
                    }
                }
                catch (Exception e1)
                {
                    Log.d("tblPersonal.setOnClick-",e1.getMessage());
                }
            }
        });

        tblOralExamForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(tblOralExamForm.getChildCount() > 4)
                    {
                        int selectCount = 0;
                        int rowCount = tblOralExamForm.getChildCount();
                        for (int i = 4; i < rowCount; i++)
                        {
                            // Get table row.
                            View rowView = tblOralExamForm.getChildAt(i);
                            if (rowView instanceof TableRow)
                            {
                                TableRow tableRow = (TableRow) rowView;
                                View columnViewSelect = tableRow.getChildAt(0);
                                CheckBox chkSelect = (CheckBox) columnViewSelect;
                                if(chkSelect.isChecked())
                                    selectCount++;
                            }
                        }
                        tvOralExamFormSelectedRecords.setText("Selected : "+Integer.toString(selectCount));
                    }
                }
                catch (Exception e1)
                {
                    Log.d("tblOralExam.setOnClick-",e1.getMessage());
                }
            }
        });

        tblBreastExamForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(tblBreastExamForm.getChildCount() > 4)
                    {
                        int selectCount = 0;
                        int rowCount = tblBreastExamForm.getChildCount();
                        for (int i = 4; i < rowCount; i++)
                        {
                            // Get table row.
                            View rowView = tblBreastExamForm.getChildAt(i);
                            if (rowView instanceof TableRow)
                            {
                                TableRow tableRow = (TableRow) rowView;
                                View columnViewSelect = tableRow.getChildAt(0);
                                CheckBox chkSelect = (CheckBox) columnViewSelect;
                                if(chkSelect.isChecked())
                                    selectCount++;
                            }
                        }
                        tvBreastExamFormSelectedRecords.setText("Selected : "+Integer.toString(selectCount));
                    }
                }
                catch (Exception e1)
                {
                    Log.d("tblBreastEx.setOnClick-",e1.getMessage());
                }
            }
        });

        btnSendPersonalInfo = (Button) rootView.findViewById(R.id.buttonViewAndSendDataPersonalInfoFormSend);
        btnSendPersonalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(connectionTypeHostOrClient.equals("Host") ||
                        connectionTypeHostOrClient.equals("Client"))
                {
                    int selectCount = 0;
                    int rowCount = tblPersonalInfoForm.getChildCount();
                    for (int i = 4; i < rowCount; i++)
                    {
                        // Get table row.
                        View rowView = tblPersonalInfoForm.getChildAt(i);
                        if (rowView instanceof TableRow)
                        {
                            TableRow tableRow = (TableRow) rowView;
                            View columnViewSelect = tableRow.getChildAt(0);
                            CheckBox chkSelect = (CheckBox) columnViewSelect;
                            if(chkSelect.isChecked())
                                selectCount++;

                            tableRow.setBackgroundColor(Color.TRANSPARENT);
                        }
                    }
                    if(selectCount>0)
                    {
                        SendPersonalInfoFormData();
                    }
                    else {
                        Toast.makeText(getContext(),
                                "No data selected from Personal Information Form. First select data then click on SEND DATA", Toast.LENGTH_LONG).show();
                    }

                }
                else
                {
                    Toast.makeText(getContext(), "Receiving device not connected or your device is a client device.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        etMultiLine = (EditText) rootView.findViewById(R.id.editTextViewAndSendDataMultiLineLog);

        return rootView;
    }

    public void SendPersonalInfoFormData()
    {
        try {
            if (tblPersonalInfoForm.getChildCount() > 4)
            {
                if(receiverDeviceName.equals(""))
                {
                    Toast.makeText(getContext(),
                            "No RECEIVER DEVICE FOUND. Connect to the device and then send data", Toast.LENGTH_LONG).show();
                }
                else {

                    //below code will execute when the user selects the data that need to be sent and click on the send button.
                    //i.e. the list of selectedRowUID is empty
                    //The will fill the list selectedRowUID with UIDs that need to be sent
                    //below code will not run when selectedRowUID is not empty. i.e. already packets are sent and we are about to send next packet
                    if(selectedRowUID.size() == 0) {
                        //add all UIDs in the list selectedRowUID
                        int totalRows = tblPersonalInfoForm.getChildCount();
                        for (int i = 4; i < totalRows; i++) {
                            // Get table row.
                            View rowView = tblPersonalInfoForm.getChildAt(i);
                            if (rowView instanceof TableRow) {
                                TableRow tableRow = (TableRow) rowView;
                                View columnViewSelect = tableRow.getChildAt(0);
                                CheckBox chkSelect = (CheckBox) columnViewSelect;
                                if (chkSelect.isChecked()) {
                                    View columnViewHID = tableRow.getChildAt(1);
                                    TextView tvUID = (TextView) columnViewHID;
                                    selectedRowUID.add(tvUID.getText().toString() + "," + Integer.toString(i));
                                }
                            }
                        }

                        //clearing the log as new data has been sent
                        //only clearing the log when user selects rows and click on send button
                        etMultiLine.setText("");
                    }

                    //for testing
                    String selectedUids="";
                    for(int i=0; i<selectedRowUID.size(); i++)
                        selectedUids= selectedUids + " " + selectedRowUID.get(i);
                    Toast.makeText(getContext(),
                            "selected hids : "+selectedUids+" " +
                                    "size : "+Integer.toString(selectedRowUID.size()-1)+" "
                            , Toast.LENGTH_LONG).show();
                    //end of for testing


                    //getting last UID from the list
                    String UIDAndPositionCSV = selectedRowUID.get(selectedRowUID.size()-1);

                    //[0] = UID
                    //[1] = position of UID in table
                    String UIDAndPositionArr[] = UIDAndPositionCSV.split(",");

                    //get insert values statement that needs to be send at receiver so that he can use this values in insert statement
                    String insertValuesStatement = GenerateInsertValuesStatementForPersonalInfo(UIDAndPositionArr[0]);
                    if(insertValuesStatement.equals(""))
                    {
                        Toast.makeText(getContext(), "error while generating insert statement",Toast.LENGTH_LONG).show();
                        return;
                    }
                    Toast.makeText(getContext(),
                            "LastUIDList : "+UIDAndPositionCSV+" UID : "+UIDAndPositionArr[0]+" insertStatement : "+insertValuesStatement,
                            Toast.LENGTH_LONG).show();

                    String packet =
                            WiFiDirectBroadcastReceiver.WiFiDirectDeviceName+ //sender device name
                                    "," + receiverDeviceName +                          //receiver device name
                                    "," + "INSERT_PERSONAL_INFO" +                       //function/operation name
                                    "," + CommonVariables.UserId +                       //logged in user id
                                    "," + insertValuesStatement;                       //values/data
                    if(connectionTypeHostOrClient.equals("Host"))
                        serverClass.write(packet.getBytes());
                    else if(connectionTypeHostOrClient.equals("Client"))
                        cLientClass.write(packet.getBytes());

                    sendingDataPackets = true;

                    String messagformultiline = "By sender (you) - " + packet + "\n\n" + etMultiLine.getText();
                    etMultiLine.setText(messagformultiline);
                }
            }
        }
        catch (Exception e1)
        {
            Log.d("SendPersonalInfoForm - ",e1.getMessage());
        }
    }

    public String GenerateInsertValuesStatementForPersonalInfo(String UID)
    {
        try {
            Cursor curHH = db.GetAllPersonalInfoDetailsAgainstUID(UID);
            curHH.moveToFirst();
            String statement = "";
            StringBuilder sbStatement = new StringBuilder(statement);
            // Personal info table has 12 cols
            //in for loop adding only 11 cols data and later add last col data separately
            for(int i=0; i<11; i++)
            {
                //statement = statement + "'" + curHH.getString(i) + "'$";
                sbStatement.append("'" + curHH.getString(i) + "'$");
            }
            //statement = statement + "'" + curHH.getString(26) + "'";

            //add last col data separately
            sbStatement.append("'" + curHH.getString(11) + "'");


            //return statement;
            return sbStatement.toString();
        }
        catch (Exception e1)
        {
            Log.d("GenerateInsertValuesSt-",e1.getMessage());
        }
        return "";
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker.
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            String sYear = Integer.toString(year);
            String sMonth = ("00" + Integer.toString(month)).substring(Integer.toString(month).length());
            String sDay = ("00" + Integer.toString(day)).substring(Integer.toString(day).length());
            sDate = sYear+"-"+sMonth+"-"+sDay;

            // Create a new instance of DatePickerDialog and return it.
            return new DatePickerDialog(requireContext(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date the user picks.
            month = month + 1;
            String sYear = Integer.toString(year);
            String sMonth = ("00" + Integer.toString(month)).substring(Integer.toString(month).length());
            String sDay = ("00" + Integer.toString(day)).substring(Integer.toString(day).length());
            sDate = sYear+"-"+sMonth+"-"+sDay;
            //ViewAndSendDataFragment v1 = new ViewAndSendDataFragment();
            //v1.setDate();
        }
    }

    public void setDate()
    {
        tvDate.setText(sDate);
    }

    public void getAllPersonalInfoFormAgainstDate()
    {
        DBHandler db = new DBHandler(getActivity().getApplicationContext());

        Cursor cursorPersonalInfoFormDataList = db.getAllGetAllPersonalInfoFormAgainstDate(tvDate.getText().toString());

        while(tblPersonalInfoForm.getChildCount() > 4)
        {
            tblPersonalInfoForm.removeViewAt(tblPersonalInfoForm.getChildCount()-1);
        }

        while(cursorPersonalInfoFormDataList.moveToNext())
        {
            try {

                TableRow tableRow = new TableRow(getActivity().getApplicationContext());

                // Set new table row layout parameters.
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                layoutParams.gravity = Gravity.CENTER;
                tableRow.setLayoutParams(layoutParams);
                tableRow.setPadding(9, 9, 9, 9);

                // Add a select checkbox in the 0 column.
                CheckBox checkBoxSelect = new CheckBox(getActivity().getApplicationContext());
                checkBoxSelect.setText("Select");
                checkBoxSelect.setTextColor(Color.parseColor("#FFFFFF"));

                tableRow.addView(checkBoxSelect, 0);

                TextView textViewUID = new TextView(getActivity().getApplicationContext());
                textViewUID.setText(cursorPersonalInfoFormDataList.getString(0));
                textViewUID.setPadding(9, 9, 9, 9);
                textViewUID.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewUID, 1);

                TextView textViewVillage = new TextView(getActivity().getApplicationContext());
                textViewVillage.setText(cursorPersonalInfoFormDataList.getString(1));
                textViewVillage.setPadding(9, 9, 9, 9);
                textViewVillage.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewVillage, 2);

                TextView textViewName = new TextView(getActivity().getApplicationContext());
                textViewName.setText(cursorPersonalInfoFormDataList.getString(2));
                textViewName.setPadding(9, 9, 9, 9);
                textViewName.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewName, 3);

                TextView textViewSex = new TextView(getActivity().getApplicationContext());
                textViewSex.setText(cursorPersonalInfoFormDataList.getString(3));
                textViewSex.setPadding(9, 9, 9, 9);
                textViewSex.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewSex, 4);

                TextView textViewAge = new TextView(getActivity().getApplicationContext());
                textViewAge.setText(cursorPersonalInfoFormDataList.getString(4));
                textViewAge.setPadding(9, 9, 9, 9);
                textViewAge.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewAge, 5);

                TextView textViewContactNo = new TextView(getActivity().getApplicationContext());
                textViewContactNo.setText(cursorPersonalInfoFormDataList.getString(5));
                textViewContactNo.setPadding(9, 9, 9, 9);
                textViewContactNo.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewContactNo, 6);

                TextView textViewOralSymptoms = new TextView(getActivity().getApplicationContext());
                textViewOralSymptoms.setText(cursorPersonalInfoFormDataList.getString(6));
                textViewOralSymptoms.setPadding(9, 9, 9, 9);
                textViewOralSymptoms.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewOralSymptoms, 7);

                TextView textViewBreastSymptoms = new TextView(getActivity().getApplicationContext());
                textViewBreastSymptoms.setText(cursorPersonalInfoFormDataList.getString(7));
                textViewBreastSymptoms.setPadding(9, 9, 9, 9);
                textViewBreastSymptoms.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewBreastSymptoms, 8);

                TextView textViewCervixSymptoms = new TextView(getActivity().getApplicationContext());
                textViewCervixSymptoms.setText(cursorPersonalInfoFormDataList.getString(8));
                textViewCervixSymptoms.setPadding(9, 9, 9, 9);
                textViewCervixSymptoms.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewCervixSymptoms, 9);

                TextView textViewDEOName = new TextView(getActivity().getApplicationContext());
                textViewDEOName.setText(cursorPersonalInfoFormDataList.getString(9));
                textViewDEOName.setPadding(9, 9, 9, 9);
                textViewDEOName.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewDEOName, 10);

                TextView textViewDEODate = new TextView(getActivity().getApplicationContext());
                textViewDEODate.setText(cursorPersonalInfoFormDataList.getString(10));
                textViewDEODate.setPadding(9, 9, 9, 9);
                textViewDEODate.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewDEODate, 11);

                TextView textViewSavedAtServer = new TextView(getActivity().getApplicationContext());
                textViewSavedAtServer.setText(cursorPersonalInfoFormDataList.getString(11));
                textViewSavedAtServer.setPadding(9, 9, 9, 9);
                textViewSavedAtServer.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewSavedAtServer, 12);

                tblPersonalInfoForm.addView(tableRow);
            }
            catch (Exception e) {
                Toast.makeText(getContext(),
                        "Error occurred at getAllPersonalInfoFormAgainstDate method. " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void getAllOralExamFormAgainstDate()
    {
        try {
            DBHandler db = new DBHandler(getActivity().getApplicationContext());

            Cursor cursorOralExamFormDataList = db.getAllGetAllOralExamFormAgainstDate(tvDate.getText().toString());

            while(tblOralExamForm.getChildCount() > 4)
            {
                tblOralExamForm.removeViewAt(tblOralExamForm.getChildCount()-1);
            }

            while(cursorOralExamFormDataList.moveToNext())
            {
                TableRow tableRow = new TableRow(getActivity().getApplicationContext());

                // Set new table row layout parameters.
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                layoutParams.gravity = Gravity.CENTER;
                tableRow.setLayoutParams(layoutParams);
                tableRow.setPadding(9,9,9,9);

                // Add a select checkbox in the 0 column.
                CheckBox checkBoxSelect = new CheckBox(getActivity().getApplicationContext());
                checkBoxSelect.setText("Select");
                checkBoxSelect.setTextColor(Color.parseColor("#FFFFFF"));

                tableRow.addView(checkBoxSelect, 0);

                TextView textViewUID = new TextView(getActivity().getApplicationContext());
                textViewUID.setText(cursorOralExamFormDataList.getString(0));
                textViewUID.setPadding(9,9,9,9);
                textViewUID.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewUID, 1);

                TextView textViewVillage = new TextView(getActivity().getApplicationContext());
                textViewVillage.setText(cursorOralExamFormDataList.getString(1));
                textViewVillage.setPadding(9,9,9,9);
                textViewVillage.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewVillage, 2);

                TextView textViewName = new TextView(getActivity().getApplicationContext());
                textViewName.setText(cursorOralExamFormDataList.getString(2));
                textViewName.setPadding(9,9,9,9);
                textViewName.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewName, 3);

                TextView textViewSex = new TextView(getActivity().getApplicationContext());
                textViewSex.setText(cursorOralExamFormDataList.getString(3));
                textViewSex.setPadding(9,9,9,9);
                textViewSex.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewSex, 4);

                TextView textViewAge = new TextView(getActivity().getApplicationContext());
                textViewAge.setText(cursorOralExamFormDataList.getString(4));
                textViewAge.setPadding(9,9,9,9);
                textViewAge.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewAge, 5);

                TextView textViewContactNo = new TextView(getActivity().getApplicationContext());
                textViewContactNo.setText(cursorOralExamFormDataList.getString(5));
                textViewContactNo.setPadding(9,9,9,9);
                textViewContactNo.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewContactNo, 6);

                TextView textViewOralHygiene = new TextView(getActivity().getApplicationContext());
                textViewOralHygiene.setText(cursorOralExamFormDataList.getString(6));
                textViewOralHygiene.setPadding(9,9,9,9);
                textViewOralHygiene.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewOralHygiene, 7);

                TextView textViewScreenPositive = new TextView(getActivity().getApplicationContext());
                textViewScreenPositive.setText(cursorOralExamFormDataList.getString(7));
                textViewScreenPositive.setPadding(9,9,9,9);
                textViewScreenPositive.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewScreenPositive, 8);

                TextView textViewLesionType = new TextView(getActivity().getApplicationContext());
                textViewLesionType.setText(cursorOralExamFormDataList.getString(8));
                textViewLesionType.setPadding(9,9,9,9);
                textViewLesionType.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewLesionType, 9);

                TextView textViewDEOName = new TextView(getActivity().getApplicationContext());
                textViewDEOName.setText(cursorOralExamFormDataList.getString(9));
                textViewDEOName.setPadding(9,9,9,9);
                textViewDEOName.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewDEOName, 10);

                TextView textViewDEODate = new TextView(getActivity().getApplicationContext());
                textViewDEODate.setText(cursorOralExamFormDataList.getString(10));
                textViewDEODate.setPadding(9,9,9,9);
                textViewDEODate.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewDEODate, 11);

                TextView textViewSavedAtServer = new TextView(getActivity().getApplicationContext());
                textViewSavedAtServer.setText(cursorOralExamFormDataList.getString(11));
                textViewSavedAtServer.setPadding(9,9,9,9);
                textViewSavedAtServer.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewSavedAtServer, 12);

                tblOralExamForm.addView(tableRow);
            }
        }
        catch (Exception e) {
            Toast.makeText(getContext(),
                    "Error occurred at getAllOralExamFormAgainstDate method. " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void getAllBreastExamFormAgainstDate()
    {
        try {
            DBHandler db = new DBHandler(getActivity().getApplicationContext());

            Cursor cursorBreastExamFormDataList = db.getAllGetAllBreastExamFormAgainstDate(tvDate.getText().toString());

            while(tblBreastExamForm.getChildCount() > 4)
            {
                tblBreastExamForm.removeViewAt(tblBreastExamForm.getChildCount()-1);
            }

            while(cursorBreastExamFormDataList.moveToNext())
            {
                TableRow tableRow = new TableRow(getActivity().getApplicationContext());

                // Set new table row layout parameters.
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                layoutParams.gravity = Gravity.CENTER;
                tableRow.setLayoutParams(layoutParams);
                tableRow.setPadding(9,9,9,9);

                // Add a select checkbox in the 0 column.
                CheckBox checkBoxSelect = new CheckBox(getActivity().getApplicationContext());
                checkBoxSelect.setText("Select");
                checkBoxSelect.setTextColor(Color.parseColor("#FFFFFF"));

                tableRow.addView(checkBoxSelect, 0);

                TextView textViewUID = new TextView(getActivity().getApplicationContext());
                textViewUID.setText(cursorBreastExamFormDataList.getString(0));
                textViewUID.setPadding(9,9,9,9);
                textViewUID.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewUID, 1);

                TextView textViewVillage = new TextView(getActivity().getApplicationContext());
                textViewVillage.setText(cursorBreastExamFormDataList.getString(1));
                textViewVillage.setPadding(9,9,9,9);
                textViewVillage.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewVillage, 2);

                TextView textViewName = new TextView(getActivity().getApplicationContext());
                textViewName.setText(cursorBreastExamFormDataList.getString(2));
                textViewName.setPadding(9,9,9,9);
                textViewName.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewName, 3);

                TextView textViewSex = new TextView(getActivity().getApplicationContext());
                textViewSex.setText(cursorBreastExamFormDataList.getString(3));
                textViewSex.setPadding(9,9,9,9);
                textViewSex.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewSex, 4);

                TextView textViewAge = new TextView(getActivity().getApplicationContext());
                textViewAge.setText(cursorBreastExamFormDataList.getString(4));
                textViewAge.setPadding(9,9,9,9);
                textViewAge.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewAge, 5);

                TextView textViewContactNo = new TextView(getActivity().getApplicationContext());
                textViewContactNo.setText(cursorBreastExamFormDataList.getString(5));
                textViewContactNo.setPadding(9,9,9,9);
                textViewContactNo.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewContactNo, 6);

                TextView textViewBreastLump = new TextView(getActivity().getApplicationContext());
                textViewBreastLump.setText(cursorBreastExamFormDataList.getString(6));
                textViewBreastLump.setPadding(9,9,9,9);
                textViewBreastLump.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewBreastLump, 7);

                TextView textViewNippleDischarge = new TextView(getActivity().getApplicationContext());
                textViewNippleDischarge.setText(cursorBreastExamFormDataList.getString(7));
                textViewNippleDischarge.setPadding(9,9,9,9);
                textViewNippleDischarge.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewNippleDischarge, 8);

                TextView textViewOtherLesion = new TextView(getActivity().getApplicationContext());
                textViewOtherLesion.setText(cursorBreastExamFormDataList.getString(8));
                textViewOtherLesion.setPadding(9,9,9,9);
                textViewOtherLesion.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewOtherLesion, 9);

                TextView textViewDEOName = new TextView(getActivity().getApplicationContext());
                textViewDEOName.setText(cursorBreastExamFormDataList.getString(9));
                textViewDEOName.setPadding(9,9,9,9);
                textViewDEOName.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewDEOName, 10);

                TextView textViewDEODate = new TextView(getActivity().getApplicationContext());
                textViewDEODate.setText(cursorBreastExamFormDataList.getString(10));
                textViewDEODate.setPadding(9,9,9,9);
                textViewDEODate.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewDEODate, 11);

                TextView textViewSavedAtServer = new TextView(getActivity().getApplicationContext());
                textViewSavedAtServer.setText(cursorBreastExamFormDataList.getString(11));
                textViewSavedAtServer.setPadding(9,9,9,9);
                textViewSavedAtServer.setTextColor(Color.parseColor("#FFFFFF"));
                tableRow.addView(textViewSavedAtServer, 12);

                tblBreastExamForm.addView(tableRow);
            }
        }
        catch (Exception e) {
            Toast.makeText(getContext(),
                    "Error occurred at getAllBreastExamFormAgainstDate method. " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
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
            catch (Exception e1)
            {
                Log.d("WifiP2pM.PeerListLis-",e1.getMessage());
            }
        }
    };

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable (WifiP2pInfo wifiP2pInfo)
            {
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
                catch (Exception e1)
                {
                    Log.d("WifiP2pM.ConnectionIn-",e1.getMessage());
                }
            }

    };

    @Override
    public void onResume() {
        try {
            super.onResume();
            requireActivity().registerReceiver(receiver, intentFilter);
        }
        catch (Exception e1)
        {
            Log.d("onResume-",e1.getMessage());
        }
    }

    @Override
    public void onPause() {
        try {
            super.onPause();
            requireActivity().unregisterReceiver(receiver);
        }
        catch (Exception e1)
        {
            Log.d("onPause-",e1.getMessage());
        }
    }

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
                //Toast.makeText(getContext(),"Error occurred at Server write method. " + e.getMessage(), Toast.LENGTH_LONG).show();
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
                    //Toast.makeText(getContext(),"Error occurred at Server Run method internal catch. " + e.getMessage(), Toast.LENGTH_LONG).show();
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
                                //Toast.makeText(getContext(), "Error occurred at Server handler method. " + e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.d("server executorService-",e.getMessage());
                            }
                        }
                    }
                });
            }
            catch (Exception e) {
                //Toast.makeText(getContext(), "Error occurred at Server Run method external catch " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("server run-",e.getMessage());
            }
        }
    }

    public void HandlePackets(String tempMsg)
    {
        try {


            String messagformultiline = "From receiver device - " + tempMsg + "\n\n" + etMultiLine.getText();
            etMultiLine.setText(messagformultiline);

            if (sendingDataPackets) {
                //already packets are sent for data transfer
                //now we have received acknowledgement from RECEIVER

                String[] delimitedMsg = tempMsg.split(",");
                if (delimitedMsg[2].equals("ACK")) {
                    //it is ACK acknowledgement
                    if (delimitedMsg[6].equals("INSERT_PERSONAL_INFO")) {
                        //set the respective row's color to green/red of HID/UID and unselect the row
                        //send next packet

                        //get last UID and its position from selectRowUID whose packet was sent at receiver
                        //String UID[] = selectedRowUID.get(selectedRowUID.size() - 1).split(",");

                        String UID[] = new String[2];

                        //search the UID received from ACK in the selectedRowUID and get the UID and position of UID in table
                        //for(int i=0;i<selectedRowUID.size();i++)
                        for (int i = selectedRowUID.size() - 1; i >= 0; i--) {
                            if (selectedRowUID.get(i).split(",")[0].equals(delimitedMsg[5])) {
                                UID = selectedRowUID.get(i).split(",");
                                break;
                            }
                        }

                        //get the row from table personal info based on the location present in UID i.e. array UID[1]
                        View rowView = tblPersonalInfoForm.getChildAt(Integer.parseInt(UID[1]));
                        TableRow tableRow = (TableRow) rowView;

                        //get the first column of row i.e. the Select checkbox
                        View columnViewSelect = tableRow.getChildAt(0);
                        CheckBox chkSelect = (CheckBox) columnViewSelect;

                        //unselect the checkbox of row
                        chkSelect.setChecked(false);

                        if (delimitedMsg[4].equals("SUCCESS")) {
                            //set bgcolor of row to green
                            tableRow.setBackgroundColor(Color.GREEN);
                        } else if (delimitedMsg[4].equals("FAILED")) {
                            //set bgcolor of row to RED
                            tableRow.setBackgroundColor(Color.RED);
                        } else if (delimitedMsg[4].equals("CANCEL")) {
                            //set bgcolor of row to GRAY
                            tableRow.setBackgroundColor(Color.GRAY);
                        }

                        //remove the last row (sent row)
                        //selectedRowUID.remove(selectedRowUID.size() - 1);

                        //remove the UID received from ACK from the selectedRowUID list
                        //for(int i=0;i<selectedRowUID.size();i++)
                        for (int i = selectedRowUID.size() - 1; i >= 0; i--) {
                            if (selectedRowUID.get(i).split(",")[0].equals(delimitedMsg[5].replace("'",""))) {
                                selectedRowUID.remove(i);
                                break;
                            }
                        }

                        //send next Uid
                        if (selectedRowUID.size() > 0) {
                                                        /*if (tvSDAConnectionStatus.getText().toString().equals("Client") || tvSDAConnectionStatus.getText().toString().equals("Server/Host"))
                                                        {
                                                            String nextHID[] = selectedRowHID.get(selectedRowHID.size() - 1).split(",");
                                                            String insertValuesStatement = GenerateInsertValuesStatementForHH(nextHID[0]);
                                                            String packet =
                                                                    WiFiDirectBroadcastReceiver.WiFiDirectDeviceName + //sender device name
                                                                            "," + receiverDeviceName +                          //receiver device name
                                                                            "," + "INSERTHH" +                                    //function/operation name
                                                                            "," + GlobalVariables.UserID +                      //logged in user id
                                                                            "," + insertValuesStatement;                       //values/data;

                                                            sendReceive.write(packet.getBytes());
                                                        }*/

                            //will send next UID
                            SendPersonalInfoFormData();
                        } else {
                            sendingDataPackets = false;

                            int greenRows = 0;
                            int redRows = 0;
                            int grayRows = 0;

                            int totalRows = tblPersonalInfoForm.getChildCount();
                            for (int i = 4; i < totalRows; i++) {
                                // Get table row.
                                View rowView2 = tblPersonalInfoForm.getChildAt(i);
                                if (rowView2 instanceof TableRow) {
                                    TableRow tableRow2 = (TableRow) rowView2;
                                    //tableRow.setBackgroundColor(Color.GRAY);
                                    Drawable bg = tableRow2.getBackground();
                                    int rowColor = ((ColorDrawable) bg.mutate()).getColor();
                                    if (rowColor == Color.GREEN)
                                        greenRows++;
                                    else if (rowColor == Color.RED)
                                        redRows++;
                                    else if (rowColor == Color.GRAY)
                                        grayRows++;
                                }
                            }


                            AlertDialog ad = new AlertDialog.Builder(getActivity())
                                    .create();
                            ad.setCancelable(false);
                            ad.setTitle("Send data result");
                            String summaryMsg = "Following is the result of Personal Info form data import (" + Integer.toString(greenRows + redRows + grayRows) + " rows) - " + CommonVariables.DeviceName +
                                    "\n\n" +
                                    "Rows inserted - " + Integer.toString(greenRows) + ".\n" +
                                    "Rows rejected  - " + Integer.toString(grayRows) + ".\n" +
                                    "Rows insertion error - " + Integer.toString(redRows) + ".";
                            ad.setMessage(summaryMsg);
                            ad.setButton(getActivity().getString(R.string.ok_text), new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            ad.show();

                            String packet =
                                    WiFiDirectBroadcastReceiver.WiFiDirectDeviceName+ //sender device name
                                            "," + receiverDeviceName +                          //receiver device name
                                            "," + "SUMMARY" +                       //function/operation name
                                            "," + CommonVariables.UserId +                       //logged in user id
                                            "," + summaryMsg;                       //values/data
                            if(connectionTypeHostOrClient.equals("Host"))
                                serverClass.write(packet.getBytes());
                            else if(connectionTypeHostOrClient.equals("Client"))
                                cLientClass.write(packet.getBytes());
                            
                        }
                    }
                }
            }
        }
        catch(Exception e) {
            //Toast.makeText(getContext(),"Error occurred at Handler method. " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("server HandlePackets-",e.getMessage());
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
            } catch(Exception e) {
                //Toast.makeText(getContext(),                        "Error occurred at Client write method. " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("client write-",e.getMessage());
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
                    //Toast.makeText(getContext(),                            "Error occurred at Client run method internal catch. " + e.getMessage(), Toast.LENGTH_LONG).show();
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
                                //Toast.makeText(getContext(),                                        "Error occurred at Client handler method. " + e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.d("client executorService-",e.getMessage());
                            }
                        }
                    }
                });
            }
            catch (Exception e) {
                //Toast.makeText(getContext(),                        "Error occurred at Client run method external catch. " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("client run-",e.getMessage());
            }
        }
    }
}