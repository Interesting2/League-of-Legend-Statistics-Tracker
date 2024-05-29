package major_project.View;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import major_project.Presenter.RedditPresenter;

public class RedditView {

    private RedditPresenter presenter;

    public RedditView(RedditPresenter presenter) {
        this.presenter = presenter;
    }

    /**
     * Displays the login page for oauth
     * @param gameView the main game view
     */
    public void showLoginPage(GameView gameView) {
        // create a login stage with javafx
        // show the login stage

        Stage loginStage = new Stage();
        loginStage.setResizable(false);
        loginStage.setTitle("Reddit Login");

        VBox centerBox = new VBox();
        TextField usernameField = new TextField("");
        TextField passwordField = new TextField("");

        // create a submit button
        Button submit = new Button("Submit");
        submit.setOnAction(e -> {
            presenter.loginRequest(usernameField.getText(), passwordField.getText());
            // loginStage.close();
        });

        // add text field to center box
        centerBox.getChildren().addAll(usernameField, passwordField, submit);
        centerBox.setSpacing(10);


        // set placeholder
        usernameField.setPromptText("Username");
        passwordField.setPromptText("Password");

        // set width and height of text fields
        usernameField.setMaxWidth(200);
        passwordField.setMaxWidth(200);
        usernameField.setMaxHeight(30);
        passwordField.setMaxHeight(30);


        BorderPane topPane = new BorderPane();
        StackPane stackPane = new StackPane();

        gameView.displayProgressIndicator(false);
        // progressIndicators.get(progressIndicators.size() - 1).setVisible(false);
        ProgressIndicator progressIndicator = new ProgressIndicator();
        gameView.setUpProgressIndicator(progressIndicator);
        gameView.addProgressIndicator(progressIndicator);
        
        HBox header = new HBox();

        Scene loginScene = gameView.setUpScene(topPane, "Yasuo_18.jpg", stackPane);
        gameView.setUpNavBar(topPane, header, false, true);
        gameView.setUpKeyListener(loginScene, topPane);

        centerBox.setAlignment(Pos.CENTER);

        // BorderPane.setAlignment(centerBox, Pos.CENTER);
        topPane.setCenter(centerBox);

        loginStage.setScene(loginScene);
        loginStage.initModality(Modality.APPLICATION_MODAL);
        // loginStage.initOwner(gameView.getScene().getWindow());
        loginStage.showAndWait();

        gameView.removeLastProgressIndicator();
    }

    /**
     * Displays the login success message to the user
     */
    public void displaySuccess() {
        // create alert dialog box
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Login");
        alert.setHeaderText("Success");
        alert.setContentText("Login successfully");
        alert.showAndWait();
    }

    /**
     * Displays the login failure message to the user
     */
    public void displayFail() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login");
        alert.setHeaderText("Fail");
        alert.setContentText("Login Failed");
        alert.showAndWait();
    }

    /**
     * Displays the Reddit post instructions to user
     */
    public void showPostPage(GameView gameView) {
        // create a instruction dialog box
        // show the instruction dialog box
        String instructions = "To post a text submission, you should first set up REDDIT_API_KEY " 
                                + "and REDDIT_API_SECRET env variables. You can then login with the "
                                + "Reddit login button by entering your reddit username and password. "
                                + "After you login, you can post a text submission by clicking the "
                                + "short output or long output buttons after searching a summoner. "
                                + "You will get to choose to use Twilio or Reddit Post.";
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Reddit Post");
        alert.setHeaderText("Instructions");
        alert.setContentText(instructions);
        alert.showAndWait();
    }
}
