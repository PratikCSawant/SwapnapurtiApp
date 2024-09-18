package com.example.swapnapurtiapp;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;



public class LoginFragment extends Fragment {

    Button btnLogin;
    EditText etUserId, etUserPassword;

    DBHandler db;
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        View rootView =  inflater.inflate(R.layout.fragment_login, container, false);

        db = new DBHandler(getActivity().getApplicationContext());
        db.CreateTablesIfNotExist();
        //db.createBreastTable();

        //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Swapnapurti - Login");

        btnLogin = (Button) rootView.findViewById(R.id.btnLoginLogin);
        etUserId = (EditText) rootView.findViewById(R.id.editTextLoginUserId);
        etUserPassword = (EditText) rootView.findViewById(R.id.editTextLoginUserPassword);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etUserId.getText().toString().equals("")) {
                    Toast.makeText(rootView.getContext(), "User-ID is blank!", Toast.LENGTH_SHORT).show();
                    etUserId.requestFocus();
                    return;
                }

                if (etUserPassword.getText().toString().equals("")) {
                    Toast.makeText(rootView.getContext(), "Password is blank!", Toast.LENGTH_SHORT).show();
                    etUserPassword.requestFocus();
                    return;
                }

                Cursor res = db.CheckUserIDAndPassword(etUserId.getText().toString(), etUserPassword.getText().toString());
                if(res.getCount() > 0)
                {
                    CommonVariables.UserId = etUserId.getText().toString();
                    String data[] = (db.GetUserNameAndEmpCode(etUserId.getText().toString())).split(",");
                    CommonVariables.UserName = data[0];
                    CommonVariables.EmpCode = data[1];
                    NavHostFragment.findNavController(LoginFragment.this)
                            .navigate(R.id.action_FirstFragment_to_SecondFragment);
                }
                else
                {
                    Toast.makeText(rootView.getContext(), "Invalid ID or Password! Cannot login.", Toast.LENGTH_SHORT).show();
                    etUserPassword.requestFocus();
                    return;
                }
            }
        });

        return rootView;

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* btnLogin.setOnClickListener(v ->
                NavHostFragment.findNavController(LoginFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment)
        );*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}