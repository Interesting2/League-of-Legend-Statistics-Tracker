package major_project.View;

import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

import javafx.scene.control.ChoiceDialog;
import java.util.Optional;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

import major_project.Model.GameModel;
import major_project.Model.Summoner;
import major_project.Presenter.CreditTrackerPresenter;
import major_project.Presenter.GamePresenter;

/** 
 * This class is the view for the game. It displays the game and the user can interact with it.
*/
public class GameView {

    private GameModel model;
    private Scene scene;

    private MediaPlayer mediaPlayer;
    private boolean music_is_played;

    private GamePresenter gamePresenter;
    private CreditTrackerPresenter creditTrackerPresenter;

    private Label creditsRemaining;

    List<ProgressIndicator> progressIndicators = new ArrayList<ProgressIndicator>();
    // private ProgressIndicator progressIndicator = new ProgressIndicator();

    /**
     * Constructor for the GameView class.
     * @param model The model for the game.
     */
    public GameView(GameModel model) {
        this.model = model;
        this.music_is_played = true;
    }

    public void setPresenter(GamePresenter presenter) {
        this.gamePresenter = presenter;
        // playerDataPresenter = new PlayerDataPresenter(this.model, this);
        this.setUp();
    }

    private void setUp() {
        creditTrackerPresenter = new CreditTrackerPresenter(this.model, this);

        StackPane stackPane = new StackPane();
        BorderPane bp = new BorderPane();
        ProgressIndicator progressIndicator = new ProgressIndicator();
        setUpProgressIndicator(progressIndicator);
        progressIndicators.add(progressIndicator);
        
        this.scene = setUpScene(bp, "Azir_1.png", stackPane);


        setUpKeyListener(this.scene, bp);
        setUpNavBar(bp, null, true, false);
        setUpHomePage(bp);

        // play background music
        mediaPlayer = new MediaPlayer(new Media(new File("bg-music.wav").toURI().toString()));
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();
    }

    public void setUpProgressIndicator(ProgressIndicator progressIndicator) {
        progressIndicator.setProgress(-1d);
        progressIndicator.setPrefSize(100, 100);
        progressIndicator.setStyle("-fx-progress-color: #00bfff");
        progressIndicator.setVisible(false);
        progressIndicator.setLayoutX(500);
        progressIndicator.setLayoutY(300);

        progressIndicator.setMaxSize(100, 100);
        progressIndicator.setMinSize(100, 100);
        // progressIndicator.setStyle("-fx-progress-color: #00ff00");
        progressIndicator.setStyle("-fx-progress-size: 100px");
        progressIndicator.setStyle("-fx-progress-indeterminate-rate: 1");
        progressIndicator.setStyle("-fx-progress-indeterminate-transition: linear");
        progressIndicator.setStyle("-fx-progress-indeterminate-duration: 1");
        progressIndicator.setStyle("-fx-progress-indeterminate-cycle-duration: 1");
        System.out.println("Finish setting up progress indicator\n\n\n");
    }

    /**
     * This method sets up the scene for the game.
     * @param bp The border pane for the scene.
     * @param imageName The background image for the scene.
     * @return The scene for the game.
    */
    public Scene setUpScene(BorderPane bp, String imageName, StackPane stackPane) {
        
        Image bgImage = new Image(imageName, true);

        BackgroundImage background = new BackgroundImage(bgImage, 
                BackgroundRepeat.NO_REPEAT, 
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, 
                new BackgroundSize(1.0, 1.0, true, true, false, false)
        );

        bp.setBackground(new Background(background));
        

        // add bp and progressIndicator to stackpane
        stackPane.getChildren().addAll(bp, progressIndicators.get(progressIndicators.size() - 1));

        return new Scene(stackPane, 1100, 700, Color.TRANSPARENT);
    }

    public void addProgressIndicator(ProgressIndicator progressIndicator) {
        progressIndicators.add(progressIndicator);
    }

    public ProgressIndicator getLastProgressIndicator() {
        return progressIndicators.get(progressIndicators.size() - 1);
    }

    public void removeLastProgressIndicator() {
        progressIndicators.remove(progressIndicators.size() - 1);
    }

    /**
     * This method displays an error message.
     * @param message The message to display.
    */ 
    public void displayError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void displayProgressIndicator(boolean display) {
        // this.progressIndicator.setVisible(display);
        // set visible for the last progress indicator in list
        progressIndicators.get(progressIndicators.size() - 1).setVisible(display);
    }

    /**
     * This method sets up the key listener for the scene.
     * @param scene The scene to set up the key listener for.
     * @param pane The pane to set up the key listener for.
    */
    public void setUpKeyListener(Scene scene, BorderPane bp) {
        // this.progressIndicator = progressIndicator;
        scene.setOnKeyPressed(event -> {

            if (event.getCode() == javafx.scene.input.KeyCode.ENTER && gamePresenter.getSearchBarFocus()) {
                System.out.println("Enter pressed");
                this.gamePresenter.searchSummoner();
            }
        });
    }

