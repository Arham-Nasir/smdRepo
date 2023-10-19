package com.arhamnasir.i191962;

//// Country.java
//public class Country {
//    private String name;
//
//    public Country(String name) {
//        this.name = name;
//    }
//
//    public String getName() {
//        return name;
//    }
//}


// Country.java
public class Country {
    private int id;
    private String name;

    public Country(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name; // This will display the country name in the AutoCompleteTextView
    }
}