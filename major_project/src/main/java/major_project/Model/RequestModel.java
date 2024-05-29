package major_project.Model;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import java.util.Base64;


/** 
 * This class is used to send requests to the server.
 */
public class RequestModel {

    private JsonModel jsonModel;

    public RequestModel() {
        jsonModel = new JsonModel();
    }

    /**
     * This method is used to send a league request to the server.
     * @param input_api_key riot games api key
     * @param summonerId unique id of the summoner
     * @param inputStatus the offline/online status of the program
     * @return the response body fetched from the server
    */

    public String searchLeaguesRequest(String input_api_key, String summonerId, String inputStatus) {
        
        if (inputStatus.equals("offline")) {
            return "";
        }

        try {
            HttpRequest request = HttpRequest.newBuilder(new URI("https://oc1.api.riotgames.com/lol/league/v4/entries/by-summoner/" + summonerId))
                    .header("X-Riot-Token", input_api_key)
                    .GET()
                    .build();
            
            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response status code was: " + response.statusCode());
            System.out.println("Response headers were: " + response.headers());
            System.out.println("Response body was:\n" + response.body());
            
            if (response.statusCode() != 200) {
                return null;
            }
            return response.body();
            
            
        } catch (IOException | InterruptedException e) {
            System.out.println("Something went wrong with our request!");
            System.out.println(e.getMessage());

        } catch (URISyntaxException ignored) {
            // This would mean our URI is incorrect - this is here because often the URI you use will not be (fully)
            // hard-coded and so needs a way to be checked for correctness at runtime.
            System.out.println("Something went wrong with the URI!");
            System.out.println(ignored.getMessage());
        } 
        return null;
    }

    /**
     * This method is used to send a summoner request to the server.
     * @param input_api_key riot games api key
     * @param name the name of the summoner
     * @param inputStatus the offline/online status of the program
     * @return the response body fetched from the server
    */ 
    public String searchSummonerRequest(String input_api_key, String name, String inputStatus) {
        System.out.println("INput status: " + inputStatus);

        if (inputStatus.equals("offline")) {
            return "";
        } 

        try {
            HttpRequest request = HttpRequest.newBuilder(new URI("https://oc1.api.riotgames.com/lol/summoner/v4/summoners/by-name/" + name))
            .header("X-Riot-Token", input_api_key)
            .GET()
            .build();

            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response status code was: " + response.statusCode());
            System.out.println("Response headers were: " + response.headers());
            System.out.println("Response body was:\n" + response.body());

            return response.body();


        } catch (IOException | InterruptedException e) {
            System.out.println("Something went wrong with our request!");
            System.out.println(e.getMessage());
        } catch (URISyntaxException ignored) {
            // This would mean our URI is incorrect - this is here because often the URI you use will not be (fully)
            // hard-coded and so needs a way to be checked for correctness at runtime.
            System.out.println("Something went wrong with the URI!");
            System.out.println(ignored.getMessage());
        } 
        return null;
    }


    public void sendMessage(String data, String sid, String key) {
        String targetURI = "https://api.twilio.com/2010-04-01/Accounts/" + sid + "/Messages.json";
        try {
            HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(targetURI))
            .setHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((sid + ":" + key).getBytes()))
            .header("Content-Type", "application/x-www-form-urlencoded")
            // .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(data.toString()))
            .build();


            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response status code was: " + response.statusCode());
            System.out.println("Response headers were: " + response.headers());
            System.out.println("Response body was:\n" + response.body());
        } catch (IOException | InterruptedException e) {
            System.out.println("Something went wrong with our request!");
            System.out.println(e.getMessage());
        } catch (URISyntaxException ignored) {
            // This would mean our URI is incorrect - this is here because often the URI you use will not be (fully)
            // hard-coded and so needs a way to be checked for correctness at runtime.
            System.out.println("Something went wrong with the URI!");
            System.out.println(ignored.getMessage());
        } catch (Exception e) {
            System.out.println("Something went wrong!");
            System.out.println(e.getMessage());
        }
    }

    public String loginRequest(String data, String key, String secret) {
        String targetURI = "https://www.reddit.com/api/v1/access_token";

        try {
            HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(targetURI))
            .setHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((key + ":" + secret).getBytes()))
            .header("User-Agent", "major_project/v0.1")
            .header("Content-Type", "application/x-www-form-urlencoded")
            // .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(data.toString()))
            .build();

            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response status code was: " + response.statusCode());
            System.out.println("Response headers were: " + response.headers());
            System.out.println("Response body was:\n" + response.body());

            if (response.statusCode() != 200) {
                throw new IllegalStateException("Login request failed");
            }

            return response.body();
        } catch (IOException | InterruptedException e) {
            System.out.println("Something went wrong with our request!");
            System.out.println(e.getMessage());
        } catch (URISyntaxException ignored) {
            // This would mean our URI is incorrect - this is here because often the URI you use will not be (fully)
            // hard-coded and so needs a way to be checked for correctness at runtime.
            System.out.println("Something went wrong with the URI!");
            System.out.println(ignored.getMessage());
        } catch (Exception e) {
            System.out.println("Something went wrong!");
            System.out.println(e.getMessage());
        }
        return null;
    }


    public String postRequest(String post, String accessToken) {
        String targetURI = "https://oauth.reddit.com/api/submit";

        System.out.println("Access Token: " + accessToken);
        System.out.println("Post Body: " + post);
        try {
            HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(targetURI))
            .setHeader("Authorization", "bearer " + accessToken+"")
            .header("User-Agent", "major_project/v0.1")
            .header("Content-Type", "application/x-www-form-urlencoded")
            // .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(post))
            .build();

            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response status code was: " + response.statusCode());
            System.out.println("Response headers were: " + response.headers());
            System.out.println("Response body was:\n" + response.body());

            return response.body();
        } catch (IOException | InterruptedException e) {
            System.out.println("Something went wrong with our request!");
            System.out.println(e.getMessage());
        } catch (URISyntaxException ignored) {
            // This would mean our URI is incorrect - this is here because often the URI you use will not be (fully)
            // hard-coded and so needs a way to be checked for correctness at runtime.
            System.out.println("Something went wrong with the URI!");
            System.out.println(ignored.getMessage());
        } catch (Exception e) {
            System.out.println("Something went wrong!");
            System.out.println(e.getMessage());
        }
        return null;
    }


}
