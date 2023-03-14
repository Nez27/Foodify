package com.capstone.foodify;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.capstone.foodify.Model.Basket.Basket;
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

public class Common {
    public static List<Basket> LIST_BASKET_FOOD = new ArrayList<>();

    public static String TOKEN = null;

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

    public static boolean checkInternetConnection(Context context){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network activeNetwork = connectivityManager.getActiveNetwork();
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);

        if (capabilities != null) {
            if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_SUPL)) return true;
            else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) return true;
            else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) return true;
            else return false;
        }
        return false;
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
        if(checkInternetConnection(context)){
            //Has internet connection
            showErrorServerNotification(activity);
        } else {
            //No internet, show notification
            showErrorInternetConnectionNotification(activity);
        }
    }

    private static void showErrorServerNotification(Activity activity){
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
