package com.capstone.foodify.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.capstone.foodify.API.FoodApi;
import com.capstone.foodify.API.FoodApiToken;
import com.capstone.foodify.Adapter.CommentAdapter;
import com.capstone.foodify.Common;
import com.capstone.foodify.Model.Basket;
import com.capstone.foodify.Model.Category;
import com.capstone.foodify.Model.Food;
import com.capstone.foodify.Model.Image;
import com.capstone.foodify.Model.Response.Comments;
import com.capstone.foodify.Model.Response.CustomResponse;
import com.capstone.foodify.Model.Comment;
import com.capstone.foodify.R;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.mcdev.quantitizerlibrary.AnimationStyle;
import com.mcdev.quantitizerlibrary.HorizontalQuantitizer;
import com.mcdev.quantitizerlibrary.QuantitizerListener;
import com.sjapps.library.customdialog.BasicDialog;
import com.sjapps.library.customdialog.DialogButtonEvent;
import com.sjapps.library.customdialog.DialogButtonEvents;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.willy.ratingbar.RotationRatingBar;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodDetailActivity extends AppCompatActivity {
    ArrayList<Comment> listComment = new ArrayList<>();
    private static final int CREATE_COMMENT = 1;
    private static final int EDIT_COMMENT = 2;
    private static int CURRENT_PAGE = 0;
    private static final int PAGE_SIZE = 8;
    private static final String SORT_BY = "id";
    private static final String SORT_DIR = "asc";
    private static boolean LAST_PAGE, IS_BUY;
    private HorizontalQuantitizer horizontalQuantitizer;
    private ImageSlider imageSlider;
    private ImageView back_image, not_favorite, is_favorite, delete_button, edt_button;
    private String foodId = "";
    private Food food;
    private TextView foodName_txt, shopName_txt, discount_txt, price_txt, countRating_txt, description_content_txt, endOfListText,
            userNameComment, contentComment, count_sold_txt;
    private ConstraintLayout content_view;
    private Button add_to_basket_button;
    private float totalPrice, price;
    private RecyclerView recyclerView_comment;
    private CommentAdapter commentAdapter;
    private Button rating_button;
    private ScaleRatingBar ratingBar, ratingBarComment;
    private ConstraintLayout progressLayout;
    private NestedScrollView nestedScrollView;
    private ProgressBar progressBar;
    private CardView user_comment_layout;
    private Comment commentUser = null;
    private ChipGroup list_category;
    private SwipeRefreshLayout swipeRefreshLayout;

    private void initComponent() {
        horizontalQuantitizer = findViewById(R.id.quantity_option);
        imageSlider = findViewById(R.id.slider);
        back_image = findViewById(R.id.back_image);
        foodName_txt = findViewById(R.id.food_name_text_view);
        shopName_txt = findViewById(R.id.shop_name_text_view);
        discount_txt = findViewById(R.id.discount);
        price_txt = findViewById(R.id.price);
        add_to_basket_button = findViewById(R.id.add_to_basket_button);
        not_favorite = findViewById(R.id.not_favorite);
        is_favorite = findViewById(R.id.is_favorite);
        countRating_txt = findViewById(R.id.count_rating_text_view);
        description_content_txt = findViewById(R.id.content_description_text_view);
        recyclerView_comment = findViewById(R.id.recycler_view_comment);
        rating_button = findViewById(R.id.rating_button);
        content_view = findViewById(R.id.content_view);
        progressLayout = findViewById(R.id.progress_layout);
        nestedScrollView = findViewById(R.id.food_detail);
        endOfListText = findViewById(R.id.end_of_list_text);
        progressBar = findViewById(R.id.progress_bar);
        ratingBar = findViewById(R.id.rating_bar);
        userNameComment = findViewById(R.id.user_name_text_view);
        contentComment = findViewById(R.id.comment_user);
        ratingBarComment = findViewById(R.id.rating_bar_comment);
        delete_button = findViewById(R.id.delete_button);
        user_comment_layout = findViewById(R.id.user_comment_layout);
        edt_button = findViewById(R.id.edit_button);
        list_category = findViewById(R.id.list_category);
        count_sold_txt = findViewById(R.id.count_sold);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        //Init Component

        initComponent();

        commentAdapter = new CommentAdapter(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView_comment.setLayoutManager(linearLayoutManager);

        //Get data
        if (getIntent() != null)
            foodId = getIntent().getStringExtra("FoodId");

        recyclerView_comment.setAdapter(commentAdapter);

        hideUI();

        getData();

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.primaryColor, null));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listComment.clear();

                getData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        if(Common.CURRENT_USER == null){
            is_favorite.setVisibility(View.GONE);
            not_favorite.setVisibility(View.GONE);
        }


        //Customize UI Horizontal Quantitizer
        horizontalQuantitizer.setTextAnimationStyle(AnimationStyle.SLIDE_IN_REVERSE);
        horizontalQuantitizer.setPlusIconBackgroundColor(R.color.white);
        horizontalQuantitizer.setMinusIconBackgroundColor(R.color.white);

        horizontalQuantitizer.setQuantitizerListener(new QuantitizerListener() {
            @Override
            public void onIncrease() {
            }

            @Override
            public void onDecrease() {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onValueChanged(int i) {

                if (totalPrice >= 0)
                    totalPrice = price * i;
                add_to_basket_button.setText("THÊM VÀO GIỎ HÀNG - " + Common.changeCurrencyUnit(totalPrice));
            }
        });

        //Turn previous action when click
        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //Open dialog comment
        rating_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Common.CURRENT_USER != null){
                    if(IS_BUY){
                        //If user already buy it
                        openCommentDialog(Gravity.CENTER, CREATE_COMMENT);
                    } else {
                        //If not
                        Common.notificationDialog(FoodDetailActivity.this, DialogStyle.TOASTER, DialogType.INFO, "Thông báo!", "Bạn cần phải mua sản phẩm này trước khi bình luận!");
                    }
                } else
                    Common.notificationDialog(FoodDetailActivity.this, DialogStyle.TOASTER, DialogType.WARNING, "Thông báo!", "Bạn cần phải đăng nhập trước khi thực hiện hành động này!");
            }
        });

        //Add food to basket
        add_to_basket_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFoodToBasket();
            }
        });

        //Move to shop detail screen when onClick
        shopName_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FoodDetailActivity.this, ShopDetailActivity.class);
                intent.putExtra("ShopId", food.getShop().getId());
                startActivity(intent);
            }
        });

        //Add food to favorite when user on click
        not_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFoodToFavorite();
            }
        });

        //Remove food from favorite
        is_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFavoriteFood();
            }
        });

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

        //Edit button
        edt_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCommentDialog(Gravity.CENTER, EDIT_COMMENT);
            }
        });

        //Delete comment
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(commentUser != null)
                    showDeleteDialogConfirm();
            }
        });
    }

    private void getData() {
        CURRENT_PAGE = 0;
        commentAdapter.setData(getListComment(0));
        checkUserBuyThisProduct();
        getFoodById(foodId);
    }

    private void checkUserBuyThisProduct(){

        if(Common.CURRENT_USER != null){
            FoodApi.apiService.checkUserBuyProduct(Integer.parseInt(foodId), Common.CURRENT_USER.getId()).enqueue(new Callback<CustomResponse>() {
                @Override
                public void onResponse(Call<CustomResponse> call, Response<CustomResponse> response) {
                    if(response.code() == 200){
                        
                        IS_BUY = response.body().isTrue();
                        
                    } else if(response.code() != 404){
                        Toast.makeText(FoodDetailActivity.this, "Đã xảy ra lỗi hệ thống! Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<CustomResponse> call, Throwable t) {
                    Toast.makeText(FoodDetailActivity.this, "Đã xảy ra lỗi kết nối đến hệ thống!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void isFavoriteIcon(){
        not_favorite.setVisibility(View.GONE);
        is_favorite.setVisibility(View.VISIBLE);
    }

    private void notFavoriteIcon(){
        not_favorite.setVisibility(View.VISIBLE);
        is_favorite.setVisibility(View.GONE);
    }

    private void addFoodToFavorite() {
        FoodApiToken.apiService.addFoodToFavorite(Common.CURRENT_USER.getId(), Integer.parseInt(foodId)).enqueue(new Callback<Food>() {
            @Override
            public void onResponse(Call<Food> call, Response<Food> response) {
                Toast.makeText(FoodDetailActivity.this, "Đã thêm " + food.getName() + " vào danh sách yêu thích!", Toast.LENGTH_SHORT).show();
                isFavoriteIcon();
            }

            @Override
            public void onFailure(Call<Food> call, Throwable t) {
                Toast.makeText(FoodDetailActivity.this, "Oops! Đã có lỗi, vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeFavoriteFood(){
        FoodApiToken.apiService.removeFoodFromFavorite(Common.CURRENT_USER.getId(), Integer.parseInt(foodId)).enqueue(new Callback<Food>() {
            @Override
            public void onResponse(Call<Food> call, Response<Food> response) {
                Toast.makeText(FoodDetailActivity.this, "Đã xoá " + food.getName() + " khỏi danh sách yêu thích!", Toast.LENGTH_SHORT).show();
                notFavoriteIcon();
            }

            @Override
            public void onFailure(Call<Food> call, Throwable t) {
                Toast.makeText(FoodDetailActivity.this, "Oops! Đã có lỗi, vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addFoodToBasket(){
        if(horizontalQuantitizer.getValue() > 0){
            if(Common.FINAL_SHOP == null && Common.LIST_BASKET_FOOD.size() == 0){
                Common.FINAL_SHOP = food.getShop().getName();
            }

            if(Common.FINAL_SHOP.equals(food.getShop().getName())){
                addFood();
            } else {
                Common.notificationDialog(this, DialogStyle.FLAT, DialogType.INFO, "Thông báo!", "Bạn chỉ có thể đặt những món ăn cùng cửa hàng, xin vui lòng kiểm tra lại giỏ hàng!");
            }

        } else {
            Toast.makeText(FoodDetailActivity.this, "Bạn chưa chọn số lượng đồ ăn cần đặt!", Toast.LENGTH_SHORT).show();
        }
    }

    private void addFood() {
        Basket foodInBasket = Common.getFoodExistInBasket(food.getId());
        if(foodInBasket == null){
            //If item is not exist in basket

            //Check food image is null or not
            String imageTemp = null;
            if(food.getImages().size() > 0)
                imageTemp = food.getImages().get(0).getImageUrl();

            Common.LIST_BASKET_FOOD.add(new Basket(food.getId(), imageTemp, food.getName(), food.getCost(), food.getShop().getName(),
                    String.valueOf(horizontalQuantitizer.getValue()), food.getDiscountPercent(), food.getShop().getId()));
        } else {
            //If item is exist in basket

            for(int i = 0; i < Common.LIST_BASKET_FOOD.size(); i++){
                String foodId = Common.LIST_BASKET_FOOD.get(i).getId();
                //Find foodId exist
                if(foodId.equals(food.getId())){
                    //Get quantity food from basket
                    String quantity = Common.LIST_BASKET_FOOD.get(i).getQuantity();

                    //Change quantity food from basket
                    int quantityInt = Integer.parseInt(quantity) + horizontalQuantitizer.getValue();
                    Common.LIST_BASKET_FOOD.get(i).setQuantity(String.valueOf(quantityInt));
                    break;
                }
            }

        }
        Toast.makeText(FoodDetailActivity.this, "Đã thêm " + food.getName() + " vào giỏ hàng!", Toast.LENGTH_SHORT).show();
        onBackPressed();
        finish();
    }

    private void getCommentUser(){
        FoodApi.apiService.getUserComment(Integer.parseInt(foodId), Common.CURRENT_USER.getId()).enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if(response.code() == 200){
                    //Have user comment
                    Comment commentData = response.body();

                    if(commentData != null){
                        //Init data
                        commentUser = commentData;
                        userNameComment.setText(Common.CURRENT_USER.getFullName());
                        ratingBarComment.setRating(commentData.getRating());
                        contentComment.setText(commentData.getContent());

                        user_comment_layout.setVisibility(View.VISIBLE);

                        showUI();

                        rating_button.setVisibility(View.GONE);
                    }
                } else if(response.code() == 404){
                    //Don't have user comment
                    user_comment_layout.setVisibility(View.GONE);
                    showUI();
                } else {
                    Toast.makeText(FoodDetailActivity.this, "Có lỗi từ hệ thống! Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                    showUI();
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                showUI();
                Common.showErrorServerNotification(FoodDetailActivity.this, "Lỗi kết nối!");
            }
        });
    }
    private List<Comment> getListComment(int page) {
        FoodApi.apiService.getCommentByProductId(Integer.parseInt(foodId), page, PAGE_SIZE, SORT_BY, SORT_DIR).enqueue(new Callback<Comments>() {
            @Override
            public void onResponse(Call<Comments> call, Response<Comments> response) {
                if(response.code() == 200){

                    List<Comment> tempCommentList = response.body().getComments();

                    //Init data
                    listComment.addAll(tempCommentList);
                    LAST_PAGE = response.body().getPage().isLast();

                    if(LAST_PAGE){
                        progressBar.setVisibility(View.GONE);
                        endOfListText.setVisibility(View.VISIBLE);
                    }

                    //Get user's comment
                    if(Common.CURRENT_USER != null){
                        for(Comment tempComment: listComment){
                            if(tempComment.getUser().getEmail().equals(Common.CURRENT_USER.getEmail())){
                                //Remove comment from list
                                listComment.remove(tempComment);

                                //Remove button comment
                                rating_button.setVisibility(View.GONE);
                                break;
                            }
                        }
                    }


                    if(listComment.size() == 0)
                        endOfListText.setVisibility(View.GONE);

                    commentAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(FoodDetailActivity.this, "Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Comments> call, Throwable t) {
                Toast.makeText(FoodDetailActivity.this, "Đã có lỗi từ hệ thống!", Toast.LENGTH_SHORT).show();
            }
        });

        return listComment;
    }
    private void hideUI() {
        content_view.setVisibility(View.GONE);
    }

    private void showUI() {
        content_view.setVisibility(View.VISIBLE);

        progressLayout.setVisibility(View.GONE);
    }

    private void initData() {
        LayoutInflater inflater = LayoutInflater.from(this);

        foodName_txt.setText(food.getName());
        shopName_txt.setText(food.getShop().getName());
        description_content_txt.setText(food.getDescription());
        ratingBar.setRating(food.getAverageRating());

        if(food.getReviewCount() == null || Integer.parseInt(food.getReviewCount()) == 0){
            countRating_txt.setText("Chưa có bình luận nào!");
        } else {
            countRating_txt.setText(food.getReviewCount() + " đánh giá");
        }

        if(food.getSold() == 0){
            count_sold_txt.setText("Chưa có lượt bán nào!");
        } else {
            count_sold_txt.setText(food.getSold() + " đã bán");
        }

        list_category.removeAllViews();
        //Init category
        for(Category category: food.getCategories()){
            Chip chip = (Chip) inflater.inflate(R.layout.chip_item, null, false);
            chip.setText(category.getName());
            chip.setClickable(false);

            list_category.addView(chip);
        }


        add_to_basket_button.setText("THÊM VÀO GIỎ HÀNG - " + Common.changeCurrencyUnit(0));

        //Set slider image food
        List<SlideModel> slideModels = new ArrayList<>();
        //Get image food
        if (food.getImages().size() > 0) {
            for (Image imageUrl : food.getImages())
                slideModels.add(new SlideModel(imageUrl.getImageUrl(), null));
        } else {
            slideModels.add(new SlideModel(R.drawable.dish, null));
        }
        imageSlider.setImageList(slideModels, ScaleTypes.FIT);

        //Show discount percent when value greater than 0 and show the final cost after discount!
        if (food.getDiscountPercent() > 0) {
            discount_txt.setText("-" + food.getDiscountPercent() + "%");

            float finalCost = food.getCost() - (food.getCost() * food.getDiscountPercent() / 100);
            price_txt.setText(Common.changeCurrencyUnit(finalCost));

            //Set data price to calculate
            price = finalCost;
        } else {
            discount_txt.setVisibility(View.GONE);

            price_txt.setText(Common.changeCurrencyUnit(food.getCost()));

            //Set data price to calculate
            price = food.getCost();
        }
    }

    private void checkFoodIsFavorite(int foodId){
        FoodApiToken.apiService.checkFoodIsFavorite(Common.CURRENT_USER.getId(), foodId).enqueue(new Callback<CustomResponse>() {
            @Override
            public void onResponse(Call<CustomResponse> call, Response<CustomResponse> response) {
                CustomResponse dataResponse = response.body();

                boolean isFavorite = false;

                if(dataResponse != null)
                    isFavorite = dataResponse.isTrue();

                if(isFavorite)
                    isFavoriteIcon();
                else
                    notFavoriteIcon();

            }

            @Override
            public void onFailure(Call<CustomResponse> call, Throwable t) {
                Toast.makeText(FoodDetailActivity.this, "Oops, đã có lỗi xảy ra! Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getFoodById(String foodId) {
        FoodApi.apiService.detailFood(foodId).enqueue(new Callback<Food>() {
            @Override
            public void onResponse(Call<Food> call, Response<Food> response) {
                Food tempFood = response.body();
                if (tempFood != null)
                    food = tempFood;

                initData();

                if(Common.CURRENT_USER != null){
                    checkFoodIsFavorite(Integer.parseInt(foodId));
                    getCommentUser();
                } else
                    showUI();
            }

            @Override
            public void onFailure(Call<Food> call, Throwable t) {
                Common.showNotificationError(getBaseContext(), FoodDetailActivity.this);
            }
        });
    }

    private void openCommentDialog(int gravity, int actionCode){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(actionCode == CREATE_COMMENT){
            dialog.setContentView(R.layout.dialog_comment);
        } else if(actionCode == EDIT_COMMENT) {
            dialog.setContentView(R.layout.dialog_comment_edit_delete);
        }

        Window window = dialog.getWindow();
        if(window == null)
            return;

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        if(Gravity.BOTTOM == gravity){
            dialog.setCancelable(true);
        } else {
            dialog.setCancelable(false);
        }

        EditText comment_content = dialog.findViewById(R.id.edit_text_comment);
        RotationRatingBar ratingBarDialog = dialog.findViewById(R.id.rating_bar);
        Button confirm = dialog.findViewById(R.id.confirm_button);
        Button cancel = dialog.findViewById(R.id.cancel_button);

        if(actionCode == EDIT_COMMENT){
            //Init data
            if(commentUser != null){
                ratingBarDialog.setRating(commentUser.getRating());
                comment_content.setText(commentUser.getContent());
            }

        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(comment_content.getText().toString().trim().length() > 0){
                    progressLayout.setVisibility(View.VISIBLE);

                    if(actionCode == CREATE_COMMENT){
                        //Create comment
                        createComment(comment_content.getText().toString(), ratingBarDialog);

                        dialog.dismiss();

                    } else if(actionCode == EDIT_COMMENT){
                        //Edit comment
                        if(commentUser != null){

                            editComment(comment_content.getText().toString(), ratingBarDialog);

                            dialog.dismiss();
                        }

                    }
                } else {
                    comment_content.setError("Khung bình luận không được để trống!");
                }
            }
        });

        dialog.show();
    }

    private void editComment(String content, RotationRatingBar ratingBarDialog) {
        //Create object comment
        Comment newComment = new Comment(content, ratingBarDialog.getRating(), Common.CURRENT_USER.getId());

        FoodApiToken.apiService.updateComment(Integer.parseInt(foodId), commentUser.getId(), newComment).enqueue(new Callback<CustomResponse>() {
            @Override
            public void onResponse(Call<CustomResponse> call, Response<CustomResponse> response) {
                if(response.code() == 200){
                    Toast.makeText(FoodDetailActivity.this, "Đã cập nhật bình luận thành công!", Toast.LENGTH_SHORT).show();

                    ratingBarComment.setRating(ratingBarDialog.getRating());
                    contentComment.setText(content);

                    //Update information food
                    progressLayout.setVisibility(View.VISIBLE);
                    getFoodById(foodId);


                } else {
                    Toast.makeText(FoodDetailActivity.this, "Đã có lỗi hệ thống! Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CustomResponse> call, Throwable t) {
                Toast.makeText(FoodDetailActivity.this, "Đã có lỗi kết nối đến hệ thống!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createComment(String content, RotationRatingBar ratingBarDialog){
        Comment comment = new Comment(content, ratingBarDialog.getRating(), Common.CURRENT_USER.getId());
        FoodApiToken.apiService.createComment(Integer.parseInt(foodId), comment).enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if(response.code() == 201){
                    Toast.makeText(FoodDetailActivity.this, "Đã thêm bình luận thành công!", Toast.LENGTH_SHORT).show();

                    //Init data
                    getCommentUser();

                    //Show comment layout and hide rating button when comment completed!
                    user_comment_layout.setVisibility(View.VISIBLE);
                    rating_button.setVisibility(View.GONE);

                    //Update information food
                    progressLayout.setVisibility(View.VISIBLE);
                    getFoodById(foodId);

                } else {
                    Toast.makeText(FoodDetailActivity.this, "Lỗi hệ thống! Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Toast.makeText(FoodDetailActivity.this, "Đã xuất hiện lỗi hệ thống!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void deleteComment(){
        FoodApiToken.apiService.deleteComment(Integer.parseInt(foodId), commentUser.getId()).enqueue(new Callback<CustomResponse>() {
            @Override
            public void onResponse(Call<CustomResponse> call, Response<CustomResponse> response) {
                if(response.code() == 200){
                    CustomResponse responseData = response.body();
                    
                    if(responseData != null){
                        if(responseData.isTrue()){
                            Toast.makeText(FoodDetailActivity.this, "Đã xoá bình luận thành công!", Toast.LENGTH_SHORT).show();

                            //Remove layout comment
                            rating_button.setVisibility(View.VISIBLE);
                            user_comment_layout.setVisibility(View.GONE);

                            //Update information food
                            progressLayout.setVisibility(View.VISIBLE);
                            getFoodById(foodId);

                            commentUser = null;
                        }
                    }
                } else {
                    Toast.makeText(FoodDetailActivity.this, "Lỗi hệ thống! Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CustomResponse> call, Throwable t) {
                Toast.makeText(FoodDetailActivity.this, "Đã có lỗi kết nối tới hệ thống!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showDeleteDialogConfirm(){
        BasicDialog dialog = new BasicDialog();

        dialog.Builder(this)
                .setTitle("Xác nhận xoá bình luận?")
                .setLeftButtonText("Có")
                .setRightButtonText("Không")
                .setDialogBackgroundColor(getResources().getColor(R.color.white, null))
                .setLeftButtonColor(getResources().getColor(R.color.primaryColor, null))
                .setRightButtonColor(getResources().getColor(R.color.gray, null))
                .onButtonClick(new DialogButtonEvents() {
                    @Override
                    public void onLeftButtonClick() {
                        deleteComment();
                        dialog.dismiss();
                    }

                    @Override
                    public void onRightButtonClick() {
                        dialog.dismiss();
                    }
                })
                .show();
    }
    private void dataLoadMore() {
        if(!LAST_PAGE){
            getListComment(++CURRENT_PAGE);
        } else {
            progressBar.setVisibility(View.GONE);
            endOfListText.setVisibility(View.VISIBLE);
        }
    }
}