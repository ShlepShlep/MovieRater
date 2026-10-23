package com.mycompany.movierater;

public class SavedMovie {
    private String imdbId;
    private String status;
    private String review;
    private int rating;
    
    public SavedMovie() {
        
    }
    
    public SavedMovie(Movie movie) {
         this.imdbId = movie.getImdbID();
         this.status = movie.getStatus();
         this.review = movie.getUserReview();
         this.rating = movie.getUserRating();
    }
    
    public String getImdbID(){
        return imdbId;
    }
    public void setImdbID(String imdbId) {
        this.imdbId = imdbId;
    }
    
    public String getStatus(){
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getUserReview(){
        return review;
    }
    public void setUserReview(String review) {
        this.review = review;
    }
    
    public int getUserRating(){
        return rating;
    }
    public void setUserRating(int rating) {
        this.rating = rating;
    }
}
