package com.capstone.foodify.Adapter;

import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.ItemTouchHelperListener;

public class RecyclerViewItemTouchHelperFavoriteFood extends ItemTouchHelper.SimpleCallback{

    private ItemTouchHelperListener listener;

    public RecyclerViewItemTouchHelperFavoriteFood(int dragDirs, int swipeDirs, ItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if(listener != null)
            listener.onSwiped(viewHolder);
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if(viewHolder != null){
            View foreGroundView = ((FoodFavoriteAdapter.FoodFavoriteViewHolder) viewHolder).layoutForeGround;
            getDefaultUIUtil().onSelected(foreGroundView);
        }

    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        assert viewHolder != null;
        View foreGroundView = ((FoodFavoriteAdapter.FoodFavoriteViewHolder) viewHolder).layoutForeGround;
        getDefaultUIUtil().onDrawOver(c, recyclerView, foreGroundView, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View foreGroundView = ((FoodFavoriteAdapter.FoodFavoriteViewHolder) viewHolder).layoutForeGround;
        getDefaultUIUtil().onDraw(c, recyclerView, foreGroundView, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        View foreGroundView = ((FoodFavoriteAdapter.FoodFavoriteViewHolder) viewHolder).layoutForeGround;
        getDefaultUIUtil().clearView(foreGroundView);
    }
}
