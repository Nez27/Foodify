package com.capstone.foodify.Fragment.Order;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.foodify.API.FoodApiToken;
import com.capstone.foodify.Common;
import com.capstone.foodify.Model.Order;
import com.capstone.foodify.Adapter.OrderAdapter;
import com.capstone.foodify.Model.Response.Orders;
import com.capstone.foodify.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShipOrderFragment extends Fragment {

    private static final String STATUS = "SHIPPING";
    private static int CURRENT_PAGE = 0;
    private static final int PAGE_SIZE = 8;
    private static final String SORT_BY = "orderTime";
    private static final String SORT_DIR = "desc";
    private static boolean LAST_PAGE;
    private List<Order> listOrders = new ArrayList<>();
    RecyclerView recyclerView;
    OrderAdapter orderAdapter;
    NestedScrollView nestedScrollView;
    private ProgressBar progressBar;
    private TextView endOfListText;
    private LinearLayout empty_layout;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ship_order, container, false);

        //Init Component
        initComponent(view);

        getOrderUser(CURRENT_PAGE);

        orderAdapter = new OrderAdapter(getContext());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        //Set event when user scroll down
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

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.primaryColor, null));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listOrders.clear();
                CURRENT_PAGE = 0;

                getOrderUser(CURRENT_PAGE);

                nestedScrollView.setVisibility(View.VISIBLE);
                empty_layout.setVisibility(View.GONE);

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    private void initComponent(View view) {
        recyclerView = view.findViewById(R.id.list_order);
        nestedScrollView = view.findViewById(R.id.root_view);
        progressBar = view.findViewById(R.id.progress_bar);
        endOfListText = view.findViewById(R.id.end_of_list_text);
        empty_layout = view.findViewById(R.id.empty_layout);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
    }

    private void getOrderUser(int page){
        FoodApiToken.apiService.getOrderUser(Common.CURRENT_USER.getId(), STATUS, page, PAGE_SIZE, SORT_BY, SORT_DIR).enqueue(new Callback<Orders>() {
            @Override
            public void onResponse(Call<Orders> call, Response<Orders> response) {
                if(response.code() == 200){

                    Orders orderResponse = response.body();

                    //Init data
                    listOrders.addAll(orderResponse.getOrders());
                    LAST_PAGE = orderResponse.getPage().isLast();

                    if(LAST_PAGE)
                        hideProgressBarAndShowEndOfList();

                    //If don't have order
                    if(listOrders.size() == 0){
                        nestedScrollView.setVisibility(View.GONE);

                        empty_layout.setVisibility(View.VISIBLE);
                    } else {
                        nestedScrollView.setVisibility(View.VISIBLE);

                        empty_layout.setVisibility(View.GONE);
                    }

                    orderAdapter.setData(listOrders);
                    recyclerView.setAdapter(orderAdapter);
                } else {
                    Toast.makeText(getContext(), "Đã có lỗi hệ thống! Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Orders> call, Throwable t) {
                Toast.makeText(getContext(), "Đã có lỗi kết nối đến hệ thống!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void dataLoadMore() {
        if(!LAST_PAGE){
            getOrderUser(++CURRENT_PAGE);
        } else {
            hideProgressBarAndShowEndOfList();
        }
    }

    private void hideProgressBarAndShowEndOfList() {
        progressBar.setVisibility(View.GONE);
        endOfListText.setVisibility(View.VISIBLE);
    }
}