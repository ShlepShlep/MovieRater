package com.mycompany.movierater;

import java.io.InputStream;
import java.io.IOException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MovieRater {

    public static void main(String[] args) {
        
        MovieRater object = new MovieRater();
        Movie[] movieDatabase = object.loadMovies(); //read the json file and convert it to a movie list
        java.awt.EventQueue.invokeLater(() -> new AppUI(movieDatabase).setVisible(true)); //make the program appear and have it have the movie database
        
        //check how many titles are loaded, supposed to be 2000
        System.out.println("Loaded: " + movieDatabase.length + " movies/tv-shows");

        //print out the first few to check if it works
        for (int i = 0; i < 3; i++) {
            Movie m = movieDatabase[i];
            System.out.println(m.getTitle() + " (" + m.getYear() + ") | "
                    + m.getType() + " | rating " + m.getRating() + " | "
                    + m.getImdbID() + " | genres: " + String.join(", ", m.getGenres()));
        }    
    }
    
    public Movie[] loadMovies() {
        //tries to do smt and if an error occurs then goes to the catch part
        try (InputStream input = MovieRater.class.getResourceAsStream("/titles.json")){//reads the json file, converts it to a stream)
            ObjectMapper objectMapper = new ObjectMapper(); //creates a new object mapper which can convert json to objects
            return objectMapper.readValue(input, Movie[].class); //returns the converted list
        } catch (IOException error) {//works if we get an error
            System.out.println("Could not load movies list, check for errors");
            return new Movie[0]; //returns empty list if there is a problem
        }
        
    }
    
}
