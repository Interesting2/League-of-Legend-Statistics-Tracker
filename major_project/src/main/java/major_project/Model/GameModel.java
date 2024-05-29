package major_project.Model;

import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;

import java.util.List;
import java.util.ArrayList;

import java.util.InputMismatchException;



/** 
 * This class is the main model class
 */
public class GameModel {

    private String input_api_key;

    private List<Summoner> summoner;
    private List<League[]> leagues;

    private String inputStatus;
    private String outputStatus;

    private RequestModel requestModel;
    private DatabaseModel dbModel;
    private JsonModel jsonModel;

    private GameModelObserver gameModelObserver;
    private CreditObservers creditObserver;
    private StatusStrategy statusStrategy;

    private String accessToken;
    private String username;

    private int remainingCredit;

    /**
     * This constructor is used for testing
    */ 
    public GameModel() {
        this.input_api_key = "test";
        this.summoner = new ArrayList<Summoner>();
        this.leagues = new ArrayList<League[]>();  
        this.remainingCredit = 0;
    }
    
    /**
     * This constructor is used for the main program
     * @param input_api_key riot games api key
     * @param outputStatus The offline/online output status of the program
    */ 
     public GameModel(String inputStatus, String outputStatus) {
        input_api_key = System.getenv("INPUT_API_KEY");
        this.summoner = new ArrayList<Summoner>();
        this.leagues = new ArrayList<League[]>();

        this.inputStatus = inputStatus;
        this.outputStatus = outputStatus;
        this.chooseStrategy();

        this.requestModel = new RequestModel();
        this.dbModel = new DatabaseModel();
        this.jsonModel = new JsonModel();

    }

    private void chooseStrategy() {
        if (this.inputStatus.equals("online")) {
            this.statusStrategy = new OnlineStrategy();
        } else {
            this.statusStrategy = new OfflineStrategy();
        }
    }


    public void injectStrategy(StatusStrategy statusStrategy) {
        this.statusStrategy = statusStrategy;
    }

    /**
     * This method is used to inject jsonModel to this class
    */
    public void injectJson(JsonModel jsonModel) {
        this.jsonModel = jsonModel;
    }   

    /**
     * This method is for injecting the request model to this class
    */ 
    public void injectRequest(RequestModel requestModel) {
        this.requestModel = requestModel;
    }

    /**
     * This method is for injecting the database model to this class
    */ 
    public void injectDb(DatabaseModel dbModel) {
        this.dbModel = dbModel;
    }

    public void setInputStatus(String inputStatus) {
        this.inputStatus = inputStatus;
        if (inputStatus.equals("online")) {
            statusStrategy = new OnlineStrategy();
        } else {
            statusStrategy = new OfflineStrategy();
        }
    }

    public void setOutputStatus(String outputStatus) {
        this.outputStatus = outputStatus;
    }

    public String getInputStatus() {
        return inputStatus;
    }

    public String getOutputStatus() {
        return outputStatus;
    }

    // for testing purpose
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    // for testing purpose
    public void setUsername(String username) {
        this.username = username;
    }

