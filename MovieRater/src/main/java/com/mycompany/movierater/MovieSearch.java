package com.mycompany.movierater;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class finds movie titles that match a search text, including ones with small
 * typos. Used for the autocomplete suggestions in the search bar.
 * 
 * @author amelia
 */
public class MovieSearch {
    private final Movie[] movieDatabase;
    
    public MovieSearch(Movie[] movieDatabase) {
        this.movieDatabase = movieDatabase;
    }
    
    /**
     * This method returns the best matching movies for a search text, best match first.
     * Equally good matches are ordered by their rating, highest first.
     *
     * @param query      the text typed by the user (at least 2 characters, otherwise nothing is returned)
     * @param maxResults the maximum number of movies to return
     * @return the matching movies, or an empty list if there are none
     */
    public List<Movie> search(String query, int maxResults) {
        query = query.trim().toLowerCase();
        
        if (query.length() < 2) {
            return new ArrayList<>();
        }
        
                //score every title, lower score = better match, -1 = no match
        HashMap<Movie, Integer> scores = new HashMap<>();
        ArrayList<Movie> matches = new ArrayList<>();
        for (Movie m : movieDatabase) {
            int score = matchScore(m.getTitle().toLowerCase(), query);
            if (score >= 0) {
                scores.put(m, score);
                matches.add(m);
            }
        }

        //best score first, and if equal then the higher rated movie first
        matches.sort((a, b) -> {
            int byScore = Integer.compare(scores.get(a), scores.get(b));
            return byScore != 0 ? byScore : Double.compare(b.getRating(), a.getRating());
        });
        
        List<Movie> results = new ArrayList<>();
        for (int i = 0; i < Math.min(maxResults, matches.size()); i++) {
            results.add(matches.get(i));
        }
        return results;
    } 
    
    /**
     * This method assigns a score to how well a title matches the query, where a lower score is better.
     * 0 means the title starts with the query, 1 means a word in the title does,
     * 2 means the title contains it, and 3 or more means a close match with
     * typos (the more typos, the higher the score). The number of typos allowed
     * grows with the length of the query.
     *
     * @param title the movie title, in lowercase
     * @param query the search text, in lowercase
     * @return the score, or -1 if the title doesn't match
     */
    private int matchScore(String title, String query) {
        if (title.startsWith(query)) {
            return 0;
        }
        for (int s = 1; s < title.length(); s++) {
            if (title.charAt(s - 1) == ' ' && title.startsWith(query, s)) {
                return 1;
            }
        }
        if (title.contains(query)) {
            return 2;
        }

        //how many typos we allow depends on how much was typed
        int maxTypos = query.length() <= 3 ? 0 : (query.length() <= 6 ? 1 : 2);
        if (maxTypos == 0) {
            return -1;
        }

        //compare the query to the start of the title and the start of every word,
        //using chunks about as long as the query
        int best = Integer.MAX_VALUE;
        for (int s = 0; s < title.length(); s++) {
            if (s != 0 && title.charAt(s - 1) != ' ') {
                continue;
            }
            for (int len = query.length() - 1; len <= query.length() + 1; len++) {
                if (len > 0 && s + len <= title.length()) {
                    best = Math.min(best, editDistance(query, title.substring(s, s + len)));
                }
            }
        }
        return best <= maxTypos ? 3 + best : -1;
    }
    
    /**
     * This method counts the fewest single-letter changes needed to turn one text into the other.
     * Adding, removing or replacing a letter counts as one change, and so does swapping two neighbouring letters.
     *
     * @param a the first text ("source")
     * @param b the second text ("target")
     * @return the number of changes
     */
    private int editDistance(String a, String b) {
        int[][] d = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) {
            d[i][0] = i;
        }
        for (int j = 0; j <= b.length(); j++) {
            d[0][j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                d[i][j] = Math.min(Math.min(d[i - 1][j] + 1, d[i][j - 1] + 1), d[i - 1][j - 1] + cost);
                if (i > 1 && j > 1 && a.charAt(i - 1) == b.charAt(j - 2) && a.charAt(i - 2) == b.charAt(j - 1)) {
                    d[i][j] = Math.min(d[i][j], d[i - 2][j - 2] + 1); //swapped letters like "lr" vs "rl"
                }
            }
        }
        return d[a.length()][b.length()];
    }
}
