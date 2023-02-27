package com.capstone.foodify.Fragment;

import static com.capstone.foodify.Fragment.HomeFragment.listFood;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.API.FoodApi;
import com.capstone.foodify.Model.Category.Category;
import com.capstone.foodify.Model.Food.Food;
import com.capstone.foodify.Model.Food.FoodSearchAdapter;
import com.capstone.foodify.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private static int CURRENT_PAGE = 1;
    private static final int LIMIT = 18;
    private static int TOTAL_PAGE = 5;
    private static int ACTION_CODE = -1;
    private static final int LIST_FOOD = -1;
    private static final int SEARCH_FOOD = 0;
    private String searchQuery = "";
    private RecyclerView recycler_view_search;
    private NestedScrollView nestedScrollView;
    private TextView end_of_list_text_view;
    private FoodSearchAdapter foodAdapter;
    private ProgressBar progressBar;
    private static final List<Food> listFoodSearch = new ArrayList<>();
    private ChipGroup list_Category;
    private final List<String> statusCategoryChecked = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        //Init Component
        SearchView searchView = view.findViewById(R.id.search_view);
        recycler_view_search = view.findViewById(R.id.recycler_view_search);
        list_Category = view.findViewById(R.id.list_category);
        nestedScrollView = view.findViewById(R.id.search_fragment);
        foodAdapter = new FoodSearchAdapter(getContext());
        progressBar = view.findViewById(R.id.progress_bar);
        end_of_list_text_view = view.findViewById(R.id.end_of_list_text);

        getListCategory();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recycler_view_search.setLayoutManager(gridLayoutManager);
        recycler_view_search.setNestedScrollingEnabled(false);
        recycler_view_search.setAdapter(foodAdapter);

        getListFoodByPagination(1);
        if (nestedScrollView != null) {
            nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                        //When user scroll to bottom, load more data
                        dataLoadMore();
                    }
                }
            });
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ACTION_CODE = SEARCH_FOOD;
                CURRENT_PAGE = 1;
                showProgressBarAndHideEndOfListText();
                listFoodSearch.clear();
                if (!query.isEmpty()) {
                    searchQuery = query;
                    getListFoodBySearch(searchQuery, CURRENT_PAGE);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (searchView.getQuery().length() == 0) {
                    CURRENT_PAGE = 1;
                    ACTION_CODE = LIST_FOOD;
                    showProgressBarAndHideEndOfListText();
                    foodAdapter.setData(listFood);
                    foodAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });

        return view;
    }

    private void showProgressBarAndHideEndOfListText(){
        progressBar.setVisibility(View.VISIBLE);
        end_of_list_text_view.setVisibility(View.GONE);
    }

    private void hideProgressBarAndShowEndOfListText(){
        progressBar.setVisibility(View.GONE);
        end_of_list_text_view.setVisibility(View.VISIBLE);
    }

    private void getListFoodBySearch(String query, int page) {
        FoodApi.apiService.listFood(query, page, LIMIT).enqueue(new Callback<List<Food>>() {
            @Override
            public void onResponse(Call<List<Food>> call, Response<List<Food>> response) {
                loadFoodAdapter(response);
            }

            @Override
            public void onFailure(Call<List<Food>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi" + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadFoodAdapter(Response<List<Food>> response) {
        List<Food> foodData = response.body();

        if(foodData != null) {
            listFoodSearch.addAll(foodData);
            foodAdapter.setData(listFoodSearch);
            foodAdapter.notifyDataSetChanged();
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
                listFoodSearch.clear();
                loadFoodAdapter(response);
            }

            @Override
            public void onFailure(Call<List<Food>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getListFoodByPagination(int page){
        FoodApi.apiService.listFood(page, LIMIT).enqueue(new Callback<List<Food>>() {
            @Override
            public void onResponse(Call<List<Food>> call, Response<List<Food>> response) {
                loadFoodAdapter(response);
            }

            @Override
            public void onFailure(Call<List<Food>> call, Throwable t) {

            }
        });
    }

    private void dataLoadMore() {
        switch (ACTION_CODE){
            case LIST_FOOD:{
                if(CURRENT_PAGE < TOTAL_PAGE){
                    getListFoodByPagination(++CURRENT_PAGE);
                } else {
                    hideProgressBarAndShowEndOfListText();
                }
                break;
            }
            case SEARCH_FOOD:{
                if(CURRENT_PAGE < TOTAL_PAGE){
                    getListFoodBySearch(searchQuery, ++CURRENT_PAGE);
                } else {
                    hideProgressBarAndShowEndOfListText();
                }
                break;
            }
        }
    }

}