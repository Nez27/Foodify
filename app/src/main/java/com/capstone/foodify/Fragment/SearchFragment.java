package com.capstone.foodify.Fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.API.FoodApi;
import com.capstone.foodify.Common;
import com.capstone.foodify.Model.Category;
import com.capstone.foodify.Model.Food;
import com.capstone.foodify.Adapter.FoodSearchAdapter;
import com.capstone.foodify.Model.Response.Categories;
import com.capstone.foodify.Model.Response.Foods;
import com.capstone.foodify.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;

public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";
    private static int CURRENT_PAGE = 0;
    private static final int LIMIT = 8;
    private static boolean LAST_PAGE;
    private static int ACTION_CODE = -1;
    private static final int LIST_FOOD = -1;
    private static final int SEARCH_FOOD = 0;
    private static final int LIST_FOOD_BY_CATEGORY = 2;
    private static final int LIST_FOOD_BY_CATEGORY_AND_NAME = 3;
    private String searchQuery = "";
    private LinearLayout searchNotFound;
    private RecyclerView recycler_view_search;
    private NestedScrollView nestedScrollView;
    private TextView end_of_list_text_view;
    private FoodSearchAdapter foodAdapter;
    private ProgressBar progressBar;
    private static final List<Food> listFoodSearch = new ArrayList<>();
    private ChipGroup list_Category;
    private final List<Integer> statusCategoryChecked = new ArrayList<>();
    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        //Init Component
        searchView = view.findViewById(R.id.search_view);
        recycler_view_search = view.findViewById(R.id.recycler_view_search);
        list_Category = view.findViewById(R.id.list_category);
        nestedScrollView = view.findViewById(R.id.search_fragment);
        foodAdapter = new FoodSearchAdapter(getContext());
        progressBar = view.findViewById(R.id.progress_bar);
        end_of_list_text_view = view.findViewById(R.id.end_of_list_text);
        searchNotFound = view.findViewById(R.id.search_not_found);

        //Check status internet
        if(Common.getConnectionType(requireContext()) == 0){
            Common.showErrorInternetConnectionNotification(getActivity());
        }

        getListCategory();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recycler_view_search.setLayoutManager(linearLayoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        recycler_view_search.addItemDecoration(itemDecoration);

        //Fix for lagging when scrolling
        recycler_view_search.setNestedScrollingEnabled(false);
        recycler_view_search.setAdapter(foodAdapter);

        getListFood(0);
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

                //Hide UI search not found
                searchNotFound.setVisibility(View.GONE);


                listFoodSearch.clear();
                showProgressBarAndHideEndOfListText();
                if (!query.isEmpty()) {

                    searchQuery = query;

                    //Search food with name and category
                    if(statusCategoryChecked.size() != 0){
                        ACTION_CODE = LIST_FOOD_BY_CATEGORY_AND_NAME;
                        CURRENT_PAGE = 0;

                        getListFoodByCategoryAndName(0);
                    } else {
                        //Set action code
                        ACTION_CODE = SEARCH_FOOD;
                        CURRENT_PAGE = 0;

                        getListFoodBySearch(CURRENT_PAGE);
                    }
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(newText.length() == 0){
                    searchQuery = "";
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


    private void getListFoodBySearch(int page) {
        FoodApi.apiService.searchFoodByName(searchQuery, page, LIMIT, "id", "asc").enqueue(new Callback<Foods>() {
            @Override
            public void onResponse(Call<Foods> call, Response<Foods> response) {
                //Init total page and total elements value
                LAST_PAGE = response.body().getPage().isLast();

                loadFoodAdapter(response);

            }

            @Override
            public void onFailure(Call<Foods> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private void loadFoodAdapter(Response<Foods> response) {
        Foods foodData = response.body();

        List<Food> tempFood = foodData.getProducts();

        listFoodSearch.addAll(tempFood);
        foodAdapter.setData(listFoodSearch);
        foodAdapter.notifyDataSetChanged();

        //If list data don't have pagination
        if(LAST_PAGE)
            hideProgressBarAndShowEndOfListText();

        //If search not found
        if(listFoodSearch.size() == 0){
            end_of_list_text_view.setVisibility(View.GONE);
            searchNotFound.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }

    }

    private void getListCategory() {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        FoodApi.apiService.listCategory().enqueue(new Callback<Categories>() {
            @Override
            public void onResponse(Call<Categories> call, Response<Categories> response) {

                if(response.code() == 200){
                    for(Category tempCategory: response.body().getCategories()){
                        Chip chip = (Chip) inflater.inflate(R.layout.chip_item, null, false);
                        chip.setText(tempCategory.getName());
                        chip.setEnabled(true);

                        chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                //Add category to list category have checked
                                if(b) {
                                    statusCategoryChecked.add(Integer.parseInt(tempCategory.getId()));
                                } else {
                                    statusCategoryChecked.remove(Integer.valueOf(tempCategory.getId()));
                                }


                                listFoodSearch.clear();
                                CURRENT_PAGE = 0;
                                showProgressBarAndHideEndOfListText();
                                searchNotFound.setVisibility(View.GONE);

                                //Check category list null or not
                                if(statusCategoryChecked.size() == 0){

                                    //Check query text
                                    if(!searchQuery.isEmpty()){
                                        //Set action code
                                        ACTION_CODE = SEARCH_FOOD;
                                        CURRENT_PAGE = 0;

                                        getListFoodBySearch(CURRENT_PAGE);

                                    } else {
                                        //If null, return list food
                                        ACTION_CODE = LIST_FOOD;
                                        getListFood(0);
                                    }
                                } else {
                                    //If have category check
                                    if(!searchQuery.isEmpty()){
                                        //Return list food by name and category

                                        ACTION_CODE = LIST_FOOD_BY_CATEGORY_AND_NAME;
                                        CURRENT_PAGE = 0;

                                        getListFoodByCategoryAndName(0);
                                    } else {
                                        //Return list food by category

                                        ACTION_CODE = LIST_FOOD_BY_CATEGORY;
                                        getListFoodByCategory(CURRENT_PAGE);
                                    }

                                }
                            }
                        });

                        list_Category.addView(chip);
                    }
                }

            }

            @Override
            public void onFailure(Call<Categories> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private void getListFoodByCategoryAndName(int page){
        FoodApi.apiService.listFoodByCategoriesAndName(statusCategoryChecked, searchQuery, page, LIMIT, "id", "asc").enqueue(new Callback<Foods>() {
            @Override
            public void onResponse(Call<Foods> call, Response<Foods> response) {
                searchNotFound.setVisibility(View.GONE);
                showProgressBarAndHideEndOfListText();

                if(response.code() == 200){
                    LAST_PAGE = response.body().getPage().isLast();
                    loadFoodAdapter(response);
                }
            }

            @Override
            public void onFailure(Call<Foods> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private void getListFoodByCategory(int page) {
        FoodApi.apiService.listFoodByCategory(statusCategoryChecked, page, LIMIT, "id", "asc").enqueue(new Callback<Foods>() {
            @Override
            public void onResponse(Call<Foods> call, Response<Foods> response) {

                searchNotFound.setVisibility(View.GONE);
                showProgressBarAndHideEndOfListText();

                //Init total page and total elements value
                LAST_PAGE = response.body().getPage().isLast();
                loadFoodAdapter(response);
            }

            @Override
            public void onFailure(Call<Foods> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private void getListFood(int page){
        FoodApi.apiService.listFood(page, LIMIT, "id", "asc").enqueue(new Callback<Foods>() {
            @Override
            public void onResponse(Call<Foods> call, Response<Foods> response) {
                LAST_PAGE = response.body().getPage().isLast();
                loadFoodAdapter(response);
            }

            @Override
            public void onFailure(Call<Foods> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private void dataLoadMore() {
        switch (ACTION_CODE){
            case LIST_FOOD:{
                if(!LAST_PAGE){
                    getListFood(++CURRENT_PAGE);
                } else {
                    hideProgressBarAndShowEndOfListText();
                }
                break;
            }
            case SEARCH_FOOD:{
                if(!LAST_PAGE){
                    getListFoodBySearch(++CURRENT_PAGE);
                } else {
                    hideProgressBarAndShowEndOfListText();
                }
                break;
            }
            case LIST_FOOD_BY_CATEGORY:{
                if(!LAST_PAGE){
                    getListFoodByCategory(++CURRENT_PAGE);
                } else {
                    hideProgressBarAndShowEndOfListText();
                }
                break;
            }
            case LIST_FOOD_BY_CATEGORY_AND_NAME:{
                if(!LAST_PAGE){
                    getListFoodByCategoryAndName(++CURRENT_PAGE);
                } else {
                    hideProgressBarAndShowEndOfListText();
                }
                break;
            }
        }
    }

}