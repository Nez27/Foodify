package com.capstone.foodify.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.foodify.Common;
import com.capstone.foodify.Model.Comment;
import com.capstone.foodify.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> listComment;
    private Context context;

    public CommentAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Comment> listComment){
        this.listComment = listComment;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = listComment.get(position);

        holder.name.setText(comment.getUser().getFullName());
        holder.ratingScore.setRating(comment.getRating());
        holder.content.setText(comment.getContent());

        //Bind image avatar user
        if(comment.getUser().getImageUrl() != null){
            Picasso.get().load(comment.getUser().getImageUrl()).into(holder.profile_avatar);
        }
    }

    @Override
    public int getItemCount() {
        if(listComment != null)
            return listComment.size();
        return 0;
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{

        private final TextView name, content;
        private final ScaleRatingBar ratingScore;
        private final RoundedImageView profile_avatar;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.user_name_text_view);
            ratingScore = itemView.findViewById(R.id.rating_bar);
            content = itemView.findViewById(R.id.comment_user);

            profile_avatar = itemView.findViewById(R.id.profile_avatar);
        }
    }
}