    /**
     * This method sets up the home page / main window
     * It sets the title, credit amount, and search bar in the middle of the window.
     * @param bp The border pane to set up the home page / main window
    */ 
    public void setUpHomePage(BorderPane bp) {
        GridPane gp = new GridPane();
        gp.setAlignment(Pos.CENTER);
        gp.setVgap(10);
        gp.setHgap(10);

        Label label = new Label("Welcome to the game!");
        label.setText("Welcome to the Riot Games API GUI!");
        label.setTextFill(Color.WHITE);
        label.setStyle("-fx-font-size: 30px;");
        label.setWrapText(true);

        gp.add(label, 0, 0);

        // set up credit score label
        Label creditScoreLabel = new Label("Search Credits remaining: ");
        creditScoreLabel.setAlignment(Pos.CENTER);
        creditScoreLabel.setTextFill(Color.AQUAMARINE);
        creditScoreLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");
        creditScoreLabel.setWrapText(true);

        this.creditsRemaining = creditScoreLabel;   // store the label
        gp.add(creditScoreLabel, 0, 1);

        // create search bar
        TextField textField = createSearchBar();
        textField.setMaxWidth(500);
        textField.setPrefHeight(50);
        gp.add(textField, 0, 2);

        bp.setCenter(gp);

        // create initial stage to enter credit amount
        inputCreditScore(); 
    }

    /**
     * This method creates a stage that asks user for the desired credit amount
     * Credit amount is between 1 and 10 (inclusively)
    */
    private void inputCreditScore() {
        // pop up to let user enter a number between 1 to 10
        TextField creditField = new TextField();
        creditField.setPromptText("Enter a number between 1 to 10");
        creditField.setMaxWidth(350);
        creditField.setPrefHeight(35);
        creditField.setAlignment(Pos.CENTER);
        creditField.setStyle("-fx-font-size: 20px;");


        // initial stage before the main window
        Stage stage = new Stage();
        stage.setTitle("Enter a number between 1 to 10");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.setOnCloseRequest((event) -> {
            // end program if user clicks on the close button
            System.exit(0);
        });

        // create a button to confirm the input and pass to presenter
        Button submitButton = new Button("Submit");
        submitButton.setStyle("-fx-background-color: #00bfff;-fx-text-fill: white; -fx-font-size: 20px;");
        submitButton.setOnAction(e -> {
            String text = creditField.getText();
            try {
                // validate and save the credit if it's is between 1 to 10
                creditTrackerPresenter.submitCredit(text);  
                stage.close();
            } catch (IllegalArgumentException ae) {
                // not an integer
                displayError(ae.getMessage());
            } catch (InputMismatchException im) {
                // not between 1 and 10
                displayError(im.getMessage());
            }
        });
        
        // create a vbox that contains the creditField and submitButton
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);
        vbox.getChildren().addAll(creditField, submitButton);

