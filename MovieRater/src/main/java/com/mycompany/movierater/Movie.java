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
    
    public String returnTitle() {
        return title;
    }
    
    public String returnImdbID() {
        return imdbID;
    }
    
    public int returnYear() {
        return year;
    }
    
    public double returnRating() {
        return rating;
    }
    
    public String returnType() {
        return type;
    }
    
    public String[] returnGenres() {
        if (genresString == null || genresString.isEmpty()) {
            return new String[0];
        }
        return genresString.split(",");
    }
}
