package com.capstone.foodify.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.capstone.foodify.API.FoodApiToken;
import com.capstone.foodify.Activity.MainActivity;
import com.capstone.foodify.Common;
import com.capstone.foodify.Model.Response.CustomResponse;
import com.capstone.foodify.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationService extends FirebaseMessagingService {

    private static final String TAG = "NotificationService";
    public static final String channelId = "foodify-notification";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

        sendNotification(remoteMessage.getNotification().getTitle() , remoteMessage.getNotification().getBody());
    }

    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_IMMUTABLE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        createNotificationChannel(notificationManager);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void createNotificationChannel(NotificationManager notificationManager) {
        Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + this.getPackageName() + "/" + R.raw.notificationsound); //Here is FILE_NAME is the name of file that you want to play
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        CharSequence name = "Notification";
        String description = "testing";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build();
        NotificationChannel channel = new NotificationChannel(channelId, name, importance);
        channel.setDescription(description);
        channel.enableLights(true); channel.enableVibration(true);
        channel.setSound(sound, audioAttributes);
        notificationManager.createNotificationChannel(channel);
    }


    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        if(Common.CURRENT_USER != null){
            FoodApiToken.apiService.updateFCMToken(Common.CURRENT_USER.getId(), token).enqueue(new Callback<CustomResponse>() {
                @Override
                public void onResponse(Call<CustomResponse> call, Response<CustomResponse> response) {
                    if(response.code() != 200)
                        Toast.makeText(NotificationService.this, "Không thể cập nhật FCM Token. Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<CustomResponse> call, Throwable t) {
                    Toast.makeText(NotificationService.this, "Đã có lỗi khi kết nối đến hệ thống!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        Log.d(TAG, "Renew token: " + token);
    }
}
