package com.arhamnasir.i191962;
//
//// City.java
//public class City {
//    private String name;
//
//    public City(String name) {
//        this.name = name;
//    }
//
//    public String getName() {
//        return name;
//    }
//}
// City.java
//public class City {
//    private String name;
//    private String countryName; // Reference to the country it belongs to
//
//    public City(String name, String countryName) {
//        this.name = name;
//        this.countryName = countryName;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public String getCountryName() {
//        return countryName;
//    }
//}

// City.java
public class City {
    private int id;
    private String name;
    private int countryId;

    public City(int id, String name, int countryId) {
        this.id = id;
        this.name = name;
        this.countryId = countryId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCountryId() {
        return countryId;
    }

    @Override
    public String toString() {
        return name; // This will display the city name in the AutoCompleteTextView
    }
}