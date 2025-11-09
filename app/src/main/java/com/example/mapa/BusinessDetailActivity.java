package com.example.mapa;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BusinessDetailActivity extends AppCompatActivity {

    private Business business;
    private TextView businessNameTextView;
    private TextView businessAddressTextView;
    private TextView businessServiceTextView;
    private TextView averageRatingTextView;
    private RatingBar ratingBar;
    private EditText commentEditText;
    private Button submitReviewButton;
    private Button viewMapButton;
    private RecyclerView reviewsRecyclerView;
    private ReviewAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_detail);

        String businessName = getIntent().getStringExtra("businessName");
        String businessAddress = getIntent().getStringExtra("businessAddress");

        // Buscar el negocio en la lista
        business = findBusiness(businessName, businessAddress);

        if (business == null) {
            Toast.makeText(this, "Negocio no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupViews();
        setupListeners();
        loadReviews();
    }

    private Business findBusiness(String name, String address) {
        return BusinessData.getInstance().findBusinessByNameAndAddress(name, address);
    }

    private void initializeViews() {
        businessNameTextView = findViewById(R.id.business_name_detail);
        businessAddressTextView = findViewById(R.id.business_address_detail);
        businessServiceTextView = findViewById(R.id.business_service_detail);
        averageRatingTextView = findViewById(R.id.average_rating);
        ratingBar = findViewById(R.id.rating_bar);
        commentEditText = findViewById(R.id.comment_edit_text);
        submitReviewButton = findViewById(R.id.submit_review_button);
        viewMapButton = findViewById(R.id.view_map_button);
        reviewsRecyclerView = findViewById(R.id.reviews_recycler_view);
    }

    private void setupViews() {
        businessNameTextView.setText(business.getName());
        businessAddressTextView.setText(business.getAddress());
        businessServiceTextView.setText(business.getService());

        double avgRating = business.getAverageRating();
        if (avgRating > 0) {
            averageRatingTextView.setText(String.format("Valoración: %.1f ⭐", avgRating));
        } else {
            averageRatingTextView.setText("Sin valoraciones aún");
        }

        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupListeners() {
        submitReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReview();
            }
        });

        viewMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });
    }

    private void submitReview() {
        float rating = ratingBar.getRating();
        String comment = commentEditText.getText().toString().trim();

        if (rating == 0) {
            Toast.makeText(this, "Por favor, selecciona una valoración", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener email del usuario autenticado
        String userEmail = "Usuario Anónimo";
        com.google.firebase.auth.FirebaseUser currentUser = 
            com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getEmail() != null) {
            userEmail = currentUser.getEmail();
        } else {
            // Fallback a SharedPreferences si no hay usuario de Firebase
            SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
            userEmail = prefs.getString("user_email", "Usuario Anónimo");
        }

        Business.Review review = new Business.Review(rating, comment, userEmail);
        business.addReview(review);

        // Actualizar en Firebase a través de BusinessData
        BusinessData.getInstance().updateBusiness(business);

        // Actualizar la vista
        double avgRating = business.getAverageRating();
        averageRatingTextView.setText(String.format("Valoración: %.1f ⭐", avgRating));
        ratingBar.setRating(0);
        commentEditText.setText("");

        loadReviews();
        Toast.makeText(this, "Reseña agregada exitosamente", Toast.LENGTH_SHORT).show();
    }

    private void openMap() {
        // Abrir MapActivity dentro de la aplicación para ver el mapa y obtener indicaciones
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("businessName", business.getName());
        intent.putExtra("businessAddress", business.getAddress());
        startActivity(intent);
    }

    private void loadReviews() {
        List<Business.Review> reviews = business.getReviews();
        reviewAdapter = new ReviewAdapter(reviews);
        reviewsRecyclerView.setAdapter(reviewAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar en caso de que haya cambios
        if (business != null) {
            double avgRating = business.getAverageRating();
            if (avgRating > 0) {
                averageRatingTextView.setText(String.format("Valoración: %.1f ⭐", avgRating));
            }
            loadReviews();
        }
    }
}

