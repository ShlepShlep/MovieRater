/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.movierater;

/**
 *
 * @author agnes
 */
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
    
    public String getReview(){
        return review;
    }
    public void setReview(String review) {
        this.review = review;
    }
    
    public int getRating(){
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }
}
