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
    private static String apiKey = "80af13f9"; //api key
    private static String urlBase = "https://www.omdbapi.com/?apikey=";
    private HttpClient client = HttpClient.newHttpClient(); //talks to the internet so i can use request and response
    private ObjectMapper objectMapper = new ObjectMapper(); //changes json to object and vice versa
    
    public JsonNode getMovieJson (String imdbId) {
        try {
            String fullUrl = urlBase + apiKey + "&i=" + imdbId;
            
            HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(fullUrl)).GET().build(); //newBuilder creates the new request with the url, get says it wants to read and build finishes it 
            
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());//sends the request and then wait for answer, gets the answer as text because of BH.ofString
            
            JsonNode movieJson = objectMapper.readTree(response.body()); //converts text to json
            
            if(movieJson.path("Response").asText().equals("False")) { //if the response failed
                System.out.println("Response is false");
                return null;
            } else {
                return movieJson;
            }
            
        } catch (Exception e){
            System.out.println("Error during fetching a response from the API");
            return null;
        }
    }
    
    public String getPosterUrl(String imdbId) {
        JsonNode movie = getMovieJson(imdbId);
        
        if (movie == null) {
            return null;
        } else {
            if (movie.path("Poster").asText("N/A").equals("N/A")) { //goes to the path in the json and if there isnt a movie then returns null
                return null;
            } else {
                return movie.path("Poster").asText(); //gets the url as text
            }
        }
    }
}
