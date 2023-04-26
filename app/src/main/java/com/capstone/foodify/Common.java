package com.capstone.foodify;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.capstone.foodify.API.FoodApiToken;
import com.capstone.foodify.Model.Basket;
import com.capstone.foodify.Model.Food;
import com.capstone.foodify.Model.User;
import com.google.firebase.appdistribution.FirebaseAppDistribution;
import com.google.firebase.auth.FirebaseUser;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.thecode.aestheticdialogs.OnDialogClickListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Common {
    //https://foodify-backend-production.up.railway.app/
    public static final String BASE_URL = "https://foodify-backend-production.up.railway.app/api/";
    public static String FCM_TOKEN = null;
    public static Location CURRENT_LOCATION = null;
    public static FirebaseUser FIREBASE_USER = null;
    public static final String DEFAULT_IMAGE_URL =  "https://firebasestorage.googleapis.com/v0/b/foodify-55954.appspot.com/o/Admin%2Fuser-default-01.png?alt=media&token=b13f27da-b39a-4d89-9cba-acba869d60ce";
    public static final String MAP_API = "AIzaSyAY14Ic32UP26Hg6GILznOfbBihiY5BUxw";
    public static final String FORMAT_DATE="dd-MM-yyyy";
    public static final String VALID_EMAIL_ADDRESS_REGEX = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
    public static final String PHONE_CODE = "+84";
    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!_])(?=\\S+$).{4,}$";
    public static final String PHONE_PATTERN = "^0[98753]{1}\\d{8}$";
    public static List<Basket> LIST_BASKET_FOOD = new ArrayList<>();
    public static String tempCurrentAddressUser = null;
    public static String TOKEN = null;
    public static User CURRENT_USER = null;
    public static String FINAL_SHOP = null;
    public static final FirebaseAppDistribution firebaseAppDistribution = FirebaseAppDistribution.getInstance();

    public static String changeCurrencyUnit(float price){
        Locale locale = new Locale("vi", "VN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        return fmt.format(price);
    }

    public static Typeface setFontKohoBold(AssetManager assetManager){
        return Typeface.createFromAsset(assetManager, "font/koho_bold.ttf");
    }

    public static Typeface setFontKoho(AssetManager assetManager){
        return Typeface.createFromAsset(assetManager, "font/koho.ttf");
    }

    public static void reloadUser(Activity activity){
        FoodApiToken.apiService.getUserFromEmail(Common.CURRENT_USER.getEmail()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.code() == 200){
                    User userData = response.body();
                    if(userData != null){
                        Common.CURRENT_USER =  userData;
                    }
                } else if(response.code() == 401){
                    //Unauthorized


                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                System.out.println("ERROR: " + t);
                Common.showErrorServerNotification(activity, "Lỗi kết nối hệ thống!");
            }
        });
    }

    @IntRange(from = 0, to = 3)
    public static int getConnectionType(Context context) {
        int result = 0; // Returns connection type. 0: none; 1: mobile data; 2: wifi
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
        return result;
    }

    public static void notificationDialog(Activity activity, DialogStyle dialogStyle, DialogType dialogType, String title, String message){
        new AestheticDialog.Builder(activity, dialogStyle, dialogType)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .show();
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
            showErrorServerNotification(activity, "Đã có lỗi từ hệ thống! Xin vui lòng thử lại sau!");
        } else {
            //No internet, show notification
            showErrorInternetConnectionNotification(activity);
        }
    }

    public static void showErrorServerNotification(Activity activity, String message){
        new AestheticDialog.Builder(activity, DialogStyle.RAINBOW, DialogType.ERROR)
                .setTitle("LỖI!")
                .setMessage(message)
                .setCancelable(false)
                .setOnClickListener(new OnDialogClickListener() {
                    @Override
                    public void onClick(@NonNull AestheticDialog.Builder builder) {

                        activity.finishAffinity();
                        System.exit(0);
                    }
                }).show();
    }

    public static Basket getFoodExistInBasket(String foodId) {
        return Common.LIST_BASKET_FOOD.stream().filter(food -> foodId.equals(food.getId())).findFirst().orElse(null);
    }

    public static void addFoodToBasket(Food food) {
        Basket foodInBasket = Common.getFoodExistInBasket(food.getId());
        if (foodInBasket == null) {
            //If item is not exist in basket

            //Check food image is null or not
            String imageTemp = null;
            if (food.getImages().size() > 0)
                imageTemp = food.getImages().get(0).getImageUrl();

            Common.LIST_BASKET_FOOD.add(new Basket(food.getId(), imageTemp, food.getName(), food.getCost(), food.getShop().getName(),
                    "1", food.getDiscountPercent(), food.getShop().getId()));
        } else {
            //If item is exist in basket

            for (int i = 0; i < Common.LIST_BASKET_FOOD.size(); i++) {
                String foodId = Common.LIST_BASKET_FOOD.get(i).getId();
                //Find foodId exist
                if (foodId.equals(food.getId())) {
                    //Get quantity food from basket
                    String quantity = Common.LIST_BASKET_FOOD.get(i).getQuantity();

                    //Change quantity food from basket
                    int quantityInt = Integer.parseInt(quantity) + 1;
                    Common.LIST_BASKET_FOOD.get(i).setQuantity(String.valueOf(quantityInt));
                    break;
                }
            }

        }
    }
}
