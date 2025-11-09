package com.example.mapa;

import android.util.Log;

import java.util.ArrayList;

public class BusinessData {

    private static final String TAG = "BusinessData";
    private static final BusinessData instance = new BusinessData();
    private final ArrayList<Business> businessList = new ArrayList<>();
    private FirebaseHelper firebaseHelper;
    private boolean isLoaded = false;

    private BusinessData() {
        firebaseHelper = FirebaseHelper.getInstance();
    }

    public static BusinessData getInstance() {
        return instance;
    }

    public ArrayList<Business> getBusinessList() {
        return businessList;
    }

    public void addBusiness(Business business) {
        businessList.add(business);
        // Guardar en Firebase
        firebaseHelper.addBusiness(business, task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Negocio guardado en Firebase exitosamente");
            } else {
                Log.e(TAG, "Error al guardar negocio en Firebase", task.getException());
            }
        });
    }

    public void updateBusiness(Business business) {
        // Actualizar en la lista local
        for (int i = 0; i < businessList.size(); i++) {
            if (businessList.get(i).getId() != null && 
                businessList.get(i).getId().equals(business.getId())) {
                businessList.set(i, business);
                break;
            }
        }
        // Actualizar en Firebase
        firebaseHelper.updateBusiness(business, task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Negocio actualizado en Firebase exitosamente");
            } else {
                Log.e(TAG, "Error al actualizar negocio en Firebase", task.getException());
            }
        });
    }

    public void loadBusinessesFromFirebase(FirebaseHelper.OnBusinessesLoadedListener listener) {
        if (isLoaded) {
            // Ya están cargados, solo notificar
            if (listener != null) {
                listener.onBusinessesLoaded(businessList);
            }
            return;
        }

        firebaseHelper.getAllBusinesses(new FirebaseHelper.OnBusinessesLoadedListener() {
            @Override
            public void onBusinessesLoaded(ArrayList<Business> businesses) {
                businessList.clear();
                businessList.addAll(businesses);
                isLoaded = true;
                Log.d(TAG, "Negocios cargados desde Firebase: " + businessList.size());
                if (listener != null) {
                    listener.onBusinessesLoaded(businessList);
                }
            }
        });
    }

    public void refreshBusinesses(FirebaseHelper.OnBusinessesLoadedListener listener) {
        isLoaded = false;
        loadBusinessesFromFirebase(listener);
    }

    public Business findBusinessById(String id) {
        for (Business business : businessList) {
            if (business.getId() != null && business.getId().equals(id)) {
                return business;
            }
        }
        return null;
    }

    public Business findBusinessByNameAndAddress(String name, String address) {
        for (Business business : businessList) {
            if (business.getName().equals(name) && business.getAddress().equals(address)) {
                return business;
            }
        }
        return null;
    }
}