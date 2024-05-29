package major_project;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import major_project.Model.GameModel;
import major_project.Presenter.GamePresenter;
import major_project.View.GameView;

public class Main extends Application{

    public void start(Stage primaryStage) {
        String inputStatus = getParameters().getRaw().get(0);
        String outputStatus = getParameters().getRaw().get(1);

        System.out.println(inputStatus + " " + outputStatus);

        
        GameModel model = new GameModel(inputStatus, outputStatus);
        GameView view = new GameView(model);
        GamePresenter presenter = new GamePresenter(view, model); 
        // primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(view.getScene());
        primaryStage.setResizable(false);
        primaryStage.setTitle("Riot Games API GUI");
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }    
}
