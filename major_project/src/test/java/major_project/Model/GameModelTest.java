package major_project.Model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileNotFoundException;

public class GameModelTest {
    
    private GameModel model;

    @BeforeEach
    public void setUp() {
        model = new GameModel();
    }

    @Test
    public void testSearchValidSummoner() {

        // given
        RequestModel requestMock = mock(RequestModel.class); 
        JsonModel jsonModel = mock(JsonModel.class);
        StatusStrategy statusStrategy = mock(StatusStrategy.class);
        CreditObservers creditObserver = mock(CreditObservers.class);

        model.setInputStatus("offline");  // for testing purpose
        
        String input_api_key = "test";
        String summonerName = "hypebooba";
        
        // when

        try {            
            Gson gson = new Gson(); 
            File file = new File("src/main/java/major_project/Model/DummySummoner.json");
            
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            Summoner summonerDummy = gson.fromJson(bufferedReader, Summoner.class);
            
            when(requestMock.searchSummonerRequest(input_api_key, summonerName, "offline")).thenReturn("");
            when(jsonModel.offlineParseSummoner()).thenReturn(summonerDummy);
            when(statusStrategy.searchSummoner(input_api_key, summonerName, requestMock, jsonModel)).thenReturn(summonerDummy);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
        
        // then
        model.injectRequest(requestMock);
        model.injectJson(jsonModel);
        model.injectStrategy(statusStrategy);
        model.registerCreditObserver(creditObserver);


        Summoner summonerActual = model.handleRequests(summonerName, false);
        assertNotNull(summonerActual);

        // assert name, puuid, level
        assertEquals(summonerActual.getName(), "Hypebooba");
        assertEquals(summonerActual.getPuuid(), "nb1yNtUk13go1HH2qyoxMHKDlMlUAWX_wd7y7rFqwfjcpbYKemgEzFOfgFblBeR4Q59D1jQNAEuXcw");
        assertEquals(summonerActual.getSummonerLevel(), 473);

        
        String actual = summonerActual.toString();
        System.out.println(actual);
        String expected = "Summoner{id='ge7L5_IvQUUA9xy-1iVL1MhMI6ewem6YbXgF37KjfM2U', accountId='_aW4sn3PirLRowz9bh96bqfNJRqB93BbYb6xmL7DT99R8vw', puuid='nb1yNtUk13go1HH2qyoxMHKDlMlUAWX_wd7y7rFqwfjcpbYKemgEzFOfgFblBeR4Q59D1jQNAEuXcw', name='Hypebooba', profileIconId=4968, revisionDate=1652352727000, summonerLevel=473}";
        
        assertEquals(actual, expected);
        
        // verify(requestMock).searchSummonerRequest(input_api_key, summonerName, "offline");
        // verify(jsonModel).offlineParseSummoner();
        verify(statusStrategy).searchSummoner(input_api_key, summonerName, requestMock, jsonModel);
    }

    @Test
    public void testSearchNotValidSummoner() {

        // given
        RequestModel requestMock = mock(RequestModel.class); 
        JsonModel jsonModel = mock(JsonModel.class);
        StatusStrategy statusStrategy = mock(StatusStrategy.class);
        CreditObservers creditObservers = mock(CreditObservers.class);

        model.setInputStatus("offline");  // for testing purpose

        String input_api_key = "test";
        String summonerName = "hypebooba";

        // when
        try {            
            Gson gson = new Gson(); 
            File file = new File("src/main/java/major_project/Model/DummySummonerInvalid.json");

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            Summoner summonerDummy = gson.fromJson(bufferedReader, Summoner.class);

            when(requestMock.searchSummonerRequest(input_api_key, summonerName, "offline")).thenReturn("");
            when(jsonModel.offlineParseSummoner()).thenReturn(summonerDummy);
            when(statusStrategy.searchSummoner(input_api_key, summonerName, requestMock, jsonModel)).thenReturn(summonerDummy);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        // then
        model.injectRequest(requestMock);
        model.injectJson(jsonModel);
        model.injectStrategy(statusStrategy);
        model.registerCreditObserver(creditObservers);

        assertThrows(IllegalStateException.class, () -> {
            model.handleRequests(summonerName, false);
        });
        
        // verify(requestMock).searchSummonerRequest(input_api_key, summonerName, "offline");
        // verify(jsonModel).offlineParseSummoner();
        verify(statusStrategy).searchSummoner(input_api_key, summonerName, requestMock, jsonModel);
    }

    @Test
    public void testGetLeagues() {
        // given
        RequestModel requestMock = mock(RequestModel.class); 
        JsonModel jsonModel = mock(JsonModel.class);
        StatusStrategy statusStrategy = mock(StatusStrategy.class);
        model.setInputStatus("offline");  // for testing purpose

        String input_api_key = "test";
        String summonerName = "hypebooba";

        // when
        try {
            Gson gson = new Gson();
            File file = new File("src/main/java/major_project/Model/DummyLeagues.json");

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            League[] leagues = Arrays.stream(gson.fromJson(bufferedReader, League[].class)).filter(league -> league.getLeagueId() != null).toArray(League[]::new);

            when(requestMock.searchLeaguesRequest(input_api_key, summonerName, "offline")).thenReturn("");
            when(jsonModel.offlineParseLeagues()).thenReturn(leagues);
            when(statusStrategy.getLeagues(input_api_key, summonerName, requestMock, jsonModel)).thenReturn(leagues);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
    
        // then
        model.injectRequest(requestMock);
        model.injectJson(jsonModel);
        model.injectStrategy(statusStrategy);

        League[] leagues = model.getLeagues(summonerName, false);
        assertNotNull(leagues);
        assertThat(leagues.length, equalTo(2));

        League league1 = leagues[0];
        League league2 = leagues[1];

        // League{queueType='RANKED_FLEX_SR', summonerId='ge7L5_IvQUUA9xy-1iVL1MhMI6ewem6YbXgF37KjfM2U', summonerName='Hypebooba', leaguePoints=100, wins=6, losses=6, veteran=false, inactive=false, freshBlood=false, hotStreak=false, leagueId='5d8b7e50-c459-44d5-bfcb-eca8e51a64bd', tier='PLATINUM', rank='I', miniSeries=MiniSeries{target=3, wins=0, losses=1, progress=LNNNN}}

        assertEquals(league1.getQueueType(), "RANKED_FLEX_SR");
        assertEquals(league1.getSummonerId(), "ge7L5_IvQUUA9xy-1iVL1MhMI6ewem6YbXgF37KjfM2U");
        assertEquals(league1.getSummonerName(), "Hypebooba");
        assertEquals(league1.getLeaguePoints(), 100);
        assertEquals(league1.getWins(), 6);
        assertEquals(league1.getLosses(), 6);
        assertEquals(league1.getVeteran(), false);
        assertEquals(league1.getInactive(), false);
        assertEquals(league1.getFreshBlood(), false);
        assertEquals(league1.getHotStreak(), false);
        assertEquals(league1.getLeagueId(), "5d8b7e50-c459-44d5-bfcb-eca8e51a64bd");
        assertEquals(league1.getTier(), "PLATINUM");
        assertEquals(league1.getRank(), "I");
        assertEquals(league1.getMiniSeries().getTarget(), 3);
        assertEquals(league1.getMiniSeries().getWins(), 0);
        assertEquals(league1.getMiniSeries().getLosses(), 1);
        assertEquals(league1.getMiniSeries().getProgress(), "LNNNN");


        //League{queueType='RANKED_SOLO_5x5', summonerId='ge7L5_IvQUUA9xy-1iVL1MhMI6ewem6YbXgF37KjfM2U', summonerName='Hypebooba', leaguePoints=75, wins=118, losses=98, veteran=false, inactive=false, freshBlood=false, hotStreak=false, leagueId='4c343be4-2204-4507-acd4-17b1974046b4', tier='DIAMOND', rank='II', miniSeries=null}
        assertEquals(league2.getQueueType(), "RANKED_SOLO_5x5");
        assertEquals(league2.getSummonerId(), "ge7L5_IvQUUA9xy-1iVL1MhMI6ewem6YbXgF37KjfM2U");
        assertEquals(league2.getSummonerName(), "Hypebooba");
        assertEquals(league2.getLeaguePoints(), 96);
        assertEquals(league2.getWins(), 117);
        assertEquals(league2.getLosses(), 96);
        assertEquals(league2.getVeteran(), false);
        assertEquals(league2.getInactive(), false);
        assertEquals(league2.getFreshBlood(), false);
        assertEquals(league2.getHotStreak(), true);
        assertEquals(league2.getLeagueId(), "4c343be4-2204-4507-acd4-17b1974046b4");
        assertEquals(league2.getTier(), "DIAMOND");
        assertEquals(league2.getRank(), "II");
        assertEquals(league2.getMiniSeries(), null);
        
        // verify(requestMock).searchLeaguesRequest(input_api_key, summonerName, "offline");
        // verify(jsonModel).offlineParseLeagues();
        verify(statusStrategy).getLeagues(input_api_key, summonerName, requestMock, jsonModel);
    }


    @Test
    public void testGetLeaguesNone() {
        // given
        RequestModel requestMock = mock(RequestModel.class); 
        JsonModel jsonModel = mock(JsonModel.class);
        StatusStrategy statusStrategy = mock(StatusStrategy.class);
        model.setInputStatus("offline");  // for testing purpose

        String input_api_key = "test";
        String summonerName = "hypebooba";

        // when
        when(requestMock.searchLeaguesRequest(input_api_key, summonerName, "offline")).thenReturn(""); 
        when(jsonModel.offlineParseLeagues()).thenReturn(new League[]{});
        when(statusStrategy.getLeagues(input_api_key, summonerName, requestMock, jsonModel)).thenReturn(new League[]{});
    
        // then
        model.injectRequest(requestMock);
        model.injectJson(jsonModel);
        model.injectStrategy(statusStrategy);

        League[] leagues = model.getLeagues(summonerName, false);
        assertNotNull(leagues);
        assertThat(leagues.length, equalTo(0));
        
        // verify(requestMock).searchLeaguesRequest(input_api_key, summonerName, "offline");
        // verify(jsonModel).offlineParseLeagues();
    }


    @Test
    public void testPopLastElement() {
        String input_api_key = "test";
        String summonerName = "hypebooba";

        RequestModel requestMock = mock(RequestModel.class);
        JsonModel jsonModel = mock(JsonModel.class);
        StatusStrategy statusStrategy = mock(StatusStrategy.class);
        CreditObservers creditObserver = mock(CreditObservers.class);

        model.setInputStatus("offline");


        // when
        try {            
            Gson gson = new Gson(); 
            File file = new File("src/main/java/major_project/Model/DummySummoner.json");

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            Summoner summonerDummy = gson.fromJson(bufferedReader, Summoner.class);

            file = new File("src/main/java/major_project/Model/DummyLeagues.json");

            bufferedReader = new BufferedReader(new FileReader(file));
            League[] leagues = Arrays.stream(gson.fromJson(bufferedReader, League[].class)).filter(league -> league.getLeagueId() != null).toArray(League[]::new);

            when(requestMock.searchLeaguesRequest(input_api_key, summonerName, "offline")).thenReturn("");
            when(jsonModel.offlineParseLeagues()).thenReturn(leagues);
            when(requestMock.searchSummonerRequest(input_api_key, summonerName, "offline")).thenReturn("");
            when(jsonModel.offlineParseSummoner()).thenReturn(summonerDummy);

            when(statusStrategy.searchSummoner(input_api_key, summonerName, requestMock, jsonModel)).thenReturn(summonerDummy);
            when(statusStrategy.getLeagues(input_api_key, summonerDummy.getId(), requestMock, jsonModel)).thenReturn(leagues);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }


        model.injectRequest(requestMock);
        model.injectJson(jsonModel);
        model.injectStrategy(statusStrategy);
        model.registerCreditObserver(creditObserver);

        Summoner summoner = model.handleRequests(summonerName, false);
        System.out.println(summoner);
        League[] leaguesActual = model.getLeagues(summoner.getId(), false);
        model.validateData(summoner, leaguesActual);

        assertThat(model.getLeaguesLength(), equalTo(1));
        assertThat(model.getSummonerLength(), equalTo(1));

        model.removeLastData();

        assertThat(model.getSummonerLength(), equalTo(0));
        assertThat(model.getLeaguesLength(), equalTo(0));

        // verify(requestMock).searchLeaguesRequest(input_api_key, summonerName, "offline");
        // verify(jsonModel).offlineParseLeagues();
        verify(statusStrategy).searchSummoner(input_api_key, summonerName, requestMock, jsonModel);
        verify(statusStrategy).getLeagues(input_api_key, summoner.getId(), requestMock, jsonModel);
    }

    
    @Test
    public void testGetShortMessage1() {
       
        String input_api_key = "test";
        String summonerName = "hypebooba";

        RequestModel requestMock = mock(RequestModel.class);
        JsonModel jsonModel = mock(JsonModel.class);
        StatusStrategy statusStrategy = mock(StatusStrategy.class);
        CreditObservers creditObserver = mock(CreditObservers.class);

        model.setInputStatus("offline");
        model.setOutputStatus("offline");

        // when
        try {            
            Gson gson = new Gson(); 
            File file = new File("src/main/java/major_project/Model/DummySummoner.json");

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            Summoner summonerDummy = gson.fromJson(bufferedReader, Summoner.class);

            file = new File("src/main/java/major_project/Model/DummyLeagues.json");

            bufferedReader = new BufferedReader(new FileReader(file));
            League[] leagues = Arrays.stream(gson.fromJson(bufferedReader, League[].class)).filter(league -> league.getLeagueId() != null).toArray(League[]::new);

            when(requestMock.searchLeaguesRequest(input_api_key, summonerDummy.getId(), "offline")).thenReturn("");
            when(jsonModel.offlineParseLeagues()).thenReturn(leagues);
            when(requestMock.searchSummonerRequest(input_api_key, summonerName, "offline")).thenReturn("");
            when(jsonModel.offlineParseSummoner()).thenReturn(summonerDummy);

            when(statusStrategy.searchSummoner(input_api_key, summonerName, requestMock, jsonModel)).thenReturn(summonerDummy);
            when(statusStrategy.getLeagues(input_api_key, summonerDummy.getId(), requestMock, jsonModel)).thenReturn(leagues);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        // then
        model.injectRequest(requestMock);
        model.injectJson(jsonModel);
        model.injectStrategy(statusStrategy);
        model.registerCreditObserver(creditObserver);

        Summoner summoner = model.handleRequests(summonerName, false);
        
        League[] leaguesActual = model.getLeagues(summoner.getId(), false);
        League league1 = leaguesActual[0];

        String expectedMessage = "Summoner Name: Hypebooba\nSummoner Level: 473\nWin %: 50.0";  
        String actualMessage = model.getShortMessage(league1);

        assertEquals(actualMessage, expectedMessage);

        // verify(requestMock).searchLeaguesRequest(input_api_key, summonerName, "offline");
        // verify(jsonModel).offlineParseLeagues();
        verify(statusStrategy).searchSummoner(input_api_key, summonerName, requestMock, jsonModel);
        verify(statusStrategy).getLeagues(input_api_key, summoner.getId(), requestMock, jsonModel);
    }

    @Test
    public void testGetShortMessage2() {
       
        String input_api_key = "test";
        String summonerName = "hypebooba";

        RequestModel requestMock = mock(RequestModel.class);
        JsonModel jsonModel = mock(JsonModel.class);
        StatusStrategy statusStrategy = mock(StatusStrategy.class);
        CreditObservers creditObserver = mock(CreditObservers.class);

        model.setInputStatus("offline");
        model.setOutputStatus("online");

        // when
        try {            
            Gson gson = new Gson(); 
            File file = new File("src/main/java/major_project/Model/DummySummoner.json");

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            Summoner summonerDummy = gson.fromJson(bufferedReader, Summoner.class);

            file = new File("src/main/java/major_project/Model/DummyLeagues.json");

            bufferedReader = new BufferedReader(new FileReader(file));
            League[] leagues = Arrays.stream(gson.fromJson(bufferedReader, League[].class)).filter(league -> league.getLeagueId() != null).toArray(League[]::new);

            when(requestMock.searchLeaguesRequest(input_api_key, summonerDummy.getId(), "offline")).thenReturn("");
            when(jsonModel.offlineParseLeagues()).thenReturn(leagues);
            when(requestMock.searchSummonerRequest(input_api_key, summonerName, "offline")).thenReturn("");
            when(jsonModel.offlineParseSummoner()).thenReturn(summonerDummy);

            when(statusStrategy.searchSummoner(input_api_key, summonerName, requestMock, jsonModel)).thenReturn(summonerDummy);
            when(statusStrategy.getLeagues(input_api_key, summonerDummy.getId(), requestMock, jsonModel)).thenReturn(leagues);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        // then
        model.injectRequest(requestMock);
        model.injectJson(jsonModel);
        model.injectStrategy(statusStrategy);

        model.registerCreditObserver(creditObserver);

        Summoner summoner = model.handleRequests(summonerName, false);

        League[] leaguesActual = model.getLeagues(summoner.getId(), false);
        League league1 = leaguesActual[0];  

        model.validateData(summoner, leaguesActual);

        String expectedMessage = "Summoner Name: Hypebooba\nSummoner Level: 473\nWin %: 50.0";  
        String actualMessage = model.getShortMessage(league1);
        System.out.println(actualMessage);

        assertEquals(actualMessage, expectedMessage);

        // verify(requestMock).searchLeaguesRequest(input_api_key, summonerName, "offline");
        // verify(jsonModel).offlineParseLeagues();
        verify(statusStrategy).searchSummoner(input_api_key, summonerName, requestMock, jsonModel);
        verify(statusStrategy).getLeagues(input_api_key, summoner.getId(), requestMock, jsonModel);
    }


    @Test
    public void testGetLongMessage1() {
        
        String input_api_key = "test";
        String summonerName = "hypebooba";

        RequestModel requestMock = mock(RequestModel.class);
        JsonModel jsonModel = mock(JsonModel.class);
        StatusStrategy statusStrategy = mock(StatusStrategy.class);
        CreditObservers creditObserver = mock(CreditObservers.class);

        model.setInputStatus("offline");
        model.setOutputStatus("offline");

        // when
        try {            
            Gson gson = new Gson(); 
            File file = new File("src/main/java/major_project/Model/DummySummoner.json");

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            Summoner summonerDummy = gson.fromJson(bufferedReader, Summoner.class);

            file = new File("src/main/java/major_project/Model/DummyLeagues.json");

            bufferedReader = new BufferedReader(new FileReader(file));
            League[] leagues = Arrays.stream(gson.fromJson(bufferedReader, League[].class)).filter(league -> league.getLeagueId() != null).toArray(League[]::new);

            when(requestMock.searchLeaguesRequest(input_api_key, summonerDummy.getId(), "offline")).thenReturn("");
            when(jsonModel.offlineParseLeagues()).thenReturn(leagues);
            when(requestMock.searchSummonerRequest(input_api_key, summonerName, "offline")).thenReturn("");
            when(jsonModel.offlineParseSummoner()).thenReturn(summonerDummy);

            when(statusStrategy.searchSummoner(input_api_key, summonerName, requestMock, jsonModel)).thenReturn(summonerDummy);
            when(statusStrategy.getLeagues(input_api_key, summonerDummy.getId(), requestMock, jsonModel)).thenReturn(leagues);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        // then
        model.injectRequest(requestMock);
        model.injectJson(jsonModel);
        model.injectStrategy(statusStrategy);
        model.registerCreditObserver(creditObserver);

        Summoner summoner = model.handleRequests(summonerName, false);
        League[] leaguesActual = model.getLeagues(summoner.getId(), false);

        model.validateData(summoner, leaguesActual);

        String expectedMessage = "Summoner Name: Hypebooba\nSummoner Level: 473\nRANKED_FLEX_SR\n\tWins: 6\n\tLosses: 6\n\tWin %: 50.0";
        String actualMessage = model.getLongMessage();

        assertEquals(actualMessage, expectedMessage);

        // verify(requestMock).searchSummonerRequest(input_api_key, summonerName, "offline");
        // verify(requestMock).searchLeaguesRequest(input_api_key, summonerName, "offline");
        // verify(jsonModel).offlineParseLeagues();
        // verify(jsonModel).offlineParseSummoner();
        verify(statusStrategy).searchSummoner(input_api_key, summonerName, requestMock, jsonModel);
        verify(statusStrategy).getLeagues(input_api_key, summoner.getId(), requestMock, jsonModel);
    }

    @Test
    public void testGetLongMessage2() {
        
        String input_api_key = "test";
        String summonerName = "hypebooba";

        RequestModel requestMock = mock(RequestModel.class);
        JsonModel jsonModel = mock(JsonModel.class);
        StatusStrategy statusStrategy = mock(StatusStrategy.class);
        CreditObservers creditObserver = mock(CreditObservers.class);

        model.setInputStatus("offline");
        model.setOutputStatus("online");

        // when
        try {            
            Gson gson = new Gson(); 
            File file = new File("src/main/java/major_project/Model/DummySummoner.json");

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            Summoner summonerDummy = gson.fromJson(bufferedReader, Summoner.class);

            file = new File("src/main/java/major_project/Model/DummyLeagues.json");

            bufferedReader = new BufferedReader(new FileReader(file));
            League[] leagues = Arrays.stream(gson.fromJson(bufferedReader, League[].class)).filter(league -> league.getLeagueId() != null).toArray(League[]::new);

            when(requestMock.searchLeaguesRequest(input_api_key, summonerDummy.getId(), "offline")).thenReturn("");
            when(jsonModel.offlineParseLeagues()).thenReturn(leagues);
            when(requestMock.searchSummonerRequest(input_api_key, summonerName, "offline")).thenReturn("");
            when(jsonModel.offlineParseSummoner()).thenReturn(summonerDummy);
                
            when(statusStrategy.searchSummoner(input_api_key, summonerName, requestMock, jsonModel)).thenReturn(summonerDummy);
            when(statusStrategy.getLeagues(input_api_key, summonerDummy.getId(), requestMock, jsonModel)).thenReturn(leagues);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        // then
        model.injectRequest(requestMock);
        model.injectJson(jsonModel);
        model.injectStrategy(statusStrategy);
        model.registerCreditObserver(creditObserver);

        Summoner summoner = model.handleRequests(summonerName, false);
        League[] leaguesActual = model.getLeagues(summoner.getId(), false);

        model.validateData(summoner, leaguesActual);

        String expectedMessage = "Summoner Name: Hypebooba\nSummoner Level: 473\nRANKED_FLEX_SR\n\tWins: 6\n\tLosses: 6\n\tWin %: 50.0\nRANKED_SOLO_5x5\n\tWins: 117\n\tLosses: 96\n\tWin %: 55.0";
        String actualMessage = model.getLongMessage();

        assertEquals(actualMessage, expectedMessage);

        // verify(requestMock).searchSummonerRequest(input_api_key, summonerName, "offline");
        // verify(requestMock).searchLeaguesRequest(input_api_key, summonerName, "offline");
        // verify(jsonModel).offlineParseLeagues();
        // verify(jsonModel).offlineParseSummoner();
        verify(statusStrategy).searchSummoner(input_api_key, summonerName, requestMock, jsonModel);
        verify(statusStrategy).getLeagues(input_api_key, summoner.getId(), requestMock, jsonModel);
    }

    @Test
    public void testValidateDataValid() {
       
        String input_api_key = "test";
        String summonerName = "hypebooba";

        RequestModel requestMock = mock(RequestModel.class);
        JsonModel jsonModel = mock(JsonModel.class);
        StatusStrategy statusStrategy = mock(StatusStrategy.class);
        CreditObservers creditObserver = mock(CreditObservers.class);

        model.setInputStatus("offline");
        model.setOutputStatus("offline");

        // when
        try {            
            Gson gson = new Gson(); 
            File file = new File("src/main/java/major_project/Model/DummySummoner.json");

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            Summoner summonerDummy = gson.fromJson(bufferedReader, Summoner.class);

            file = new File("src/main/java/major_project/Model/DummyLeagues.json");

            bufferedReader = new BufferedReader(new FileReader(file));
            League[] leagues = Arrays.stream(gson.fromJson(bufferedReader, League[].class)).filter(league -> league.getLeagueId() != null).toArray(League[]::new);

            when(requestMock.searchLeaguesRequest(input_api_key, summonerDummy.getId(), "offline")).thenReturn("");
            when(jsonModel.offlineParseLeagues()).thenReturn(leagues);
            when(requestMock.searchSummonerRequest(input_api_key, summonerName, "offline")).thenReturn("");
            when(jsonModel.offlineParseSummoner()).thenReturn(summonerDummy);
                    
            when(statusStrategy.searchSummoner(input_api_key, summonerName, requestMock, jsonModel)).thenReturn(summonerDummy);
            when(statusStrategy.getLeagues(input_api_key, summonerDummy.getId(), requestMock, jsonModel)).thenReturn(leagues);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        // then
        model.injectRequest(requestMock);
        model.injectJson(jsonModel);
        model.injectStrategy(statusStrategy);
        model.registerCreditObserver(creditObserver);

        Summoner summoner = model.handleRequests(summonerName, false);
        League[] leaguesActual = model.getLeagues(summoner.getId(), false);

        model.validateData(summoner, leaguesActual);

        assertThat(model.getSummonerLength(), equalTo(1));
        assertThat(model.getLeaguesLength(), equalTo(1));

        // verify(requestMock).searchSummonerRequest(input_api_key, summonerName, "offline");
        // verify(requestMock).searchLeaguesRequest(input_api_key, summonerName, "offline");
        // verify(jsonModel).offlineParseLeagues();
        // verify(jsonModel).offlineParseSummoner();

        verify(statusStrategy).searchSummoner(input_api_key, summonerName, requestMock, jsonModel);
        verify(statusStrategy).getLeagues(input_api_key, summoner.getId(), requestMock, jsonModel);
    }


    @Test
    public void testValidateDataNotValid() {
       
        String input_api_key = "test";
        String summonerName = "hypebooba";

        RequestModel requestMock = mock(RequestModel.class);
        JsonModel jsonModel = mock(JsonModel.class);
        StatusStrategy statusStrategy = mock(StatusStrategy.class);
        CreditObservers creditObserver = mock(CreditObservers.class);

        model.setInputStatus("offline");
        model.setOutputStatus("offline");

        // when
        try {            
            Gson gson = new Gson(); 
            File file = new File("src/main/java/major_project/Model/DummySummoner.json");

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            Summoner summonerDummy = gson.fromJson(bufferedReader, Summoner.class);

            file = new File("src/main/java/major_project/Model/DummyLeagues.json");

            bufferedReader = new BufferedReader(new FileReader(file));
            League[] leagues = Arrays.stream(gson.fromJson(bufferedReader, League[].class)).filter(league -> league.getLeagueId() != null).toArray(League[]::new);

            when(requestMock.searchLeaguesRequest(input_api_key, summonerDummy.getId(), "offline")).thenReturn("");
            when(jsonModel.offlineParseLeagues()).thenReturn(leagues);
            when(requestMock.searchSummonerRequest(input_api_key, summonerName, "offline")).thenReturn("");
            when(jsonModel.offlineParseSummoner()).thenReturn(summonerDummy);
                        
            when(statusStrategy.searchSummoner(input_api_key, summonerName, requestMock, jsonModel)).thenReturn(summonerDummy);
            when(statusStrategy.getLeagues(input_api_key, summonerDummy.getId(), requestMock, jsonModel)).thenReturn(leagues);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        // then
        model.injectRequest(requestMock);
        model.injectJson(jsonModel);
        model.injectStrategy(statusStrategy);
        model.registerCreditObserver(creditObserver);

        Summoner summoner = model.handleRequests(summonerName, false);
        League[] leaguesActual = model.getLeagues(summoner.getId(), false);

        model.validateData(summoner, new League[]{});

        // verify(requestMock).searchSummonerRequest(input_api_key, summonerName, "offline");
        // verify(requestMock).searchLeaguesRequest(input_api_key, summonerName, "offline");
        // verify(jsonModel).offlineParseLeagues();
        // verify(jsonModel).offlineParseSummoner();

        verify(statusStrategy).searchSummoner(input_api_key, summonerName, requestMock, jsonModel);
        verify(statusStrategy).getLeagues(input_api_key, summoner.getId(), requestMock, jsonModel);
    }

    @Test
    public void testGetAllSummoners() {
        String input_api_key = "test";
        String summonerName = "hypebooba";

        DatabaseModel dbMock = mock(DatabaseModel.class);
        model.setInputStatus("online");

        // when
        try {            
            Gson gson = new Gson(); 
            File file = new File("src/main/java/major_project/Model/DummySummoner.json");

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            Summoner summonerDummy = gson.fromJson(bufferedReader, Summoner.class);

            when(dbMock.getAllSummoners()).thenReturn(new Summoner[] { summonerDummy });
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        // then
        model.injectDb(dbMock);

        Summoner[] summoner = model.getAllSummoners();
        assertEquals(1, summoner.length);

        String expected = "Summoner{id='ge7L5_IvQUUA9xy-1iVL1MhMI6ewem6YbXgF37KjfM2U', accountId='_aW4sn3PirLRowz9bh96bqfNJRqB93BbYb6xmL7DT99R8vw', puuid='nb1yNtUk13go1HH2qyoxMHKDlMlUAWX_wd7y7rFqwfjcpbYKemgEzFOfgFblBeR4Q59D1jQNAEuXcw', name='Hypebooba', profileIconId=4968, revisionDate=1652352727000, summonerLevel=473}";
        String actual = summoner[0].toString();
        assertEquals(expected, actual);

        verify(dbMock).getAllSummoners();
    }

    @Test
    public void testGetAllSummonersOffline() {
        String input_api_key = "test";
        String summonerName = "hypebooba";

        DatabaseModel dbMock = mock(DatabaseModel.class);
        model.setInputStatus("offline");

        // when
        try {            
            Gson gson = new Gson(); 
            File file = new File("src/main/java/major_project/Model/DummySummoner.json");

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            Summoner summonerDummy = gson.fromJson(bufferedReader, Summoner.class);

            when(dbMock.getAllSummoners()).thenReturn(new Summoner[] { summonerDummy });
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        // then
        model.injectDb(dbMock);

        Summoner[] summoner = model.getAllSummoners();
        assertNull(summoner);
    }

    @Test
    public void testClearCache() {
        String input_api_key = "test";
        String summonerName = "hypebooba";

        DatabaseModel dbMock = mock(DatabaseModel.class);
        model.setInputStatus("online");

        // when
        try {            
            Gson gson = new Gson(); 
            File file = new File("src/main/java/major_project/Model/DummySummoner.json");

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            Summoner summonerDummy = gson.fromJson(bufferedReader, Summoner.class);

            when(dbMock.getAllSummoners()).thenReturn(new Summoner[] { summonerDummy });
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        // then
        model.injectDb(dbMock);

        Summoner[] summoner = model.getAllSummoners();
        assertEquals(1, summoner.length);

        String expected = "Summoner{id='ge7L5_IvQUUA9xy-1iVL1MhMI6ewem6YbXgF37KjfM2U', accountId='_aW4sn3PirLRowz9bh96bqfNJRqB93BbYb6xmL7DT99R8vw', puuid='nb1yNtUk13go1HH2qyoxMHKDlMlUAWX_wd7y7rFqwfjcpbYKemgEzFOfgFblBeR4Q59D1jQNAEuXcw', name='Hypebooba', profileIconId=4968, revisionDate=1652352727000, summonerLevel=473}";
        String actual = summoner[0].toString();
        assertEquals(expected, actual);

        when(dbMock.getAllSummoners()).thenReturn(new Summoner[] {});   // simulate clear cache
        model.clearCache("ge7L5_IvQUUA9xy-1iVL1MhMI6ewem6YbXgF37KjfM2U");

        summoner = model.getAllSummoners();
        assertEquals(0, summoner.length);

        verify(dbMock, times(2)).getAllSummoners();
    }

    @Test
    public void testCacheSummonerData() {
        String input_api_key = "test";
        String summonerName = "hypebooba";

        DatabaseModel dbMock = mock(DatabaseModel.class);
        RequestModel requestMock = mock(RequestModel.class);
        JsonModel jsonModel = mock(JsonModel.class);
        StatusStrategy statusStrategy = mock(StatusStrategy.class);
        CreditObservers creditObserver = mock(CreditObservers.class);

        // when
        try {            
            Gson gson = new Gson(); 
            File file = new File("src/main/java/major_project/Model/DummySummoner.json");

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            Summoner summonerDummy = gson.fromJson(bufferedReader, Summoner.class);

            when(requestMock.searchSummonerRequest(input_api_key, summonerName, "offline")).thenReturn("");
            when(jsonModel.offlineParseSummoner()).thenReturn(summonerDummy);
            when(dbMock.getSummoner(summonerName)).thenReturn(summonerDummy);

            when(statusStrategy.searchSummoner(input_api_key, summonerName, requestMock, jsonModel)).thenReturn(summonerDummy);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        // then
        model.injectRequest(requestMock);
        model.injectDb(dbMock);
        model.injectJson(jsonModel);
        model.registerCreditObserver(creditObserver);

        model.setInputStatus("offline");
        model.injectStrategy(statusStrategy);
        Summoner actual = model.handleRequests(summonerName, false);

        model.setInputStatus("online");
        model.injectStrategy(statusStrategy);
        Summoner cached = model.handleRequests(summonerName, true); 


        assertEquals(actual.toString(), cached.toString());

        // verify(requestMock).searchSummonerRequest(input_api_key, summonerName, "offline");
        verify(dbMock).getSummoner(summonerName);   // second request
        // verify(jsonModel).offlineParseSummoner();
        verify(statusStrategy).searchSummoner(input_api_key, summonerName, requestMock, jsonModel); // first request
    }

    @Test
    public void testGetCachedSummonerData() {
        String input_api_key = "test";
        String summonerName = "hypebooba";

        DatabaseModel dbMock = mock(DatabaseModel.class);
        StatusStrategy statusStrategy = mock(StatusStrategy.class);
        RequestModel requestMock = mock(RequestModel.class);
        JsonModel jsonModel = mock(JsonModel.class);
        CreditObservers creditObserver = mock(CreditObservers.class);

        model.setInputStatus("online");

        // when
        try {            
            Gson gson = new Gson(); 
            File file = new File("src/main/java/major_project/Model/DummySummoner.json");

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            Summoner summonerDummy = gson.fromJson(bufferedReader, Summoner.class);

            when(dbMock.getSummoner(summonerName)).thenReturn(summonerDummy);

            when(requestMock.searchSummonerRequest(input_api_key, summonerName, "online")).thenReturn("");
            when(jsonModel.offlineParseSummoner()).thenReturn(summonerDummy);
            when(statusStrategy.searchSummoner(input_api_key, summonerName, requestMock, jsonModel)).thenReturn(summonerDummy);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        // then
        model.injectDb(dbMock);
        model.injectRequest(requestMock);
        model.injectJson(jsonModel);
        model.injectStrategy(statusStrategy);
        model.registerCreditObserver(creditObserver);

        Summoner summoner = model.handleRequests(summonerName, true);
        verify(dbMock).getSummoner(summonerName);
    } 

    @Test
    public void testGetCachedLeaguesData() {
        String input_api_key = "test";
        String summonerName = "hypebooba";

        DatabaseModel dbMock = mock(DatabaseModel.class);
        model.setInputStatus("online");

        // when
        try {            
            Gson gson = new Gson();
            File file = new File("src/main/java/major_project/Model/DummyLeagues.json");

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            League[] leagues = Arrays.stream(gson.fromJson(bufferedReader, League[].class)).filter(league -> league.getLeagueId() != null).toArray(League[]::new);

            when(dbMock.getLeagues("ge7L5_IvQUUA9xy-1iVL1MhMI6ewem6YbXgF37KjfM2U")).thenReturn(leagues);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        // then
        model.injectDb(dbMock);

        League[] leagues = model.getLeagues("ge7L5_IvQUUA9xy-1iVL1MhMI6ewem6YbXgF37KjfM2U", true);
        assertNotNull(leagues);
        assertThat(leagues.length, equalTo(2));

        League league1 = leagues[0];
        League league2 = leagues[1];

        // League{queueType='RANKED_FLEX_SR', summonerId='ge7L5_IvQUUA9xy-1iVL1MhMI6ewem6YbXgF37KjfM2U', summonerName='Hypebooba', leaguePoints=100, wins=6, losses=6, veteran=false, inactive=false, freshBlood=false, hotStreak=false, leagueId='5d8b7e50-c459-44d5-bfcb-eca8e51a64bd', tier='PLATINUM', rank='I', miniSeries=MiniSeries{target=3, wins=0, losses=1, progress=LNNNN}}

        assertEquals(league1.getQueueType(), "RANKED_FLEX_SR");
        assertEquals(league1.getSummonerId(), "ge7L5_IvQUUA9xy-1iVL1MhMI6ewem6YbXgF37KjfM2U");
        assertEquals(league1.getSummonerName(), "Hypebooba");
        assertEquals(league1.getLeaguePoints(), 100);
        assertEquals(league1.getWins(), 6);
        assertEquals(league1.getLosses(), 6);
        assertEquals(league1.getVeteran(), false);
        assertEquals(league1.getInactive(), false);
        assertEquals(league1.getFreshBlood(), false);
        assertEquals(league1.getHotStreak(), false);
        assertEquals(league1.getLeagueId(), "5d8b7e50-c459-44d5-bfcb-eca8e51a64bd");
        assertEquals(league1.getTier(), "PLATINUM");
        assertEquals(league1.getRank(), "I");
        assertEquals(league1.getMiniSeries().getTarget(), 3);
        assertEquals(league1.getMiniSeries().getWins(), 0);
        assertEquals(league1.getMiniSeries().getLosses(), 1);
        assertEquals(league1.getMiniSeries().getProgress(), "LNNNN");


        //League{queueType='RANKED_SOLO_5x5', summonerId='ge7L5_IvQUUA9xy-1iVL1MhMI6ewem6YbXgF37KjfM2U', summonerName='Hypebooba', leaguePoints=75, wins=118, losses=98, veteran=false, inactive=false, freshBlood=false, hotStreak=false, leagueId='4c343be4-2204-4507-acd4-17b1974046b4', tier='DIAMOND', rank='II', miniSeries=null}
        assertEquals(league2.getQueueType(), "RANKED_SOLO_5x5");
        assertEquals(league2.getSummonerId(), "ge7L5_IvQUUA9xy-1iVL1MhMI6ewem6YbXgF37KjfM2U");
        assertEquals(league2.getSummonerName(), "Hypebooba");
        assertEquals(league2.getLeaguePoints(), 96);
        assertEquals(league2.getWins(), 117);
        assertEquals(league2.getLosses(), 96);
        assertEquals(league2.getVeteran(), false);
        assertEquals(league2.getInactive(), false);
        assertEquals(league2.getFreshBlood(), false);
        assertEquals(league2.getHotStreak(), true);
        assertEquals(league2.getLeagueId(), "4c343be4-2204-4507-acd4-17b1974046b4");
        assertEquals(league2.getTier(), "DIAMOND");
        assertEquals(league2.getRank(), "II");
        assertEquals(league2.getMiniSeries(), null);       

        verify(dbMock).getLeagues("ge7L5_IvQUUA9xy-1iVL1MhMI6ewem6YbXgF37KjfM2U");
    }


    @Test
    public void testCheckNameIsEmpty() {
        model.checkNameIsEmpty("Hypebooba");
    }

    @Test
    public void testCheckNameIsEmpty2() {
        assertThrows(new IllegalArgumentException("Summoner name cannot be empty").getClass(), () -> {
            model.checkNameIsEmpty("");
        });
    }

    @Test
    public void testCheckSummonerNameExists() {
        GameModelObserver observerMock = mock(GameModelObserver.class);
        DatabaseModel dbModel = mock(DatabaseModel.class);
        model.setInputStatus("online");


        // when
        try {            
            Gson gson = new Gson(); 
            File file = new File("src/main/java/major_project/Model/DummySummoner.json");

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            Summoner summonerDummy = gson.fromJson(bufferedReader, Summoner.class);

            when(dbModel.checkSummonerNameExists("Hypebooba")).thenReturn(true);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
 
        model.registerObserver(observerMock);
        model.injectDb(dbModel);

        // then
        model.checkSummonerNameExists("Hypebooba");

        verify(dbModel).checkSummonerNameExists("Hypebooba");
        verify(observerMock).update(false);
    }


    @Test
    public void testCheckSummonerNameNotExists() {
        GameModelObserver observerMock = mock(GameModelObserver.class);
        DatabaseModel dbModel = mock(DatabaseModel.class);
        model.setInputStatus("online");


        // when
        try {            
            Gson gson = new Gson(); 
            File file = new File("src/main/java/major_project/Model/DummySummoner.json");

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            Summoner summonerDummy = gson.fromJson(bufferedReader, Summoner.class);

            when(dbModel.checkSummonerNameExists("px")).thenReturn(false);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
 
        model.registerObserver(observerMock);
        model.injectDb(dbModel);

        // then
        model.checkSummonerNameExists("px");

        verify(dbModel).checkSummonerNameExists("px");
        verify(observerMock).update(true);
    }

    @Test
    public void testMatchSummoner() {
        String input_api_key = "test";
        String summonerName = "hypebooba";

        DatabaseModel dbMock = mock(DatabaseModel.class);
        model.setInputStatus("online");

        // when
        try {            
            Gson gson = new Gson(); 
            File file = new File("src/main/java/major_project/Model/DummySummoner.json");

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            Summoner summonerDummy = gson.fromJson(bufferedReader, Summoner.class);

            when(dbMock.getAllSummoners()).thenReturn(new Summoner[] { summonerDummy });
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        // then
        model.injectDb(dbMock);

        Summoner[] summoner = model.getAllSummoners();
        assertEquals(1, summoner.length);

        String expected = "Summoner{id='ge7L5_IvQUUA9xy-1iVL1MhMI6ewem6YbXgF37KjfM2U', accountId='_aW4sn3PirLRowz9bh96bqfNJRqB93BbYb6xmL7DT99R8vw', puuid='nb1yNtUk13go1HH2qyoxMHKDlMlUAWX_wd7y7rFqwfjcpbYKemgEzFOfgFblBeR4Q59D1jQNAEuXcw', name='Hypebooba', profileIconId=4968, revisionDate=1652352727000, summonerLevel=473}";
        String actual = summoner[0].toString();
        assertEquals(expected, actual);

        when(dbMock.getAllSummoners()).thenReturn(new Summoner[] {});   // simulate clear cache

        model.matchSummoner(summoner, summonerName);

        summoner = model.getAllSummoners();
        assertEquals(0, summoner.length);

        verify(dbMock, times(2)).getAllSummoners();
    }

    @Test
    public void testRedditLogin() {
        String input_api_key = "test";
        String body = "grant_type=password&username=me&password=123";

        StatusStrategy statusStrategy = mock(StatusStrategy.class);
        RequestModel requestMock = mock(RequestModel.class);
        JsonModel jsonModel = mock(JsonModel.class);

        model.setOutputStatus("online");

        // when
        try {            
            Gson gson = new Gson(); 
            File file = new File("src/main/java/major_project/Model/DummyRedditToken.json");

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            RedditToken redditTokenDummy = gson.fromJson(bufferedReader, RedditToken.class);
            // public RedditToken loginRequest(String data, String key, String secret, RequestModel requestModel, JsonModel jsonModel) {
           
            when(statusStrategy.loginRequest(body, requestMock, jsonModel)).thenReturn(redditTokenDummy);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        // then
        model.injectStrategy(statusStrategy);
        model.injectRequest(requestMock);
        model.injectJson(jsonModel);

        String accessToken = model.login("me", "123");
        System.out.println("Access token: " + accessToken);
        assertEquals("1672722548730-rcxIl3hdEZJmt_tsFzP5N5wxOT1Qiw", accessToken);

        verify(statusStrategy).loginRequest(body, requestMock, jsonModel);
    }

    @Test
    public void testRedditPost() {
        String accessToken = "1672722548730-rcxIl3hdEZJmt_tsFzP5N5wxOT1Qiw";

        StatusStrategy statusStrategy = mock(StatusStrategy.class);
        RequestModel requestMock = mock(RequestModel.class);
        JsonModel jsonModel = mock(JsonModel.class);

        model.setOutputStatus("online");

        // when
        try {            
            Gson gson = new Gson(); 
            File file = new File("src/main/java/major_project/Model/DummyRedditToken.json");

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            RedditToken redditTokenDummy = gson.fromJson(bufferedReader, RedditToken.class);
            // public RedditToken loginRequest(String data, String key, String secret, RequestModel requestModel, JsonModel jsonModel) {
            
            // when(statusStrategy.postRequest("", accessToken, requestMock, jsonModel)).thenReturn();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        // then
        model.injectStrategy(statusStrategy);
        model.injectRequest(requestMock);
        model.injectJson(jsonModel);
        model.setAccessToken(accessToken);
        model.setUsername("me");

        model.post("Hello");
        String expectedBody = "sr=u_me&api_type=json&title=Summoner+Output+Message&text=Hello&kind=self&resubmit=true&send_replies=true";
        verify(statusStrategy).postRequest(expectedBody, accessToken, requestMock, jsonModel);
    }

    @Test
    public void testRedditPostNull() {
        String accessToken = "1672722548730-rcxIl3hdEZJmt_tsFzP5N5wxOT1Qiw";

        StatusStrategy statusStrategy = mock(StatusStrategy.class);
        RequestModel requestMock = mock(RequestModel.class);
        JsonModel jsonModel = mock(JsonModel.class);

        model.setOutputStatus("online");

        // when
        try {            
            Gson gson = new Gson(); 
            File file = new File("src/main/java/major_project/Model/DummyRedditToken.json");

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            RedditToken redditTokenDummy = gson.fromJson(bufferedReader, RedditToken.class);
            // public RedditToken loginRequest(String data, String key, String secret, RequestModel requestModel, JsonModel jsonModel) {
            
            // when(statusStrategy.postRequest("", accessToken, requestMock, jsonModel)).thenReturn();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        // then
        model.injectStrategy(statusStrategy);
        model.injectRequest(requestMock);
        model.injectJson(jsonModel);
        // model.setAccessToken(accessToken);
        model.setUsername("me");

        assertThrows(IllegalStateException.class, () -> {
            model.post("Hello");
        });
    }

    @Test
    public void testZeroCredit() {
        String summonerName = "hypebooba";
        CreditObservers creditObserver = mock(CreditObservers.class);
        
        // then
        model.registerCreditObserver(creditObserver);
        model.setInputStatus("offline");
        
        assertThrows(NotEnoughCreditException.class, () -> {
            model.searchSummoner(summonerName);
        });
    } 

    @Test
    public void testNonZeroCredit() {
        String summonerName = "hypebooba";
        CreditObservers creditObserver = mock(CreditObservers.class);
        
        // then
        model.registerCreditObserver(creditObserver);
        model.setInputStatus("offline");
        model.saveCredit("1");
        assertDoesNotThrow(() -> {
            model.searchSummoner(summonerName);
        });
    } 

    @Test
    public void testSaveCreditAlphabet() {
        CreditObservers creditObserver = mock(CreditObservers.class);
        
        // then
        model.registerCreditObserver(creditObserver);
        model.setInputStatus("online");

        
        assertThrows(IllegalArgumentException.class, () -> {
            model.saveCredit("f");
        });
    }

    @Test
    public void testSaveCreditFloat() {
        CreditObservers creditObserver = mock(CreditObservers.class);
        
        // then
        model.registerCreditObserver(creditObserver);
        model.setInputStatus("online");

        assertThrows(IllegalArgumentException.class, () -> {
            model.saveCredit("0.5");
        });
    }

    @Test
    public void testSaveCreditNotInRange() {
        CreditObservers creditObserver = mock(CreditObservers.class);
        
        // then
        model.registerCreditObserver(creditObserver);
        model.setInputStatus("online");

        assertThrows(InputMismatchException.class, () -> {
            model.saveCredit("11");
        });
    }

    @Test
    public void testSaveMinCreditSuccess() {
        CreditObservers creditObserver = mock(CreditObservers.class);
        
        // then
        model.registerCreditObserver(creditObserver);
        model.setInputStatus("online");

        assertDoesNotThrow(() -> {
            model.saveCredit("1");
        });
        assert(model.getRemainingCredit() == 1);
    }

    @Test
    public void testSaveMaxCreditSuccess() {
        CreditObservers creditObserver = mock(CreditObservers.class);
        
        // then
        model.registerCreditObserver(creditObserver);
        model.setInputStatus("online");

        assertDoesNotThrow(() -> {
            model.saveCredit("10");
        });
        assert(model.getRemainingCredit() == 10);
    }
    
    @Test
    public void testCreditObserverNotified() {
        String input_api_key = "test";
        String summonerName = "hypebooba";

        RequestModel requestMock = mock(RequestModel.class);
        JsonModel jsonModel = mock(JsonModel.class);
        StatusStrategy statusStrategy = mock(StatusStrategy.class);
        CreditObservers creditObserver = mock(CreditObservers.class);

        // when
        try {            
            Gson gson = new Gson(); 
            File file = new File("src/main/java/major_project/Model/DummySummoner.json");

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            Summoner summonerDummy = gson.fromJson(bufferedReader, Summoner.class);

            when(requestMock.searchSummonerRequest(input_api_key, summonerName, "offline")).thenReturn("");
            when(jsonModel.offlineParseSummoner()).thenReturn(summonerDummy);

            when(statusStrategy.searchSummoner(input_api_key, summonerName, requestMock, jsonModel)).thenReturn(summonerDummy);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        // then
        model.injectRequest(requestMock);
        model.injectJson(jsonModel);
        model.registerCreditObserver(creditObserver);

        model.setInputStatus("offline");
        model.injectStrategy(statusStrategy);
        model.handleRequests(summonerName, false);

        verify(creditObserver).update();
        verify(statusStrategy).searchSummoner(input_api_key, summonerName, requestMock, jsonModel); // first request
    }

    @Test
    public void testCreditDecremented() {
        String input_api_key = "test";
        String summonerName = "hypebooba";

        RequestModel requestMock = mock(RequestModel.class);
        JsonModel jsonModel = mock(JsonModel.class);
        StatusStrategy statusStrategy = mock(StatusStrategy.class);
        CreditObservers creditObserver = mock(CreditObservers.class);

        // when
        try {            
            Gson gson = new Gson(); 
            File file = new File("src/main/java/major_project/Model/DummySummoner.json");

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            Summoner summonerDummy = gson.fromJson(bufferedReader, Summoner.class);

            when(requestMock.searchSummonerRequest(input_api_key, summonerName, "offline")).thenReturn("");
            when(jsonModel.offlineParseSummoner()).thenReturn(summonerDummy);

            when(statusStrategy.searchSummoner(input_api_key, summonerName, requestMock, jsonModel)).thenReturn(summonerDummy);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        // then
        model.injectRequest(requestMock);
        model.injectJson(jsonModel);
        model.registerCreditObserver(creditObserver);

        model.setInputStatus("offline");
        model.injectStrategy(statusStrategy);

        model.saveCredit("5");
        assert(model.getRemainingCredit() == 5);
        model.handleRequests(summonerName, false);
        assert(model.getRemainingCredit() == 4);

        verify(creditObserver).update();
        verify(statusStrategy).searchSummoner(input_api_key, summonerName, requestMock, jsonModel); // first request
    }

    @Test
    public void testSearchTwiceFail() {
        String input_api_key = "test";
        String summonerName = "hypebooba";

        RequestModel requestMock = mock(RequestModel.class);
        JsonModel jsonModel = mock(JsonModel.class);
        StatusStrategy statusStrategy = mock(StatusStrategy.class);
        CreditObservers creditObserver = mock(CreditObservers.class);

        // when
        try {            
            Gson gson = new Gson(); 
            File file = new File("src/main/java/major_project/Model/DummySummoner.json");

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            Summoner summonerDummy = gson.fromJson(bufferedReader, Summoner.class);

            when(requestMock.searchSummonerRequest(input_api_key, summonerName, "offline")).thenReturn("");
            when(jsonModel.offlineParseSummoner()).thenReturn(summonerDummy);

            when(statusStrategy.searchSummoner(input_api_key, summonerName, requestMock, jsonModel)).thenReturn(summonerDummy);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        // then
        model.injectRequest(requestMock);
        model.injectJson(jsonModel);
        model.registerCreditObserver(creditObserver);

        model.setInputStatus("offline");
        model.injectStrategy(statusStrategy);

        model.saveCredit("1");
        assertDoesNotThrow(() -> {
            model.searchSummoner(summonerName);
            model.handleRequests(summonerName, false);
        });

        assertThrows(NotEnoughCreditException.class, () -> {
            model.searchSummoner(summonerName);
            model.handleRequests(summonerName, false);
        });

        verify(creditObserver).update();
        verify(statusStrategy).searchSummoner(input_api_key, summonerName, requestMock, jsonModel); // first request
    }

    @Test
    public void testSearchTwiceSuccess() {
        String input_api_key = "test";
        String summonerName = "hypebooba";

        RequestModel requestMock = mock(RequestModel.class);
        JsonModel jsonModel = mock(JsonModel.class);
        StatusStrategy statusStrategy = mock(StatusStrategy.class);
        CreditObservers creditObserver = mock(CreditObservers.class);

        // when
        try {            
            Gson gson = new Gson(); 
            File file = new File("src/main/java/major_project/Model/DummySummoner.json");

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            Summoner summonerDummy = gson.fromJson(bufferedReader, Summoner.class);

            when(requestMock.searchSummonerRequest(input_api_key, summonerName, "offline")).thenReturn("");
            when(jsonModel.offlineParseSummoner()).thenReturn(summonerDummy);

            when(statusStrategy.searchSummoner(input_api_key, summonerName, requestMock, jsonModel)).thenReturn(summonerDummy);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        // then
        model.injectRequest(requestMock);
        model.injectJson(jsonModel);
        model.registerCreditObserver(creditObserver);

        model.setInputStatus("offline");
        model.injectStrategy(statusStrategy);

        model.saveCredit("2");
        assertDoesNotThrow(() -> {
            model.searchSummoner(summonerName);
            model.handleRequests(summonerName, false);

            model.searchSummoner(summonerName);
            model.handleRequests(summonerName, false);
        });

        verify(creditObserver, times(2)).update();
        verify(statusStrategy, times(2)).searchSummoner(input_api_key, summonerName, requestMock, jsonModel); // first request
    }

}
 