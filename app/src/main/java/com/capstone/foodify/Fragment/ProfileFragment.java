package com.capstone.foodify.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.capstone.foodify.R;
import com.capstone.foodify.Activity.SignUpActivity;

public class ProfileFragment extends Fragment {

    Button btnSignUp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        btnSignUp = (Button) rootView.findViewById(R.id.sign_up_button);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(getActivity(), SignUpActivity.class);
                startActivity(i);
            }
        });
        return rootView;
    }
}