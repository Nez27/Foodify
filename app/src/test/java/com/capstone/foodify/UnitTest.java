package com.capstone.foodify;

import org.junit.Test;

import static org.junit.Assert.*;

import com.capstone.foodify.API.FoodApi;
import com.capstone.foodify.Model.Address;
import com.capstone.foodify.Model.Comment;
import com.capstone.foodify.Model.DistrictWardResponse;
import com.capstone.foodify.Model.Food;
import com.capstone.foodify.Model.Response.Categories;
import com.capstone.foodify.Model.Response.Comments;
import com.capstone.foodify.Model.Response.CustomResponse;
import com.capstone.foodify.Model.Response.Foods;
import com.capstone.foodify.Model.Response.Shops;
import com.capstone.foodify.Model.Shop;
import com.capstone.foodify.Model.Slider;
import com.capstone.foodify.Model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Response;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UnitTest {
    @Test
    public void testGetDistrict() {
        try {
            Response<List<DistrictWardResponse>> response =  FoodApi.apiService.districtResponse().execute();

            List<DistrictWardResponse> listDistrict = response.body();


            assertTrue(response.code() == 200 && Objects.requireNonNull(listDistrict).size() > 0);
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void testGetWard() {
        try {
            Response<List<DistrictWardResponse>> response =  FoodApi.apiService.wardResponse(1).execute();

            List<DistrictWardResponse> listWard = response.body();

            assertTrue(response.code() == 200 && Objects.requireNonNull(listWard).size() > 0);

        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void testGetFoodRecommend() {
        try {
            Response<Foods> response =  FoodApi.apiService.recommendFood().execute();

            Foods listFood = response.body();

            assertTrue(response.code() == 200 && Objects.requireNonNull(listFood).getProducts().size() > 0);

        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void testGetFoodRecent() {
        try {
            Response<Foods> response =  FoodApi.apiService.recentFood().execute();

            Foods listFood = response.body();

            assertTrue(response.code() == 200 && Objects.requireNonNull(listFood).getProducts().size() > 0);

        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void testGetAllShop() {
        try {
            Response<Shops> response =  FoodApi.apiService.allShops().execute();

            Shops listFood = response.body();

            assertTrue(response.code() == 200 && Objects.requireNonNull(listFood).getShops().size() > 0);

        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void testSearchFoodByName() {
        try {
            Response<Foods> response =  FoodApi.apiService.searchFoodByName("a", 0, 8, "id", "asc").execute();

            Foods listFood = response.body();

            assertTrue(response.code() == 200 && Objects.requireNonNull(listFood).getProducts().size() > 0);

        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void testGetListFood() {
        try {
            Response<Foods> response =  FoodApi.apiService.listFood(0, 8, "id", "asc").execute();

            Foods listFood = response.body();

            assertTrue(response.code() == 200 && Objects.requireNonNull(listFood).getProducts().size() > 0);

        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void testGetListCategory() {
        try {
            Response<Categories> response =  FoodApi.apiService.listCategory().execute();

            Categories listFood = response.body();

            assertTrue(response.code() == 200 && Objects.requireNonNull(listFood).getCategories().size() > 0);

        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void testGetListFoodByCategory() {

        List<Integer> statusCategoryChecked = new ArrayList<>();
        statusCategoryChecked.add(1);
        statusCategoryChecked.add(2);

        try {
            Response<Foods> response =  FoodApi.apiService.listFoodByCategory(statusCategoryChecked, 0, 8, "id", "asc").execute();

            Foods listFood = response.body();

            assertTrue(response.code() == 200 && Objects.requireNonNull(listFood).getProducts().size() > 0);

        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void testGetListFoodByCategoryAndName() {

        List<Integer> statusCategoryChecked = new ArrayList<>();
        statusCategoryChecked.add(1);
        statusCategoryChecked.add(2);

        try {
            Response<Foods> response =  FoodApi.apiService.listFoodByCategoriesAndName(statusCategoryChecked, "a",0, 8, "id", "asc").execute();

            Foods listFood = response.body();

            assertTrue(response.code() == 200 && Objects.requireNonNull(listFood).getProducts().size() > 0);

        } catch (IOException e) {
            fail();
        }
    }


    @Test
    public void getDetailFood() {

        try {
            Response<Food> response =  FoodApi.apiService.detailFood("10").execute();

            Food food = null;

            food = response.body();

            assertTrue(response.code() == 200 && food != null);

        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void getDetailShop() {

        try {
            Response<Shop> response =  FoodApi.apiService.getShopById(1).execute();

            Shop shop = null;

            shop = response.body();

            assertTrue(response.code() == 200 && shop != null);

        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void testGetListFoodByShopId() {


        try {
            Response<Foods> response =  FoodApi.apiService.listFoodByShopId(1, 0, 8, "name", "asc").execute();

            Foods listFood = response.body();

            assertTrue(response.code() == 200 && Objects.requireNonNull(listFood).getProducts().size() > 0);

        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void testGetListSlider() {


        try {
            Response<List<Slider>> response =  FoodApi.apiService.listSlider().execute();

            List<Slider> listSlider = response.body();

            assertTrue(response.code() == 200 && Objects.requireNonNull(listSlider).size() > 0);

        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void checkPhoneExist() {
        User user = new User(new Address(), "", "", "", "", "0987654321");

        try {
            Response<CustomResponse> response =  FoodApi.apiService.checkEmailOrPhoneExist(user).execute();

            CustomResponse customResponse = response.body();

            assertTrue(response.code() == 200 && Objects.requireNonNull(customResponse).isTrue());

        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void getCommentByProductId() {
        try {
            Response<Comments> response =  FoodApi.apiService.getCommentByProductId(13, 0, 8, "id", "asc").execute();

            Comments listComment = response.body();

            assertTrue(response.code() == 200 && Objects.requireNonNull(listComment).getComments().size() > 0);

        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void getUserComment() {
        try {
            Response<Comment> response =  FoodApi.apiService.getUserComment(13, 17).execute();

            Comment comment = response.body();

            assertTrue(response.code() == 200 && comment != null);

        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void getShopById() {

        try {
            Response<CustomResponse> response =  FoodApi.apiService.checkUserBuyProduct(13, 17).execute();

            CustomResponse customResponse = response.body();

            assertTrue(response.code() == 200 && Objects.requireNonNull(customResponse).isTrue());

        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void checkIdentifiedCode() {

        try {
            Response<CustomResponse> response =  FoodApi.apiService.checkIdentifiedCode("0987654321").execute();

            CustomResponse customResponse = response.body();

            assertTrue(response.code() == 200 && Objects.requireNonNull(customResponse).isTrue());

        } catch (IOException e) {
            fail();
        }
    }
}