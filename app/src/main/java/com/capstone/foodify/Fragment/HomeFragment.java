package com.capstone.foodify.Fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.API.FoodApi;
import com.capstone.foodify.Common;
import com.capstone.foodify.Model.Food.Food;
import com.capstone.foodify.Model.Menu.Menu;
import com.capstone.foodify.Model.Menu.MenuAdapter;
import com.capstone.foodify.Model.Response.Foods;
import com.capstone.foodify.Model.Response.Shops;
import com.capstone.foodify.Model.Shop.Shop;
import com.capstone.foodify.Model.Shop.ShopAdapter;
import com.capstone.foodify.R;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.thecode.aestheticdialogs.OnDialogClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    public static List<Food> listFood = new ArrayList<>();
    private static List<Food> recentFood = new ArrayList<>();

    private static List<Shop> shopList = new ArrayList<>();

    private ImageSlider imageSlider;

    private RecyclerView rcvMenu, recyclerView_restaurant;
    private MenuAdapter menuAdapter;
    private ShopAdapter shopAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Init Component
        imageSlider = (ImageSlider) view.findViewById(R.id.slider);
        rcvMenu = view.findViewById(R.id.recycler_view_menu);
        menuAdapter = new MenuAdapter(getContext());
        recyclerView_restaurant = view.findViewById(R.id.recycler_view_restaurant);
        shopAdapter = new ShopAdapter(getContext());


        if(listFood.isEmpty() || recentFood.isEmpty()){
            getListFood();
        } else {
            menuAdapter.setData(getListMenu());
            rcvMenu.setAdapter(menuAdapter);
        }


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rcvMenu.setLayoutManager(linearLayoutManager);

        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel("https://res.cloudinary.com/dnkmxpujh/image/upload/v1676023773/banner1_dandvd.png", null));
        slideModels.add(new SlideModel("https://res.cloudinary.com/dnkmxpujh/image/upload/v1676023777/banner2_okkqac.png", null));
        slideModels.add(new SlideModel("https://res.cloudinary.com/dnkmxpujh/image/upload/v1676023775/banner3_rklfi6.png", null));
        slideModels.add(new SlideModel("https://res.cloudinary.com/dnkmxpujh/image/upload/v1676023852/banner4_ufsaps.png", null));
        slideModels.add(new SlideModel("https://res.cloudinary.com/dnkmxpujh/image/upload/v1676023778/banner5_b4d1b8.png", null));

        imageSlider.setImageList(slideModels, ScaleTypes.FIT);

        //Get list shop
        getListShop();

        // Inflate the layout for this fragment
        return view;
    }

    private List<Menu> getListMenu() {
        List<Menu> listMenu = new ArrayList<>();

        listMenu.add(new Menu("Recommend Food", listFood));
        listMenu.add(new Menu("Popular Food", recentFood));

        return listMenu;
    }

    private void getListFood() {
        FoodApi.apiService.recommendFood().enqueue(new Callback<Foods>() {
            @Override
            public void onResponse(Call<Foods> call, Response<Foods> response) {
                Foods foodResponse = response.body();

                //Check null data
                if(foodResponse == null)
                    return;

                listFood = foodResponse.getProducts();


                recentFood();
            }

            @Override
            public void onFailure(Call<Foods> call, Throwable t) {
                //Check internet connection
                if(Common.checkInternetConnection(getContext())){
                    //Has internet connection
                    Toast.makeText(getContext(), "Error: " + t, Toast.LENGTH_SHORT).show();
                } else {
                    //No internet, show notification
                    Common.showErrorInternetConnectionNotification(getActivity());
                }
            }
        });
    }

    private void recentFood() {
        FoodApi.apiService.recentFood().enqueue(new Callback<Foods>() {
            @Override
            public void onResponse(Call<Foods> call, Response<Foods> response) {
                Foods foodResponse = response.body();

                //Check null data
                if(foodResponse == null)
                    return;

                recentFood = foodResponse.getProducts();

                menuAdapter.setData(getListMenu());
                rcvMenu.setAdapter(menuAdapter);
            }

            @Override
            public void onFailure(Call<Foods> call, Throwable t) {
                //Check internet connection
                if(Common.checkInternetConnection(getContext())){
                    //Has internet connection
                    Toast.makeText(getContext(), "Error: " + t, Toast.LENGTH_SHORT).show();
                } else {
                    //No internet, show notification
                    Common.showErrorInternetConnectionNotification(getActivity());
                }
            }
        });
    }

    private void getListShop(){
        FoodApi.apiService.allShops().enqueue(new Callback<Shops>() {
            @Override
            public void onResponse(Call<Shops> call, Response<Shops> response) {
                Shops shopData = response.body();

                if(shopData == null)
                    return;

                shopList = shopData.getShops();

                shopAdapter.setData(shopList);
                recyclerView_restaurant.setAdapter(shopAdapter);
            }

            @Override
            public void onFailure(Call<Shops> call, Throwable t) {
                //Check internet connection
                if(Common.checkInternetConnection(getContext())){
                    //Has internet connection
                    Toast.makeText(getContext(), "Error: " + t, Toast.LENGTH_SHORT).show();
                } else {
                    //No internet, show notification
                    Common.showErrorInternetConnectionNotification(getActivity());
                }
            }
        });
    }
}