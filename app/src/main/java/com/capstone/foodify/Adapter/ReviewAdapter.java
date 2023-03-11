package com.capstone.foodify.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.Model.Review.Review;
import com.capstone.foodify.R;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> listReview;
    private Context context;

    public ReviewAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Review> listReview){
        this.listReview = listReview;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = listReview.get(position);

        holder.name.setText(review.getName());
        holder.ratingScore.setRating(review.getRatingScore());
        holder.content.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        if(listReview != null)
            return listReview.size();
        return 0;
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder{

        private TextView name, content;
        private ScaleRatingBar ratingScore;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.user_name_text_view);
            ratingScore = itemView.findViewById(R.id.rating_bar);
            content = itemView.findViewById(R.id.comment_user);
        }
    }
}
