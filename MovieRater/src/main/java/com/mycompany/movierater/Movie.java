package com.mycompany.movierater;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true) //ignores "votes" because we dont use it
public class Movie {
    @JsonProperty("title") private String title; //looks for the specific key value in json file and then stores it to the variable
    @JsonProperty("imdbId") private String imdbID;
    @JsonProperty("type") private String type;
    @JsonProperty("year") private int year;
    @JsonProperty("rating") private double rating;
    @JsonProperty("genres") String genresString;
    
    public Movie() {
        
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getImdbID() {
        return imdbID;
    }
    
    public int getYear() {
        return year;
    }
    
    public double getRating() {
        return rating;
    }
    
    public String getType() {
        return type;
    }
    
    public String[] getGenres() {
        if (genresString == null || genresString.isEmpty()) {
            return new String[0];
        }
        return genresString.split(",");//splits the string into an array of genres
    }
    
    @Override //using this because stupid JList prints bullshit otherwise, this overrites the toString function, and if we call our object in a print or wtv we need a string, this will get called
    public String toString() {
        return getTitle();
    }
}
