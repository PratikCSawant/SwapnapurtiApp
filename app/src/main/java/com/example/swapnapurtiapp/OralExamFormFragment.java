package com.example.swapnapurtiapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OralExamFormFragment extends Fragment {

    EditText etUniqueId, etName, etAge, etContactNo, etLesionType;
    Spinner spSex, spVillage, spOralHygiene, spOralScreenPositive;
    Button btnSave, btnSearch;
    DBHandler db;
    ProgressDialog pgWaiting;
    CheckBox chkSaveAtServer;

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_oral_exam_form, container, false);

        //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Swapnapurti - Oral Exam Form");

        etUniqueId = (EditText) rootView.findViewById(R.id.editTextOralExamFormFragmentUniqueID);
        etName = (EditText) rootView.findViewById(R.id.editTextOralExamFormFragmentName);
        etAge = (EditText) rootView.findViewById(R.id.editTextOralExamFormFragmentAge);
        etContactNo = (EditText) rootView.findViewById(R.id.editTextOralExamFormFragmentContactNo);
        etLesionType = (EditText) rootView.findViewById(R.id.editTextOralExamFormFragmentLesionType);

        spSex = (Spinner) rootView.findViewById(R.id.spinnerOralExamFormFragmentSex);
        spVillage = (Spinner) rootView.findViewById(R.id.spinnerOralExamFormFragmentVillage);
        spOralHygiene = (Spinner) rootView.findViewById(R.id.spinnerOralExamFormFragmentOralHygiene);
        spOralScreenPositive = (Spinner) rootView.findViewById(R.id.spinnerOralExamFormFragmentScreenPositive);

        btnSave = (Button) rootView.findViewById(R.id.buttonOralExamFormFragmentSave);
        btnSearch = (Button) rootView.findViewById(R.id.buttonOralExamFormFragmentSearch);

        db = new DBHandler(getActivity().getApplicationContext());

        //for testing
        Toast.makeText(rootView.getContext(), db.GetTop1PersonalInfoDataAgainstUID(), Toast.LENGTH_LONG).show();

        List<String> villageList = db.getAllVillages();
        ArrayAdapter<String> dataAdapterVillages = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, villageList);
        dataAdapterVillages.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spVillage.setAdapter(dataAdapterVillages);

        List<String> sexList = db.getAllSex();
        ArrayAdapter<String> dataAdapterSex = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, sexList);
        dataAdapterSex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSex.setAdapter(dataAdapterSex);

        List<String> goodBadList = new ArrayList<String>();
        goodBadList.add("SELECT");
        goodBadList.add("1-Good");
        goodBadList.add("2-Bad");
        ArrayAdapter<String> dataAdapterGoodBad = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, goodBadList);
        dataAdapterGoodBad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spOralHygiene.setAdapter(dataAdapterGoodBad);

        List<String> yesnoList = db.getAllYesNo();
        ArrayAdapter<String> dataAdapterYesNo = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, yesnoList);
        dataAdapterYesNo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spOralScreenPositive.setAdapter(dataAdapterYesNo);

        pgWaiting = new ProgressDialog(getContext());
        chkSaveAtServer = (CheckBox) rootView.findViewById(R.id.checkboxOralExamSaveAtServer);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etUniqueId.getText().toString().equals(""))
                {
                    Toast.makeText(rootView.getContext(), "Unique-ID is blank!", Toast.LENGTH_SHORT).show();
                    etUniqueId.requestFocus();
                    return;
                }
                else {
                    String result = db.GetBasicDetailsFromPersonalInfoForm(etUniqueId.getText().toString());
                    if(result.equals(""))
                    {
                        AlertDialog ad = new AlertDialog.Builder(getActivity())
                                .create();
                        ad.setCancelable(false);
                        ad.setTitle("Search result");
                        ad.setMessage("No data found for unique ID - " + etUniqueId.getText().toString());
                        ad.setButton(getActivity().getString(R.string.ok_text), new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        ad.show();
                    }
                    else {
                        Toast.makeText(rootView.getContext(), result, Toast.LENGTH_SHORT).show();
                        String data[] = result.split(";");

                        //int spVillagePosition = dataAdapterVillages.getPosition(data[0].split("-")[1]);
                        //spVillage.setSelection(spVillagePosition);

                        spVillage.setSelection(((ArrayAdapter<String>)spVillage.getAdapter()).getPosition(data[0]));

                        etName.setText(data[1]);

                        //int spVillageSex = dataAdapterSex.getPosition(data[2].split("-")[1]);
                        //spVillage.setSelection(spVillageSex);

                        spSex.setSelection(((ArrayAdapter<String>)spSex.getAdapter()).getPosition(data[2]));

                        etContactNo.setText(data[3]);

                        etAge.setText(data[4]);
                    }

                }

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pgWaiting.setMessage("Saving Oral Exam Form data...");
                pgWaiting.setCancelable(false);
                pgWaiting.show();

                String result, title, message;
                result = saveData();
                if(result.equals("1"))
                {
                    //data saved
                    title = "Success";
                    message = "Data saved successfully on this device";
                    btnSave.setEnabled(false);
                    if (chkSaveAtServer.isChecked())
                    {
                        APIInterface apiInterface = RetrofitClient.getRetrofit().create(APIInterface.class);
                        String oralExamForm =
                                etUniqueId.getText().toString() + ";" +
                                        spVillage.getSelectedItem().toString() + ";" +
                                        etName.getText().toString() + ";" +
                                        spSex.getSelectedItem().toString() + ";" +
                                        etAge.getText().toString() + ";" +
                                        etContactNo.getText().toString() + ";" +
                                        spOralHygiene.getSelectedItem().toString() + ";" +
                                        spOralScreenPositive.getSelectedItem().toString()+ ";" +
                                        etLesionType.getText().toString()+ ";" +
                                        CommonVariables.UserId + ";" +
                                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                        Call<String> call = apiInterface.AddOralExam(oralExamForm);

                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                pgWaiting.dismiss();
                                if(response.isSuccessful())
                                {
                                    //Toast.makeText(getContext(), response.body(), Toast.LENGTH_LONG).show();

                                    db.UpdateSavedAtServerOfOralExamAgainstUID(etUniqueId.getText().toString());

                                    AlertDialog ad = new AlertDialog.Builder(getActivity())
                                            .create();
                                    ad.setCancelable(false);
                                    ad.setTitle("Success");
                                    ad.setMessage("Data saved successfully on the server");
                                    ad.setButton(getActivity().getString(R.string.ok_text), new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    ad.show();

                                    //updating the record's saved at server status from 2 to 1. i.e., saved success on server

                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                pgWaiting.dismiss();
                                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                                AlertDialog ad = new AlertDialog.Builder(getActivity())
                                        .create();
                                ad.setCancelable(false);
                                ad.setTitle("Failed");
                                ad.setMessage("Data did not saved on the server");
                                ad.setButton(getActivity().getString(R.string.ok_text), new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                ad.show();
                            }
                        });
                    }
                    pgWaiting.dismiss();
                }
                else
                {
                    //error while saving
                    title = "Error";
                    message = "Error occurred while saving the data on this device. More details: \n" + result;
                }

                pgWaiting.dismiss();

                AlertDialog ad = new AlertDialog.Builder(getActivity())
                        .create();
                ad.setCancelable(false);
                ad.setTitle(title);
                ad.setMessage(message);
                ad.setButton(getActivity().getString(R.string.ok_text), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                ad.show();
            }
        });

        AlertDialog ad = new AlertDialog.Builder(getActivity())
                .create();
        ad.setCancelable(false);
        ad.setTitle("Message");
        ad.setMessage("If you want to save the data on online server then tick the checkbox SAVE AT SERVER");
        ad.setButton(getActivity().getString(R.string.ok_text), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ad.show();

        return rootView;
    }

    public String saveData()
    {
        String data[] = new String[12];
        data[0] = etUniqueId.getText().toString();
        data[1] = spVillage.getSelectedItem().toString();
        data[2] = etName.getText().toString();
        data[3] = spSex.getSelectedItem().toString();
        data[4] = etAge.getText().toString();
        data[5] = etContactNo.getText().toString();
        data[6] = spOralHygiene.getSelectedItem().toString();
        data[7] = spOralScreenPositive.getSelectedItem().toString();
        data[8] = etLesionType.getText().toString();
        data[9] = CommonVariables.UserId;
        data[10] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        data[11] = "2";

        String result = db.InsertOralExamForm(data);
        if(result.equals("1"))
        {
            //data saved
            return "1";
        }
        else
        {
            //error while saving
            return result;
        }
    }
}