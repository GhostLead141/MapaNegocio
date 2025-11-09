package com.example.mapa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Business {
    private String id;
    private String name;
    private String address;
    private String service;
    private double averageRating;
    private List<Review> reviews;

    public Business(String name, String address, String service) {
        this.id = null; // Se asignará cuando se guarde en Firebase
        this.name = name;
        this.address = address;
        this.service = service;
        this.averageRating = 0.0;
        this.reviews = new ArrayList<>();
    }

    // Constructor para crear desde Firebase
    public Business(String id, String name, String address, String service, double averageRating, List<Review> reviews) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.service = service;
        this.averageRating = averageRating;
        this.reviews = reviews != null ? reviews : new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getService() {
        return service;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void addReview(Review review) {
        reviews.add(review);
        updateAverageRating();
    }

    private void updateAverageRating() {
        if (reviews.isEmpty()) {
            averageRating = 0.0;
            return;
        }
        double sum = 0.0;
        for (Review review : reviews) {
            sum += review.getRating();
        }
        averageRating = sum / reviews.size();
    }

    public static class Review {
        private float rating;
        private String comment;
        private String userEmail;

        public Review(float rating, String comment, String userEmail) {
            this.rating = rating;
            this.comment = comment;
            this.userEmail = userEmail;
        }

        public float getRating() {
            return rating;
        }

        public String getComment() {
            return comment;
        }

        public String getUserEmail() {
            return userEmail;
        }

        // Convertir Review a Map para Firestore
        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("rating", rating);
            map.put("comment", comment);
            map.put("userEmail", userEmail);
            return map;
        }

        // Crear Review desde Map de Firestore
        public static Review fromMap(Map<String, Object> map) {
            float rating = ((Number) map.get("rating")).floatValue();
            String comment = (String) map.get("comment");
            String userEmail = (String) map.get("userEmail");
            return new Review(rating, comment, userEmail);
        }
    }

    // Convertir Business a Map para Firestore
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("address", address);
        map.put("service", service);
        map.put("averageRating", averageRating);
        
        List<Map<String, Object>> reviewsList = new ArrayList<>();
        for (Review review : reviews) {
            reviewsList.add(review.toMap());
        }
        map.put("reviews", reviewsList);
        
        return map;
    }

    // Crear Business desde Map de Firestore
    public static Business fromMap(String id, Map<String, Object> map) {
        String name = (String) map.get("name");
        String address = (String) map.get("address");
        String service = (String) map.get("service");
        double averageRating = map.get("averageRating") != null ? 
            ((Number) map.get("averageRating")).doubleValue() : 0.0;
        
        List<Review> reviewsList = new ArrayList<>();
        if (map.get("reviews") != null) {
            List<Map<String, Object>> reviewsData = (List<Map<String, Object>>) map.get("reviews");
            for (Map<String, Object> reviewMap : reviewsData) {
                reviewsList.add(Review.fromMap(reviewMap));
            }
        }
        
        return new Business(id, name, address, service, averageRating, reviewsList);
    }
}