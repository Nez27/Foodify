package com.capstone.foodify.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.API.FoodApi;
import com.capstone.foodify.Common;
import com.capstone.foodify.Model.Food;
import com.capstone.foodify.Model.Menu;
import com.capstone.foodify.Adapter.MenuAdapter;
import com.capstone.foodify.Model.Response.Foods;
import com.capstone.foodify.Model.Response.Shops;
import com.capstone.foodify.Model.Shop;
import com.capstone.foodify.Adapter.ShopAdapter;
import com.capstone.foodify.Model.Slider;
import com.capstone.foodify.R;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.saadahmedsoft.popupdialog.PopupDialog;
import com.saadahmedsoft.popupdialog.Styles;

import java.util.ArrayList;
import java.util.List;

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
    private PopupDialog popupDialog;


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

        popupDialog = PopupDialog.getInstance(getContext());


        if(listFood.isEmpty() || recentFood.isEmpty()){
            getListFood();
        } else {
            menuAdapter.setData(getListMenu());
            rcvMenu.setAdapter(menuAdapter);
        }


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rcvMenu.setLayoutManager(linearLayoutManager);

        showSlider();

        //Get list shop
        getListShop();

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Main activity destroy!");
    }

    private void showSlider(){

        //Show progress bar
        popupDialog.setStyle(Styles.PROGRESS).setProgressDialogTint(getResources().getColor(R.color.primaryColor, null))
                .setCancelable(false).showDialog();

        FoodApi.apiService.listSlider().enqueue(new Callback<List<Slider>>() {
            @Override
            public void onResponse(Call<List<Slider>> call, Response<List<Slider>> response) {
                List<Slider> sliders = response.body();

                List<SlideModel> slideModels = new ArrayList<>();
                for(Slider slider: sliders){
                    slideModels.add(new SlideModel(slider.getImageUrl(), null));
                }

                imageSlider.setImageList(slideModels, ScaleTypes.FIT);

                popupDialog.dismissDialog();
            }

            @Override
            public void onFailure(Call<List<Slider>> call, Throwable t) {
                popupDialog.dismissDialog();
                Common.showNotificationError(getContext(), getActivity());
            }
        });
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
                Common.showNotificationError(getContext(), getActivity());
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
                Common.showNotificationError(getContext(), getActivity());
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
                Common.showNotificationError(getContext(), getActivity());
            }
        });
    }
}