package com.example.swapnapurtiapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadToServerFragment extends Fragment {

    Button btnPickDate, btnPersonalInfoSearch, btnOralExamFormSearch, btnBreastExamFormSearch,
    btnSendPersonalInfoForm, btnSendOralExamForm, btnSendBreastExamForm;
    TextView tvDate, tvOralExamFormSelectedRecords, tvPersonalInfoFormSelectedRecords, tvBreastExamFormSelectedRecords;
    CheckBox cbPersonalInfoFormSelectAll, cbOralExamFormSelectAll, cbBreastExamFormSelectAll;

    TableLayout tblPersonalInfoForm, tblOralExamForm, tblBreastExamForm;
    DBHandler db;
    static String sDate = "";
    ProgressDialog pgWaiting;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_upload_to_server, container, false);

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //for closing the display timeout



        //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Swapnapurti - View and Send Data");

        tvDate = (TextView) rootView.findViewById(R.id.textViewUploadDataDate);

        db = new DBHandler(getContext());

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        String sYear = Integer.toString(year);
        String sMonth = ("00" + Integer.toString(month)).substring(Integer.toString(month).length());
        String sDay = ("00" + Integer.toString(day)).substring(Integer.toString(day).length());
        sDate = sYear + "-" + sMonth + "-" + sDay;

        tvDate.setText(sDate);

        btnPickDate = (Button) rootView.findViewById(R.id.buttonUploadDataPickDate);
        btnPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewAndSendDataFragment.DatePickerFragment newFragment = new ViewAndSendDataFragment.DatePickerFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                newFragment.show(fragmentManager, "datePicker");
                //Toast.makeText(rootView.getContext(), "date = " + sDate, Toast.LENGTH_SHORT).show();
                setDate();
                //getAllPersonalInfoFormAgainstDate();
            }
        });

        tblPersonalInfoForm = (TableLayout) rootView.findViewById(R.id.tableLayoutUploadFragmentPersonalInfoForm);
        tblOralExamForm = (TableLayout) rootView.findViewById(R.id.tableLayoutUploadFragmentOralExamForm);
        tblBreastExamForm = (TableLayout) rootView.findViewById(R.id.tableLayoutUploadFragmentBreastExamForm);

        btnPersonalInfoSearch = (Button) rootView.findViewById(R.id.buttonUploadDataPersonalInfoFormSearch);
        btnPersonalInfoSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tvDate.getText().toString().equals("")) {
                    //date selected
                    getAllPersonalInfoFormAgainstDate();
                }
            }
        });

        btnOralExamFormSearch = (Button) rootView.findViewById(R.id.buttonUploadDataOralExamFormSearch);
        btnOralExamFormSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tvDate.getText().toString().equals("")) {
                    //date selected
                    getAllOralExamFormAgainstDate();
                }
            }
        });

        btnBreastExamFormSearch = (Button) rootView.findViewById(R.id.buttonUploadDataBreastExamFormSearch);
        btnBreastExamFormSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tvDate.getText().toString().equals("")) {
                    //date selected
                    getAllBreastExamFormAgainstDate();
                }
            }
        });

        tvPersonalInfoFormSelectedRecords = (TextView) rootView.findViewById(R.id.textViewUploadDataSelectedPersonalInfo);
        tvOralExamFormSelectedRecords = (TextView) rootView.findViewById(R.id.textViewUploadDataSelectedOralExamForm);
        tvBreastExamFormSelectedRecords = (TextView) rootView.findViewById(R.id.textViewUploadDataSelectedBreastExamForm);

        cbPersonalInfoFormSelectAll = (CheckBox) rootView.findViewById(R.id.checkboxUploadDataPersonalInfoSelectAll);
        cbPersonalInfoFormSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if(isChecked)
                    {
                        if(tblPersonalInfoForm.getChildCount() > 6)
                        {
                            int selectCount = 0;
                            int rowCount = tblPersonalInfoForm.getChildCount();
                            for (int i = 6; i < rowCount; i++)
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
                        if(tblPersonalInfoForm.getChildCount() > 6)
                        {
                            int rowCount = tblPersonalInfoForm.getChildCount();
                            for (int i = 6; i < rowCount; i++)
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


        cbOralExamFormSelectAll = (CheckBox) rootView.findViewById(R.id.checkboxUploadDataOralExamFormSelectAll);
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

        cbBreastExamFormSelectAll = (CheckBox) rootView.findViewById(R.id.checkboxUploadDataBreastExamFormSelectAll);
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
                    if(tblPersonalInfoForm.getChildCount() > 6)
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

        btnSendPersonalInfoForm = (Button) rootView.findViewById(R.id.buttonUploadDataPersonalInfoFormSend);
        btnSendPersonalInfoForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectCount = 0;
                int rowCount = tblPersonalInfoForm.getChildCount();
                for (int i = 6; i < rowCount; i++)
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
                    UploadPersonalInfoData();
                }
                else {
                    Toast.makeText(getContext(),
                            "No data selected from Personal Information Form. First select data then click on UPLOAD DATA", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnSendOralExamForm = (Button) rootView.findViewById(R.id.buttonUploadDataOralExamFormSend);
        btnSendOralExamForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                        tableRow.setBackgroundColor(Color.TRANSPARENT);
                    }
                }
                if(selectCount>0)
                {
                    UploadOralExamData();
                }
                else {
                    Toast.makeText(getContext(),
                            "No data selected from Oral Exam Form. First select data then click on UPLOAD DATA", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnSendBreastExamForm = (Button) rootView.findViewById(R.id.buttonUploadDataBreastExamFormSend);
        btnSendBreastExamForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                        tableRow.setBackgroundColor(Color.TRANSPARENT);
                    }
                }
                if(selectCount>0)
                {
                    UploadBreastExamData();
                }
                else {
                    Toast.makeText(getContext(),
                            "No data selected from Breast Exam Form. First select data then click on UPLOAD DATA", Toast.LENGTH_LONG).show();
                }
            }
        });

        pgWaiting = new ProgressDialog(getContext());

        return rootView;
    }

    public void UploadPersonalInfoData()
    {
        pgWaiting.setMessage("Uploading Personal Info Form data...");
        pgWaiting.setCancelable(false);
        pgWaiting.show();
        try {
            List<String> selectedRowUID = new ArrayList<String>();
            int totalRows = tblPersonalInfoForm.getChildCount();
            for (int i = 6; i < totalRows; i++) {
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
            APIInterface apiInterface = RetrofitClient.getRetrofit().create(APIInterface.class);
            for (String val: selectedRowUID)
            {
                String UIDAndPositionArr[] = val.split(",");

                View rowView = tblPersonalInfoForm.getChildAt(Integer.parseInt(UIDAndPositionArr[1]));
                TableRow tableRow = (TableRow) rowView;
                //get the first column of row i.e. the Select checkbox
                View columnViewSelect = tableRow.getChildAt(0);
                CheckBox chkSelect = (CheckBox) columnViewSelect;

                String insertValuesStatement = GenerateInsertValuesStatementForPersonalInfo(UIDAndPositionArr[0]);

                Call<String> call = apiInterface.AddPersonalInfo(insertValuesStatement);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        if(response.isSuccessful())
                        {
                            //updating the record's saved at server status from 2 to 1. i.e., saved success on server
                            db.UpdateSavedAtServerOfPersonalInfoFormAgainstUID(UIDAndPositionArr[0].toString());

                            //unselect the checkbox of row
                            chkSelect.setChecked(false);

                            tableRow.setBackgroundColor(Color.GREEN);

                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        //unselect the checkbox of row
                        chkSelect.setChecked(false);

                        tableRow.setBackgroundColor(Color.RED);

                    }
                });
            }

            pgWaiting.dismiss();

            int greenRows = 0;
            int redRows = 0;

            for (int i = 4; i < tblPersonalInfoForm.getChildCount(); i++) {
                View rowView2 = tblPersonalInfoForm.getChildAt(i);
                if (rowView2 instanceof TableRow) {
                    TableRow tableRow2 = (TableRow) rowView2;
                    Drawable bg = tableRow2.getBackground();
                    int rowColor = ((ColorDrawable) bg.mutate()).getColor();
                    if (rowColor == Color.GREEN)
                        greenRows++;
                    else if (rowColor == Color.RED)
                        redRows++;
                }
            }


            AlertDialog ad = new AlertDialog.Builder(getActivity())
                    .create();
            ad.setCancelable(false);
            ad.setTitle("Upload data result");
            String summaryMsg = "Following is the result of Personal Info form data uploading to server (" + Integer.toString(greenRows + redRows) + " rows) - " +
                    "\n\n" +
                    "Rows uploaded - " + Integer.toString(greenRows) + ".\n" +
                    "Rows uploading error - " + Integer.toString(redRows) + ".";
            ad.setMessage(summaryMsg);
            ad.setButton(getActivity().getString(R.string.ok_text), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            ad.show();

        }
        catch (Exception e1)
        {
            pgWaiting.dismiss();
            Log.d("UploadPersonalInfo - ",e1.getMessage());
        }
    }

    public void UploadOralExamData()
    {
        pgWaiting.setMessage("Uploading Oral Exam Form data...");
        pgWaiting.setCancelable(false);
        pgWaiting.show();
        try {
            List<String> selectedRowUID = new ArrayList<String>();
            int totalRows = tblOralExamForm.getChildCount();
            for (int i = 4; i < totalRows; i++) {
                // Get table row.
                View rowView = tblOralExamForm.getChildAt(i);
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
            APIInterface apiInterface = RetrofitClient.getRetrofit().create(APIInterface.class);
            int listsize=0;
            for (String val: selectedRowUID)
            {
                listsize++;
                String UIDAndPositionArr[] = val.split(",");

                View rowView = tblOralExamForm.getChildAt(Integer.parseInt(UIDAndPositionArr[1]));
                TableRow tableRow = (TableRow) rowView;
                //get the first column of row i.e. the Select checkbox
                View columnViewSelect = tableRow.getChildAt(0);
                CheckBox chkSelect = (CheckBox) columnViewSelect;

                String insertValuesStatement = GenerateInsertValuesStatementForOralExam(UIDAndPositionArr[0]);

                Call<String> call = apiInterface.AddOralExam(insertValuesStatement);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        if(response.isSuccessful())
                        {
                            //updating the record's saved at server status from 2 to 1. i.e., saved success on server
                            db.UpdateSavedAtServerOfOralExamAgainstUID(UIDAndPositionArr[0].toString());

                            //unselect the checkbox of row
                            chkSelect.setChecked(false);

                            tableRow.setBackgroundColor(Color.GREEN);

                        }


                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        //unselect the checkbox of row
                        chkSelect.setChecked(false);

                        tableRow.setBackgroundColor(Color.RED);

                    }
                });
            }

            pgWaiting.dismiss();



        }
        catch (Exception e1)
        {
            pgWaiting.dismiss();
            Log.d("UploadOralExam - ",e1.getMessage());
        }
    }

    public void showUploadSummary()
    {
        int greenRows = 0;
        int redRows = 0;

        for (int i = 4; i < tblOralExamForm.getChildCount(); i++) {
            View rowView2 = tblOralExamForm.getChildAt(i);
            if (rowView2 instanceof TableRow) {
                TableRow tableRow2 = (TableRow) rowView2;
                Drawable bg = tableRow2.getBackground();
                int rowColor = ((ColorDrawable) bg.mutate()).getColor();
                if (rowColor == Color.GREEN)
                    greenRows++;
                else if (rowColor == Color.RED)
                    redRows++;
            }
        }


        AlertDialog ad = new AlertDialog.Builder(getActivity())
                .create();
        ad.setCancelable(false);
        ad.setTitle("Upload data result");
        String summaryMsg = "Following is the result of Oral Exam form data uploading to server (" + Integer.toString(greenRows + redRows) + " rows) - " +
                "\n\n" +
                "Rows uploaded - " + Integer.toString(greenRows) + ".\n" +
                "Rows uploading error - " + Integer.toString(redRows) + ".";
        ad.setMessage(summaryMsg);
        ad.setButton(getActivity().getString(R.string.ok_text), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ad.show();
    }

    public void UploadBreastExamData()
    {
        pgWaiting.setMessage("Uploading Breast Exam Form data...");
        pgWaiting.setCancelable(false);
        pgWaiting.show();
        try {
            List<String> selectedRowUID = new ArrayList<String>();
            int totalRows = tblBreastExamForm.getChildCount();
            for (int i = 4; i < totalRows; i++) {
                // Get table row.
                View rowView = tblBreastExamForm.getChildAt(i);
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
            APIInterface apiInterface = RetrofitClient.getRetrofit().create(APIInterface.class);
            for (String val: selectedRowUID)
            {
                String UIDAndPositionArr[] = val.split(",");

                View rowView = tblBreastExamForm.getChildAt(Integer.parseInt(UIDAndPositionArr[1]));
                TableRow tableRow = (TableRow) rowView;
                //get the first column of row i.e. the Select checkbox
                View columnViewSelect = tableRow.getChildAt(0);
                CheckBox chkSelect = (CheckBox) columnViewSelect;

                String insertValuesStatement = GenerateInsertValuesStatementForBreastExam(UIDAndPositionArr[0]);

                Call<String> call = apiInterface.AddBreastExam(insertValuesStatement);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        if(response.isSuccessful())
                        {
                            //updating the record's saved at server status from 2 to 1. i.e., saved success on server
                            db.UpdateSavedAtServerOfOralExamAgainstUID(UIDAndPositionArr[0].toString());

                            //unselect the checkbox of row
                            chkSelect.setChecked(false);

                            tableRow.setBackgroundColor(Color.GREEN);

                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        //unselect the checkbox of row
                        chkSelect.setChecked(false);

                        tableRow.setBackgroundColor(Color.RED);

                    }
                });
            }

            pgWaiting.dismiss();

            /*
            int greenRows = 0;
            int redRows = 0;

            for (int i = 4; i < tblBreastExamForm.getChildCount(); i++) {
                View rowView2 = tblBreastExamForm.getChildAt(i);
                if (rowView2 instanceof TableRow) {
                    TableRow tableRow2 = (TableRow) rowView2;
                    Drawable bg = tableRow2.getBackground();
                    int rowColor = ((ColorDrawable) bg.mutate()).getColor();
                    if (rowColor == Color.GREEN)
                        greenRows++;
                    else if (rowColor == Color.RED)
                        redRows++;
                }
            }


            AlertDialog ad = new AlertDialog.Builder(getActivity())
                    .create();
            ad.setCancelable(false);
            ad.setTitle("Upload data result");
            String summaryMsg = "Following is the result of Breast Exam form data uploading to server (" + Integer.toString(greenRows + redRows) + " rows) - " +
                    "\n\n" +
                    "Rows uploaded - " + Integer.toString(greenRows) + ".\n" +
                    "Rows uploading error - " + Integer.toString(redRows) + ".";
            ad.setMessage(summaryMsg);
            ad.setButton(getActivity().getString(R.string.ok_text), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            ad.show();
            */
        }
        catch (Exception e1)
        {
            pgWaiting.dismiss();
            Log.d("UploadBreastExam - ",e1.getMessage());
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
                sbStatement.append("" + curHH.getString(i) + ";");
            }
            //statement = statement + "'" + curHH.getString(26) + "'";

            //add last col data separately
            sbStatement.append("" + curHH.getString(11) + "");


            //return statement;
            return sbStatement.toString();
        }
        catch (Exception e1)
        {
            Log.d("GenerateInsertValuesSt-",e1.getMessage());
        }
        return "";
    }

    public String GenerateInsertValuesStatementForOralExam(String UID)
    {
        try {
            Cursor curHH = db.GetAllOralExamDetailsAgainstUID(UID);
            curHH.moveToFirst();
            String statement = "";
            StringBuilder sbStatement = new StringBuilder(statement);
            // Personal info table has 12 cols
            //in for loop adding only 11 cols data and later add last col data separately
            for(int i=0; i<11; i++)
            {
                //statement = statement + "'" + curHH.getString(i) + "'$";
                sbStatement.append("" + curHH.getString(i) + ";");
            }
            //statement = statement + "'" + curHH.getString(26) + "'";

            //add last col data separately
            sbStatement.append("" + curHH.getString(11) + "");


            //return statement;
            return sbStatement.toString();
        }
        catch (Exception e1)
        {
            Log.d("GenerateInsertOralExa-",e1.getMessage());
        }
        return "";
    }

    public String GenerateInsertValuesStatementForBreastExam(String UID)
    {
        try {
            Cursor curHH = db.GetAllBreastExamDetailsAgainstUID(UID);
            curHH.moveToFirst();
            String statement = "";
            StringBuilder sbStatement = new StringBuilder(statement);
            // Personal info table has 12 cols
            //in for loop adding only 11 cols data and later add last col data separately
            for(int i=0; i<11; i++)
            {
                //statement = statement + "'" + curHH.getString(i) + "'$";
                sbStatement.append("" + curHH.getString(i) + ";");
            }
            //statement = statement + "'" + curHH.getString(26) + "'";

            //add last col data separately
            sbStatement.append("" + curHH.getString(11) + "");


            //return statement;
            return sbStatement.toString();
        }
        catch (Exception e1)
        {
            Log.d("GenerateInsertBreastEx-",e1.getMessage());
        }
        return "";
    }

    public void setDate()
    {
        tvDate.setText(sDate);
    }

    public void getAllPersonalInfoFormAgainstDate()
    {
        DBHandler db = new DBHandler(getActivity().getApplicationContext());

        Cursor cursorPersonalInfoFormDataList = db.getAllGetAllNonUploadedPersonalInfoFormAgainstDate(tvDate.getText().toString());

        while(tblPersonalInfoForm.getChildCount() > 6)
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

            Cursor cursorOralExamFormDataList = db.getAllGetAllNonUploadedOralExamFormAgainstDate(tvDate.getText().toString());

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

            Cursor cursorBreastExamFormDataList = db.getAllGetAllNonUploadedBreastExamFormAgainstDate(tvDate.getText().toString());

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
}