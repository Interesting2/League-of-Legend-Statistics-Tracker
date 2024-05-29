package major_project.View;

import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;  
import javafx.collections.ObservableList; 
import javafx.scene.chart.PieChart; 

import major_project.Model.Summoner;
import major_project.Presenter.PlayerDataPresenter;
import major_project.Model.League;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.application.Platform;

/** 
 * This class is used to display the player data such as Summoner and their Leagues data view.
*/
public class PlayerDataView {

    private PlayerDataPresenter playerDataPresenter;
    private BorderPane topPane;
    private ScrollPane queueScrollContainer;
    private GridPane queueContainer;
    private HBox header;
    private Stage dataStage;
    /** 
     * This constructor is used to create a new PlayerDataView object and set the model
     */
    public PlayerDataView(PlayerDataPresenter playerDataPresenter) {
        this.playerDataPresenter = playerDataPresenter;
    }


    /**
     * This method gets the image of the corresponding tier
     * @param tier The tier of the summoner
     * @return The imageview of the tier
    */ 
    private ImageView getTierImage(String tier) {
        String tierLowerCase = tier.toLowerCase();
        String tierModified = tierLowerCase.substring(0, 1).toUpperCase() + tierLowerCase.substring(1);
        String tierImagePath = "/ranked-emblems/Emblem_" + tierModified + ".png";
        System.out.println(tierImagePath);
        return new ImageView(new Image(tierImagePath, 100, 100, false, true));
    }

    /**
     * This method helps to display the leagues data of the given summoner
     * @param leagues The leagues of the summoner
     * @param ProgressIndicator The spinning progress indicator of the scene
    */ 
    public void setUpLeagues(League[] leagues, ProgressIndicator progressIndicator) {
        int rowCount = 0;
        for (League league : leagues) {
            System.out.println(league);
            
            Label queueType = new Label(league.getQueueType());
            // blue color text
            queueType.setStyle("-fx-font-size: 20px;-fx-font-weight:bold;-fx-text-fill: #00a8ff;");
            GridPane splitPanel = new GridPane();
            // splitPanel.setHgap(10);
            // splitPanel.setVgap(10);
            splitPanel.setAlignment(Pos.CENTER);
            splitPanel.setPrefWidth(1100);
            // splitPanel.setStyle("-fx-background-color: rgba(255, 255, 100, 0.5);");

            
            // splitPanel.setStyle("-fx-background-color: RED;");
            ColumnConstraints cc1 = new ColumnConstraints();
            cc1.setHgrow(Priority.ALWAYS);
            ColumnConstraints cc2 = new ColumnConstraints();
            cc2.setHgrow(Priority.ALWAYS);
            ColumnConstraints cc3 = new ColumnConstraints(300);
            cc3.setHgrow(Priority.ALWAYS);

            splitPanel.getColumnConstraints().addAll(cc1, cc2, cc3);

            // splitPanel.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));
            // splitPanel.setGridLinesVisible(true);

            GridPane leftPanel = new GridPane();
            // leftPanel.setStyle("-fx-background-color: #00a8ff;");
            // leftPanel.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));
            leftPanel.setAlignment(Pos.CENTER);
            
            Label tierRank = new Label(league.getTier() + " " + league.getRank());
            // add color tierRank
            tierRank.setStyle("-fx-font-size: 20px;-fx-font-weight:bold;-fx-text-fill: purple;");

            // center tierRank horizontally 
            GridPane.setHalignment(tierRank, HPos.CENTER); 

            // emphasize tierImage by adding dropshadow
            ImageView tierImage = getTierImage(league.getTier());
            tierImage.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.8), 20, 0, 0, 0);");

            leftPanel.add(tierRank, 0, 0);
            leftPanel.add(tierImage, 0, 1);

            GridPane rightPanel = new GridPane();
            // rightPanel.setStyle("-fx-background-color: rgba(100, 255, 100, 0.5);");
            rightPanel.setVgap(20);

            Label leaguePoints = new Label("League Points: " + league.getLeaguePoints().toString());
            Label wins = new Label("Wins: " + league.getWins().toString());
            Label losses = new Label("Losses: " + league.getLosses().toString());
            Label veteran = new Label("Veteran: " + (league.getVeteran() == false ? "None" : "Yes"));
            Label inactive = new Label("Inactive: " + (league.getInactive() == false ? "None" : "Yes"));
            Label freshBlood = new Label("Fresh Blood: " + (league.getFreshBlood() == false ? "None" : "Yes"));
            Label hotStreak = new Label("Hotstreak: " + (league.getHotStreak() == false ? "None" : "Yes"));

            Label detailsSubTitle = new Label("Details");
            // orange text
            detailsSubTitle.setStyle("-fx-font-size: 20px;-fx-font-weight:bold;-fx-text-fill: #ffa500;");
            Label specialsSubTitle = new Label("Specials");
            specialsSubTitle.setStyle("-fx-font-size: 20px;-fx-font-weight:bold;-fx-text-fill: #ffa500;");
            Label promosSubTitle = new Label("Promos");
            promosSubTitle.setStyle("-fx-font-size: 20px;-fx-font-weight:bold;-fx-text-fill: #ffa500;");

