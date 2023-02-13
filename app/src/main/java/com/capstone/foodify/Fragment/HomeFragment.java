package com.capstone.foodify.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.API.FoodApi;
import com.capstone.foodify.Model.Food.Food;
import com.capstone.foodify.Model.Menu.Menu;
import com.capstone.foodify.Model.Menu.MenuAdapter;
import com.capstone.foodify.Model.Shop.Shop;
import com.capstone.foodify.Model.Shop.ShopAdapter;
import com.capstone.foodify.R;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static List<Food> listFood = new ArrayList<>();
    private static List<Food> drinkFood = new ArrayList<>();

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


        getListFood();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rcvMenu.setLayoutManager(linearLayoutManager);

        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel("https://res.cloudinary.com/dnkmxpujh/image/upload/v1676023773/banner1_dandvd.png", null));
        slideModels.add(new SlideModel("https://res.cloudinary.com/dnkmxpujh/image/upload/v1676023777/banner2_okkqac.png", null));
        slideModels.add(new SlideModel("https://res.cloudinary.com/dnkmxpujh/image/upload/v1676023775/banner3_rklfi6.png", null));
        slideModels.add(new SlideModel("https://res.cloudinary.com/dnkmxpujh/image/upload/v1676023852/banner4_ufsaps.png", null));
        slideModels.add(new SlideModel("https://res.cloudinary.com/dnkmxpujh/image/upload/v1676023778/banner5_b4d1b8.png", null));

        imageSlider.setImageList(slideModels, ScaleTypes.FIT);

        //Restaurant
        List<Shop> shopList = new ArrayList<>();
        shopList.add(new Shop(R.drawable.burgerking));
        shopList.add(new Shop(R.drawable.dominopizza));
        shopList.add(new Shop(R.drawable.mcdonals));
        shopList.add(new Shop(R.drawable.pizzahut));
        shopList.add(new Shop(R.drawable.subway));

        shopAdapter.setData(shopList);
        recyclerView_restaurant.setAdapter(shopAdapter);

        // Inflate the layout for this fragment
        return view;
    }

    private List<Menu> getListMenu() {
        List<Menu> listMenu = new ArrayList<>();

        listMenu.add(new Menu("Recommend Food", listFood));
        listMenu.add(new Menu("Drinks", drinkFood));

        return listMenu;
    }

    private void getListFood() {
        FoodApi.apiService.bestFoodResponse().enqueue(new Callback<List<Food>>() {
            @Override
            public void onResponse(Call<List<Food>> call, Response<List<Food>> response) {
                List<Food> foodData = response.body();

                for(Food tempFood: foodData){
                    listFood.add(tempFood);
                }

                drinkListFood();
            }

            @Override
            public void onFailure(Call<List<Food>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi" + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void drinkListFood() {
        FoodApi.apiService.drinksFoodResponse().enqueue(new Callback<List<Food>>() {
            @Override
            public void onResponse(Call<List<Food>> call, Response<List<Food>> response) {
                List<Food> foodData = response.body();

                for(Food tempFood: foodData){
                    drinkFood.add(tempFood);
                }

                menuAdapter.setData(getListMenu());
                rcvMenu.setAdapter(menuAdapter);
            }

            @Override
            public void onFailure(Call<List<Food>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi" + t, Toast.LENGTH_LONG).show();
            }
        });
    }
}