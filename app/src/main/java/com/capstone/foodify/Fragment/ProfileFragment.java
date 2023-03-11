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
import com.capstone.foodify.Activity.AddressManagerActivity;
import com.capstone.foodify.Activity.OrderActivity;
import com.capstone.foodify.Activity.SignInActivity;
import com.capstone.foodify.Activity.SignUpActivity;
import com.capstone.foodify.R;

public class ProfileFragment extends Fragment {

    Button btnSignUp, btnSignIn;

    LinearLayout account_and_profile, manage_address, order_history;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        btnSignUp = rootView.findViewById(R.id.sign_up_button);
        account_and_profile = rootView.findViewById(R.id.account_and_profile);
        manage_address = rootView.findViewById(R.id.manage_address);
        order_history = rootView.findViewById(R.id.order_history);
        btnSignIn = rootView.findViewById(R.id.sign_in_button);

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

        manage_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddressManagerActivity.class));
            }
        });

        order_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), OrderActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), SignInActivity.class));
            }
        });
        return rootView;
    }
}