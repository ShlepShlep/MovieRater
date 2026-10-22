package com.mycompany.movierater;

import java.util.List;

/**
 *
 * @author agnes
 */
public class MovieSearch {
    private final Movie[] movieDatabase;
    
    public MovieSearch(Movie[] movieDatabase) {
        this.movieDatabase = movieDatabase;
    }
    
    public List<Movie> search(String query, int maxResults) {
        query.trim().toLowerCase();
    }
    
}
