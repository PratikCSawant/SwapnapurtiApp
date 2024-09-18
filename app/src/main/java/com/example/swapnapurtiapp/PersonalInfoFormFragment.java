package com.example.swapnapurtiapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalInfoFormFragment extends Fragment {

    Spinner spSex, spVillage, spOralSymptoms, spBreastSymptoms, spCervixSymptoms;
    EditText etUniqueID, etName, etAge, etContactNo;
    Button btnSave;
    CheckBox chkSaveAtServer;
    DBHandler db;
    ProgressDialog pgWaiting;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_personal_info_form, container, false);

        //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Swapnapurti - Personal Info Form");

        db = new DBHandler(getActivity().getApplicationContext());

        btnSave = (Button) rootView.findViewById(R.id.buttonPersonalInfoFragmentSave);

        spVillage = (Spinner) rootView.findViewById(R.id.spinnerPersonalInfoFragmentVillage);
        List<String> villageList = db.getAllVillages();
        ArrayAdapter<String> dataAdapterVillages = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, villageList);
        dataAdapterVillages.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spVillage.setAdapter(dataAdapterVillages);

        spSex = (Spinner) rootView.findViewById(R.id.spinnerPersonalInfoFragmentSex);
        List<String> sexList = db.getAllSex();
        ArrayAdapter<String> dataAdapterSex = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, sexList);
        dataAdapterSex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSex.setAdapter(dataAdapterSex);

        spOralSymptoms = (Spinner) rootView.findViewById(R.id.spinnerPersonalInfoFragmentOralSymptoms);
        List<String> yesnoList = db.getAllYesNo();
        ArrayAdapter<String> dataAdapterYesNo = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, yesnoList);
        dataAdapterYesNo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spOralSymptoms.setAdapter(dataAdapterYesNo);

        spBreastSymptoms = (Spinner) rootView.findViewById(R.id.spinnerPersonalInfoFragmentBreastSymptoms);
        spBreastSymptoms.setAdapter(dataAdapterYesNo);

        spCervixSymptoms = (Spinner) rootView.findViewById(R.id.spinnerPersonalInfoFragmentCervixSymptoms);
        spCervixSymptoms.setAdapter(dataAdapterYesNo);

        etUniqueID = (EditText) rootView.findViewById(R.id.editTextPersonalInfoFragmentUniqueID);
        etUniqueID.setText(db.GetNewUID());

        etAge = (EditText) rootView.findViewById(R.id.editTextPersonalInfoFragmentAge);
        etName = (EditText) rootView.findViewById(R.id.editTextPersonalInfoFragmentName);
        etContactNo = (EditText) rootView.findViewById(R.id.editTextPersonalInfoFragmentContactNo);

        chkSaveAtServer = (CheckBox) rootView.findViewById(R.id.checkboxPersonalInfoSaveAtServer);
        pgWaiting = new ProgressDialog(getContext());
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    pgWaiting.setMessage("Saving Personal Info Form data...");
                    pgWaiting.setCancelable(false);
                    pgWaiting.show();

                    String result, title, message;
                    result = saveData();
                    if (result.equals("1")) {
                        //data saved
                        title = "Success";
                        message = "Data saved successfully on the device";
                        btnSave.setEnabled(false);
                        if (chkSaveAtServer.isChecked())
                        {
                            APIInterface apiInterface = RetrofitClient.getRetrofit().create(APIInterface.class);
                            String personalInfoForm =
                                etUniqueID.getText().toString() + ";" +
                                spVillage.getSelectedItem().toString() + ";" +
                                etName.getText().toString() + ";" +
                                spSex.getSelectedItem().toString() + ";" +
                                etAge.getText().toString() + ";" +
                                etContactNo.getText().toString() + ";" +
                                spOralSymptoms.getSelectedItem().toString() + ";" +
                                spBreastSymptoms.getSelectedItem().toString()+ ";" +
                                spCervixSymptoms.getSelectedItem().toString()+ ";" +
                                CommonVariables.UserId + ";" +
                                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                            Call<String> call = apiInterface.AddPersonalInfo(personalInfoForm);

                            call.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    pgWaiting.dismiss();
                                    if(response.isSuccessful())
                                    {
                                        //Toast.makeText(getContext(), response.body(), Toast.LENGTH_LONG).show();

                                        db.UpdateSavedAtServerOfPersonalInfoFormAgainstUID(etUniqueID.getText().toString());

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
                    } else {
                        //error while saving
                        title = "Error";
                        message = "Error occurred while saving the data on the device. More details: \n" + result;
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
                catch (Exception e1)
                {
                    pgWaiting.dismiss();
                    Log.d("btnSave.setOnClick-",e1.getMessage());
                }
            }
        });

        chkSaveAtServer = (CheckBox) rootView.findViewById(R.id.checkboxPersonalInfoSaveAtServer);

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
        try {
            String data[] = new String[12];
            data[0] = etUniqueID.getText().toString();
            data[1] = spVillage.getSelectedItem().toString();
            data[2] = etName.getText().toString();
            data[3] = spSex.getSelectedItem().toString();
            data[4] = etAge.getText().toString();
            data[5] = etContactNo.getText().toString();
            data[6] = spOralSymptoms.getSelectedItem().toString();
            data[7] = spBreastSymptoms.getSelectedItem().toString();
            data[8] = spCervixSymptoms.getSelectedItem().toString();
            data[9] = CommonVariables.UserId;
            data[10] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            data[11] = "2";

            String result = db.InsertPersonalInfoForm(data);
            if(result.equals("1"))
            {
                //data save
                return "1";
            }
            else
            {
                //error while saving
                return result;
            }
        }
        catch (Exception e1)
        {
            Log.d("saveData-",e1.getMessage());
            return "";
        }
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public int add(int a, int b)
    {
        return a+b;
    }
}