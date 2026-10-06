package com.mycompany.movierater;

import java.io.InputStream;
import java.io.IOException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author agnesaaaa
 */
public class MovieRater {

    public static void main(String[] args) {
        
        MovieRater object = new MovieRater();
        Movie[] movies = object.loadMovies();
        
        //check how many titles are loaded, supposed to be 2000
        System.out.println("Loaded: " + movies.length + " movies/tv-shows");

        //print out the first few to check if they look okay
        for (int i = 0; i < 3; i++) {
            Movie m = movies[i];
            System.out.println(m.returnTitle() + " (" + m.returnYear() + ") | "
                    + m.returnType() + " | rating " + m.returnRating() + " | "
                    + m.returnImdbID() + " | genres: " + String.join(", ", m.returnGenres()));
        }

        // 3. Are movies and series both there?
        int movieCount = 0, seriesCount = 0, missingTitles = 0;
        for (Movie m : movies) {
            if ("movie".equals(m.returnType())) movieCount++;
            if ("tvSeries".equals(m.returnType())) seriesCount++;
            if (m.returnTitle() == null) missingTitles++;
        }
        System.out.println("Movies: " + movieCount + ", Series: " + seriesCount);
        System.out.println("Entries with no title: " + missingTitles);
        
    }
    
    public Movie[] loadMovies() {
        try (InputStream input = MovieRater.class.getResourceAsStream("/titles.json")) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(input, Movie[].class);
        } catch (IOException error) {
            System.out.println("Could not load movies list, check for errors");
            return new Movie[0];
        }
    }
}
