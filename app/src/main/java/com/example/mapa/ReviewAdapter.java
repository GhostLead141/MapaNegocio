package com.example.mapa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Business.Review> reviews;

    public ReviewAdapter(List<Business.Review> reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Business.Review review = reviews.get(position);
        holder.userEmailTextView.setText(review.getUserEmail());
        holder.ratingBar.setRating(review.getRating());
        
        if (review.getComment() != null && !review.getComment().isEmpty()) {
            holder.commentTextView.setText(review.getComment());
            holder.commentTextView.setVisibility(View.VISIBLE);
        } else {
            holder.commentTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return reviews != null ? reviews.size() : 0;
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView userEmailTextView;
        RatingBar ratingBar;
        TextView commentTextView;

        ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            userEmailTextView = itemView.findViewById(R.id.review_user_email);
            ratingBar = itemView.findViewById(R.id.review_rating_bar);
            commentTextView = itemView.findViewById(R.id.review_comment);
        }
    }
}

