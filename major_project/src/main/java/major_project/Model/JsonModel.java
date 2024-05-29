package major_project.Model;

import java.util.Arrays;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;

/** 
 * This model class is parse data into json
 */
public class JsonModel {
    
    /** 
     * This method is used to parse request response into Summoner object
    */
    public Summoner parseSummoner(String responseBody) {
        Gson gson = new Gson();
        Summoner summoner = gson.fromJson(responseBody, Summoner.class);
        return summoner;
    }

    /** 
     * This method is used to parse request response into League object
    */
    public League[] parseLeagues(String responseBody) {
        Gson gson = new Gson();
        League[] leagues = Arrays.stream(gson.fromJson(responseBody, League[].class)).filter(league -> league.getLeagueId() != null).toArray(League[]::new);
        for (League league : leagues) {
            System.out.println(league);
        }
        return leagues;
    }


    /** 
     * This method is used to parse dummy response into Summoner object
    */
    public Summoner offlineParseSummoner() {
        try {
            Gson gson = new Gson(); 

            // find file relative to current working directory
            File file = new File("src/main/java/major_project/Model/DummySummoner.json");

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            Summoner summoner = gson.fromJson(bufferedReader, Summoner.class);
            System.out.println(summoner);
            return summoner;
        } catch (IOException e) {
            System.out.println("Something went wrong with our request!");
            System.out.println(e.getMessage());
        }
        return null;
    }    

    /** 
     * This method is used to parse dummy response into League object
    */
    public League[] offlineParseLeagues() {
        try {

            Gson gson = new Gson(); 

            // find file relative to current working directory
            File file = new File("src/main/java/major_project/Model/DummyLeagues.json");

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            League[] leagues = Arrays.stream(gson.fromJson(bufferedReader, League[].class)).filter(league -> league.getLeagueId() != null).toArray(League[]::new);

            return leagues;
        } catch (IOException e) {
            System.out.println("Something went wrong with our request!");
            System.out.println(e.getMessage());
        }

        return null;
    }

    /** 
     * This method is used to parse response into RedditToken Object
    */
    public RedditToken parseRedditToken(String responseBody) {
        Gson gson = new Gson();
        RedditToken redditToken = gson.fromJson(responseBody, RedditToken.class);
        System.out.println(redditToken);
        return redditToken;
    }
}

