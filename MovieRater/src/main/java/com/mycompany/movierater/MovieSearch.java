package com.mycompany.movierater;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

//ADD JAVADOC
public class MovieSearch {
    private final Movie[] movieDatabase;
    
    public MovieSearch(Movie[] movieDatabase) {
        this.movieDatabase = movieDatabase;
    }
    
    //ADD JAVADOC
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
    
    //ADD JAVADOC
    //0 = title starts with the query, 1 = a word in the title starts with it, 2 = title contains it,
    //3+ = close but with typos, -1 = no match
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
    
    //ADD JAVADOC
    //number of single-letter changes (add, remove, replace, swap two neighbours) to turn a into b
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
