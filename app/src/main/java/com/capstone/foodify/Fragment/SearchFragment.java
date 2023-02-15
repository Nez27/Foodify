package com.capstone.foodify.Fragment;

import static com.capstone.foodify.Fragment.HomeFragment.listFood;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.API.FoodApi;
import com.capstone.foodify.Model.Food.Food;
import com.capstone.foodify.Model.Food.FoodAdapter;
import com.capstone.foodify.Model.Food.FoodSearchAdapter;
import com.capstone.foodify.R;
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
    private PersistentSearchView persistentSearchView;

    private static List<Food> listFoodSearch = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        //Init Component
        recycler_view_search = view.findViewById(R.id.recycler_view_search);
        persistentSearchView = view.findViewById(R.id.search_view);

        foodAdapter = new FoodSearchAdapter(getContext());

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
                            List<Food> foodData = response.body();

                            if(foodData != null) {
                                listFoodSearch.clear();
                                for(Food tempFood: foodData){
                                    listFoodSearch.add(tempFood);
                                }
                                foodAdapter.setData(listFoodSearch);
                                recycler_view_search.setAdapter(foodAdapter);
                            }

                        }

                        @Override
                        public void onFailure(Call<List<Food>> call, Throwable t) {
                            Toast.makeText(getContext(), "Lỗi" + t, Toast.LENGTH_LONG).show();
                        }
                    });
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

}