        Scene scene = new Scene(vbox, 380, 200);
        stage.setScene(scene);
        stage.showAndWait();
    }

    /**
     * This method is called by the CreditTrackerPresenter
     * It updates the credit amount label with the new remaining credit amount
     * @param credit The new credit amount
    */
    public void updateCredit(int credit) {
        this.creditsRemaining.setText("\tSearch Credits remaining: " + credit);
    }
    

    /**
     * This method helps create search bar to search for summoner name
     * @return a textfield where user can type a summoner name
    */ 
    private TextField createSearchBar() {
        TextField textField = new TextField();
        
        textField.setStyle("-fx-background-color: rgba(245, 240, 250, 0.8);-fx-border-radius: 10px;-fx-border-color: black;-fx-prompt-text-fill: grey;-fx-font-size: 18px;");

        textField.setFocusTraversable(false); 
        textField.setPromptText("Type a Summoner Name! Click Enter to search...");
        textField.setAlignment(Pos.CENTER);;
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            gamePresenter.setSummonerName(newValue);
            // summonerName = newValue;
            // System.out.println(this.summonerName);
        });

        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            gamePresenter.setSearchBarFocus(newValue);
        });

        return textField;
    }

    
    /**
     * This method helps pass the long output message from the model class to the setUpTwilio method
     */ 
    private void sendLongOutput() {
        String message = model.getLongMessage();
        gamePresenter.checkRedditEnv(message);
    }

    public void showOutputOptions(String message) {

        // check if env variables for reddit are set
        
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
            gamePresenter.sendOutput(option, message);
        }
    }


    /**
     * This method helps to display the about alert to the user
    */
    private void displayAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("About");
        // change font

        alert.setContentText("Game Name: Riot Games API GUI.\n\n"
                + "This GUI was created by: Lei Io Tou \n\n"
                + "References Used: \n\n"
                + "https://developer.riotgames.com/docs/portal\n"
                + "https://www.twilio.com/docs/usage/api\n"
                + "https://www.sqlite.org/docs.html\n"
                + "https://sqliteonline.com/\n"
                + "https://www.dl-sounds.com/royalty-free/category/game-film/video-game/\n"
                + "https://javadoc.io/doc/org.openjfx/javafx-base/11/index.html"
        );
        alert.showAndWait();
    }

    /**
     * This method helps to set up the navbar of a scene
     * @param bp The border pane to set up the navbar
     * @param optional The long output menu item to be added to the navbar 
     * @param longOutputOption Indicating whether a long output menu item is needed in the scene
    */ 
    public void setUpNavBar(BorderPane bp, HBox optional, boolean longOutputOption, boolean searchOption) {
        MenuBar menuBar = new MenuBar();

        Menu menu1 = new Menu("About");
        Menu menu2 = new Menu("Clear Cache");

        MenuItem menuItem1 = new MenuItem("About");
        menu1.getItems().add(menuItem1);

        menuItem1.setOnAction(event -> {
            displayAbout();
        });

        MenuItem menuItem2 = new MenuItem("Clear Cache");
        menuItem2.setOnAction(event -> {

            try {
                Summoner[] summoners = gamePresenter.getAllSummoners();
                String[] summonerNames = gamePresenter.getNames(summoners);

                 // create a choicedialog
                ChoiceDialog<String> dialog = new ChoiceDialog<>("", summonerNames);
                dialog.setTitle("Clear Cache");
                dialog.setHeaderText("Select a Summoner");
                dialog.setContentText("Choose a Summoner");

                Optional<String> input = dialog.showAndWait();

                if (input.isPresent()) {
                    String choice = input.get();
                    gamePresenter.matchSummoner(summoners, choice);
                }
            } catch (IllegalStateException e) {
                displayError(e.getMessage());
            }
        });

        menu2.getItems().add(menuItem2);

        Image searchImage = new Image("search.png", 20, 20, false, true);
        Image closeImage = new Image("close.png", 20, 20, false, true);
        
        VBox navContainer = new VBox();
        
        HBox navBar = new HBox();
        navBar.setMinWidth(this.scene.getWidth());
        navBar.setAlignment(Pos.TOP_LEFT);
        Button searchButton = new Button("Search");
        searchButton.setGraphic(new ImageView(searchImage));
        navBar.getChildren().addAll(searchButton);
        
        TextField searchBar = createSearchBar(); 
        Button closeButton = new Button("Close");
        
        closeButton.setGraphic(new ImageView(closeImage));
        closeButton.setPrefHeight(36);
        
        // searchBar take up all the space available
        HBox searchContainer = new HBox();
        searchContainer.setVisible(false);
        HBox.setHgrow(searchBar, Priority.ALWAYS);
        searchContainer.getChildren().addAll(searchBar, closeButton);
        
        navContainer.getChildren().addAll(menuBar, searchContainer);
        
        closeButton.setOnAction(e -> {
            searchContainer.setVisible(false);
        });

        // toggle music feature
        Menu menu4 = new Menu("Music");
        MenuItem menuItem4 = new MenuItem("Toggle");

        
        menuItem4.setOnAction(event -> {
            if (music_is_played) {
                mediaPlayer.stop();
                music_is_played = false;
            } else {
                mediaPlayer.play();
                music_is_played = true;
            }
        });

        menu4.getItems().add(menuItem4);
        menuBar.getMenus().addAll(menu1, menu2, menu4);

        if (optional != null) {
            if (longOutputOption) {

                Menu menu5 = new Menu("Output");
                MenuItem menuItem5 = new MenuItem("Long Output");
                menuItem5.setOnAction(event -> {
                    sendLongOutput();
                });
                menu5.getItems().add(menuItem5);
                menuBar.getMenus().add(menu5);
            }

            if (searchOption) {
                Menu menu3 = new Menu("Search");
                MenuItem menuItem3 = new MenuItem("Search");
                menuItem3.setOnAction(event -> {
                    searchContainer.setVisible(true);
                });

                menu3.getItems().add(menuItem3);
                menuBar.getMenus().add(menu3);
            }

            GridPane newNavContainer = new GridPane();

            // newNavContainer takes all the space available
            GridPane.setHgrow(navContainer, Priority.ALWAYS);

            newNavContainer.add(navContainer, 0, 0, 1, 1);
            newNavContainer.add(optional, 0, 1, 1, 1);
            bp.setTop(newNavContainer);
        } else {
            bp.setTop(navContainer);
        }

        // create menu for reddit
        Menu menu6 = new Menu("Reddit");
        MenuItem login = new MenuItem("Login");
        login.setOnAction(event -> {
            gamePresenter.login();
        });

        MenuItem post = new MenuItem("post");
        post.setOnAction(event -> {
            gamePresenter.post();
        });

        menu6.getItems().addAll(login, post);
        menuBar.getMenus().add(menu6);

    }

    /**
     * This method helps return the scene
     * @return The scene
    */ 
    public Scene getScene() {
        return scene;
    }
}