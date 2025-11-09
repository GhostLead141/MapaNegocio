package com.example.mapa;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

public class FirebaseHelper {
    private static final String TAG = "FirebaseHelper";
    private static final String COLLECTION_BUSINESSES = "businesses";
    
    private static FirebaseHelper instance;
    private FirebaseFirestore db;
    private CollectionReference businessesCollection;

    private FirebaseHelper() {
        db = FirebaseFirestore.getInstance();
        businessesCollection = db.collection(COLLECTION_BUSINESSES);
    }

    public static FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }

    // Agregar un negocio a Firebase
    public void addBusiness(Business business, OnCompleteListener<Void> listener) {
        Map<String, Object> businessMap = business.toMap();
        
        businessesCollection.add(businessMap)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    String businessId = documentReference.getId();
                    business.setId(businessId);
                    Log.d(TAG, "Negocio agregado con ID: " + businessId);
                    // Notificar éxito
                    if (listener != null) {
                        // Crear una tarea exitosa
                        Task<Void> successTask = documentReference.update("id", businessId);
                        successTask.addOnCompleteListener(listener);
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Error al agregar negocio", e);
                    // En caso de error, no llamamos al listener para evitar complicaciones
                }
            });
    }

    // Obtener todos los negocios
    public void getAllBusinesses(OnBusinessesLoadedListener listener) {
        businessesCollection.get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        ArrayList<Business> businesses = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            Map<String, Object> data = document.getData();
                            Business business = Business.fromMap(id, data);
                            businesses.add(business);
                        }
                        Log.d(TAG, "Negocios cargados: " + businesses.size());
                        if (listener != null) {
                            listener.onBusinessesLoaded(businesses);
                        }
                    } else {
                        Log.e(TAG, "Error al cargar negocios", task.getException());
                        if (listener != null) {
                            listener.onBusinessesLoaded(new ArrayList<>());
                        }
                    }
                }
            });
    }

    // Actualizar un negocio (para agregar reseñas)
    public void updateBusiness(Business business, OnCompleteListener<Void> listener) {
        if (business.getId() == null) {
            Log.e(TAG, "El negocio no tiene ID");
            return;
        }

        DocumentReference docRef = businessesCollection.document(business.getId());
        Map<String, Object> businessMap = business.toMap();
        
        docRef.update(businessMap)
            .addOnCompleteListener(listener)
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Error al actualizar negocio", e);
                }
            });
    }

    // Obtener un negocio por ID
    public void getBusinessById(String businessId, OnBusinessLoadedListener listener) {
        DocumentReference docRef = businessesCollection.document(businessId);
        docRef.get()
            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String id = document.getId();
                            Map<String, Object> data = document.getData();
                            Business business = Business.fromMap(id, data);
                            if (listener != null) {
                                listener.onBusinessLoaded(business);
                            }
                        } else {
                            Log.d(TAG, "No se encontró el negocio");
                            if (listener != null) {
                                listener.onBusinessLoaded(null);
                            }
                        }
                    } else {
                        Log.e(TAG, "Error al obtener negocio", task.getException());
                        if (listener != null) {
                            listener.onBusinessLoaded(null);
                        }
                    }
                }
            });
    }

    // Interfaces para callbacks
    public interface OnBusinessesLoadedListener {
        void onBusinessesLoaded(ArrayList<Business> businesses);
    }

    public interface OnBusinessLoadedListener {
        void onBusinessLoaded(Business business);
    }
}

