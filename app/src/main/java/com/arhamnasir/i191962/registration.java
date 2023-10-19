package com.arhamnasir.i191962;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class registration extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;

    private AutoCompleteTextView autoCompleteCountry;
    private AutoCompleteTextView autoCompleteCity;
    // Create lists to hold countries and cities
    private List<Country> countryList = new ArrayList<>();
    private List<City> cityList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        TextView loginTextView = findViewById(R.id.loginTextView);


        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();


        TextView signUpButton = findViewById(R.id.signUpButton);
        EditText signUpEmail = findViewById(R.id.signUpEmail);
        EditText signUpPassword = findViewById(R.id.signUpPassword);


        autoCompleteCountry = findViewById(R.id.auto_complete_country);
        autoCompleteCity = findViewById(R.id.auto_complete_city);

        // Populate the country and city lists with data
        populateCountryList();
        populateCityList();

        // Create and set adapters for AutoCompleteTextViews
        ArrayAdapter<Country> countryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, countryList);
        autoCompleteCountry.setAdapter(countryAdapter);

        ArrayAdapter<City> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cityList);
        autoCompleteCity.setAdapter(cityAdapter);

        // Set a listener to filter cities based on the selected country
        autoCompleteCountry.setOnItemClickListener((parent, view, position, id) -> {
            Country selectedCountry = (Country) parent.getItemAtPosition(position);
            filterCitiesByCountry(selectedCountry.getName());
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Log.d("myTag", "This is my message");

                mAuth.createUserWithEmailAndPassword(signUpEmail.getText().toString(), signUpPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d("myTag", "Successfull");

                                    Map<String, String> data = new HashMap<>();
// Add key-value pairs to the map
                                    data.put("email", signUpEmail.getText().toString());
                                    data.put("password", signUpPassword.getText().toString());

                                    CollectionReference usersCollection = mFirestore.collection("Users");

                                    usersCollection.add(data)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    // Document added successfully
                                                    String documentId = documentReference.getId();
                                                    Log.d("myTag", "Document added with ID: " + documentId);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Handle any errors that occur
                                                    Log.e("myTag", "Error adding document", e);
                                                    // You can display an error message or take appropriate action here.
                                                }
                                            });
                                    Toast.makeText(
                                            registration.this,
                                            "Successfully Created User",
                                            Toast.LENGTH_LONG).show();
                                    Toast.makeText(
                                            registration.this,
                                            mAuth.getCurrentUser().getUid(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            }
                        })

//                        .addOnCompleteListener(new onCompleteLitener<AuthResult>)
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(
//                                        MainActivity.this,
//                                        "Failed to create user",
//                                        Toast.LENGTH_LONG
//                                ).show();
//                            }
//                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("myTag", e.toString());

                                Toast.makeText(
                                        registration.this,
                                        "Failed to create user",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        });
            }
        });


        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(registration.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }


    //////////////////////////////////////////////Firebase///////////////////////////////////



    // Populate the list of countries with dummy data
    private void populateCountryList() {
        // Add placeholder countries
        countryList.add(new Country(1,"Country 1"));
        countryList.add(new Country(2,"Country 2"));
        countryList.add(new Country(3,"Country 3"));
        countryList.add(new Country(4,"Country 4"));
        // Add more countries as needed
    }

    // Populate the list of cities with dummy data
    private void populateCityList() {
        // Add placeholder cities
        cityList.add(new City(1,"City 1", 1)); // City 1 belongs to Country 1
        cityList.add(new City(2,"City 2", 1)); // City 2 belongs to Country 1
        cityList.add(new City(3,"City 3", 2)); // City 3 belongs to Country 2
        cityList.add(new City(4,"City 4", 2)); // City 4 belongs to Country 2
        // Add more cities as needed
    }


    private void filterCitiesByCountry(String selectedCountryName) {
        List<City> filteredCities = new ArrayList<>();

        // Get the ID of the selected country based on its name
        int selectedCountryId = getCountryIdByName(selectedCountryName);

        // Loop through the cityList and add cities that match the selected country
        for (City city : cityList) {
            if (city.getCountryId() == selectedCountryId) {
                filteredCities.add(city);
            }
        }

        // Create a new adapter with the filtered list of cities
        ArrayAdapter<City> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, filteredCities);
        autoCompleteCity.setAdapter(cityAdapter);
    }

    // Helper method to get the ID of the country by its name
    private int getCountryIdByName(String countryName) {
        for (Country country : countryList) {
            if (country.getName().equals(countryName)) {
                return country.getId();
            }
        }
        return -1; // Return -1 or handle not found case appropriately
    }
}
