package com.mycompany.movierater;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MovieLibrary {

    private final ArrayList<Movie> myMovies = new ArrayList<>(); //List of all movies added to one of the three lists
    private final Movie[] movieDatabase;
    private final File saveFile;
    private final ObjectMapper mapper = new ObjectMapper();

    public MovieLibrary(Movie[] movieDatabase, File saveFile) {
        this.movieDatabase = movieDatabase;
        this.saveFile = saveFile;
    }

    public static File defaultSaveFile() {
        File defaultFile = new File(System.getProperty("user.home"), "savedMovies.json"); //creates path as a file object
        return defaultFile;
    }

    public ArrayList<Movie> getMovies() {
        return myMovies;
    }

    public void addMovie(Movie movie) {
        if (!myMovies.contains(movie)) {
            myMovies.add(movie);
        }
    }

    /**
     * This method tries to save the movies added to the myMovies (by going through the list with a loop)
     * SavedMovie list contains only the needed info, so every object Movie is converted to SavedMovie
     * Then a JSPN file is created (if there was no file beforehand) or overwritten
     * 
     * @return number of movies that were saved
     * @throws IOException
     */
    public int save() throws IOException {
        ArrayList<SavedMovie> out = new ArrayList<>();//new list with objects that contain only the info that needs to be saved
        for (Movie m : myMovies) {
            out.add(new SavedMovie(m));
        }
        mapper.writerWithDefaultPrettyPrinter().writeValue(saveFile, out); //converts list to json, creates/overrides the file
        return out.size();
    }

    /**
     * This method converts the JSON file back to the SavedMovie object array
     * Then goes through every movie it converted and makes it a Movie object, as it holds more information
     * and adds it back into myMovies list
     * 
     * @return number of movies that were loaded from the saved file
     * @throws IOException 
     */
    public int load() throws IOException {
        if (!saveFile.exists()) { //if file doesnt exist
            return 0;
        }

        SavedMovie[] saved = mapper.readValue(saveFile, SavedMovie[].class);//converts json to SavedMovie object
        int loaded = 0;
        for (SavedMovie s : saved) {//goes through every save movie and converts it to a movie object
            Movie movie = findByImdbID(s.getImdbID());
            if (movie == null || myMovies.contains(movie)) {
                continue; //not in the list any more or a duplicate
            }
            movie.setStatus(s.getStatus());
            movie.setUserRating(s.getUserRating());
            if (s.getUserReview().isEmpty()) {
                movie.setUserReview("");
            }
            else {
                movie.setUserReview(s.getUserReview());
            }
            myMovies.add(movie);
            loaded++;
        }
        return loaded;
    }
    
    public void removeMovie(Movie movie) {
        myMovies.remove(movie);
        movie.setUserRating(0);
        movie.setUserReview("");
        movie.setStatus(Movie.WANT_TO_WATCH);
        
    }

    private Movie findByImdbID(String imdbID) {
        for (Movie m : movieDatabase) {
            if (m.getImdbID().equals(imdbID)) {
                return m;
            }
        }
        return null;
    }
}