    /** 
     * This method is used to form a short message and send back to the view class
     * @param league The league data of a summoner to send the request to
     * @return The short message response 
    */
    public String getShortMessage(League league) {

        if (outputStatus.equals("offline")) {
            String message = "Summoner Name: Hypebooba\nSummoner Level: 473\nWin %: 50.0";
            return message;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Summoner Name: ");
        sb.append(summoner.get(summoner.size() -1).getName());
        sb.append("\n");
        sb.append("Summoner Level: ");
        sb.append(summoner.get(summoner.size() - 1).getSummonerLevel());
        sb.append("\n");
        sb.append("Win %: ");
        sb.append(Double.toString(Math.round(Double.valueOf(league.getWins()) / Double.valueOf(league.getWins() + league.getLosses()) * 100)));

        return sb.toString();
    }

    /** 
     * This method is used to form a long message and send back to the view class
     * @return The long message response
    */ 
    public String getLongMessage() {
        if (outputStatus.equals("offline")) {
            String message = "Summoner Name: Hypebooba\nSummoner Level: 473\nRANKED_FLEX_SR\n\tWins: 6\n\tLosses: 6\n\tWin %: 50.0";
            return message;
        }

        StringBuilder sb = new StringBuilder();

        sb.append("Summoner Name: ");
        sb.append(summoner.get(summoner.size() - 1).getName());
        sb.append("\n");
        sb.append("Summoner Level: ");
        sb.append(summoner.get(summoner.size() - 1).getSummonerLevel());

        // for each queue type, append the wins, losses, and win%
        for (League league : leagues.get(leagues.size() - 1)) {
            sb.append("\n");
            sb.append(league.getQueueType());
            sb.append("\n");
            sb.append("\tWins: ");
            sb.append(league.getWins());
            sb.append("\n");
            sb.append("\tLosses: ");
            sb.append(league.getLosses());
            sb.append("\n");
            sb.append("\tWin %: ");
            sb.append(Double.toString(Math.round(Double.valueOf(league.getWins()) / Double.valueOf(league.getWins() + league.getLosses()) * 100)));
        }

        return sb.toString();
    }

    public Summoner getLastSummoner() {
        return summoner.get(summoner.size() - 1);
    }

    public League[] getLastLeague() {
        return leagues.get(leagues.size() - 1);
    }

    public int getSummonerLength() {
        return summoner.size();
    }

    public int getLeaguesLength() {
        return leagues.size();
    }

    /** 
     * This method is used to check whether there are league data correspond to a given summoner
     * @param summoner The summoner to check
     * @param leagues The leagues of the summoner
     * @return Whether the summoner has league data
     */
    public boolean validateData(Summoner summoner, League[] leagues) {
        
        if (leagues.length == 0) {
            this.summoner.add(summoner);
            this.leagues.add(leagues);
            // displayError("No League Data Found for this summonner");
            return false;

        }
        else {
            this.summoner.add(summoner);
            this.leagues.add(leagues);
            return true;          
        }
    }

    public void removeLastData() {
        this.summoner.remove(this.summoner.size() - 1);
        this.leagues.remove(this.leagues.size() - 1);
    }


    /** 
     * This method is used to send the short message to the server
     * @param key The twilio api key
     * @param sid the twilio api sid
     * @param fromPhone the phone number to send the message from
     * @param toPhone the phone number to send the message to
     * @param message the message to send
    */ 
    public void sendMessage(String key, String sid, String fromPhone, String toPhone, String message) {

        if (outputStatus.equals("offline")) {
            throw new IllegalStateException("Offline mode is on");
        }

        StringBuilder data = new StringBuilder();
        data.append(URLEncoder.encode("From", StandardCharsets.UTF_8));
        data.append("=");
        data.append(URLEncoder.encode(fromPhone, StandardCharsets.UTF_8));
        data.append("&");
        data.append(URLEncoder.encode("To", StandardCharsets.UTF_8));
        data.append("=");
        data.append(URLEncoder.encode(toPhone, StandardCharsets.UTF_8));
        data.append("&");
        data.append(URLEncoder.encode("Body", StandardCharsets.UTF_8));
        data.append("=");
        data.append(URLEncoder.encode(message, StandardCharsets.UTF_8));
        System.out.println(data.toString());

        this.statusStrategy.sendMessage(data.toString(), sid, key, requestModel);

    }

    /** 
     * This method is used to get the leagues information from the server if the input status is online
     * @param summonerId The summoner id to get the league information
     * @param useCache whether to cache the summoner data
     * @return The leagues information
    */ 
    public League[] getLeagues(String summonerId, boolean useCache) {
        System.out.println("Getting Leagues");
        if (input_api_key == null) {
            System.out.println("API Key is not set");
            System.exit(1);
        }
        if (useCache && inputStatus.equals("online")) {
            League[] leagues = dbModel.getLeagues(summonerId);
            return leagues;
        }

        League[] leagues = this.statusStrategy.getLeagues(input_api_key, summonerId, this.requestModel, this.jsonModel);
        
        if (leagues.length == 0) {
            return leagues;
        }

        if (leagues == null || leagues[0].getStatus() != null) {
            throw new IllegalStateException("Error Getting league information");
        }

        if (inputStatus.equals("online")) dbModel.saveLeague(summonerId, leagues);
        return leagues;
    }

    /** 
     * This method is to register a new observer to the model class
     */
    public void registerObserver(GameModelObserver observer) {
        this.gameModelObserver = observer;
    }

    /** 
     * This method is used to register a creditobserver to the model class
     */
    public void registerCreditObserver(CreditObservers observer) {
        this.creditObserver = observer;
    }

    /** 
     * This method is to notify the observer about the change in the model class
     */
    public void checkSummonerNameExists(String name) {
        try {
            if (inputStatus.equals("offline")) {
                return;
            }
            System.out.println("Name: " + name);

            boolean summonerExists = this.dbModel.checkSummonerNameExists(name);
            System.out.println("Summoner: " + summonerExists);
            // notify observer
            if (summonerExists) {
                System.out.println("Summoner exists in database");
                this.gameModelObserver.update(false);
            } else {
                System.out.println("Summoner doesn't exists in database");
                this.gameModelObserver.update(true);
            }
        } catch (IllegalStateException e) {
            throw e;
        }
    }

    /** 
     * This method is to check that the name is not a empty string
     */
    public void checkNameIsEmpty(String name) {
        System.out.println("Checking name is empty: " + name);
        if (name.equals("")) {
            throw new IllegalArgumentException("Summoner name cannot be empty");
        }

    }

    /** 
     * This method is to clear cache of a selected summoner
     */
    public void matchSummoner(Summoner[] summoners, String choice) {
        for (Summoner summoner : summoners) {
            if (summoner.getName().equals(choice)) {
                this.clearCache(summoner.getId());
                break;
            }
        }
    }

    /** 
     * This method is used to get the summoner information from the server if the input status is online
     * @param name The summoner name to get the summoner information
     * @throws NotEnoughCreditException if the remainingCredit is 0
     * @return The summoner information
    */
    public void searchSummoner(String name) throws NotEnoughCreditException{

        if (input_api_key == null) {
            System.out.println("API Key is not set");
            System.exit(1);
        }

        // check if search credit is 0
        if (remainingCredit == 0) {
            throw new NotEnoughCreditException("You have run out of search credits");
        } 
        checkSummonerNameExists(name);
    }

    /**
     * This method is used to save the initial credit that the user has entered
     * @param credit is the initial credit that the user has entered
     * @throws IllegalArgumentException if the credit number is not an integer
     * @throws InputMismatchException if the credit number is not between 1 and 10 (inclusively)
     */
    public void saveCredit(String credit) {
        // check if credit is a integer
        try {
            int creditInt = Integer.parseInt(credit);
            // check if creditInt is between 1 and 10
            if (creditInt >= 1 && creditInt <= 10) {
                this.remainingCredit = creditInt;
            } else {
                throw new InputMismatchException("Credit number must be between 1 and 10 (inclusively)");

            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Credit number must be an integer");
        }
    }

    /**
     * This method is used to get the remaining credit that the user has
     * This method is for testing purpose only
     * @return the remaining credit amount
     */
    public int getRemainingCredit() {
        return remainingCredit;
    }

    /**
     * This method controls the statusStrategy and returns a summoner depending on the strategy
     * Saves the summoner to database if required
     * It also modifies the remaining credit and notifies the concrete credit observers through the interface
     * @param name is the name of the summoner
     * @param useCache is the boolean to determine if the cache is used
     * @return the summoner object
     */
    public Summoner handleRequests(String name, boolean useCache){
        // GET Request
        if (input_api_key == null) {
            System.out.println("API Key is not set");
            System.exit(1);
        }

        remainingCredit --; // decrement the credit amount after each search
        this.creditObserver.update(); // update credit observer

        if (useCache && inputStatus.equals("online")) {
            System.out.println("Retrieving summoner from database");
            Summoner summoner = dbModel.getSummoner(name);

            return summoner;
        }

        Summoner summoner = this.statusStrategy.searchSummoner(input_api_key, name, this.requestModel, this.jsonModel);

        System.out.println("Returned Summoner in model ");
        System.out.println(summoner);
        
        if (summoner == null || summoner.getStatus() != null) {
            // don't cache this summoner
            // throw an exception
            System.out.println("Error fetching the summoner");
            throw new IllegalStateException("Error fetching the summoner");
        } 

        if (inputStatus.equals("online")) dbModel.saveSummoner(summoner);

        return summoner;   
    }


    /** 
     * This method is used to get all the cached summoner data from the database
     * @return The cached summoners' data
     */
    public Summoner[] getAllSummoners() {
        System.out.println("Get All Summoners");

        if (inputStatus.equals("offline")) {
            return null;
        }

        Summoner[] summoners = dbModel.getAllSummoners();
        return summoners;
    }

    /**
     * This method is used to remove all cached data related / linked to the summonerId
     */
    public void clearCache(String summonerId) {
        dbModel.clearCache(summonerId);
    }

    public String login(String username, String pwd) {

        if (outputStatus.equals("offline")) {
            throw new IllegalStateException("Offline mode is on");
        }
        // request a token
        System.out.println("Requesting a token for reddit login");
        
        // String username = "RiotGamesAPIGUI";
        // String pwd = "sDdxhYkw9hQg3x4";
        StringBuilder data = new StringBuilder();
        data.append(URLEncoder.encode("grant_type", StandardCharsets.UTF_8));
        data.append("=");
        data.append(URLEncoder.encode("password", StandardCharsets.UTF_8));
        data.append("&");
        data.append(URLEncoder.encode("username", StandardCharsets.UTF_8));
        data.append("=");
        data.append(URLEncoder.encode(username, StandardCharsets.UTF_8));
        data.append("&");
        data.append(URLEncoder.encode("password", StandardCharsets.UTF_8));
        data.append("=");
        data.append(URLEncoder.encode(pwd, StandardCharsets.UTF_8));
        System.out.println(data.toString());

        // String tmpKey = "lRFJm32ZjEOHsVmS3Jc6LA";
        // String tmpSecret = "QpH1m6Oi7a8gR6t6aO3IXXzvhcVqvg";
        try {
            RedditToken redditToken = this.statusStrategy.loginRequest(data.toString(), requestModel, jsonModel);
            if (redditToken != null) {
                String accessToken = redditToken.getAccessToken();
                this.accessToken = accessToken;
                this.username = username;
                return accessToken;
            }
        } catch (IllegalStateException e) {
            throw e;
        }
        return null;
    }

    public void post(String postText) {
        if (outputStatus.equals("offline")) {
            throw new IllegalStateException("Offline mode is on");
        }

        if (accessToken == null) {
            System.out.println("Access Token is null");
            throw new IllegalStateException("Access Token is null. Please Login");
        }
        System.out.println("Post text: " + postText);

        String redditUsername = "u_" + this.username;
        StringBuilder data = new StringBuilder();
        data.append(URLEncoder.encode("sr", StandardCharsets.UTF_8));
        data.append("=");
        data.append(URLEncoder.encode(redditUsername, StandardCharsets.UTF_8));
        data.append("&");
        data.append(URLEncoder.encode("api_type", StandardCharsets.UTF_8));
        data.append("=");
        data.append(URLEncoder.encode("json", StandardCharsets.UTF_8));
        data.append("&");
        data.append(URLEncoder.encode("title", StandardCharsets.UTF_8));
        data.append("=");
        data.append(URLEncoder.encode("Summoner Output Message", StandardCharsets.UTF_8));
        data.append("&");
        data.append(URLEncoder.encode("text", StandardCharsets.UTF_8));
        data.append("=");
        data.append(URLEncoder.encode(postText, StandardCharsets.UTF_8));
        data.append("&");
        data.append(URLEncoder.encode("kind", StandardCharsets.UTF_8));
        data.append("=");
        data.append(URLEncoder.encode("self", StandardCharsets.UTF_8));
        data.append("&");
        data.append(URLEncoder.encode("resubmit", StandardCharsets.UTF_8));
        data.append("=");
        data.append(URLEncoder.encode("true", StandardCharsets.UTF_8));
        data.append("&");
        data.append(URLEncoder.encode("send_replies", StandardCharsets.UTF_8));
        data.append("=");
        data.append(URLEncoder.encode("true", StandardCharsets.UTF_8));
        this.statusStrategy.postRequest(data.toString(), accessToken, requestModel, jsonModel);
    }

    public boolean checkRedditEnv() {
        String key = System.getenv("REDDIT_API_KEY");
        String secret = System.getenv("REDDIT_API_SECRET");
        if (key == null || secret == null) {
            System.out.println("Reddit API Key or Secret is not set");
            return false;
        }
        return true;
    }
}
