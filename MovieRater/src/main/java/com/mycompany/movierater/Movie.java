package com.mycompany.movierater;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.swing.ImageIcon;


@JsonIgnoreProperties(ignoreUnknown = true) //ignores "votes" because we dont use it
public class Movie {
    @JsonProperty("title") private String title; //looks for the specific key value in json file and then stores it to the variable
    @JsonProperty("imdbId") private String imdbID;
    @JsonProperty("type") private String type;
    @JsonProperty("year") private int year;
    @JsonProperty("rating") private double rating;
    @JsonProperty("genres") String genresString;
    @JsonIgnore ImageIcon poster; //ignores this value during serialization due to @JsonIgnore
    
    public ImageIcon getPoster() {
        return poster;
    }
    
    public void setPoster(ImageIcon poster){
        this.poster = poster;
    }
    
    
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
    
    @Override //this overwrites the default toString function, so the object can be printed
    public String toString() {
        return getTitle();
    }
        
    @JsonIgnore
    private int userRating;
    @JsonIgnore
    private String userReview = "";

    public int getUserRating() {
        return userRating;
    }

    public void setUserRating(int userRating) {
        this.userRating = userRating;
    }

    public String getUserReview() {
        return userReview;
    }

    public void setUserReview(String userReview) {
        this.userReview = userReview;
    }
    
    public static final String WATCHED = "Watched";
    public static final String WATCHING = "Watching";
    public static final String WANT_TO_WATCH = "Want to watch";

    @JsonIgnore
    private String status = WANT_TO_WATCH;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

