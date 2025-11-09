package com.example.mapa;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddBusinessActivity extends AppCompatActivity {

    private EditText businessNameEditText;
    private EditText businessAddressEditText;
    private Spinner serviceSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_business);

        businessNameEditText = findViewById(R.id.business_name);
        businessAddressEditText = findViewById(R.id.business_address);
        serviceSpinner = findViewById(R.id.service_spinner);

        // Configurar el Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.services_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serviceSpinner.setAdapter(adapter);

        Button addBusinessButton = findViewById(R.id.add_business_button);
        addBusinessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = businessNameEditText.getText().toString();
                String address = businessAddressEditText.getText().toString();
                String service = serviceSpinner.getSelectedItem().toString();

                if (!name.isEmpty() && !address.isEmpty()) {
                    Business newBusiness = new Business(name, address, service);
                    BusinessData.getInstance().addBusiness(newBusiness);
                    Toast.makeText(AddBusinessActivity.this, "Negocio agregado: " + name, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddBusinessActivity.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}