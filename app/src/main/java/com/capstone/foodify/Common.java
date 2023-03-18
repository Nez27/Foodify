package com.capstone.foodify;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.capstone.foodify.Activity.MainActivity;
import com.capstone.foodify.Model.Basket;
import com.capstone.foodify.Model.Food;
import com.capstone.foodify.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.saadahmedsoft.popupdialog.PopupDialog;
import com.saadahmedsoft.popupdialog.Styles;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.thecode.aestheticdialogs.OnDialogClickListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class Common {
    public static final String FORMAT_DATE="dd-MM-yyyy";
    public static final String VALID_EMAIL_ADDRESS_REGEX = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
    public static final String PHONE_CODE = "+84";
    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!_])(?=\\S+$).{4,}$";
    public static final String PHONE_PATTERN = "^0[98753]{1}\\d{8}$";
    public static List<Basket> LIST_BASKET_FOOD = new ArrayList<>();
    public static String TOKEN = null;
    public static User CURRENT_USER = null;

    public static String changeCurrencyUnit(float price){
        Locale locale = new Locale("vi", "VN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        return fmt.format(price);
    }

    public static Typeface setFontBebas(AssetManager assetManager){
        return Typeface.createFromAsset(assetManager, "font/bebas.ttf");
    }

    public static Typeface setFontOpenSans(AssetManager assetManager){
        return Typeface.createFromAsset(assetManager, "font/opensans.ttf");
    }

    @IntRange(from = 0, to = 3)
    public static int getConnectionType(Context context) {
        int result = 0; // Returns connection type. 0: none; 1: mobile data; 2: wifi
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (cm != null) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        result = 2;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        result = 1;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                        result = 3;
                    }
                }
            }
        } else {
            if (cm != null) {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null) {
                    // connected to the internet
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        result = 2;
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        result = 1;
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_VPN) {
                        result = 3;
                    }
                }
            }
        }
        return result;
    }

    public static void showErrorInternetConnectionNotification(Activity activity){
        new AestheticDialog.Builder(activity, DialogStyle.CONNECTIFY, DialogType.ERROR)
                .setTitle("Lỗi kết nối mạng!")
                .setMessage("Vui lòng kiểm tra kết nối và thử lại bạn nha!")
                .setCancelable(false)
                .setOnClickListener(new OnDialogClickListener() {
                    @Override
                    public void onClick(@NonNull AestheticDialog.Builder builder) {
                        activity.finishAffinity();
                        System.exit(0);
                    }
                })
                .show();
    }

    public static void showNotificationError(Context context, Activity activity) {
        if(getConnectionType(context) != 0){
            //Has internet connection
            showErrorServerNotification(activity);
        } else {
            //No internet, show notification
            showErrorInternetConnectionNotification(activity);
        }
    }

    public static void showErrorServerNotification(Activity activity){
        PopupDialog.getInstance(activity)
                .setStyle(Styles.FAILED)
                .setHeading("Lỗi")
                .setDescription("Đã xuất hiện lỗi từ hệ thống. Vui lòng thử lại sau!")
                .setCancelable(false)
                .setDismissButtonText("Thoát")
                .showDialog(new OnDialogButtonClickListener() {
                    @Override
                    public void onDismissClicked(Dialog dialog) {
                        activity.finishAffinity();
                        System.exit(0);
                    }
                });
    }

    public static Basket getFoodExistInBasket(String foodId) {
        return Common.LIST_BASKET_FOOD.stream().filter(food -> foodId.equals(food.getId())).findFirst().orElse(null);
    }
}
