package com.mycompany.movierater;

import java.io.InputStream;
import java.io.IOException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLaf;
import java.util.Collections;
import java.awt.EventQueue;

public class MovieRater {

    public static void main(String[] args) {
        FlatLaf.setGlobalExtraDefaults(Collections.singletonMap( "@accentColor", "#FCBE11" ) ); //add yellow accent color to the UI
        FlatDarculaLaf.setup(); //make everything dark mode
        
        MovieRater object = new MovieRater();
        Movie[] movieDatabase = object.loadMovies(); //read the json file and convert it to a movie list
        
        EventQueue.invokeLater(() -> new AppUI(movieDatabase).setVisible(true)); //make the program appear and have it have the movie database   
    }
    /**
     * This method tries to convert a JSON file containing 2000 movies to a custom object Movie
     * 
     * @return list of basic 2000 movies/TV-shows as an array of Movie objects
     */
    public Movie[] loadMovies() {
        //tries to do smt and if an error occurs then goes to the catch part
        try (InputStream input = MovieRater.class.getResourceAsStream("/titles.json")){//reads the json file, converts it to a stream)
            ObjectMapper objectMapper = new ObjectMapper(); //creates a new object mapper which can convert json to objects
            return objectMapper.readValue(input, Movie[].class); //returns the converted list
        } catch (IOException e) {
            System.out.println("Could not load movies list, check for errors");
            return new Movie[0]; //returns empty list if there is a problem
        }
    }
}
