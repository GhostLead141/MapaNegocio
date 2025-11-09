package com.example.mapa;

import android.os.Bundle;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchBusinessActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BusinessAdapter adapter;
    private SearchView searchView;
    private ArrayList<Business> allBusinesses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_business);

        recyclerView = findViewById(R.id.recycler_view);
        searchView = findViewById(R.id.search_view);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Cargar negocios desde Firebase
        loadBusinesses();

        // Configurar funcionalidad de búsqueda
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterBusinesses(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterBusinesses(newText);
                return false;
            }
        });
    }

    private void filterBusinesses(String query) {
        ArrayList<Business> filteredList = new ArrayList<>();
        
        if (query == null || query.isEmpty()) {
            filteredList.addAll(allBusinesses);
        } else {
            String lowerQuery = query.toLowerCase();
            for (Business business : allBusinesses) {
                if (business.getName().toLowerCase().contains(lowerQuery) ||
                    business.getAddress().toLowerCase().contains(lowerQuery) ||
                    business.getService().toLowerCase().contains(lowerQuery)) {
                    filteredList.add(business);
                }
            }
        }
        
        adapter = new BusinessAdapter(filteredList);
        recyclerView.setAdapter(adapter);
    }

    private void loadBusinesses() {
        BusinessData.getInstance().loadBusinessesFromFirebase(new FirebaseHelper.OnBusinessesLoadedListener() {
            @Override
            public void onBusinessesLoaded(ArrayList<Business> businesses) {
                allBusinesses = businesses;
                adapter = new BusinessAdapter(allBusinesses);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar negocios desde Firebase
        BusinessData.getInstance().refreshBusinesses(new FirebaseHelper.OnBusinessesLoadedListener() {
            @Override
            public void onBusinessesLoaded(ArrayList<Business> businesses) {
                allBusinesses = businesses;
                adapter = new BusinessAdapter(allBusinesses);
                recyclerView.setAdapter(adapter);
            }
        });
    }
}