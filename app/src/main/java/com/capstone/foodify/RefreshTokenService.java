package com.capstone.foodify;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.GetTokenResult;

public class RefreshTokenService extends JobService {

    private static final String TAG = "RefreshTokenService";

    @Override
    public boolean onStartJob(JobParameters params) {

        Log.d(TAG, "Refresh token service started!");

        refreshToken(params);

        return true;
    }

    private void refreshToken(JobParameters params) {

        if(Common.FIREBASE_USER != null){
            Common.FIREBASE_USER.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if(task.isSuccessful()){
                        Common.TOKEN = task.getResult().getToken();

                        Log.i(TAG, "Refresh token completed!");
                    } else {
                        Log.e(TAG, "Error when refresh token!");
                    }

                    jobFinished(params, false);
                }
            });
        }

    }

    @Override
    public boolean onStopJob(JobParameters params) {

        Log.d(TAG, "Stop serviced!");

        return true;
    }
}
