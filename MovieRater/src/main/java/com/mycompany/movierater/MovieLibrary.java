package com.mycompany.movierater;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MovieLibrary {

    private final ArrayList<Movie> myMovies = new ArrayList<>();
    private final Movie[] movieDatabase;
    private final File saveFile;
    private final ObjectMapper mapper = new ObjectMapper();

    public MovieLibrary(Movie[] movieDatabase, File saveFile) {
        this.movieDatabase = movieDatabase;
        this.saveFile = saveFile;
    }

    public static File defaultSaveFile() {
        File defaultFile = new File(System.getProperty("user.home"), "savedMovies.json");
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

    public int save() throws IOException {
        ArrayList<SavedMovie> out = new ArrayList<>();
        for (Movie m : myMovies) {
            out.add(new SavedMovie(m));
        }
        mapper.writerWithDefaultPrettyPrinter().writeValue(saveFile, out);
        return out.size();
    }

    public int load() throws IOException {
        if (!saveFile.exists()) {
            return 0;
        }

        SavedMovie[] saved = mapper.readValue(saveFile, SavedMovie[].class);
        int restored = 0;
        for (SavedMovie s : saved) {
            Movie movie = findByImdbID(s.getImdbID());
            if (movie == null || myMovies.contains(movie)) {
                continue; // not in the catalog any more, or a duplicate entry
            }
            movie.setStatus(s.getStatus());
            movie.setUserRating(s.getRating());
            movie.setUserReview(s.getReview() == null ? "" : s.getReview());
            myMovies.add(movie);
            restored++;
        }
        return restored;
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