            rightPanel.add(detailsSubTitle, 0, 0, 1, 1);
            rightPanel.add(leaguePoints, 0, 1, 1, 1);
            rightPanel.add(wins, 0, 2, 1, 1);
            rightPanel.add(losses, 0, 3, 1, 1);

            rightPanel.add(specialsSubTitle, 0, 4, 1, 1);
            rightPanel.add(veteran, 0, 5, 1, 1);
            rightPanel.add(inactive, 0, 6, 1, 1);
            rightPanel.add(freshBlood, 0, 7, 1, 1);
            rightPanel.add(hotStreak, 0, 8, 1, 1);

            rightPanel.add(promosSubTitle, 0, 9, 1, 1);
            if (league.getMiniSeries() == null) {
                rightPanel.add(new Label("No Promos"), 0, 10, 1, 1);
            } else {
                Label target = new Label("Promo matches required: " + Integer.toString(league.getMiniSeries().getTarget()));
                Label miniWins = new Label("Wins: " + Integer.toString(league.getMiniSeries().getWins()) + "/" + Integer.toString(league.getMiniSeries().getTarget()));
                Label miniLosses = new Label("Losses: " + Integer.toString(league.getMiniSeries().getLosses()) + "/" + Integer.toString(league.getMiniSeries().getTarget()));
                String progressString = league.getMiniSeries().getProgress();
                StringBuilder progressStringBuilder = new StringBuilder();

                Runnable processProgress = () -> {
                    for (char c : progressString.toCharArray()) {
                        if (c == 'W') {
                            progressStringBuilder.append("Won\n\t\t\t");
                        } else if (c == 'L') {
                            progressStringBuilder.append("Lost\n\t\t\t");
                        } else {
                            progressStringBuilder.append("None\n\t\t\t");
                        }
                    }
                    Platform.runLater(() -> {
                    });
                };
                Thread processStringThread = new Thread(processProgress);
                processStringThread.setDaemon(true);
                processStringThread.start();

                
                Label progress = new Label("Promo Progress: " + progressStringBuilder.toString());
                rightPanel.add(target, 0, 10, 1, 1);
                rightPanel.add(miniWins, 0, 11, 1, 1);
                rightPanel.add(miniLosses, 0, 12, 1, 1);
                rightPanel.add(progress, 0, 13, 1, 1);
            }

            GridPane chartPanel = new GridPane();
            // chartPanel.setStyle("-fx-background-color: rgba(255, 100, 100, 0.5);");

            Label chartSubTitle = new Label("Charts");
            chartSubTitle.setStyle("-fx-font-size: 20px;-fx-font-weight:bold;-fx-text-fill: #ffa500;");
            
            ObservableList<PieChart.Data> winChartData = FXCollections.observableArrayList(
                new PieChart.Data("Wins", league.getWins()),
                new PieChart.Data("Losses", league.getLosses())
            );
            ObservableList<PieChart.Data> loseChartData = FXCollections.observableArrayList(
                new PieChart.Data("Wins", league.getWins()),
                new PieChart.Data("Losses", league.getLosses())
            );
            
            PieChart winChart = new PieChart(winChartData);
            PieChart loseChart = new PieChart(loseChartData);
            int winPercentage = league.getWins() * 100 / (league.getWins() + league.getLosses());
            int losePercentage = league.getLosses() * 100 / (league.getWins() + league.getLosses());
            winChart.setTitle("Win Percentage(" + Integer.toString(winPercentage) + "%)");
            winChart.setStartAngle(180);
            winChart.setLabelsVisible(true); 
            loseChart.setTitle("Lose Percentage(" + Integer.toString(losePercentage) + "%)");
            loseChart.setLabelsVisible(true); 

            chartPanel.add(chartSubTitle, 0, 1, 1, 1);
            chartPanel.add(winChart, 0, 2, 1, 1);
            chartPanel.add(loseChart, 0, 3, 1, 1);
            

            splitPanel.add(leftPanel, 0, 0, 1, 1);
            splitPanel.add(rightPanel, 1, 0, 1, 1);
            splitPanel.add(chartPanel, 2, 0, 1, 1);

            queueContainer.add(queueType, 0, rowCount, 1, 1);
            
            Button shortOutput = new Button("Send Short Output");
            // same height as searchButton
            shortOutput.setPrefHeight(36);

