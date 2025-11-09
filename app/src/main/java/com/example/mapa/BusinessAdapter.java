package com.example.mapa;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.BusinessViewHolder> {

    private ArrayList<Business> businessList;

    public BusinessAdapter(ArrayList<Business> businessList) {
        this.businessList = businessList;
    }

    @NonNull
    @Override
    public BusinessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.business_item, parent, false);
        return new BusinessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusinessViewHolder holder, int position) {
        Business business = businessList.get(position);
        holder.businessName.setText(business.getName());
        holder.businessAddress.setText(business.getAddress());
        holder.businessService.setText(business.getService());

        double avgRating = business.getAverageRating();
        if (avgRating > 0) {
            holder.ratingBar.setRating((float) avgRating);
            holder.ratingBar.setVisibility(View.VISIBLE);
            holder.ratingTextView.setText(String.format("%.1f", avgRating));
            holder.ratingTextView.setVisibility(View.VISIBLE);
        } else {
            holder.ratingBar.setVisibility(View.GONE);
            holder.ratingTextView.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, BusinessDetailActivity.class);
                intent.putExtra("businessName", business.getName());
                intent.putExtra("businessAddress", business.getAddress());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return businessList.size();
    }

    public static class BusinessViewHolder extends RecyclerView.ViewHolder {
        public TextView businessName;
        public TextView businessAddress;
        public TextView businessService;
        public RatingBar ratingBar;
        public TextView ratingTextView;

        public BusinessViewHolder(@NonNull View itemView) {
            super(itemView);
            businessName = itemView.findViewById(R.id.business_name);
            businessAddress = itemView.findViewById(R.id.business_address);
            businessService = itemView.findViewById(R.id.business_service);
            ratingBar = itemView.findViewById(R.id.business_rating_bar);
            ratingTextView = itemView.findViewById(R.id.business_rating_text);
        }
    }
}