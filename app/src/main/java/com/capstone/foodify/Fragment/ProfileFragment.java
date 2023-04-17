package com.capstone.foodify.Fragment;

import static com.capstone.foodify.Common.firebaseAppDistribution;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.capstone.foodify.Activity.AccountAndProfileActivity;
import com.capstone.foodify.Activity.AddressManagerActivity;
import com.capstone.foodify.Activity.FavoriteFoodActivity;
import com.capstone.foodify.Activity.MainActivity;
import com.capstone.foodify.Activity.OrderActivity;
import com.capstone.foodify.Common;
import com.capstone.foodify.R;
import com.google.firebase.auth.FirebaseAuth;
import com.makeramen.roundedimageview.RoundedImageView;
import com.saadahmedsoft.popupdialog.PopupDialog;
import com.saadahmedsoft.popupdialog.Styles;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    LinearLayout account_and_profile, manage_address, favorite_food, order_history, feed_back, log_out;
    PopupDialog popupDialog;
    TextView user_name;
    RoundedImageView profile_avatar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        //Init component
        initComponent(rootView);

        popupDialog = PopupDialog.getInstance(getContext());

        //Check status internet
        if(Common.getConnectionType(requireContext()) == 0){
            Common.showErrorInternetConnectionNotification(getActivity());
        }

        //Set image for user
        if(Common.CURRENT_USER.getImageUrl().isEmpty()){
            profile_avatar.setImageResource(R.drawable.profile_avatar);
        } else {
            Picasso.get().load(Common.CURRENT_USER.getImageUrl()).into(profile_avatar);
        }

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

        favorite_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), FavoriteFoodActivity.class));
            }
        });

        order_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), OrderActivity.class));
            }
        });

        feed_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAppDistribution.startFeedback("Cảm ơn vì sự đóng góp ý kiến từ các bạn!");
            }
        });

        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupDialog.getInstance(getContext())
                        .setStyle(Styles.STANDARD)
                        .setHeading("Đăng xuất?")
                        .setDescription("Bạn thực sự muốn đăng xuất?")
                        .setPopupDialogIcon(R.drawable.baseline_logout)
                        .setPopupDialogIconTint(R.color.primaryColor)
                        .setPositiveButtonBackground(R.drawable.bg_color_primary_corner)
                        .setPositiveButtonText("Có")
                        .setNegativeButtonText("Không")
                        .setCancelable(false)
                        .showDialog(new OnDialogButtonClickListener() {
                            @Override
                            public void onPositiveClicked(Dialog dialog) {
                                FirebaseAuth.getInstance().signOut();

                                Common.TOKEN = null;
                                Common.CURRENT_USER = null;
                                dialog.dismiss();
                                getActivity().startActivity(new Intent(getContext(), MainActivity.class));
                                getActivity().finish();
                            }

                            @Override
                            public void onNegativeClicked(Dialog dialog) {
                                super.onNegativeClicked(dialog);
                            }
                        });
            }
        });
        return rootView;
    }

    private void initComponent(View rootView) {
        account_and_profile = rootView.findViewById(R.id.account_and_profile);
        manage_address = rootView.findViewById(R.id.manage_address);
        favorite_food = rootView.findViewById(R.id.favorite_food);
        order_history = rootView.findViewById(R.id.order_history);
        log_out = rootView.findViewById(R.id.log_out);
        user_name = rootView.findViewById(R.id.user_name);
        profile_avatar = rootView.findViewById(R.id.profile_avatar);
        feed_back = rootView.findViewById(R.id.feedback);
    }

    @Override
    public void onResume() {
        super.onResume();

        //Update user name on textview
        user_name.setText(Common.CURRENT_USER.getFullName());

        //Set image for user
        if(Common.CURRENT_USER.getImageUrl().isEmpty()){
            profile_avatar.setImageResource(R.drawable.profile_avatar);
        } else {
            Picasso.get().load(Common.CURRENT_USER.getImageUrl()).into(profile_avatar);
        }
    }
}