            shortOutput.setOnAction(e -> {
                String shortMessage = playerDataPresenter.getShortMessage(league);
                playerDataPresenter.checkRedditEnv(shortMessage);
            });
            queueContainer.add(shortOutput, 0, rowCount + 1, 1, 1);
            queueContainer.add(splitPanel, 0, rowCount + 2, 1, 1);
            rowCount += 3;
        }
    }

    public void showOutputOptions(String message) {
        // create choice dialog box
        // option 1 is twilio
        // option 2 is reddit post

        // create a list of options
        List<String> options = new ArrayList<>();
        options.add("Twilio");
        options.add("Reddit");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("Twilio", options);
        dialog.setTitle("Output Options");
        dialog.setHeaderText("Choose an option");
        dialog.setContentText("Choose your output option:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String option = result.get();
            playerDataPresenter.sendOutput(option, message);
        }
    }

    /** 
     * This method sets up the summoner information by creating javafx components
     * @param summoner The summoner to get the information from
    */
    public void setUpSummonerData(Summoner summoner) {

        // queueScrollContainer takes up all space of the window
        // queueScrollContainer.setPrefSize(800, 640);
        queueScrollContainer.setMaxWidth(Double.MAX_VALUE);
        queueScrollContainer.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        // queueScrollContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        // queueScrollContainer.setStyle("-fx-background-color: BROWN;");
        // queueScrollContainer.setStyle("-fx-background-color: rgba(255, 220, 240, 0.5)");
        queueScrollContainer.setFitToWidth(true); 
        queueScrollContainer.setFitToHeight(true);

        // queueContainer.setStyle("-fx-background-color: BLUE;");
        queueContainer.setAlignment(Pos.CENTER);
        // GridPane.setHgrow(queueContainer, Priority.ALWAYS);

        queueScrollContainer.setPadding(new javafx.geometry.Insets(20, 20, 20, 20));
        // add borders to queueContainer
        // queueContainer.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        // queueContainer.setGridLinesVisible(true);
        queueContainer.setVgap(5);
        // ColumnConstraints cc = new ColumnConstraints(100);
        // cc.setHgrow(Priority.ALWAYS); 
        // queueContainer.getColumnConstraints().add(cc);
        queueScrollContainer.setContent(queueContainer);

        
        Label nameLevel = new Label(summoner.getName() + "\nLv: " + Integer.toString(summoner.getSummonerLevel()));

        // change font size and bold, italics, purple text
        nameLevel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-font-style: italic; -fx-text-fill: #ff00ff");

        String imageURI = "https://ddragon.leagueoflegends.com/cdn/12.7.1/img/profileicon/" + summoner.getProfileIconId() + ".png";
        Image image = new Image(imageURI, true);
        // set preferred size to 150 x 150
        ImageView profileImage = new ImageView(image);
        profileImage.setFitHeight(150);
        profileImage.setFitWidth(150);
        nameLevel.setGraphic(profileImage);

        header.getChildren().addAll(nameLevel);
        // set center top
        header.setAlignment(Pos.CENTER);

        // topPane.setTop(header);
        topPane.setCenter(queueScrollContainer);
    }


    /**
     * This method sets up the player data view stage
     * @param gameView is the main game view
     */
    public void setUpStage(GameView gameView) {
        Stage dataStage = new Stage();
        dataStage.setResizable(false);
        dataStage.setTitle("Display Summoner Data");

        // Summoner Name, Level
        BorderPane topPane = new BorderPane();
        StackPane stackPane = new StackPane();

        gameView.displayProgressIndicator(false);
        // progressIndicators.get(progressIndicators.size() - 1).setVisible(false);
        ProgressIndicator progressIndicator = new ProgressIndicator();
        gameView.setUpProgressIndicator(progressIndicator);
        gameView.addProgressIndicator(progressIndicator);
        

        Scene dataScene = gameView.setUpScene(topPane, "Yasuo_18.jpg", stackPane);
        gameView.setUpKeyListener(dataScene, topPane);

        ScrollPane queueScrollContainer = new ScrollPane();
        // set background color for queueScrollContainer
        queueScrollContainer.setStyle("-fx-background-color: rgba(255, 220, 240, 0.6)");

        // set background color for queueContainer that match the color for queueScrollContainer
        // set make queueContainer border round
        GridPane queueContainer = new GridPane();

        HBox header = new HBox();

        this.topPane = topPane;
        this.queueScrollContainer = queueScrollContainer;
        this.queueContainer = queueContainer;
        this.header = header;
        this.dataStage = dataStage;

        dataStage.setScene(dataScene);
        dataStage.initModality(Modality.APPLICATION_MODAL);
    }


    /**
     * This method displays all the data related to the summoner
     * @param summoner The summoner to display the data of
     * @param leagues The leagues of the summoner
    */ 
    public void displaySummonerData(Summoner summoner, League[] leagues, GameView gameView) {
        this.setUpSummonerData(summoner);    
    }

    
    public void setUpNavBar(GameView gameView, boolean longOutputOption, boolean searchOption) {
        gameView.setUpNavBar(topPane, header, longOutputOption, searchOption);
    }

    public void showStage() {
        dataStage.showAndWait();
    }
}