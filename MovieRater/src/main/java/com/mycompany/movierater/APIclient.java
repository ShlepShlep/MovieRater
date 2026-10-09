package com.mycompany.movierater;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 * @author agnes
 */
public class APIclient {
    private static String apiKey = "80af13f9";
    private static String urlBase = "https://www.omdbapi.com/?apikey=";
    private HttpClient client = HttpClient.newHttpClient();
    private ObjectMapper objectMapper = new ObjectMapper();
    
    public JsonNode getMovieJson (String imdbId) {
        try {
            String fullUrl = urlBase + apiKey + "&i=" + imdbId;
            
            HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(fullUrl)).GET().build();
            
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            
            JsonNode movieJson = objectMapper.readTree(response.body());
            
            if(movieJson.path("Response").asText().equals("False")) {
                System.out.println("Response is false");
                return null;
            } else {
                return movieJson;
            }
            
        } catch (Exception error){
            System.out.println("Error during fetching a response from the API");
            return null;
        }
    }
    
    public String getPosterUrl(String imdbId) {
        JsonNode movie = getMovieJson(imdbId);
        
        if (movie == null) {
            return null;
        } else {
            if (movie.path("Poster").asText("N/A").equals("N/A")) {
                return null;
            } else {
                return movie.path("Poster").asText();
            }
        }
    }
}
