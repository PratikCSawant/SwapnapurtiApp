package com.example.swapnapurtiapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import android.widget.Button;

public class HomepageFragment extends Fragment {


    Button btnPersonalInfoForm, btnOralExamForm, btnViewAndSendData, btnReceiveData, btnBreastExamForm,
    btnUploadToServer;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        View rootView =  inflater.inflate(R.layout.fragment_homepage, container, false);

        //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Swapnapurti - Homepage");

        btnPersonalInfoForm = (Button) rootView.findViewById(R.id.btnHomePersonalInfoForm);

        btnPersonalInfoForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(HomepageFragment.this)
                        .navigate(R.id.action_homepageFragment_to_personalInfoFormFragment);
            }
        });

        btnOralExamForm = (Button) rootView.findViewById(R.id.btnHomeOralExamForms);
        btnOralExamForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(HomepageFragment.this)
                        .navigate(R.id.action_homepageFragment_to_oralExamFormFragment);
            }
        });

        btnViewAndSendData = (Button) rootView.findViewById(R.id.btnViewAndSendData);
        btnViewAndSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NavHostFragment.findNavController(HomepageFragment.this)
                        .navigate(R.id.action_homepageFragment_to_viewAndSendDataFragment);
            }
        });

        btnReceiveData = (Button) rootView.findViewById(R.id.btnReceiveData);
        btnReceiveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(HomepageFragment.this)
                        .navigate(R.id.action_homepageFragment_to_receiveDataFragment);
            }
        });

        btnBreastExamForm = (Button) rootView.findViewById(R.id.btnHomeBreastExamForms);
        btnBreastExamForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(HomepageFragment.this)
                        .navigate(R.id.action_homepageFragment_to_breastExamFragment);
            }
        });

        btnUploadToServer = (Button) rootView.findViewById(R.id.btnHomeUploadDataToServer);
        btnUploadToServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(HomepageFragment.this)
                        .navigate(R.id.action_homepageFragment_to_uploadToServerFragment);
            }
        });

        return rootView;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}