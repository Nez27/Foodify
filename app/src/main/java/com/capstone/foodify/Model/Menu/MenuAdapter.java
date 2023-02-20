package com.capstone.foodify.Model.Menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.Model.Food.FoodAdapter;
import com.capstone.foodify.R;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder>{

    private Context context;
    private List<Menu> listMenu;

    public MenuAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Menu> list){
        this.listMenu = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        Menu menu = listMenu.get(position);

        if(menu == null){
            return;
        }

        holder.titleMenu.setText(menu.getNameMenu());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        holder.rcvFood.setLayoutManager(linearLayoutManager);

        FoodAdapter foodAdapter = new FoodAdapter(context);
        foodAdapter.setData(menu.getFoods());

        holder.rcvFood.setAdapter(foodAdapter);
    }

    @Override
    public int getItemCount() {
        if(listMenu != null){
            return listMenu.size();
        }
        return 0;
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder{

        private TextView titleMenu;
        private RecyclerView rcvFood;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);

            titleMenu = itemView.findViewById(R.id.menuTitle);
            rcvFood = itemView.findViewById(R.id.recycler_view_food);
        }
    }
}
