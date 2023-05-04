package com.capstone.foodify.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.API.FoodApi;
import com.capstone.foodify.Activity.MainActivity;
import com.capstone.foodify.Adapter.MenuAdapter;
import com.capstone.foodify.Adapter.ShopAdapter;
import com.capstone.foodify.Common;
import com.capstone.foodify.Model.Food;
import com.capstone.foodify.Model.Menu;
import com.capstone.foodify.Model.Response.Foods;
import com.capstone.foodify.Model.Response.Shops;
import com.capstone.foodify.Model.Shop;
import com.capstone.foodify.Model.Slider;
import com.capstone.foodify.R;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    public static List<Food> listFood = new ArrayList<>();
    private static List<Food> recentFood = new ArrayList<>();

    private static List<Shop> shopList = new ArrayList<>();

    private ImageSlider imageSlider;

    private RecyclerView rcvMenu, recyclerView_restaurant;
    private MenuAdapter menuAdapter;
    private ShopAdapter shopAdapter;
    private static TextView welcomeText;


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
        welcomeText = view.findViewById(R.id.welcome_text);

        //Check status internet
        if(Common.getConnectionType(requireContext()) == 0){
            Common.showErrorInternetConnectionNotification(getActivity());
        }


        if(listFood.isEmpty() || recentFood.isEmpty()){
            //Get food
            getListFood();
            recentFood();
        } else {
            menuAdapter.setData(getListMenu());
            rcvMenu.setAdapter(menuAdapter);
        }

        setNameUser();


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rcvMenu.setLayoutManager(linearLayoutManager);

        //Get slider
        getSlider();

        //Get list shop
        getListShop();

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setNameUser();
    }

    public static void setNameUser(){
        if(Common.CURRENT_USER != null){
            //Set name user while user already login
            String[] arrayOfName = Common.CURRENT_USER.getFullName().split(" ");
            String name = arrayOfName[arrayOfName.length - 1];
            welcomeText.setText("Xin chào, " + name + "!");
        } else
            welcomeText.setText("Xin chào, khách!");
    }

    private void getSlider(){

        FoodApi.apiService.listSlider().enqueue(new Callback<List<Slider>>() {
            @Override
            public void onResponse(Call<List<Slider>> call, Response<List<Slider>> response) {
                List<Slider> sliders = response.body();

                List<SlideModel> slideModels = new ArrayList<>();
                for(Slider slider: sliders){
                    slideModels.add(new SlideModel(slider.getImageUrl(), null));
                }

                imageSlider.setImageList(slideModels, ScaleTypes.FIT);

                MainActivity.slider = true;
                MainActivity.hideProgressbar();
            }

            @Override
            public void onFailure(Call<List<Slider>> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private List<Menu> getListMenu() {
        List<Menu> listMenu = new ArrayList<>();

        listMenu.add(new Menu("Ăn gì hôm nay?", listFood));
        listMenu.add(new Menu("Món ăn được thêm gần đây", recentFood));

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

                menuAdapter.setData(getListMenu());

                MainActivity.recommendFood = true;
                MainActivity.hideProgressbar();
            }

            @Override
            public void onFailure(Call<Foods> call, Throwable t) {
                Log.e(TAG, t.toString());
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

                MainActivity.recentFood = true;
                MainActivity.hideProgressbar();
            }

            @Override
            public void onFailure(Call<Foods> call, Throwable t) {
                Log.e(TAG, t.toString());
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

                MainActivity.shop = true;
                MainActivity.hideProgressbar();
            }

            @Override
            public void onFailure(Call<Shops> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }
}