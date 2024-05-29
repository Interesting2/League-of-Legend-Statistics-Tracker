package major_project.Presenter;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;

import major_project.Model.GameModel;
import major_project.Model.League;
import major_project.Model.NotEnoughCreditException;
import major_project.Model.Summoner;
import major_project.View.GameView;


/** 
 * This is the presenter class that handles main view
 */
public class GamePresenter {
    
    private GameView view;
    private GameModel model;

    private CacheHitPresenter cacheHitPresenter;
    private PlayerDataPresenter playerDataPresenter;
    private RedditPresenter redditPresenter;

    private String summonerName;
    private boolean searchBarFocused;
    
    public GamePresenter(GameView gameView, GameModel gameModel) {
        this.view = gameView;
        this.model = gameModel;
        this.summonerName = "";

        // set up the view
        view.setPresenter(this);
        cacheHitPresenter = new CacheHitPresenter(model, view);
        playerDataPresenter = new PlayerDataPresenter(model, view);
        redditPresenter = new RedditPresenter(model, view);
    }

    public String getLastSummoner() {
        return model.getLastSummoner().getName();
    }

    public void removeLastData() {
        model.removeLastData();
    }

    public void setSummonerName(String summonerName) {
        this.summonerName = summonerName;
    }

    public boolean getSearchBarFocus() {
        return searchBarFocused;
    }

    public void setSearchBarFocus(boolean searchBarFocused) {
        this.searchBarFocused = searchBarFocused;
    }

    /** 
     * This method is used to communicate with model and display summoner information
     * @throws NotEnoughCreditException if user doesn't have enough credit
     */
    public void searchSummoner() {
        try {
            model.checkNameIsEmpty(summonerName); 
            model.searchSummoner(summonerName);
        } catch (IllegalArgumentException e) {
            view.displayError(e.getMessage());
            return;
        } catch (IllegalStateException e) {
            view.displayError(e.getMessage());
            return;
        } catch (NotEnoughCreditException e) {
            view.displayError(e.getMessage());
            return;
        }

        boolean cacheState = cacheHitPresenter.getCacheState();
        System.out.println("Cache hit state: " + cacheState);

        if (!cacheState) {
            view.displayProgressIndicator(true);
        }

        Runnable searchSummoner = () -> {
            try {
                // Thread.sleep(1000);
                final Summoner summoner = model.handleRequests(summonerName, cacheState);
                System.out.println(summoner);
                
                final String summonerId = summoner.getId();
                final League[] leagues = model.getLeagues(summonerId, cacheState);
                final boolean condition = model.validateData(summoner, leagues);
                                
                // Thread.sleep(20);
                Platform.runLater(() -> {
                    // hide progressIndicator 
                    // view.displayProgressIndicator(false);
                    this.summonerName = "";
                    this.searchBarFocused = false;

                    playerDataPresenter.setUpStage();
                    if (condition) {
                        System.out.println("Displaying summoner data\n\n\n");
                        playerDataPresenter.displaySummonerData(summoner, leagues);  
                        playerDataPresenter.setUpLeagues(leagues);
                        System.out.println("Displayed summoner data\n\n\n");
                    }
                    else {
                        System.out.println("Displaying summoner data\n\n\n");
                        playerDataPresenter.displaySummonerData(summoner, null);    
                        playerDataPresenter.setUpLeagues(null);
                        System.out.println("Displayed summoner data\n\n\n");
                    }
                    this.summonerName = getLastSummoner();
                    this.removeLastData();
                });
            
            } catch (IllegalArgumentException ae) {
                // throw new IllegalArgumentException(ae);
                Platform.runLater(() -> {
                    view.displayProgressIndicator(false);
                    view.displayError(ae.getMessage());
                });

                
            } catch (IllegalStateException e) {
                Platform.runLater(() -> {
                    view.displayProgressIndicator(false);
                    view.displayError(e.getMessage());
                });
                // throw new IllegalStateException(e);
            } 
        };
        Thread searchSummonerThread = new Thread(searchSummoner);
        searchSummonerThread.setDaemon(true);
        searchSummonerThread.start();     
    }

    public void setUpTwilio(String message) {
        System.out.println("Setting up Twilion auth");

        String key = System.getenv("TWILIO_API_KEY");
        String sid = System.getenv("TWILIO_API_SID");
        String fromPhone = System.getenv("TWILIO_API_FROM");
        String toPhone = System.getenv("TWILIO_API_TO");

        System.out.println(message);

        // concurrency for post request
        System.out.println("Set visible\n\n");

        // set visible for the last progress indicator in the list
        view.displayProgressIndicator(true); 

        Runnable sendMessage = () -> {
            try {
                model.sendMessage(key, sid, fromPhone, toPhone, message);
            } catch (IllegalStateException e) {
                Platform.runLater(() -> {
                    view.displayProgressIndicator(false);
                    view.displayError(e.getMessage());
                });
            }
            Platform.runLater(() -> {
                view.displayProgressIndicator(false);
                System.out.println("Disvisible\n\n");
            });
        };

        Thread sendMessageThread = new Thread(sendMessage);
        sendMessageThread.setDaemon(true);
        sendMessageThread.start();
    }

    public Summoner[] getAllSummoners() {
        Summoner[] summoners = model.getAllSummoners();
        System.out.println(summoners);

        // validate
        if (summoners == null) {
            throw new IllegalStateException("Caching is only available when input argument is set to online");
        } 
        if (summoners.length == 0) {
            throw new IllegalStateException("No cached summoners found");
        } 
        return summoners;
    }

    public String[] getNames(Summoner[] summoners) {
        List<String> summonerList = new ArrayList<>();
        for (Summoner summoner : summoners) {
            summonerList.add(summoner.getName());
        }
        return summonerList.toArray(new String[summonerList.size()]);
    } 


    public void matchSummoner(Summoner[] summoners, String choice) {
        model.matchSummoner(summoners, choice);
    } 

    public void login() {
        redditPresenter.login();
    }

    public void post() {
        redditPresenter.post();
    }

    public void sendOutput(String option, String message) {
        if (option.equals("Twilio")) {
            setUpTwilio(message);
        }
        else if (option.equals("Reddit")) {
            redditPresenter.postRequest(message);
        }
    }

    public void checkRedditEnv(String message) {
        boolean isSet = model.checkRedditEnv();
        if (isSet) {
            view.showOutputOptions(message);
        } else {
            setUpTwilio(message);
        }
    }
}
