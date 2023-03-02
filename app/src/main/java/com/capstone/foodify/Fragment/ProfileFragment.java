package com.capstone.foodify.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.capstone.foodify.Activity.AccountAndProfileActivity;
import com.capstone.foodify.Activity.SignUpActivity;
import com.capstone.foodify.R;

public class ProfileFragment extends Fragment {

    Button btnSignUp;

    LinearLayout account_and_profile;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        btnSignUp = (Button) rootView.findViewById(R.id.sign_up_button);
        account_and_profile = (LinearLayout) rootView.findViewById(R.id.account_and_profile);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SignUpActivity.class));
            }
        });

        account_and_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AccountAndProfileActivity.class));
            }
        });
        return rootView;
    }
}