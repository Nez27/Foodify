package com.capstone.foodify.Fragment;

import static com.capstone.foodify.Fragment.HomeFragment.listFood;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.API.FoodApi;
import com.capstone.foodify.Model.Category.Category;
import com.capstone.foodify.Model.Food.Food;
import com.capstone.foodify.Model.Food.FoodAdapter;
import com.capstone.foodify.Model.Food.FoodSearchAdapter;
import com.capstone.foodify.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.paulrybitskyi.persistentsearchview.PersistentSearchView;
import com.paulrybitskyi.persistentsearchview.listeners.OnSearchConfirmedListener;
import com.paulrybitskyi.persistentsearchview.listeners.OnSearchQueryChangeListener;
import com.paulrybitskyi.persistentsearchview.utils.VoiceRecognitionDelegate;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private RecyclerView recycler_view_search;
    private FoodSearchAdapter foodAdapter;
    private static final List<Food> listFoodSearch = new ArrayList<>();
    private ChipGroup list_Category;
    private final List<String> statusCategoryChecked = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        //Init Component
        PersistentSearchView persistentSearchView = view.findViewById(R.id.search_view);
        recycler_view_search = view.findViewById(R.id.recycler_view_search);
        list_Category = view.findViewById(R.id.list_category);

        foodAdapter = new FoodSearchAdapter(getContext());

        getListCategory();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recycler_view_search.setLayoutManager(gridLayoutManager);

        foodAdapter.setData(listFood);
        recycler_view_search.setAdapter(foodAdapter);

        persistentSearchView.setLeftButtonDrawable(R.drawable.ic_search);
        persistentSearchView.setOnSearchConfirmedListener(new OnSearchConfirmedListener() {

            @Override
            public void onSearchConfirmed(PersistentSearchView searchView, String query) {
                // Handle a search confirmation. This is the place where you'd
                // want to perform a search against your data provider.
                if(!query.isEmpty()){
                    FoodApi.apiService.listFood(query).enqueue(new Callback<List<Food>>() {
                        @Override
                        public void onResponse(Call<List<Food>> call, Response<List<Food>> response) {
                            loadFoodAdapter(response);
                        }

                        @Override
                        public void onFailure(Call<List<Food>> call, Throwable t) {
                            Toast.makeText(getContext(), "Lỗi" + t, Toast.LENGTH_LONG).show();
                        }
                    });

                    Toast.makeText(getContext(), "Category checked: " + statusCategoryChecked.toString(), Toast.LENGTH_SHORT).show();
                }

                searchView.collapse();
            }

        });

        persistentSearchView.setOnSearchQueryChangeListener(new OnSearchQueryChangeListener() {
            @Override
            public void onSearchQueryChanged(PersistentSearchView searchView, String oldQuery, String newQuery) {
                if(searchView.isInputQueryEmpty()){
                    foodAdapter.setData(listFood);
                    recycler_view_search.setAdapter(foodAdapter);
                }
            }
        });

        return view;
    }

    private void loadFoodAdapter(Response<List<Food>> response) {
        List<Food> foodData = response.body();

        if(foodData != null) {
            listFoodSearch.clear();
            listFoodSearch.addAll(foodData);
            foodAdapter.setData(listFoodSearch);
            recycler_view_search.setAdapter(foodAdapter);
        }
    }

    private void getListCategory() {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        FoodApi.apiService.listCategory().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                List<Category> categoriesData = response.body();

                if(categoriesData != null){
                    for(Category tempCategory: categoriesData){
                        Chip chip = (Chip) inflater.inflate(R.layout.chip_item, null, false);
                        chip.setText(tempCategory.getName());
                        chip.setEnabled(true);

                        chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                loadFoodByCategory(tempCategory.getId());
                                if(b) {
                                    statusCategoryChecked.add(tempCategory.getId());
                                } else {
                                    statusCategoryChecked.remove(tempCategory.getId());
                                }
                            }
                        });

                        list_Category.addView(chip);
                    }
                }

            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFoodByCategory(String id) {

        FoodApi.apiService.listFoodByCategory(id).enqueue(new Callback<List<Food>>() {
            @Override
            public void onResponse(Call<List<Food>> call, Response<List<Food>> response) {
                loadFoodAdapter(response);
            }

            @Override
            public void onFailure(Call<List<Food>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t, Toast.LENGTH_SHORT).show();
            }
        });
    }

}