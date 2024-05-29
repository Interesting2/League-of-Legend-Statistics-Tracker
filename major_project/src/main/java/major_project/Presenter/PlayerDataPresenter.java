package major_project.Presenter;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import major_project.Model.GameModel;
import major_project.Model.League;
import major_project.Model.Summoner;
import major_project.View.GameView;
import major_project.View.PlayerDataView;

public class PlayerDataPresenter {

    private List<PlayerDataView> playerDataViews;
    private GameModel gameModel;
    private GameView gameView;

    public PlayerDataPresenter(GameModel gameModel, GameView gameView) {
        this.gameModel = gameModel;
        this.gameView = gameView;

        this.playerDataViews = new ArrayList<>();
    }

    public void setUpStage() {
        PlayerDataView playerDataView = new PlayerDataView(this);
        playerDataViews.add(playerDataView);
        playerDataView.setUpStage(gameView);
    }

    /** 
     * This method is to display the most recent player data view
     * @param summoner the summoner object
     * @param leagues List of league objects
     */
    public void displaySummonerData(Summoner summoner, League[] leagues) {
        playerDataViews.get(playerDataViews.size() - 1).displaySummonerData(summoner, leagues, gameView);
    }

    /** 
     * This method is to validate the list of league objects
     * @param leagues List of league objects
    */
    public void validateLeagues(League[] leagues) {
        PlayerDataView currView = playerDataViews.get(playerDataViews.size() - 1);
        if (leagues != null && leagues.length > 0) {   
            currView.setUpNavBar(gameView, true, true);
            currView.setUpLeagues(leagues, gameView.getLastProgressIndicator());
        } else {
            currView.setUpNavBar(gameView, false, true);
        }
    }

    /** 
     * This method is to display league information to scene
     * @param leagues List of league objects
    */
    public void setUpLeagues(League[] leagues) {
        this.validateLeagues(leagues);        
        System.out.println("\n\n\nNew Stage waiting\n\n\n");
        PlayerDataView currView = playerDataViews.get(playerDataViews.size() - 1);
        currView.showStage(); 
        
        // remove last progress indicator from list and last playerDataView
        gameView.removeLastProgressIndicator();
        playerDataViews.remove(playerDataViews.size() - 1);
        System.out.println("\n\n\nNew Stage closed\n\n\n");
    }

    /**
     * This method helps pass the output message to the model class
     * @param message The message to pass to the model class
    */ 
    public void setUpTwilio(String message) {
        System.out.println("Setting up Twilion auth");

        String key = System.getenv("TWILIO_API_KEY");
        String sid = System.getenv("TWILIO_API_SID");
        String fromPhone = System.getenv("TWILIO_API_FROM");
        String toPhone = System.getenv("TWILIO_API_TO");

        System.out.println(message);

        // set visible 
        gameView.displayProgressIndicator(true);
        Runnable sendMessage = () -> {
            try {
                gameModel.sendMessage(key, sid, fromPhone, toPhone, message);
            } catch (IllegalStateException e) {
                Platform.runLater(() -> {
                    gameView.displayProgressIndicator(false);
                    gameView.displayError(e.getMessage());
                });
            }

            Platform.runLater(() -> {
                gameView.displayProgressIndicator(false);
            });
        };

        Thread sendMessageThread = new Thread(sendMessage);
        sendMessageThread.start();
    }

    /**
     * This method helps pass the short output message from the model class to the setUpTwilio method
    */

    public String getShortMessage(League league) {
        return gameModel.getShortMessage(league);
    }

    /**
     * This method sends output to whatever output the user chose, such as Twilio or Reddit
    */
    public void sendOutput(String option, String message) {
        if (option.equals("Twilio")) {
            setUpTwilio(message);
        } else if (option.equals("Reddit")) {
            gameView.displayProgressIndicator(true); 

            Runnable sendMessage = () -> {
                try {
                    gameModel.post(message);
                } catch (IllegalStateException e) {
                    Platform.runLater(() -> {
                        gameView.displayError(e.getMessage());
                    });
                }
                Platform.runLater(() -> {
                    gameView.displayProgressIndicator(false);
                    System.out.println("Disvisible\n\n");
                });
            };

            Thread sendMessageThread = new Thread(sendMessage);
            sendMessageThread.setDaemon(true);
            sendMessageThread.start();
        }
    }

    /**
     * This method is to check whether the reddit env variables are set or not
     * @param message the message to send to the post request
    */
    public void checkRedditEnv(String message) {
        boolean isSet = gameModel.checkRedditEnv();
        if (isSet) {
            playerDataViews.get(playerDataViews.size() - 1).showOutputOptions(message);
        } else {
            setUpTwilio(message);
        }
    }
}
